/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 0:05
 * @des: ASTK - Add Static Keyword to Method
 *      The static keyword used for a synchronized method indicates that the method is synchronized using
 *      the class object not the instance object. The ASTK operator adds static to non-static synchronized
 *      methods and causes synchronization to occur on the class object instead of the instance object.
 *      The ASTK operator is an example of the wrong lock bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class ASTK extends Mutator {

    public ASTK(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public ASTK(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
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
     * 判断是否为可以添加static关键字的方法声明
     * 注：此判断逻辑并不全面
     * <p>
     * ASTK变异算子必须满足以下条件：
     * 1：方法声明本身不含static关键字
     * 2：方法声明必须含有synchronized关键字
     * 3：方法内部所使用的变量必须为static
     * 4：方法内部调用的其他方法必须为static
     * <p>
     * 存在的问题：
     * 在实际情况下满足上述情况且不是static的方法声明极其罕见
     * 由于这一系列判断需要消耗大量的计算资源，且通常不可能满足
     * <p>
     * 解决方案：
     * 为了减少复杂的判断逻辑和巨大的计算开销，采取以下方案：
     * 1：直接给满足1、2两个条件的方法声明添加static关键字
     * 2：生成临时变异体
     * 3：编译临时变异体，若不通过则说明不满足条件3和4，删除临时变异体；
     * 若通过则说明满足条件3和4，接受当前临时变异体
     *
     * @param methodDeclaration 方法声明
     * @return true or false
     */
    private boolean isTarget(MethodDeclaration methodDeclaration) {
        // 含有 synchronized 但是不含 static 关键字
        ModifierList modifierList = methodDeclaration.getModifiers();
        return modifierList.contains(ModifierList.SYNCHRONIZED) && !modifierList.contains(ModifierList.STATIC);
    }

    /**
     * Output ASTK mutants to file
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
            ASTK_Writer writer = new ASTK_Writer(mutant_dir, out);
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
