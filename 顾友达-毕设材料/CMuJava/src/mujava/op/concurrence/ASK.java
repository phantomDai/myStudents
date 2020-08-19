/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 9:08
 * @des: ASK - Add Synchronized Keyword to Method
 *      The synchronized keyword is added to a non-synchronized method in a class
 *      that has synchronized methods or statements. The ASK operator has the potential
 *      to cause a deadlock.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class ASK extends Mutator {
    private CompilationUnit compilationUnit;
    private boolean hasSynchronized = false;

    public ASK(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        compilationUnit = comp_unit;
        hasSynchronized = isTarget();
    }

    public ASK(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        compilationUnit = comp_unit;
        hasSynchronized = isTarget();
    }


    @Override
    public void visit(MethodDeclaration methodDeclaration) throws ParseTreeException {
        /*判断是否含有 synchronized 关键字
          如果不包含，则添加
         */
        if (hasSynchronized) {
            ModifierList modifierList = methodDeclaration.getModifiers();
            if (modifierList.contains(ModifierList.SYNCHRONIZED)) {
                super.visit(methodDeclaration);
            } else {
                outputToFile(methodDeclaration);
            }
        } else {
            super.visit(methodDeclaration);
        }

    }

    private boolean isTarget() {

        hasSynchronized = false;
        ASK_Checker checker = new ASK_Checker(file_env, compilationUnit);
        try {
            compilationUnit.accept(checker);
        } catch (ParseTreeException e) {
            e.printStackTrace();
        }
        return checker.isHasSynchronized();
    }


    public void outputToFile(MethodDeclaration methodDeclaration) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            ASK_Writer writer = new ASK_Writer(mutant_dir, out);
            writer.setMutant(methodDeclaration);
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

    class ASK_Checker extends Mutator {
        private boolean hasSynchronized = false;

        public ASK_Checker(Environment env, CompilationUnit comp_unit) {
            super(env, comp_unit);
        }

        public ASK_Checker(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
            super(env, comp_unit);
        }

        public boolean isHasSynchronized() {
            return hasSynchronized;
        }

        public void setHasSynchronized(boolean hasSynchronized) {
            this.hasSynchronized = hasSynchronized;
        }

        @Override
        public void visit(SynchronizedStatement statement) throws ParseTreeException {
            setHasSynchronized(true);
            super.visit(statement);
        }

        @Override
        public void visit(MethodDeclaration methodCall) throws ParseTreeException {
            // 判断是否含有 synchronized 关键字
            ModifierList modifierList = methodCall.getModifiers();
            if (modifierList.contains(ModifierList.SYNCHRONIZED)) {
                setHasSynchronized(true);
                super.visit(methodCall);
            }
        }
    }
}
