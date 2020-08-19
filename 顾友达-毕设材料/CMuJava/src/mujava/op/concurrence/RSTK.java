/*
 * @author: GuYouda
 * @date: 2018/5/5
 * @time: 0:05
 * @des: RSTK - Remove Static Keyword from Method
 *      The RSTK operator removes static from static synchronized methods and causes synchronization
 *      to occur on the instance object instead of the class object. Similar to the ASTK operator,
 *      the RSTK operator is an examples of the wrong lock bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RSTK extends Mutator {

    public RSTK(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RSTK(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) throws ParseTreeException {
        if (isTarget(methodDeclaration)) {
            outputToFile(methodDeclaration);
        } else {
            super.visit(methodDeclaration);
        }
    }

    /**
     * 判断是否为可以删除static关键字的方法声明
     *
     * @param methodDeclaration 方法声明
     * @return true or false
     */
    private boolean isTarget(MethodDeclaration methodDeclaration) {
        // 含有 synchronized 并且含 static 关键字
        ModifierList modifierList = methodDeclaration.getModifiers();
        return modifierList.contains(ModifierList.SYNCHRONIZED) && modifierList.contains(ModifierList.STATIC);
    }

    /**
     * Output RSTK mutants to file
     *
     * @param original 原始方法声明
     */
    public void outputToFile(MethodDeclaration original) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RSTK_Writer writer = new RSTK_Writer(mutant_dir, out);
            writer.setMutant(original);
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
