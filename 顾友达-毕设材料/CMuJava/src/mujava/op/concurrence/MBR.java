/*
 * @author: GuYouda
 * @date: 2018/4/13
 * @time: 21:33
* @des: The CyclicBarrier constructor has a parameter that is an optional runnable thread that can happen
 *       after all the threads complete and reach the barrier. The MBR operator modifies the runnable thread
 *       parameter by removing it if it is present. This is a special case of the method level mutation
 *       operator, statement deletion(SDL).
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.AllocationExpression;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

import java.io.IOException;
import java.io.PrintWriter;

public class MBR extends Mutator {
    public MBR(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public MBR(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(AllocationExpression statement) throws ParseTreeException {
        if (isTarget(statement)) {
            outputToFile(statement);
        } else {
            super.visit(statement);
        }
    }

    /**
     * 输出MBR并发变异体
     *
     * @param statement 原始方法声明
     */
    public void outputToFile(AllocationExpression statement) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            MBR_Writer writer = new MBR_Writer(mutant_dir, out, getEnvironment());
            AllocationExpression mutant = (AllocationExpression) statement.makeRecursiveCopy();
            writer.setMutant(statement, mutant);
            comp_unit.accept(writer);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("fails to create " + f_name);
        } catch (ParseTreeException e) {
            System.err.println("errors during printing " + f_name);
            e.printStackTrace();
        }
    }

    private boolean isTarget(AllocationExpression expression) {
        return "java.util.concurrent.CyclicBarrier".equals(expression.getClassType().getName())
                && expression.getArguments().size() == 2;
    }

}
