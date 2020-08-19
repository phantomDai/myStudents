/*
 * @author: GuYouda
 * @date: 2018/5/3
 * @time: 8:58
 * @des: EELO - Exchange Explicit Lock Object
 *      We have already seen the exchanging of two implicit lock objects in a synchronized block
 *      and the potential for deadlock. The EELO operator is identical only it  exchanges two explicit
 *      lock object instances.
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

public class EELO extends Mutator {
    private List<MethodCall> targetMethodCall = new ArrayList<>();
    // java.util.concurrent.locks.Lock接口的已知实现类
    private final String[] lockClasses =
            {"java.util.concurrent.locks.ReentrantLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock",
                    "java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock"};

    public EELO(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        init(comp_unit);
        genMutants(comp_unit);
    }

    public EELO(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        init(comp_unit);
        genMutants(comp_unit);
    }

    /**
     * 初始化
     * 检查是否存在可变异的内容
     *
     * @param compilationUnit CompilationUnit
     */
    private void init(CompilationUnit compilationUnit) {
        try {
            EELO_Checker checker = new EELO_Checker(getEnvironment(), compilationUnit);
            compilationUnit.accept(checker);
        } catch (ParseTreeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成EELO变异体
     *
     * @param compilationUnit compilationUnit
     */
    private void genMutants(CompilationUnit compilationUnit) {
        if (targetMethodCall.size() < 2) {
            return;
        }
        /*
        for (MethodCall mc : targetMethodCall) {
            for (MethodCall mc1 : targetMethodCall) {
                String exprStr1 = mc.getReferenceExpr().toString();
                String exprStr2 = mc1.getReferenceExpr().toString();
                if (!exprStr1.equals(exprStr2)) {
                    outputToFile(mc, mc1, compilationUnit);
                }
            }
        }
        */

        for (int i=0;i<=targetMethodCall.size()-2;i++) {
            for (int j=i+1;j<=targetMethodCall.size()-1;j++) {
                MethodCall mc = targetMethodCall.get(i);
                MethodCall mc1 = targetMethodCall.get(j);
                String exprStr1 = mc.getReferenceExpr().toString();
                String exprStr2 = mc1.getReferenceExpr().toString();
                if (!exprStr1.equals(exprStr2)) {
                    outputToFile(mc, mc1, compilationUnit);
                }
            }
        }
    }

    /**
     * 输出EELO并发变异体
     *
     * @param methodCall1     方法调用1
     * @param methodCall2     方法调用2
     * @param compilationUnit compilationUnit
     */
    public void outputToFile(MethodCall methodCall1, MethodCall methodCall2, CompilationUnit compilationUnit) {
        if (compilationUnit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            EELO_Writer writer = new EELO_Writer(mutant_dir, out);
            writer.setMutant(methodCall1, methodCall2);
            compilationUnit.accept(writer);
            writer.writeMutationLog();
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
     * 寻找出可变异的地方
     */
    class EELO_Checker extends Mutator {

        EELO_Checker(Environment env, CompilationUnit comp_unit) {
            super(env, comp_unit);
        }

        @Override
        public void visit(MethodCall methodCall) throws ParseTreeException {
            Expression expression = methodCall.getReferenceExpr();
            if (expression == null) {
                return;
            }

            OJClass exprType = null;
            try {
                exprType = expression.getType(getEnvironment());
            } catch (Exception e) {
                //e.printStackTrace();
            }
            String exprClassName = exprType != null ? exprType.getName() : "";

            if ("java.util.concurrent.locks.Lock".equals(exprClassName)
                    || ArrayUtil.inArray(exprClassName, lockClasses)) {
                targetMethodCall.add(methodCall);
            }
        }

    }
}

