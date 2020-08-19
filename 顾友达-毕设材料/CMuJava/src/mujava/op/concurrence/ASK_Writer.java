/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 11:33
 * @des: ASK 变异体输出
 */
package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;


public class ASK_Writer extends ConcurrentMutantCodeWriter {
    private MethodDeclaration mutant = null;

    /**
     * 设置变异体
     *
     * @param methodDeclaration 方法
     */
    public void setMutant(MethodDeclaration methodDeclaration) {
        mutant = methodDeclaration;
    }

    ASK_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(MethodDeclaration p) throws ParseTreeException {
        if (isSameObject(p, mutant)) {
            // -------------------------------------------------------------
            mutated_line = line_num;
            //out.print(mutated_modifier);
            writeLog(removeNewline("add synchronized to methodDeclaration"));
            // -------------------------------------------------------------
            MethodDeclaration temp = (MethodDeclaration) p.makeRecursiveCopy();
            ModifierList m = temp.getModifiers();
            m.add(ModifierList.SYNCHRONIZED);
            super.visit(temp);
        } else {
            super.visit(p);
        }
    }
}
