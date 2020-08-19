/*
 * @author: GuYouda
 * @date: 2018/03/21
 * @time: 23:33
 * @des: RSK - Remove Synchronized Keyword from Method
 *      The synchronized keyword is important in defining concurrent methods and the omission of
 *      this keyword is a plausible mistake that a programmer might make when writing concurrent
 *      source code. The RSK operator removes the synchronized keyword from a synchronized method
 *      and causes a potential no lock bug.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.CompilationUnit;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ClassDeclaration;

import java.io.IOException;
import java.io.PrintWriter;

public class RSK extends Mutator {

    public RSK(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RSK(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(MethodDeclaration p) throws ParseTreeException {
        // 判断是否含有 synchronized 关键字
        ModifierList modifierList = p.getModifiers();
        if (modifierList.contains(ModifierList.SYNCHRONIZED)) {
            outputToFile(p);
        }

    }

    /**
     * Output RSK mutants to file
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
            RSK_Writer writer = new RSK_Writer(mutant_dir, out);
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
