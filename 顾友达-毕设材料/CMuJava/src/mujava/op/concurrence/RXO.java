/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 20:37
 * @des: RXO - Replace One Concurrency Mechanism-X with Another
 *      When two instances of the same concurrency mechanism exist we replace a call to one with a c
 *      all to the other.We apply the RXO operator when 2 or more objects exist of type Semaphore,
 *      CountDownLatch, Lock, CyclicBarrier, Exchanger and Condition.
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
import java.util.List;

public class RXO extends Mutator {

    // java.util.concurrent.locks.Lock接口的已知实现类
    private final String[] lockClasses =
            {"java.util.concurrent.locks.ReentrantLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock"};
    private List<String> lockFields = new ArrayList<>();

    // java.util.concurrent.locks.Condition接口的已知实现类
    private final String[] conditionClasses =
            {"java.util.concurrent.locks.AbstractQueuedLongSynchronizer.ConditionObject",
                    "java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject"};
    private List<String> conditionFields = new ArrayList<>();

    // Semaphore类
    private final String[] semaphoreClasses = {"java.util.concurrent.Semaphore"};
    private List<String> semaphoreFields = new ArrayList<>();

    // Barrier类
    private final String[] barrierClasses = {"java.util.concurrent.CyclicBarrier"};
    private List<String> barrierFields = new ArrayList<>();

    // Latch类
    private final String[] latchClasses = {"java.util.concurrent.CountDownLatch"};
    private List<String> latchFields = new ArrayList<>();


    public RXO(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RXO(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) throws ParseTreeException {
        String typeName = fieldDeclaration.getTypeSpecifier().getName();
        String variableName = fieldDeclaration.getVariable();
        if ("java.util.concurrent.locks.Lock".equals(typeName)
                || ArrayUtil.inArray(typeName, lockClasses)) {
            lockFields.add(variableName);
        } else if ("java.util.concurrent.locks.Condition".equals(typeName)
                || ArrayUtil.inArray(typeName, conditionClasses)) {
            conditionFields.add(variableName);
        } else if (ArrayUtil.inArray(typeName, barrierClasses)) {
            barrierFields.add(variableName);
        } else if (ArrayUtil.inArray(typeName, semaphoreClasses)) {
            semaphoreFields.add(variableName);
        } else if (ArrayUtil.inArray(typeName, latchClasses)) {
            latchFields.add(variableName);
        }
    }

    @Override
    public void visit(MethodCall methodCall) throws ParseTreeException {

        try {
            MethodCall mutant = genMutant(methodCall);
            if (mutant != null) {
                outputToFile(methodCall, mutant);
            } else {
                super.visit(methodCall);
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.visit(methodCall);
        }
    }

    /**
     * 输出RXO并发变异体
     *
     * @param methodCall 原始方法声明
     */
    public void outputToFile(MethodCall methodCall, MethodCall mutant) {
        if (comp_unit == null) {
            return;
        }

        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RXO_Writer writer = new RXO_Writer(mutant_dir, out);
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
    private MethodCall genMutant(MethodCall methodCall) throws Exception {
        Expression expression = methodCall.getReferenceExpr();
        if (expression == null) {
            return null;
        }
        OJClass exprType = expression.getType(getEnvironment());
        String exprClassName = exprType.getName();
        OJClass[] interfaces = exprType.getInterfaces();

        MethodCall temp = (MethodCall) methodCall.makeRecursiveCopy();
        Variable variable = null;

        if ("java.util.concurrent.Semaphore".equals(exprClassName) && semaphoreFields.size() > 1) {
            for (String field : semaphoreFields) {
                if (!field.equals(expression.toString())) {
                    variable = new Variable(field);
                    break;
                }
            }
            temp.setReferenceExpr(variable);
            return temp;
        }
        if ("java.util.concurrent.CountDownLatch".equals(exprClassName) && latchFields.size() > 1) {
            for (String field : latchFields) {
                if (!field.equals(expression.toString())) {
                    variable = new Variable(field);
                    break;
                }
            }
            temp.setReferenceExpr(variable);
            return temp;
        }
        if ("java.util.concurrent.CyclicBarrier".equals(exprClassName) && barrierFields.size() > 1) {
            for (String field : barrierFields) {
                if (!field.equals(expression.toString())) {
                    variable = new Variable(field);
                    break;
                }
            }
            temp.setReferenceExpr(variable);
            return temp;
        }

        if ("java.util.concurrent.locks.Lock".equals(exprClassName) && lockFields.size() > 1) {
            for (String field : lockFields) {
                if (!field.equals(expression.toString())) {
                    variable = new Variable(field);
                    break;
                }
            }
            temp.setReferenceExpr(variable);
            return temp;
        }

        if ("java.util.concurrent.locks.Condition".equals(exprClassName) && conditionFields.size() > 1) {
            for (String field : conditionFields) {
                if (!field.equals(expression.toString())) {
                    variable = new Variable(field);
                    break;
                }
            }
            temp.setReferenceExpr(variable);
            return temp;
        }

        for (OJClass ojClass : interfaces) {
            if ("java.util.concurrent.locks.Lock".equals(ojClass.getName())
                    && lockFields.size() > 1) {
                for (String field : lockFields) {
                    if (!field.equals(expression.toString())) {
                        variable = new Variable(field);
                        break;
                    }
                }
                temp.setReferenceExpr(variable);
                return temp;
            } else if ("java.util.concurrent.locks.Condition".equals(ojClass.getName())
                    && conditionFields.size() > 1) {
                for (String field : conditionFields) {
                    if (!field.equals(expression.toString())) {
                        variable = new Variable(field);
                        break;
                    }
                }
                temp.setReferenceExpr(variable);
                return temp;
            }
        }
        return null;
    }
}
