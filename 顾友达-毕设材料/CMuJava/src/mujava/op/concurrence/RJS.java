/*
 * @author: GuYouda
 * @date: 2018/04/01
 * @time: 10:03
 * @des: RJS - Replace Join() with Sleep()
 *      The RJS operator replaces a join() with a sleep() and is an example of the sleep() bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RJS extends Mutator {
    private final String[] type1 = {"byte", "short", "int"};
    private final String[] type2 = {"byte", "short", "int", "long"};

    public RJS(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RJS(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
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
     * 输出RJS并发变异体
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
            RJS_Writer writer = new RJS_Writer(mutant_dir, out);
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
        // 获取方法名
        String methodMane = methodCall.getName();
        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        // 获取方法参数
        ExpressionList arguments = methodCall.getArguments();

        // 判断是否join方法调用，如有则进行替换，否则忽略
        // join(),join(long),join(long,int)
        if (referenceExpr != null && "join".equals(methodMane)) {
            try {
                // 获取调用join方法的对象
//                Variable variable = (Variable) referenceExpr;
                // 获取变量的类型
                OJClass variableType = referenceExpr.getType(getEnvironment());
                OJClass superClass = variableType.getSuperclass();

                // 变量必须是Thread类型或者继承自Thread类
                if (!"java.lang.Thread".equals(variableType.getName())
                        && !"java.lang.Thread".equals(superClass.getName())) {
                    return false;
                }
                // join()
                if (methodCall.getArguments().size() == 0) {
                    return true;
                }

                // join(long)
                else if (methodCall.getArguments().size() == 1) {
                    Expression expression = arguments.get(0);
                    OJClass argument = expression.getType(getEnvironment());
                    String argumentName = argument.getName();
                    if (ArrayUtil.inArray(argumentName, type2)) {
                        return true;
                    }
                }
                // join(long,int)
                else if ((methodCall.getArguments().size() == 2)) {
                    // 获取第一个参数
                    Expression expression0 = arguments.get(0);
                    OJClass argument0 = expression0.getType(getEnvironment());
                    String argumentName0 = argument0.getName();

                    // 获取第二个参数
                    Expression expression1 = arguments.get(1);
                    OJClass argument1 = expression1.getType(getEnvironment());
                    String argumentName1 = argument1.getName();

                    // 第一个参数为long,第二个参数为int
                    if ((ArrayUtil.inArray(argumentName0, type2))
                            && ArrayUtil.inArray(argumentName1, type1)) {
                        return true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
