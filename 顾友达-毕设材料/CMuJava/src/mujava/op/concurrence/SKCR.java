/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 21:14
 * @des: SKCR - Shrink Critical Region
 *      Shrinking a critical region will have similar consequences (interference) to shifting a region
 *      since both the SHCR and SKCR operators move statements that require synchronization outside the
 *      critical section.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class SKCR extends Mutator {


    public SKCR(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public SKCR(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    @Override
    public void visit(SynchronizedStatement statement) throws ParseTreeException {
        StatementList statementList = statement.getStatements();
        if (statementList.size() >= 2) {
            outputToFile(statement);
        } else {
            super.visit(statement);
        }
    }

    /**
     * 输出SKCR并发变异体
     *
     * @param statement Synchronized语句块
     */
    public void outputToFile(SynchronizedStatement statement) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            SKCR_Writer writer = new SKCR_Writer(mutant_dir, out);
            SynchronizedStatement mutant = (SynchronizedStatement) statement.makeRecursiveCopy();
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

