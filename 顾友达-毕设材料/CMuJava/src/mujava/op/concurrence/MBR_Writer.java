/*
 * @author: GuYouda
 * @date: 2018/4/13
 * @time: 21:48
* @des: The CyclicBarrier constructor has a parameter that is an optional runnable thread that can happen
 *       after all the threads complete and reach the barrier. The MBR operator modifies the runnable thread
 *       parameter by removing it if it is present. This is a special case of the method level mutation
 *       operator, statement deletion(SDL).
 *
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.mop.Environment;
import openjava.ptree.AllocationExpression;
import openjava.ptree.ExpressionList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;

public class MBR_Writer extends ConcurrentMutantCodeWriter {

    private AllocationExpression original = null;
    private AllocationExpression mutant = null;
    private Environment environment = null;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public MBR_Writer(String file_name, PrintWriter out, Environment env) {
        super(file_name, out);
        this.environment = env;
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
            AllocationExpression temp = (AllocationExpression) expression.makeRecursiveCopy();
            ExpressionList tempArgs = new ExpressionList(expression.getArguments().get(0));
            temp.setArguments(tempArgs);
            mutated_line = line_num;
            writeLog(removeNewline(" new CyclicBarrier(int, Runnable): ")
                    + " the 2nd argument is removed");
            line_num++;
            super.visit(temp);
        }

    }
}