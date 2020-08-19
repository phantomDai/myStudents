/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 21:55
 * @des: SHCR - Shift Critical Region
 * Shifting a critical region up or down can potentially cause interference bugs
 * by no longer synchronizing access to a shared variable.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class SHCR_Writer extends ConcurrentMutantCodeWriter {

    private StatementList original = null;
    private StatementList mutant = null;

    public SHCR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(StatementList original, StatementList mutant) {
        this.original = original;
        this.mutant = mutant;
    }

    @Override
    public void visit(StatementList statementList) throws ParseTreeException {
        if (!(isSameObject(statementList, original))) {
            super.visit(statementList);
        } else {
            int synPosition = -1;
            // 获取synchronizedBlock前后的语句列表
            StatementList beforeStatements = new StatementList();
            StatementList afterStatements = new StatementList();

            for (int i = 0; i < statementList.size(); i++) {
                Statement statement = statementList.get(i);
                if (statement instanceof SynchronizedStatement) {
                    synPosition = i;
                }
                if (synPosition == -1 || i < synPosition) {
                    beforeStatements.add(statement);
                }
                if (synPosition != -1 && i > synPosition) {
                    afterStatements.add(statement);
                }
            }
            // 获取SynchronizedBlock内语句列表
            SynchronizedStatement synStatement = (SynchronizedStatement) statementList.get(synPosition);
            StatementList synStatementsList = synStatement.getStatements();
            SynchronizedStatement synStatementTemp = (SynchronizedStatement) synStatement.makeRecursiveCopy();
            StatementList synStatementListTemp = (StatementList) synStatementsList.makeRecursiveCopy();


            // synchronizedBlock是第一个且后面还有其他语句，向下移动一个
            if (synPosition == 0 && afterStatements.size() != 0) {
                Statement firstStatement = synStatementsList.get(0);
                beforeStatements.add(firstStatement);
                synStatementListTemp.remove(0);
                synStatementListTemp.add(afterStatements.get(0));
                afterStatements.remove(0);
            }

            // synchronizedBlock是最后一个且前面有其他与句，向上移动一个
            if (synPosition == statementList.size() - 1 && beforeStatements.size() != 0) {
                Statement lastStatement = synStatementsList.get(synStatementsList.size() - 1);
                afterStatements.insertElementAt(lastStatement, 0);
                synStatementListTemp.remove(synStatementListTemp.size() - 1);
                synStatementListTemp.insertElementAt(beforeStatements.get(beforeStatements.size() - 1), 0);
                beforeStatements.remove(beforeStatements.size() - 1);
            }


            // synchronizedBlock在中间，向上或向下移动一个
            if (synPosition > 0 && synPosition < statementList.size() - 1) {
                Statement lastStatement = synStatementsList.get(synStatementsList.size() - 1);
                afterStatements.insertElementAt(lastStatement, 0);
                synStatementListTemp.remove(synStatementListTemp.size() - 1);
                synStatementListTemp.insertElementAt(beforeStatements.get(beforeStatements.size() - 1), 0);
                beforeStatements.remove(beforeStatements.size() - 1);
            }

            synStatementTemp.setStatements(synStatementListTemp);
            StatementList newStatementList = new StatementList();
            newStatementList.addAll(beforeStatements);
            newStatementList.add(synStatementTemp);
            newStatementList.addAll(afterStatements);

            // 记录变异位置
            mutated_line = line_num;
            writeLog(removeNewline("synchronized block has been changed."));

            super.visit(newStatementList);

        }
    }


}
