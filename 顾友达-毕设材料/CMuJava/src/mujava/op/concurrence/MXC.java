/*
 * @author: GuYouda
 * @date: 2018/4/13
 * @time: 6:33
 * @des: MXC  Modify Concurrency Mechanism-X Count
 *      The MXC operator is applied to parameters in three of Java’s concurrency mechanisms: Semaphores,
 *      Latches, and Barriers. A latch allows a set of threads to countdown a set of operations and a
 *      barrier allows a set of threads to wait at a point until a number of threads reach that point.
 *      The count being modified in Semaphores is the set of permits, and in Latches and Barriers it is the
 *      number of threads. We will next provide an example of the MXC operator for Semaphores, Latches,
 *      and Barriers.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MXC extends Mutator {
    private final String[] className = {"java.util.concurrent.Semaphore",
            "java.util.concurrent.CountDownLatch", "java.util.concurrent.CyclicBarrier"};

    public MXC(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public MXC(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(AllocationExpression statement) throws ParseTreeException {
        String typeName = statement.getClassType().getName();
        if (ArrayUtil.inArray(typeName, className)) {
            outputToFile(statement);
        } else {
            super.visit(statement);
        }

    }

    /**
     * 输出MXC并发变异体
     *
     * @param statement 原始方法声明
     */
    public void outputToFile(AllocationExpression statement) {
        if (comp_unit == null) {
            return;
        }
        ExpressionList arguments = statement.getArguments();
        Expression arg0 = arguments.get(0);
        java.util.List<Integer> opList = new ArrayList<>();
        if (arg0 instanceof Variable) {
            opList.add(MXC_Writer.ADD);
            opList.add(MXC_Writer.DEC);
            opList.add(MXC_Writer.ADD_AFTER);
            opList.add(MXC_Writer.DEC_AFTER);
            opList.add(MXC_Writer.ADD_BEFORE);
            opList.add(MXC_Writer.DEC_BEFORE);
        } else {
            opList.add(MXC_Writer.ADD);
            opList.add(MXC_Writer.DEC);
        }

        for (Integer op : opList) {
            num++;
            String f_name = getSourceName(this);
            String mutant_dir = getMuantID();

            try {
                PrintWriter out = getPrintWriter(f_name);
                MXC_Writer writer = new MXC_Writer(mutant_dir, out);
                writer.setMutant(statement, op);
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

}
