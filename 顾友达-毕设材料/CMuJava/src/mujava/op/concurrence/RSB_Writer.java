/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:35
 * @des: RSB - Remove Synchronized Block
 * Similar to the RSB operator, the RSB operator removes the synchronized keyword
 * from around a statement block which can cause a no lock bug.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class RSB_Writer extends ConcurrentMutantCodeWriter {
    private SynchronizedStatement mutant = null;

    /**
     * 设置变异体
     *
     * @param synchronizedStatement 同步代码块
     */
    public void setMutant(SynchronizedStatement synchronizedStatement) {
        mutant = synchronizedStatement;
    }

    public RSB_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) throws ParseTreeException {
        if (isSameObject(synchronizedStatement, mutant)) {
            mutated_line = line_num;
            writeLog(removeNewline("synchronized is removed"));
            StatementList stmts = synchronizedStatement.getStatements();
            writeStatements(stmts);
            out.println();
            line_num++;
        } else {
            super.visit(synchronizedStatement);
        }
    }

    /**
     * 输出语句集合
     * @param stmts 语句集合
     * @throws ParseTreeException 异常
     */
    private void writeStatements(StatementList stmts)
            throws ParseTreeException {
        line_num++;
        pushNest();
        stmts.accept(this);
        popNest();
        writeTab();
    }
}
