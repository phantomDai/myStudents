/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:27
 * @des: RSB - Remove Synchronized Block
 *      Similar to the RSK operator, the RSB operator removes the synchronized keyword
 *      from around a statement block which can cause a no lock bug.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RSB extends Mutator {

    public RSB(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    public RSB(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
    }

    @Override
    public void visit(SynchronizedStatement statement) throws ParseTreeException {
        outputToFile(statement);
    }

    /**
     * Output RSB mutants to file
     *
     * @param synchronizedStatement 同步代码块
     */
    public void outputToFile(SynchronizedStatement synchronizedStatement) {
        if (comp_unit == null) {
            return;
        }
        num++;
        String f_name = getSourceName(this);
        String mutant_dir = getMuantID();

        try {
            PrintWriter out = getPrintWriter(f_name);
            RSB_Writer writer = new RSB_Writer(mutant_dir, out);
            writer.setMutant(synchronizedStatement);
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
