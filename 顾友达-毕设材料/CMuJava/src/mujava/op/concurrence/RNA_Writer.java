/*
 * @author: GuYouda
 * @date: 2018/04/02
 * @time: 11:55
 * @des: RNA(Replace notifyAll() with notify()) 变异体输出
 */
package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;


public class RNA_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall mutant = null;
    private boolean isMutantTarget = false;

    /**
     * 设置变异体
     *
     * @param methodCall 方法
     */
    public void setMutant(MethodCall methodCall) {
        mutant = methodCall;
        isMutantTarget = true;
    }

    public RNA_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (isSameObject(methodCall, mutant) && isMutantTarget) {
            MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
            temp.setName("notify");
            super.visit(temp);

            // -------------------------------------------------------------
            mutated_line = line_num;
            writeLog(removeNewline("notifyAll() --> notify()"));
            // -------------------------------------------------------------
        } else {
            super.visit(methodCall);
        }
    }
}
