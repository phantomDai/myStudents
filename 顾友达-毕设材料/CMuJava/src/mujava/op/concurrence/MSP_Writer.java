/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:48
 * @des:
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class MSP_Writer extends ConcurrentMutantCodeWriter {

    private SynchronizedStatement original = null;
    private SynchronizedStatement mutant = null;

    public MSP_Writer(String file_name, PrintWriter out) {
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


    public void visit(SynchronizedStatement statement) throws ParseTreeException {
        if (!(isSameObject(statement, original))) {
            super.visit(statement);
        } else {
            mutated_line = line_num;
            writeLog(removeNewline("synchronized (" + statement.getExpression().toString() + ")")
                    + " is changed.");
            super.visit(mutant);
        }
    }


}
