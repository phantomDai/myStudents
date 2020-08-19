/*
 * @author: GuYouda
 * @date: 2018/4/12
 * @time: 16:33
 * @des: Modify Semaphore Fairness
 *      A semaphore maintains a set of permits for accessing a resource. In the constructor of a Semaphore
 *      there is an optional parameter for a boolean fairness setting. When the fairness setting is not used
 *      the default fairness value is false which allows for unfair permit acquisition. If the fairness
 *      parameter is a constant then the MSF operator is a special case of the Constant Replacement (CRP)
 *      method level operator and replaces a true value with false and a false value with true. In the case
 *      that a boolean variable is used as a parameter we simply negate it.
 *      A potential consequence of expecting a semaphore to be fair when in fact it is not is that there is
 *      a potential for starvation because no guarantees about permit acquisition ordering can be given.
 *      In fact, when a semaphore is unfair any thread that invokes the Semaphore’s acquire() method to
 *      obtain a permit may receive one prior to an already waiting thread - this is known as barging.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class MSF extends Mutator {

    public MSF(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public MSF(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(AllocationExpression statement) throws ParseTreeException {
        String typeName = statement.getClassType().getName();
        if("java.util.concurrent.Semaphore".equals(typeName)){
            outputToFile(statement);
        }else {
            super.visit(statement);
        }

    }

    /**
     * 输出MSF并发变异体
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
            MSF_Writer writer = new MSF_Writer(mutant_dir, out);
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

}
