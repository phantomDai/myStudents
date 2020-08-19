/*
 * @author: GuYouda
 * @date: 2018/4/14
 * @time: 9:24
 * @des: RCXC - Remove Concurrency Mechanism Method-X Call
 *      The RCXC operator can be applied to the following concurrency mechanisms: Locks (lock(), unlock()),
 *      Condition (signal(), signalAll()), Semaphore (acquire(), release()), Latch(countDown()),
 *      and ExecutorService (e.g., submit()).
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RCXC extends Mutator {

    // 所有需要考虑的方法
    private final String[] methods = {"lock", "unlock", "signal", "signalAll",
            "acquire", "release", "countDown", "submit"};

    // java.util.concurrent.locks.Lock接口的已知实现类
    // lock(), unlock()
    private final String[] lockClasses =
            {"java.util.concurrent.locks.ReentrantLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock"};
    private final String[] removedLockMethods = {"lock", "unlock"};

    // java.util.concurrent.locks.Condition接口的已知实现类
    // signal(), signalAll()
    private final String[] conditionClasses =
            {"java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject",
                    "java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject"};
    private final String[] removedConditionMethods = {"signal", "signalAll"};

    // Semaphore类
    // acquire(), release()
    private final String[] semaphoreClasses = {"java.util.concurrent.Semaphore"};
    private final String[] removedSemaphoreMethods = {"acquire", "release"};

    // Latch类
    // countDown()
    private final String[] latchClasses = {"java.util.concurrent.CountDownLatch"};
    private final String[] removedLatchMethods = {"countDown"};

    // java.util.concurrent.ExecutorService 接口已知的实现类
    private final String[] executorServiceClasses =
            {"java.util.concurrent.AbstractExecutorService",
                    "java.util.concurrent.ScheduledThreadPoolExecutor",
                    "java.util.concurrent.ThreadPoolExecutor"};
    private final String[] removedExecutorServiceMethods = {"awaitTermination", "invokeAll", "invokeAny",
            "isShutdown", "isTerminated", "shutdown", "shutdownNow", "submit"};


    public RCXC(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public RCXC(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
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

    @Override
    public void visit(AssignmentExpression assignmentExpression) throws ParseTreeException {
        Expression expression = assignmentExpression.getRight();
        if (expression instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) expression;
            if (!ArrayUtil.inArray(methodCall.getName(), removedExecutorServiceMethods)) {
                super.visit(assignmentExpression);
            } else {
                try {
                    OJClass refClass = methodCall.getReferenceExpr().getType(getEnvironment());
                    if ("java.util.concurrent.ExecutorService".equals(refClass.getName()) ||
                            ArrayUtil.inArray(refClass.getName(), executorServiceClasses)) {
                        outputToFile(assignmentExpression);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.visit(assignmentExpression);
        }
    }

    /**
     * 输出RCXC并发变异体
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
            RCXC_Writer writer = new RCXC_Writer(mutant_dir, out);
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
     * 输出RCXC并发变异体
     *
     * @param assignmentExpression 变量赋值
     */
    public void outputToFile(AssignmentExpression assignmentExpression) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RCXC_Writer writer = new RCXC_Writer(mutant_dir, out);
            writer.setAssignmentExpression(assignmentExpression);
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
        // 如果不是需要处理的方法调用，直接返回
        if (!ArrayUtil.inArray(methodName, methods)) {
            return false;
        }
        // 获取调用该方法的表达式
        Expression expression = methodCall.getReferenceExpr();
        if (expression == null) {
            return false;
        }
        // 获取表达式的类
        OJClass exprType = expression.getType(getEnvironment());
        // 获取类名
        String exprClassName = exprType.getName();
        // 获取已实现的接口
        OJClass[] interfaces = exprType.getInterfaces();

        if ("java.util.concurrent.Semaphore".equals(exprClassName)) {
            return isSemaphore(methodCall);
        }
        if ("java.util.concurrent.CountDownLatch".equals(exprClassName)) {
            return isLatch(methodCall);
        }
        if ("java.util.concurrent.locks.Lock".equals(exprClassName)) {
            return isLock(methodCall);
        }
        if ("java.util.concurrent.locks.Condition".equals(exprClassName)) {
            return isCondition(methodCall);
        }
        if ("java.util.concurrent.ExecutorService".equals(exprClassName)) {
            return isExecutorService(methodCall);
        }

        for (OJClass ojClass : interfaces) {
            if ("java.util.concurrent.locks.Lock".equals(ojClass.getName())) {
                return isLock(methodCall);
            } else if ("java.util.concurrent.locks.Condition".equals(ojClass.getName())) {
                return isCondition(methodCall);
            } else if ("java.util.concurrent.ExecutorService".equals(ojClass.getName())) {
                return isExecutorService(methodCall);
            }
        }
        return false;
    }

    /**
     * 判断是否为目标方法调用
     * @param methodCall 方法调用
     * @return boolean
     */
    private boolean isLock(MethodCall methodCall) {
        String methodName = methodCall.getName();
        ExpressionList arguments = methodCall.getArguments();
        return ArrayUtil.inArray(methodName, removedLockMethods) && arguments.isEmpty();
    }

    /**
     * 判断是否为目标方法调用
     *
     * @param methodCall 方法调用
     * @return boolean
     */
    private boolean isSemaphore(MethodCall methodCall) {
        String methodName = methodCall.getName();
        ExpressionList arguments = methodCall.getArguments();
        return ArrayUtil.inArray(methodName, removedSemaphoreMethods) && arguments.isEmpty();
    }

    /**
     * 判断是否为目标方法调用
     *
     * @param methodCall 方法调用
     * @return boolean
     */
    private boolean isCondition(MethodCall methodCall) {
        String methodName = methodCall.getName();
        ExpressionList arguments = methodCall.getArguments();
        return ArrayUtil.inArray(methodName, removedConditionMethods) && arguments.isEmpty();
    }

    /**
     * 判断是否为目标方法调用
     *
     * @param methodCall 方法调用
     * @return boolean
     */
    private boolean isLatch(MethodCall methodCall) {
        String methodName = methodCall.getName();
        ExpressionList arguments = methodCall.getArguments();
        return ArrayUtil.inArray(methodName, removedLatchMethods) && arguments.isEmpty();
    }

    /**
     * java.util.concurrent.ExecutorService 接口需要移除的方法：
     * 该方法只对返回值为空的方法调用进行判断
     * 其他返回值的判断逻辑在visit(AssignmentExpression assignmentExpression)
     *
     * @param methodCall 方法调用
     * @return boolean
     */
    private boolean isExecutorService(MethodCall methodCall) throws Exception {
        String methodName = methodCall.getName();
        if (!ArrayUtil.inArray(methodName, removedExecutorServiceMethods)) {
            return false;
        }
        String returnType = methodCall.getType(getEnvironment()).getName();
        return "void".equals(returnType);
    }

}