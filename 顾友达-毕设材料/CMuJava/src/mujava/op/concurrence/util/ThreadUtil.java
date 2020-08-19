/*
 * @author: GuYouda
 * @date: 2018/4/6
 * @time: 11:08
 * @des: Java多线程相关工具类
 */

package mujava.op.concurrence.util;

import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.MethodCall;
import openjava.ptree.Variable;

public class ThreadUtil {

    /**
     * 判断join,sleep,yield 方法调用是否为多线程方法调用
     * 避免一些用户自定义或重载的同名方法调用
     *
     * @param methodCall 方法调用
     * @return true or false
     */
    public static boolean isThreadMethodCall(MethodCall methodCall, Environment environment) throws Exception {
        String[] threadMethodList = {"join", "sleep", "yield"};
        // 获取方法名
        String methodName = methodCall.getName();
        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        if (referenceExpr == null || !(referenceExpr instanceof Variable)) {
            return false;
        }

        Variable variable = (Variable) referenceExpr;
        OJClass variableType = variable.getType(environment);
        OJClass superClass = variableType.getSuperclass();
        ExpressionList arguments = methodCall.getArguments();


        if (!ArrayUtil.inArray(methodName, threadMethodList)) {
            return false;
        }
        // 判断方法调用对象是否为线程类型或者其子类
        if (!("java.lang.Thread".equals(variableType.getName())
                || "java.lang.Thread".equals(superClass.getName()))) {
            return false;
        }

        // join();yield()
        if (("join".equals(methodName) || "yield".equals(methodName))
                && arguments.isEmpty()) {
            return true;
        }

        // 如果只有一个参数且为int或long类型
        // join(long),sleep(long)
        if (("join".equals(methodName) || "sleep".equals(methodName))
                && arguments.size() == 1) {
            Expression expression = arguments.get(0);
            OJClass argument = expression.getType(environment);
            String argumentName = argument.getName();
            if ("int".equals(argumentName) || "long".equals(argumentName)) {
                return true;
            }
        }


        // join(long,int),sleep(long,int)
        // 如果有两个参数
        if (("join".equals(methodName) || "sleep".equals(methodName))
                && arguments.size() == 2) {
            // 获取第一个参数
            Expression expression0 = arguments.get(0);
            OJClass argument0 = expression0.getType(environment);
            String argumentName0 = argument0.getName();

            // 获取第二个参数
            Expression expression1 = arguments.get(1);
            OJClass argument1 = expression1.getType(environment);
            String argumentName1 = argument1.getName();

            // 第一个参数为int或long,第二个参数为int
            if (("int".equals(argumentName0) || "long".equals(argumentName0))
                    && "int".equals(argumentName1)) {
                return true;
            }
        }

        return false;
    }


}
