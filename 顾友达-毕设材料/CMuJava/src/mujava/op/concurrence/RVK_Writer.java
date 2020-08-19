/*
 * @author: GuYouda
 * @date: 2018/04/01
 * @time: 08:52
 * @des: RVK 变异体输出
 */
package mujava.op.concurrence;

import mujava.op.concurrence.util.ModifierListUtil;
import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;


public class RVK_Writer extends ConcurrentMutantCodeWriter {
    private FieldDeclaration mutant = null;
    private boolean isMutantTarget = false;

    /**
     * 设置变异体
     *
     * @param fieldDeclaration 变量定义
     */
    public void setMutant(FieldDeclaration fieldDeclaration) {
        mutant = fieldDeclaration;
        isMutantTarget = false;
    }

    public RVK_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(FieldDeclaration p) throws ParseTreeException {
        if (isSameObject(p, mutant)) {
            isMutantTarget = true;
            super.visit(p);
            isMutantTarget = false;
        } else {
            super.visit(p);
        }
    }

    public void visit(ModifierList p) throws ParseTreeException {
        if (isMutantTarget) {
            boolean empt = false;
            ModifierList temp = (ModifierList) p.makeRecursiveCopy();
            if (temp.isEmpty()) {
                empt = true;
            } else {
                ModifierList newModifiers = ModifierListUtil.removeModifier("volatile", temp);
                super.visit(newModifiers);
            }


            if (empt)
                out.print(" ");

            // -------------------------------------------------------------
            mutated_line = line_num;
            //out.print(mutated_modifier);
            writeLog(removeNewline("volatile is remove"));
            // -------------------------------------------------------------
        } else {
            super.visit(p);
        }
    }
}
