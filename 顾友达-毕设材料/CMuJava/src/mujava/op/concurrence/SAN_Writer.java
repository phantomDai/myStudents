/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 10:44
 * @des:  SAN - Switch Atomic Call with Non-Atomic
 * A call to the getAndSet() method in an atomic variable class is replaced by a call to the get()
 * method and a call to the set() method. The effect of this replacement is that the combined get
 * and set commands are no longer atomic.
 */
package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.PrintWriter;


public class SAN_Writer extends ConcurrentMutantCodeWriter {
    private MethodCall mutant = null;
    private boolean isMutantTarget = false;
    private Environment environment;
    private final String atomicClassPackage = "java.util.concurrent.atomic";
    // getAndSet方法只含有一个参数的类
    private final String[] oneArgClass = {"AtomicBoolean", "AtomicInteger", "AtomicLong", "AtomicReference"};
    // getAndSet方法含有两个参数的类
    private final String[] twoArgClass = {"AtomicIntegerArray", "AtomicLongArray", "AtomicIntegerFieldUpdater",
            "AtomicLongFieldUpdater", "AtomicReferenceArray", "AtomicReferenceFieldUpdater"};

    public SAN_Writer(String file_name, PrintWriter out, Environment environment) {
        super(file_name, out);
        this.environment = environment;
    }

    /**
     * 设置变异体
     *
     * @param expression 方法
     */
    public void setMutant(MethodCall expression) {
        mutant = expression;
        isMutantTarget = true;
    }

    @Override
    public void visit(MethodCall methodCall) throws ParseTreeException {
        if (isSameObject(methodCall, mutant) && isMutantTarget) {
            ExpressionList args = methodCall.getArguments();
            String methodName = methodCall.getName();
            switch (methodName) {
                case "getAndSet":
                    if (args.size() == 1) {
                        outputOneArgMutant(methodCall);
                    } else if (args.size() == 2) {
                        outputTwoArgMutant(methodCall);
                    } else {
                        super.visit(methodCall);
                    }
                    break;
                case "getAndIncrement":
                    outputGetAndIncrement(methodCall);
                    break;

            }

        } else {
            super.visit(methodCall);
        }
    }

    /**
     * 输出只含有一个参数的方法调用变异体
     *
     * @param methodCall 方法调用
     * @throws ParseTreeException 异常
     */
    private void outputGetAndIncrement(MethodCall methodCall) throws ParseTreeException {

        MethodCall getMethodCall = (MethodCall) methodCall.makeRecursiveCopy();
        getMethodCall.setName("get");
        out.print(getMethodCall);
        out.print(";");

        out.print("\n");
        writeTab();
        StringBuffer setMethod = new StringBuffer();
        setMethod.append(methodCall.getReferenceExpr().toString() + ".set(");
        setMethod.append(methodCall.getReferenceExpr().toString() + ".intValue() + 1 )");
        System.out.println(setMethod.toString());
        out.print(setMethod.toString());
//        out.print(";");


        // -------------------------------------------------------------
        mutated_line = line_num;
        writeLog(removeNewline(methodCall.toString() + "--> " +
                getMethodCall.toString() + ";" + setMethod.toString() + ";"));
        // -------------------------------------------------------------

    }


    /**
     * 输出只含有一个参数的方法调用变异体
     *
     * @param methodCall 方法调用
     * @throws ParseTreeException 异常
     */
    private void outputOneArgMutant(MethodCall methodCall) throws ParseTreeException {

        MethodCall getMethodCall = (MethodCall) methodCall.makeRecursiveCopy();
        getMethodCall.setName("get");
        getMethodCall.setArguments(new ExpressionList());
        out.print(getMethodCall);
        out.print(";");

        out.print("\n");
        writeTab();
        MethodCall setMethodCall = (MethodCall) methodCall.makeRecursiveCopy();
        setMethodCall.setName("set");
        out.print(setMethodCall);
//        out.print(";");


        // -------------------------------------------------------------
        mutated_line = line_num;
        writeLog(removeNewline(methodCall.toString() + "--> " +
                getMethodCall.toString() + ";" + setMethodCall.toString()));
        // -------------------------------------------------------------

    }

    /**
     * 输出含有两个参数的方法调用变异体
     *
     * @param methodCall 方法调用
     * @throws ParseTreeException 异常
     */
    private void outputTwoArgMutant(MethodCall methodCall) throws ParseTreeException {
        MethodCall getMethodCall = (MethodCall) methodCall.makeRecursiveCopy();
        ExpressionList arguments = methodCall.getArguments();
        getMethodCall.setName("get");
        getMethodCall.setArguments(new ExpressionList(arguments.get(0)));
        out.print(getMethodCall);
        out.print(";");

        out.print("\n");
        writeTab();
        MethodCall setMethodCall = (MethodCall) methodCall.makeRecursiveCopy();
        setMethodCall.setName("set");
        setMethodCall.setArguments(new ExpressionList(arguments.get(0), arguments.get(1)));
        out.print(setMethodCall);
//        out.print(";");


        // -------------------------------------------------------------
        mutated_line = line_num;
        writeLog(removeNewline(methodCall.toString() + "--> " +
                getMethodCall.toString() + ";" + setMethodCall.toString()));
        // -------------------------------------------------------------
    }

}
