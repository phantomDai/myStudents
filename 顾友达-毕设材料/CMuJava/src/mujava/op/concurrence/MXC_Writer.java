/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:48
 * @des: Modify Concurrency Mechanism-X Count
 *      Semaphore, CountDownLatch, CyclicBarrier
 *      需要修改的构造方法:
 *      Semaphore: Semaphore(int); Semaphore(int,boolean);
 *      CountDownLatch: CountDownLatch(int)
 *      CyclicBarrier: CyclicBarrier(int); CyclicBarrier(int,Runnable)
 *
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;

public class MXC_Writer extends ConcurrentMutantCodeWriter {

    private AllocationExpression original = null;
    static final int ADD = 0; // i+1
    static final int DEC = 1; //i-1
    static final int ADD_AFTER = 2; // i++
    static final int DEC_AFTER = 3; // i--
    static final int ADD_BEFORE = 4; // ++i
    static final int DEC_BEFORE = 5; // --i

    private int mutant = 0;

    public MXC_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异规则
     */
    public void setMutant(AllocationExpression original, int mutant) {
        this.original = original;
        this.mutant = mutant;
    }

    public void visit(AllocationExpression expression) throws ParseTreeException {
        if (!(isSameObject(expression, original))) {
            super.visit(expression);
        } else {

            Expression encloser = expression.getEncloser();
            if (encloser != null) {
                encloser.accept(this);
                out.print(" . ");
            }

            out.print("new ");

            TypeName classType = expression.getClassType();
            classType.accept(this);

            out.print("(");

            ExpressionList arguments = expression.getArguments();
            Expression arg0 = arguments.get(0);

            switch (mutant) {
                case ADD:
                    out.print(arg0.toString() + " +1 ");
                    break;
                case DEC:
                    out.print(arg0.toString() + " -1 ");
                    break;
                case ADD_AFTER:
                    if (arg0 instanceof Variable) {
                        out.print(arg0.toString() + "++ ");
                    } else {
                        out.print(arg0.toString() + " +1 ");
                    }
                    break;
                case DEC_AFTER:
                    if (arg0 instanceof Variable) {
                        out.print(arg0.toString() + "-- ");
                    } else {
                        out.print(arg0.toString() + " -1 ");
                    }
                    break;
                case ADD_BEFORE:
                    if (arg0 instanceof Variable) {
                        out.print(" ++" + arg0.toString());
                    } else {
                        out.print(arg0.toString() + " +1 ");
                    }
                    break;
                case DEC_BEFORE:
                    if (arg0 instanceof Variable) {
                        out.print(" --" + arg0.toString());
                    } else {
                        out.print(arg0.toString() + " -1 ");
                    }
                    break;
            }


            mutated_line = line_num;
            writeLog(removeNewline("new " + classType.getName() + "(..): ")
                    + " the first argument has been changed...");

            ExpressionList args = arguments.subList(1, arguments.size());
            if (!args.isEmpty()) {
                out.print(", ");
                args.accept(this);
                out.print(" ");
            } else {
                args.accept(this);
            }
            out.print(")");

            line_num++;
            MemberDeclarationList mdlst = expression.getClassBody();
            if (mdlst != null) {
                out.println("{");
                line_num++;
                pushNest();
                mdlst.accept(this);
                popNest();
                writeTab();
                out.print("}");
            }

        }

    }
}