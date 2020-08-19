/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 21:55
 * @des: SHCR - Shift Critical Region
 *      Shifting a critical region up or down can potentially cause interference bugs
 *      by no longer synchronizing access to a shared variable.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class SHCR extends Mutator {

    public SHCR(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public SHCR(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(StatementList statementList) throws ParseTreeException {
        int synchronizedBlockPosition = -1;
        for (int i = 0; i < statementList.size(); i++) {
            Statement statement = statementList.get(i);
            if (statement instanceof SynchronizedStatement) {
                synchronizedBlockPosition = i;
            }
        }
        /*
         必须含有synchronized块，并且synchronized块前面或者后面必须有其他语句
         */
        if (synchronizedBlockPosition != -1 && statementList.size() > 1) {
            outputToFile(statementList);
        }
    }

    /**
     * 输出SHCR并发变异体
     *
     * @param statementList 语句集合
     */
    public void outputToFile(StatementList statementList) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            SHCR_Writer writer = new SHCR_Writer(mutant_dir, out);
            StatementList mutant = (StatementList) statementList.makeRecursiveCopy();
            writer.setMutant(statementList, mutant);
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
