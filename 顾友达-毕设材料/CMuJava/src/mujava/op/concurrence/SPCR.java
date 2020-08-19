/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 20:31
 * @des: SPCR - Split Critical Region
 *      Splitting a critical region into two regions will not cause statements to move outside of
 *      the critical region. However, the consequences of splitting a critical region into 2 regions
 *      is potentially just as serious since a split may cause a set of statements that were meant
 *      to be atomic to be nonatomic. For example, in between the two split critical regions another
 *      thread might be able to acquire the lock for the region and modify the value of shared
 *      variables before the second half of the old critical region is executed.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class SPCR extends Mutator {


    public SPCR(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        this.env = env;
    }

    public SPCR(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
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
     * 输出SPCR并发变异体
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
            SPCR_Writer writer = new SPCR_Writer(mutant_dir, out);
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
