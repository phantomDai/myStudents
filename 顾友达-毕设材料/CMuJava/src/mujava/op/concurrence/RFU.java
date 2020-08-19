/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:53
 * @des: RFU - Remove Finally Around Unlock
 *      The finally keyword is important in releasing explicit locks.
 *      The finally keyword ensures that the unlock() method call will occur after a try block regardless
 *      of whether or not an exception is thrown. If finally is removed the unlock() will not occur in the
 *      presence of an exception and cause a blocking critical section bug.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RFU extends Mutator {
    private ClassDeclaration cles;
    private CompilationUnit comp_unit;

    public RFU(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.comp_unit = comp_unit;
    }

    public RFU(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.cles = cles;
        this.comp_unit = comp_unit;
    }

    @Override
    public void visit(TryStatement statement) throws ParseTreeException {
        if (isTarget(statement)) {
            outputToFile(statement);
        } else {
            super.visit(statement);
        }
    }

    private boolean isTarget(TryStatement tryStatement) throws ParseTreeException {
        RFU_Checker rfu_checker = new RFU_Checker(getEnvironment(), cles, comp_unit);
        tryStatement.accept(rfu_checker);
        return rfu_checker.isTarget();
    }

    /**
     * Output RFU mutants to file
     *
     * @param tryStatement finally代码块
     */
    public void outputToFile(TryStatement tryStatement) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RFU_Writer writer = new RFU_Writer(mutant_dir, out);
            writer.setMutant(tryStatement);
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

    class RFU_Checker extends Mutator {
        private boolean hasFinallyBlock = false;
        private boolean hasUnlockCall = false;
        private boolean flag = false;
        private Environment environment;

        public boolean isTarget() {
            return hasFinallyBlock && hasUnlockCall;
        }

        public RFU_Checker(Environment env, CompilationUnit comp_unit) {
            super(env, comp_unit);
            environment = env;
        }

        public RFU_Checker(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
            super(env, comp_unit);
            environment = env;
        }

        @Override
        public void visit(TryStatement tryStatement) throws ParseTreeException {
            hasFinallyBlock = false;
            hasUnlockCall = false;
            StatementList finallyBody = tryStatement.getFinallyBody();
            hasFinallyBlock = !finallyBody.isEmpty();
            flag = true;
            super.visit(tryStatement);
            flag = false;
        }

        @Override
        public void visit(MethodCall methodCall) throws ParseTreeException {
            if (flag) {
                // 获取方法名
                String methodName = methodCall.getName();
                if (!"unlock".equals(methodName)) {
                    return;
                }

                // 获取方法调用对象
                Expression referenceExpr = methodCall.getReferenceExpr();
                try {
                    OJClass referenceExprClass = referenceExpr.getType(environment);
                    OJClass[] interfaces = referenceExprClass.getInterfaces();
                    ExpressionList arguments = methodCall.getArguments();

                    if (arguments.isEmpty()) {
                        if ("java.util.concurrent.locks.Lock".equals(referenceExprClass.getName())) {
                            hasUnlockCall = true;
                            return;
                        }
                        for (OJClass ojClass : interfaces) {
                            if ("java.util.concurrent.locks.Lock".equals(ojClass.getName())) {
                                hasUnlockCall = true;
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                super.visit(methodCall);
            }
        }

    }
}
