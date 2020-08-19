/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 10:44
 * @des:  SAN - Switch Atomic Call with Non-Atomic
 *      A call to the getAndSet() method in an atomic variable class is replaced by a call to the get()
 *      method and a call to the set() method. The effect of this replacement is that the combined get
 *      and set commands are no longer atomic.
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

public class SAN extends Mutator {
    private List<WhileStatement> targetWhileStatementList = new ArrayList<>();
    private List<MethodCall> targetMethodCallList = new ArrayList<>();

    private final String[] targetMethods = {"getAndSet", "getAndIncrement"};

    public SAN(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public SAN(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(MethodCall methodCall) throws ParseTreeException {
        System.out.println(methodCall.toString());
        if (isTarget(methodCall)) {
            outputToFile(methodCall);
        } else {
            super.visit(methodCall);
        }
    }

    /**
     * 输出SAN并发变异体
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
            SAN_Writer writer = new SAN_Writer(mutant_dir, out, getEnvironment());
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
        /*
        if (!"getAndSet".equals(methodCall.getName())) {
            return false;
        }
        */
        if (!ArrayUtil.inArray(methodCall.getName(), targetMethods)) {
            return false;
        }
        try {
            OJClass referenceExprType = methodCall.getReferenceExpr().getType(getEnvironment());
            String referenceExprTypeName = referenceExprType.getName();
            String packageName = referenceExprTypeName.substring(0, referenceExprTypeName.lastIndexOf("."));
            // Java 内置原子对象均位于java.util.concurrent.atomic包内
            if ("java.util.concurrent.atomic".equals(packageName)) {

                /*
                如果getAndSet方法调用出现在while的条件中
                while(a.getAndSet(100) > 0)
                */
                if (methodCall.getParent().getParent() instanceof WhileStatement
                        && methodCall.getParent() instanceof BinaryExpression) {
                    WhileStatement whileStatement = (WhileStatement) methodCall.getParent().getParent();
                    targetWhileStatementList.add(whileStatement);
                    return false;
                }

                /*
                如果getAndSet方法调用出现其他方法调用内部
                obj.call(a.getAndSet())
                */
                if (methodCall.getParent() instanceof ExpressionList
                        && methodCall.getParent().getParent() instanceof MethodCall) {
                    MethodCall mc = (MethodCall) methodCall.getParent().getParent();
                    targetMethodCallList.add(mc);
                    return false;
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void visit(ForStatement forStatement) throws ParseTreeException {
        Expression expression = forStatement.getCondition();
        VariableDeclarator[] expressionList = forStatement.getInitDecls();
        ExpressionList expressionList1 = forStatement.getIncrement();
    }
}

