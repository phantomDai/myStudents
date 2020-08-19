/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:37
 * @des: ESP: Exchange Synchronized Block Parameters
 * If a critical region is guarded by multiple synchronized blocks with implicit monitor locks
 * the ESP operator exchanges two adjacent lock objects.
 */

package mujava.op.concurrence;

import mujava.op.util.Mutator;
import openjava.mop.Environment;
import openjava.ptree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class ESP extends Mutator {
    ESP_Checker checker;
    SynchronizedStatement innerSynStatement = null;

    public ESP(Environment env, CompilationUnit comp_unit) {
        super(env, comp_unit);
        checker = new ESP_Checker(getEnvironment(), comp_unit);
    }

    public ESP(Environment env, ClassDeclaration cles, CompilationUnit comp_unit) {
        super(env, comp_unit);
        checker = new ESP_Checker(getEnvironment(), comp_unit);
    }

    @Override
    public void visit(SynchronizedStatement statement) throws ParseTreeException {
        try {
            innerSynStatement = getTarget(statement);
            if (innerSynStatement != null) {
                outputToFile(statement);
            } else {
                super.visit(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.visit(statement);
        }
    }

    /**
     * 输出ESP并发变异体
     *
     * @param statement 原始方法声明
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
            ESP_Writer writer = new ESP_Writer(mutant_dir, out);
            writer.setStatement(statement, innerSynStatement);
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

    /**
     * 判断是否是需要进行变异的SynchronizedStatement
     * 如果一个Synchronize块里面嵌套的有Synchronized块则返回内部Synchronized块，否则返回null
     *
     * @param synchronizedStatement 方法调用
     * @return SynchronizedStatement or null
     */
    private SynchronizedStatement getTarget(SynchronizedStatement synchronizedStatement) throws Exception {
        return checker.check(synchronizedStatement, checker);
    }

    class ESP_Checker extends Mutator {
        private SynchronizedStatement innerSynStatement = null;
        private boolean isTarget = false;

        public SynchronizedStatement check(SynchronizedStatement statement, ESP_Checker checker) throws ParseTreeException {
            isTarget = false;
            StatementList statementList = statement.getStatements();
            statementList.accept(checker);

            return isTarget ? innerSynStatement : null;

        }

        ESP_Checker(Environment env, CompilationUnit comp_unit) {
            super(env, comp_unit);
        }

        @Override
        public void visit(SynchronizedStatement statement) throws ParseTreeException {
            innerSynStatement = (SynchronizedStatement) statement.makeCopy();
            isTarget = true;
        }

    }
}
