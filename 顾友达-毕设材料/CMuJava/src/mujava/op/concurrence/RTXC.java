/*
 * @author: GuYouda
 * @date: 2018/04/03
 * @time: 09:18
 * @des: RTXC - Remove Thread Method-X Call
 *      The RTXC operator removes calls to the following methods: wait(), join(),sleep(), yield(), notify(),
 *      and notifyAll(). Removing the wait() method can cause potential interference, removing the join()
 *      and sleep() methods can cause the sleep() bug pattern, and removing the notify() and notifyAll()
 *      method calls is an example of losing a notify bug.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.concurrence.util.ThreadUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RTXC extends Mutator {
    // 可以去除的方法
    private String[] threadMethodList = {"join", "sleep", "yield"};
    private String[] objectMethodList = {"wait", "notify", "notifyAll"};

    private final String[] type1 = {"byte", "short", "int"};
    private final String[] type2 = {"byte", "short", "int", "long"};

    public RTXC(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RTXC(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(MethodCall methodCall) throws ParseTreeException {

        try {
            if (isTarget(methodCall)) {
                outputToFile(methodCall);
            } else {
                super.visit(methodCall);
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.visit(methodCall);
        }
    }

    /**
     * 输出RTXC变异体
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
            RTXC_Writer writer = new RTXC_Writer(mutant_dir, out);
            MethodCall mutant = (MethodCall) methodCall.makeRecursiveCopy();
            writer.setMutant(methodCall, mutant);
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
    private boolean isTarget(MethodCall methodCall) throws Exception {
        // 获取方法名
        String methodName = methodCall.getName();
        // 获取方法参数
        ExpressionList arguments = methodCall.getArguments();

        /*
        判断是否是需要处理的几个方法调用，如果不是直接返回
        由于绝大部分方法调用都不是目标方法调用，在此加这一层判断主要为了提升效率
        避免后续多次判断
        */
        if (!ArrayUtil.inArray(methodName, objectMethodList)
                && !ArrayUtil.inArray(methodName, threadMethodList)) {
            return false;
        }

        // 判断是否为线程方法调用
        if (ThreadUtil.isThreadMethodCall(methodCall, getEnvironment())) {
            return true;
        }

        // notify(),notifyAll(),wait()
        if (ArrayUtil.inArray(methodName, objectMethodList) && arguments.isEmpty()) {
            return true;
        }
        // wait(long);wait(long,int)
        if ("wait".equals(methodName)) {
            try {
                // 如果只有一个参数且为long类型
                if (arguments.size() == 1) {
                    Expression expression = arguments.get(0);
                    OJClass argument = expression.getType(getEnvironment());
                    String argumentName = argument.getName();
                    if (ArrayUtil.inArray(argumentName, type2)) {
                        return true;
                    }
                }
                // 如果有两个参数
                else if (arguments.size() == 2) {
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
                } else {
                    System.out.println(" not target method");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        return false;
    }
}
