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

public class RXO_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall original = null;
    private MethodCall mutant = null;

    public RXO_Writer(String file_name, PrintWriter out) {
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
            super.visit(mutant);
            // -------------------------
            mutated_line = line_num;
            writeLog(removeNewline(methodCall.toString()) + " has been modified.");
            // -------------------------

            line_num++;
        }
    }
}
