/*
 * @author: GuYouda
 * @date: 2018/03/22
 * @time: 02:33
 * @des: RSK 变异体输出
 */
package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;


public class RJS_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall mutant = null;
    private boolean isMutantTarget = false;

    /**
     * 设置变异体
     *
     * @param methodCall 方法
     */
    public void setMutant(MethodCall methodCall) {
        mutant = methodCall;
        isMutantTarget = false;
    }

    public RJS_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (isSameObject(methodCall, mutant)) {
            MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
            temp.setName("sleep");

            if (temp.getArguments().size() != 0) {
                super.visit(temp);
            }
            else {
                Expression expr = temp.getReferenceExpr();
                TypeName reftype = temp.getReferenceType();

                if (expr != null) {

                    if (expr instanceof Leaf
                            || expr instanceof ArrayAccess
                            || expr instanceof FieldAccess
                            || expr instanceof MethodCall
                            || expr instanceof Variable) {
                        expr.accept(this);
                    } else {
                        writeParenthesis(expr);
                    }
                } else if (reftype != null) {
                    reftype.accept(this);
                }
                out.print(".");
                String name = temp.getName();
                out.print(name);
                // 如果没有参数，默认1s
                out.print("(1000)");
            }

            // -------------------------------------------------------------
            mutated_line = line_num;
            //out.print(mutated_modifier);
            writeLog(removeNewline("join() --> sleep()"));
            // -------------------------------------------------------------
        } else {
            super.visit(methodCall);
        }
    }
}
