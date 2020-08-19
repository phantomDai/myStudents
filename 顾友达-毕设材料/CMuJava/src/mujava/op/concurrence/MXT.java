/*
 * @author: GuYouda
 * @date: 2018/4/8
 * @time: 9:54
  * @des: Modify Method-X Timeout
 *      The MXT operator can be applied to the wait(), sleep(), and join() method calls
 *      that include an optional timeout parameter. For example, in Java a call to wait()
 *      with the optional timeout parameter will cause a thread to no longer be runnable
 *      until a condition is satisfied or a timeout has occurred. The MXT replaces the
 *      timeout parameter, t, of the wait() method by some appropriately chosen fraction
 *      or multiple of t (e.g., t/2 and t * 2). We could replace the timeout parameter
 *      by a variable of an equivalent type however since we know that the parameter
 *      represents a time value it is just as meaningful to mutate the method to both
 *      increase and decrease the time by a factor of 2.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class MXT extends Mutator {
    // 可以去除的方法
    private String[] methodList = {"wait", "join", "sleep"};
    private String[] types = {"byte", "short", "int", "long"};
    private String[] types1 = {"byte", "short", "int"};

    public MXT(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public MXT(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
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
     * 输出MXT并发变异体
     *
     * @param methodCall 原始方法声明
     */
    public void outputToFile(MethodCall methodCall) {
        if (comp_unit == null) {
            return;
        }
        String[] modifier = {"*", "/"};
        for (String str : modifier) {
            num++;
            String f_name = getSourceName(this);
            String mutant_dir = getMuantID();

            try {
                PrintWriter out = getPrintWriter(f_name);
                MXT_Writer writer = new MXT_Writer(mutant_dir, out);
                MethodCall mutant = (MethodCall) methodCall.makeRecursiveCopy();
                writer.setMutant(methodCall, mutant, str);
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

    /**
     * 判断是否是需要进行变异的方法调用
     *
     * @param methodCall 方法调用
     * @return true or false
     */
    private boolean isTarget(MethodCall methodCall) throws Exception {
        // 获取方法名
        String methodName = methodCall.getName();
        /*
        判断是否是需要处理的几个方法调用，如果不是直接返回
        */
        if (!ArrayUtil.inArray(methodName, methodList)) {
            return false;
        }

        switch (methodName) {
            case "wait":
                return handleWait(methodCall);
            case "join":
                return handleJoin(methodCall);
            case "sleep":
                return handleSleep(methodCall);
            default:
                return false;
        }
    }

    private boolean handleWait(MethodCall methodCall) throws Exception {
        // 获取方法名
        String methodName = methodCall.getName();
        if (!"wait".equals(methodName)) {
            return false;
        }
        // 获取方法参数
        ExpressionList arguments = methodCall.getArguments();
        try {
            // 如果只有一个参数且为byte,short,int或long类型
            if (arguments.size() == 1) {
                Expression expression = arguments.get(0);
                OJClass argument = expression.getType(getEnvironment());
                String argumentTypeName = argument.getName();
                if (ArrayUtil.inArray(argumentTypeName, types)) {
                    return true;
                }
            }
            // 如果有两个参数
            else if (arguments.size() == 2) {
                // 获取第一个参数
                Expression expression0 = arguments.get(0);
                OJClass argument0 = expression0.getType(getEnvironment());
                String argumentTypeName0 = argument0.getName();

                // 获取第二个参数
                Expression expression1 = arguments.get(1);
                OJClass argument1 = expression1.getType(getEnvironment());
                String argumentTypeName1 = argument1.getName();

                // 第一个参数为long,第二个参数为int
                if (ArrayUtil.inArray(argumentTypeName0, types)
                        && ArrayUtil.inArray(argumentTypeName1, types1)) {
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

    private boolean handleJoin(MethodCall methodCall) throws Exception {
        // 获取方法名
        String methodName = methodCall.getName();
        if (!"join".equals(methodName)) {
            return false;
        }
        // 获取方法参数
        ExpressionList arguments = methodCall.getArguments();
        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        // 判断参数是否正确
        if (arguments.isEmpty() || arguments.size() > 2) {
            return false;
        }
        if (referenceExpr == null || !(referenceExpr instanceof Variable)) {
            return false;
        }
        Variable variable = (Variable) referenceExpr;
        OJClass variableType = variable.getType(getEnvironment());
        OJClass superClass = variableType.getSuperclass();
        // 判断方法调用对象是否为线程类型或者其子类
        if (!("java.lang.Thread".equals(variableType.getName())
                || "java.lang.Thread".equals(superClass.getName()))) {
            return false;
        }

        try {
            if (arguments.size() == 1) {
                Expression expression = arguments.get(0);
                OJClass argument = expression.getType(getEnvironment());
                String argumentTypeName = argument.getName();
                if (ArrayUtil.inArray(argumentTypeName, types)) {
                    System.out.println("wait(long)-->" + methodCall);
                    return true;
                }
            } else if (arguments.size() == 2) {
                // 获取第一个参数
                Expression expression0 = arguments.get(0);
                OJClass argument0 = expression0.getType(getEnvironment());
                String argumentTypeName0 = argument0.getName();

                // 获取第二个参数
                Expression expression1 = arguments.get(1);
                OJClass argument1 = expression1.getType(getEnvironment());
                String argumentTypeName1 = argument1.getName();

                // 第一个参数为long,第二个参数为int
                if (ArrayUtil.inArray(argumentTypeName0, types)
                        && ArrayUtil.inArray(argumentTypeName1, types1)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean handleSleep(MethodCall methodCall) throws Exception {
        // 获取方法名
        String methodName = methodCall.getName();
        if (!"sleep".equals(methodName)) {
            return false;
        }
        // 获取方法参数
        ExpressionList arguments = methodCall.getArguments();
        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        TypeName typeName = methodCall.getReferenceType();

        // 判断参数是否正确
        if (arguments.isEmpty() || arguments.size() > 2) {
            return false;
        }

        if (referenceExpr == null) {
            if (typeName != null) {
                if (!"Thread".equals(typeName.toString())) {
                    return false;
                }
            }
        } else {
            OJClass referenceExprType = referenceExpr.getType(getEnvironment());
            OJClass superClass = referenceExprType.getSuperclass();
            // 判断方法调用对象是否为线程类型或者其子类
            if (!("java.lang.Thread".equals(referenceExprType.getName())
                    || "java.lang.Thread".equals(superClass.getName()))) {
                return false;
            }
        }
        try {
            if (arguments.size() == 1) {
                Expression expression = arguments.get(0);
                OJClass argument = expression.getType(getEnvironment());
                String argumentTypeName = argument.getName();
                if (ArrayUtil.inArray(argumentTypeName, types)) {
                    System.out.println("wait(long)-->" + methodCall);
                    return true;
                }
            } else if (arguments.size() == 2) {
                // 获取第一个参数
                Expression expression0 = arguments.get(0);
                OJClass argument0 = expression0.getType(getEnvironment());
                String argumentTypeName0 = argument0.getName();

                // 获取第二个参数
                Expression expression1 = arguments.get(1);
                OJClass argument1 = expression1.getType(getEnvironment());
                String argumentTypeName1 = argument1.getName();

                // 第一个参数为long,第二个参数为int
                if (ArrayUtil.inArray(argumentTypeName0, types)
                        && ArrayUtil.inArray(argumentTypeName1, types1)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}