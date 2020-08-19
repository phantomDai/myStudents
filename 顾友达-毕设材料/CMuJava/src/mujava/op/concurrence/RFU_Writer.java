/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:55
 * @des: RFU - Remove Finally Around Unlock
 * The finally keyword is important in releasing explicit locks.
 * The finally keyword ensures that the unlock() method call will occur after a try block regardless
 * of whether or not an exception is thrown. If finally is removed the unlock() will not
 * occur in the presence of an exception and cause a blocking critical section bug.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.CatchList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.TryStatement;

import java.io.PrintWriter;

public class RFU_Writer extends ConcurrentMutantCodeWriter {
    private TryStatement mutant = null;

    /**
     * 设置变异体
     *
     * @param tryStatement finally代码块
     */
    public void setMutant(TryStatement tryStatement) {
        mutant = tryStatement;
    }

    public RFU_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(TryStatement tryStatement) throws ParseTreeException {
        if (isSameObject(tryStatement, mutant)) {
            writeTab();
            out.print("try ");
            StatementList stmts = tryStatement.getBody();
            writeStatementsBlock(stmts);

            CatchList catchlist = tryStatement.getCatchList();
            if (!catchlist.isEmpty()) {
                catchlist.accept(this);
            }

            StatementList finstmts = tryStatement.getFinallyBody();
            if (!finstmts.isEmpty()) {
//                out.println(" finally ");
                mutated_line = line_num;
                writeLog(removeNewline("finally is removed"));
                line_num++;
                writeStatements(finstmts);
            }

            out.println();
            line_num++;
        } else {
            super.visit(tryStatement);
        }
    }

    protected void writeStatements(StatementList statementList) throws ParseTreeException {
        line_num++;
        pushNest();
        statementList.accept(this);
        popNest();
        writeTab();
    }
}
