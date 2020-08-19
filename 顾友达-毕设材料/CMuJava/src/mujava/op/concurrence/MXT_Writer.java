/*
 * @author: GuYouda
 * @date: 2018/4/8
 * @time: 9:50
 * @des: Modify Method-X Timeout
 *      The MXT operator can be applied to the wait(), sleep(), and join() method calls
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;

public class MXT_Writer extends ConcurrentMutantCodeWriter {

    private MethodCall original = null;
    private MethodCall mutant = null;
    private String modifier = "*";

    public MXT_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(MethodCall original, MethodCall mutant, String modifier) {
        this.original = original;
        this.mutant = mutant;
        this.modifier = modifier;
    }

    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (!(isSameObject(methodCall, original))) {
            super.visit(methodCall);
        } else {

            Expression expr = methodCall.getReferenceExpr();
            TypeName reftype = methodCall.getReferenceType();

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

            String name = methodCall.getName();
            out.print(name);

            ExpressionList args = methodCall.getArguments();
            out.print("(");
            if (args.size() == 1) {
                Expression arg0 = args.get(0);
                arg0.accept(this);
                out.print(modifier + " 2");
//                out.print(" * 2 ");
            } else if (args.size() == 2) {
                Expression arg0 = args.get(0);
                Expression arg1 = args.get(1);
                arg0.accept(this);
                out.print(modifier + " 2, ");
//                out.print(" * 2, ");
                arg1.accept(this);
            }
            out.print(")");


            // -------------------------
            mutated_line = line_num;
            writeLog(removeNewline(mutant.toString()) + " is changed.");
            // -------------------------

            line_num++;
        }
    }


}
