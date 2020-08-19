/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 8:46
 * @des: ELPA - Exchange Lock/Permit Acquistion
 *      In a Semaphore the acquire(), acquireUninterruptibly() and tryAcquire() methods
 *      can be used to obtain one or more permits to access a shared resource. The ELPA
 *      operator exchanges one method for another whichcan lead to potential timing
 *      changes as well as starvation. For example, an acquire() method will try and
 *      obtain one or more permits and will block and wait until the permit or permits
 *      become available. If thethread that invoked the acquire() method is interrupted
 *      it will no longer continue to block and wait. If the acquire() method invocation
 *      is changed to acquireUninterruptibly() it will behave exactly the same except it
 *      can no longer be interupted. Thus in situations where the semaphore is unfair or
 *      if for other reasons the number of requested permits never becomes available the
 *      thread that invoked the acquireUninterruptibly() will stay dormant and wait.
 *      If an acquire() method invocation is changed to a tryAcquire() then a permit will
 *      be acquired if one is available otherwise the thread will not block and wait.
 *      tryAcquire() will acquire a permit or permits unfairly even if the fairness setting
 *      is set to fair. Use of tryAcquire() may cause starvation for threads waiting for permits.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ELPA extends Mutator {
    private final String[] semaphoreMethods = {"acquire", "tryAcquire", "acquireUninterruptibly"};
    private final String[] lockMethods = {"lock", "tryLock", "lockInterruptibly"};
    private ArrayList<String> unusedMethodName = new ArrayList<>();

    public ELPA(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public ELPA(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
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
     * 输出ELPA并发变异体
     *
     * @param methodCall 原始方法声明
     */
    public void outputToFile(MethodCall methodCall) {
        if (comp_unit == null) {
            return;
        }

        updateUnusedMethodName(methodCall);

        for (String methodName : unusedMethodName) {
            num++;
            String f_name = getSourceName(this);
            String mutant_dir = getMuantID();
            try {
                PrintWriter out = getPrintWriter(f_name);
                ELPA_Writer writer = new ELPA_Writer(mutant_dir, out);
                writer.setMutant(methodCall);
                writer.setMutantMechodCallName(methodName);
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
    private boolean isTarget(MethodCall methodCall) {
        final String[] types = {"byte", "short", "int"};
        // 获取方法名
        String methodName = methodCall.getName();
        if (!ArrayUtil.inArray(methodName, semaphoreMethods) && !ArrayUtil.inArray(methodName, lockMethods)) {
            return false;
        }

        // 获取方法调用对象
        Expression referenceExpr = methodCall.getReferenceExpr();
        if (referenceExpr == null) {
            return false;
        }
        try {
            OJClass referenceExprClass = referenceExpr.getType(getEnvironment());
            if (referenceExprClass == null) {
                return false;
            }
            OJClass[] interfaces = referenceExprClass.getInterfaces();
            ExpressionList arguments = methodCall.getArguments();
            /*
            Semaphore:
            acquire(), acquire(int)
            tryAcquire(),tryAcquire(int)
            acquireUninterruptibly(),acquireUninterruptibly(int)
           */
            if ("java.util.concurrent.Semaphore".equals(referenceExprClass.getName())
                    && ArrayUtil.inArray(methodName, semaphoreMethods)) {
                if (arguments.isEmpty()) {
                    return true;
                } else if (arguments.size() == 1) {
                    Expression arg = arguments.get(0);
                    return ArrayUtil.inArray(arg.getType(getEnvironment()).getName(), types);
//                    return "int".equals(arg.getType(getEnvironment()).getName());
                }
            }

            /*
           Lock:
           lock(),tryLock(),lockInterruptibly()
           */
            if (ArrayUtil.inArray(methodName, lockMethods) && arguments.isEmpty()) {
                if ("java.util.concurrent.locks.Lock".equals(referenceExprClass.getName())) {
                    return true;
                }
                for (OJClass ojClass : interfaces) {
                    if ("java.util.concurrent.locks.Lock".equals(ojClass.getName())) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 更新变异体方法名集合
     *
     * @param methodCall 方法调用
     */
    private void updateUnusedMethodName(MethodCall methodCall) {
        if (!unusedMethodName.isEmpty()) {
            unusedMethodName.clear();
        }
        String name = methodCall.getName();
        if (ArrayUtil.inArray(name, semaphoreMethods)) {
            for (String method : semaphoreMethods) {
                if (!name.equals(method)) {
                    unusedMethodName.add(method);
                }
            }
        } else if (ArrayUtil.inArray(name, lockMethods)) {
            for (String method : lockMethods) {
                if (!name.equals(method)) {
                    unusedMethodName.add(method);
                }
            }
        }
    }
}
