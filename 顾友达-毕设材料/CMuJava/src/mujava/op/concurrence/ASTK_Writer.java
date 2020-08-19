/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 0:09
 * @des: ASTK - Add Static Keyword to Method
 * The static keyword used for a synchronized method indicates that the method is synchronized using
 * the class object not the instance object. The ASTK operator adds static to non-static synchronized
 * methods and causes synchronization to occur on the class object instead of the instance object.
 * The ASTK operator is an example of the wrong lock bug pattern.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

import java.io.PrintWriter;

public class ASTK_Writer extends ConcurrentMutantCodeWriter {
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

    public ASTK_Writer(String file_name, PrintWriter out) {
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
            temp.add(ModifierList.STATIC);
            mutated_line = line_num;
            writeLog(removeNewline("add static keyword"));
            super.visit(temp);
        } else {
            super.visit(modifierList);
        }
    }
}

