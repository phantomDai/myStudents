/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:37
 * @des: MSP:Modify Synchronized Block Parameter
 *      Common parameters for a synchronized block include the this keyword,
 *      indicating that synchronization occurs with respect to the instance
 *      object of the class, and implicit monitor objects. If the keyword
 *      this or an object is used as a parameter for a synchronized block
 *      we can replace the parameter by another object or the keyword this.
 *
 *      synchronized块参数通常为两类：this 或 Object
 *      提供以下两种变异方案：
 *      1： 如果参数是this,则用一个对象替换
 *      2： 如果参数是一个对象，则用this或另一个对象替换
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ArrayUtil;
import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MSP extends Mutator {
    private java.util.List<String> fieldDeclarationList = new ArrayList<>();

    public MSP(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        fieldDeclarationList.add("this");
    }

    public MSP(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        fieldDeclarationList.add("this");
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) {
        TypeName typeName = fieldDeclaration.getTypeSpecifier();
        if (!ArrayUtil.inArray(typeName.getName(), ArrayUtil.BASE_TYPE)) {
            fieldDeclarationList.add(fieldDeclaration.getVariable());
        }
    }


    @Override
    public void visit(SynchronizedStatement statement) throws ParseTreeException {
        outputToFile(statement);
    }

    /**
     * 输出MSP并发变异体
     *
     * @param statement 原始方法声明
     */
    public void outputToFile(SynchronizedStatement statement) {
        String arg = statement.getExpression().toString();
        if (comp_unit == null) {
            return;
        }

        for (String str : fieldDeclarationList) {
            if (str.equals(arg)) {
                continue;
            }
            num++;
            String f_name = getSourceName(this);
            String mutant_dir = getMuantID();

            try {
                PrintWriter out = getPrintWriter(f_name);
                MSP_Writer writer = new MSP_Writer(mutant_dir, out);

                SynchronizedStatement mutant = (SynchronizedStatement) statement.makeRecursiveCopy();
                mutant.setExpression(new Variable(str));
                writer.setMutant(statement, mutant);
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
}
