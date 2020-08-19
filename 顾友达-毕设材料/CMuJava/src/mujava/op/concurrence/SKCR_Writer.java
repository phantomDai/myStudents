/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 20:31
 * @des: SKCR - Shrink Critical Region
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class SKCR_Writer extends ConcurrentMutantCodeWriter {

    private SynchronizedStatement original = null;
    private SynchronizedStatement mutant = null;

    public SKCR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(SynchronizedStatement original, SynchronizedStatement mutant) {
        this.original = original;
        this.mutant = mutant;
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) throws ParseTreeException {
        if (!(isSameObject(synchronizedStatement, original))) {
            super.visit(synchronizedStatement);
        } else {
            Expression expression = synchronizedStatement.getExpression();
            StatementList statements = synchronizedStatement.getStatements();
            StatementList beforeStatementList = new StatementList();
            StatementList statementList = new StatementList();
            StatementList afterStatementList = new StatementList();

            int size = statements.size();
            if (size == 2) {
                beforeStatementList.add(statements.get(0));
                statementList.add(statements.get(1));
            } else if (size > 2) {
                beforeStatementList.add(statements.get(0));
                afterStatementList.add(statements.get(size - 1));
                statementList = (StatementList) statements.makeRecursiveCopy();
                statementList.remove(size - 1);
                statementList.remove(0);
            }

            // 记录变异位置
            mutated_line = line_num;
            writeLog(removeNewline("synchronized (" + expression.toString() + ")")
                    + " is changed.");

            // 输出变异块
            writeTab();
            writeStatements(beforeStatementList);
            out.print("synchronized( " + expression + " )");
            line_num++;
            writeStatementsBlock(statementList);
            out.println();
            if (size > 2) {
                writeStatements(afterStatementList);
            }
        }
    }

    /**
     * 输出语句列表
     * @param statementList 语句列表
     * @throws ParseTreeException 异常
     */
    private void writeStatements(StatementList statementList) throws ParseTreeException {
        line_num++;
        pushNest();
        statementList.accept(this);
        popNest();
        writeTab();
    }


}
