/*
 * @author: GuYouda
 * @date: 2018/4/14
 * @time: 10:36
 * @des: RCXC - Remove Concurrency Mechanism Method-X Call
 *      The RCXC operator can be applied to the following concurrency mechanisms: Locks (lock(), unlock()),
 *      Condition (signal(), signalAll()), Semaphore (acquire(), release()), Latch(countDown()),
 *      and ExecutorService (e.g., submit()).
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;

public class RCXC_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall original = null;
    private MethodCall mutant = null;
    private AssignmentExpression assignmentExpression = null;

    RCXC_Writer(String file_name, PrintWriter out) {
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

    void setAssignmentExpression(AssignmentExpression assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
    }

    @Override
    public void visit(AssignmentExpression assExpr) throws ParseTreeException {
        if (!(isSameObject(assExpr, assignmentExpression))) {
            super.visit(assExpr);
        } else {
            // writeTab();
            out.print("// ");
            super.visit(assignmentExpression);
            // -------------------------
            mutated_line = line_num;
            writeLog(removeNewline(mutant.toString()) + " is deleted.");
            // -------------------------
            line_num++;
        }
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
