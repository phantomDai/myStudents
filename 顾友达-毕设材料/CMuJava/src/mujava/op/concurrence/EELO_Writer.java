/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 20:37
 * @des: RXO - Replace One Concurrency Mechanism-X with Another
 *      When two instances of the same concurrency mechanism exist we replace a call to one with a c
 *      all to the other.We apply the RXO operator when 2 or more objects exist of type Semaphore,
 *      CountDownLatch, Lock, CyclicBarrier, Exchanger.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;

public class EELO_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall methodCall1 = null;
    private MethodCall methodCall2 = null;

    private String log = "";

    EELO_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param methodCall1 原表达式
     * @param methodCall2 变异体
     */
    public void setMutant(MethodCall methodCall1, MethodCall methodCall2) {
        this.methodCall1 = methodCall1;
        this.methodCall2 = methodCall2;
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (!(isSameObject(methodCall, methodCall1)) &&
                !(isSameObject(methodCall, methodCall2))) {
            super.visit(methodCall);
        } else if (isSameObject(methodCall, methodCall1)) {
            MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
            temp.setReferenceExpr(methodCall2.getReferenceExpr());
            // -------------------------
            mutated_line = line_num;
            log += "line " + line_num;
            // writeLog(removeNewline(methodCall.toString()) + " has been modified.");
            // -------------------------
            super.visit(temp);
        } else if (isSameObject(methodCall, methodCall2)) {
            MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
            temp.setReferenceExpr(methodCall1.getReferenceExpr());
            // -------------------------
            mutated_line = line_num;
            log += ", line" + line_num + "has been changed";
            // writeLog(removeNewline(methodCall.toString()) + " has been modified.");
            // -------------------------
            super.visit(temp);
        }
    }

    void writeMutationLog() {
        writeLog(log);
    }


}
