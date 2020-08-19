/*
 * @author: GuYouda
 * @date: 2018/4/20
 * @time: 08:55
 * @des: EXCR - Expand Critical Region
 * Expanding a critical region to include statements above and below the statements
 * required to be in the critical region can cause performance issues by unnecessarily
 * reducing the degree of concurrency.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class EXCR_Writer extends ConcurrentMutantCodeWriter {

    private StatementList original = null;
    private StatementList mutant = null;

    public EXCR_Writer(String file_name, PrintWriter out) {
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

            // synchronizedBlock之前包含其他语句
            if (beforeStatements.size() != 0) {
                synStatementListTemp.insertElementAt(beforeStatements.remove(beforeStatements.size() - 1), 0);
            }

            // synchronizedBlock之后包含其他语句
            if (afterStatements.size() != 0) {
                synStatementListTemp.add(afterStatements.remove(0));
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
