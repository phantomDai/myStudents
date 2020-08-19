/*
 * @author: GuYouda
 * @date: 2018/03/22
 * @time: 02:33
 * @des: RSK 变异体输出
 */
package mujava.op.concurrence;

import mujava.op.concurrence.util.ModifierListUtil;
import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;


public class RSK_Writer extends ConcurrentMutantCodeWriter {
    private MethodDeclaration mutant = null;
    private boolean isMutantTarget = false;

    /**
     * 设置变异体
     *
     * @param methodDeclaration 方法
     */
    public void setMutant(MethodDeclaration methodDeclaration) {
        mutant = methodDeclaration;
        isMutantTarget = false;
    }

    public RSK_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void visit(MethodDeclaration p) throws ParseTreeException {
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
                ModifierList newModifiers = ModifierListUtil.removeModifier("synchronized", temp);
                super.visit(newModifiers);
            }


            if (empt) {
                out.print(" ");
            }
            // -------------------------------------------------------------
            mutated_line = line_num;
            writeLog(removeNewline("synchronized is remove"));
            // -------------------------------------------------------------
        } else {
            super.visit(p);
        }
    }
}
