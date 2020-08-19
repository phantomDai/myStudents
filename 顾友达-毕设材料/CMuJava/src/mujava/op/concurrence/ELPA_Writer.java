/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 11:55
 * @des: ELPA - Exchange Lock/Permit Acquistion
 */
package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;


public class ELPA_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall mutant = null;
    private boolean isMutantTarget = false;
    private String newMethodCallName = null;

    /**
     * 设置变异体
     *
     * @param methodCall 方法
     */
    public void setMutant(MethodCall methodCall) {
        mutant = methodCall;
        isMutantTarget = true;
    }

    public void setMutantMechodCallName(String newMethodCall) {
        newMethodCallName = newMethodCall;
    }

    public ELPA_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (isSameObject(methodCall, mutant) && isMutantTarget) {
            MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
            if (newMethodCallName != null) {
                temp.setName(newMethodCallName);
            }
            // -------------------------------------------------------------
            mutated_line = line_num;
            writeLog(removeNewline(methodCall.toString() + "-->" + temp.toString()));
            // -------------------------------------------------------------
            if ("lockInterruptibly".equals(newMethodCallName)) {
                out.print("try {\n");
                super.visit(temp);
                out.print(";\n");
                out.print("} catch (InterruptedException e) {\n");
                out.print("e.printStackTrace();\n");
                out.print("}\n");
            } else {
                super.visit(temp);
            }
        } else {
            super.visit(methodCall);
        }
    }
}
