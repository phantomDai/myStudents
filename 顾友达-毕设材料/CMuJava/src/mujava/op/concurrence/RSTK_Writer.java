/*
 * @author: GuYouda
 * @date: 2018/5/5
 * @time: 2:09
 * @des: RSTK - Remove Static Keyword from Method
 *      The RSTK operator removes static from static synchronized methods and causes synchronization
 *      to occur on the instance object instead of the class object. Similar to the ASTK operator,
 *      the RSTK operator is an examples of the wrong lock bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.concurrence.util.ModifierListUtil;
import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;

public class RSTK_Writer extends ConcurrentMutantCodeWriter {
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

    RSTK_Writer(String file_name, PrintWriter out) {
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

    public void visit(ModifierList modifierList) throws ParseTreeException {
        if (isMutantTarget) {
            ModifierList temp = (ModifierList) modifierList.makeRecursiveCopy();
            temp = ModifierListUtil.removeModifier("static", temp);
            mutated_line = line_num;
            writeLog(removeNewline("static has been removed"));
            super.visit(temp);
        } else {
            super.visit(modifierList);
        }
    }
}

