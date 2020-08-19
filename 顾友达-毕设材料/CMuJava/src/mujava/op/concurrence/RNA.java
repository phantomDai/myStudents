/*
 * @author: GuYouda
 * @date: 2018/04/02
 * @time: 12:03
 * @des: RNA - Replace NotifyAll() with Notfiy()
 *      The RNA operator replaces a notifyAll() with a notify() and is an example of the notify instead of
 *      notify all bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RNA extends Mutator {
    private Environment env = null;

    public RNA(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public RNA(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(MethodCall methodCall) throws ParseTreeException {

        if (isTarget(methodCall)) {
            outputToFile(methodCall);
        } else {
            super.visit(methodCall);
        }
    }

    /**
     * 输出RNA并发变异体
     *
     * @param methodCall 原始方法声明
     */
    public void outputToFile(MethodCall methodCall) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RNA_Writer writer = new RNA_Writer(mutant_dir, out);
            writer.setMutant(methodCall);
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

    /**
     * 判断是否是需要进行变异的方法调用
     *
     * @param methodCall 方法调用
     * @return true or false
     */
    private boolean isTarget(MethodCall methodCall) {
        boolean isTarget = false;
        // 获取方法名
        String methodName = methodCall.getName();
        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        ExpressionList arguments = methodCall.getArguments();

        // 判断是否notifyAll方法调用，如有则进行替换，否则忽略
        // 并且notifyAll方法的参数为空，避免对象重载notifyAll方法导致错误。
        if (referenceExpr != null && "notifyAll".equals(methodName)
                && arguments.isEmpty()) {
            isTarget = true;
        }
        return isTarget;
    }
}
