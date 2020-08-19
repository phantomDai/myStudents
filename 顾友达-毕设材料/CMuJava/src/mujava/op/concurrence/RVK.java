/*
 * @author: GuYouda
 * @date: 2018/04/01
 * @time: 08:43
 * @des: RVK - Remove Volatile Keyword
 *      The volatile keyword is used with a shared variable and prevents operations on the variable
 *      from being reordered in memory with other operations. In the below example we remove the
 *      volatile keyword from a shared long variable. If a long variable, which is 64-bit, is not
 *      declared volatile then reads and writes will be treated as two 32-bit operations instead of
 *      one operation. Therefore, the RVK operator can cause a situation where a nonatomic operation
 *      is assumed to be atomic.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RVK extends Mutator {

    public RVK(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RVK(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(FieldDeclaration p) throws ParseTreeException {
        // 判断变量定义是否为 volatile 类型
        System.out.println(p.toString());
        ModifierList modifierList = p.getModifiers();
        if (modifierList.contains(ModifierList.VOLATILE)) {
            outputToFile(p);
        }
    }

    /**
     * Output RVK mutants to file
     *
     * @param original 原始方法声明
     */
    public void outputToFile(FieldDeclaration original) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RVK_Writer writer = new RVK_Writer(mutant_dir, out);
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
