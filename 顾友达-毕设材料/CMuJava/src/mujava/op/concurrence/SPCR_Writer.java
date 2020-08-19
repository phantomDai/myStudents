/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 20:31
 * @des: SPCR - Split Critical Region
 * Splitting a critical region into two regions will not cause statements to move outside of
 * the critical region. However, the consequences of splitting a critical region into 2 regions
 * is potentially just as serious since a split may cause a set of statements that were meant
 * to be atomic to be nonatomic. For example, in between the two split critical regions another
 * thread might be able to acquire the lock for the region and modify the value of shared
 * variables before the second half of the old critical region is executed.
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.SynchronizedStatement;

import java.io.PrintWriter;

public class SPCR_Writer extends ConcurrentMutantCodeWriter {

    private SynchronizedStatement original = null;
    private SynchronizedStatement mutant = null;

    public SPCR_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    /**
     * Set original source code and mutated code
     *
     * @param original 原表达式
     * @param mutant   变异体
     */
    public void setMutant(SynchronizedStatement original, SynchronizedStatement mutant) {
        this.original = original;
        this.mutant = mutant;
    }


    public void visit(SynchronizedStatement synchronizedStatement) throws ParseTreeException {
        if (!(isSameObject(synchronizedStatement, original))) {
            super.visit(synchronizedStatement);
        } else {
            Expression expression = synchronizedStatement.getExpression();
            StatementList statementList = synchronizedStatement.getStatements();
            StatementList statementList1 = new StatementList();
            StatementList statementList2 = new StatementList();

            for (int i = 0; i < statementList.size(); i++) {
                if (i < statementList.size() / 2) {
                    statementList1.add(statementList.get(i));
                } else {
                    statementList2.add(statementList.get(i));
                }
            }

            // 输出变异块1
            writeTab();
            out.print("synchronized( " + expression + " )");
            // 记录变异位置
            mutated_line = line_num;
            writeLog(removeNewline("synchronized (" + expression.toString() + ")")
                    + " is changed.");
            line_num++;
            writeStatementsBlock(statementList1);
            out.println();
            line_num++;

            // 输出变异块2
            writeTab();
            out.print("synchronized( " + expression + " )");
            line_num++;
            writeStatementsBlock(statementList2);
            out.println();
            line_num++;
        }
    }


}
