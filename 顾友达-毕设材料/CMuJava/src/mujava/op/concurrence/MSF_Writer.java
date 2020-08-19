/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:48
 * @des: Modify Semaphore Fairness
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;

public class MSF_Writer extends ConcurrentMutantCodeWriter {

    private AllocationExpression original = null;
    private AllocationExpression mutant = null;

    public MSF_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(AllocationExpression original, AllocationExpression mutant) {
        this.original = original;
        this.mutant = mutant;
    }

    public void visit(AllocationExpression expression) throws ParseTreeException {
        if (!(isSameObject(expression, original))) {
            super.visit(expression);
        } else {

            ExpressionList arguments = expression.getArguments();
            Expression arg0 = null, arg1 = null;
            if (arguments.size() == 1) {
                arg0 = arguments.get(0);
                arg1 = new Literal(Literal.BOOLEAN, "true");
            } else if (arguments.size() == 2) {
                arg0 = arguments.get(0);
                arg1 = arguments.get(1);
                String fairness = "false".equals(arg1.toString()) ? "true" : "false";
                arg1 = new Literal(Literal.BOOLEAN, fairness);
            }
            ExpressionList newArgs = new ExpressionList(arg0, arg1);

            AllocationExpression temp = (AllocationExpression) expression.makeRecursiveCopy();
            temp.setArguments(newArgs);
            mutated_line = line_num;
            writeLog(removeNewline(expression.toString()) + " is changed...");
            line_num++;
            super.visit(temp);

        }

    }
}