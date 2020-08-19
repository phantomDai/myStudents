/*
 * @author: GuYouda
 * @date: 2018/4/6
 * @time: 9:36
 * @des: RTXC变异算子输出
 *      Remove Thread Method-X Call
 *      (wait(), join(), sleep(), yield(), notify(), notifyAll() Methods)
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;

public class RTXC_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall original = null;
    private MethodCall mutant = null;

    public RTXC_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(MethodCall original, MethodCall mutant) {
        this.original = original;
        this.mutant = mutant;
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (!(isSameObject(methodCall, original))) {
            super.visit(methodCall);
        } else {
            // writeTab();
            out.print("// ");
            super.visit(methodCall);

            /*
            out.print(methodCall.getReferenceExpr().toString());
            out.print(".");
            out.print(methodCall.getName());
            out.print("("+methodCall.getArguments().toString()+");");
            */

            // -------------------------
            mutated_line = line_num;
            writeLog(removeNewline(mutant.toString()) + " is deleted.");
            // -------------------------

            line_num++;
        }
    }
}
