/*
 * @author: GuYouda
 * @date: 2018/4/10
 * @time: 21:48
 * @des:
 */

package mujava.op.concurrence;

import mujava.op.util.ConcurrentMutantCodeWriter;
import openjava.ptree.*;

import java.io.PrintWriter;

public class ESP_Writer extends ConcurrentMutantCodeWriter {

    private SynchronizedStatement outter, inner;

    public ESP_Writer(String file_name, PrintWriter out) {
        super(file_name, out);
    }

    public void setStatement(SynchronizedStatement outter, SynchronizedStatement inner) {
        this.outter = outter;
        this.inner = inner;
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) throws ParseTreeException {
        if (isSame(synchronizedStatement, inner)) {
            SynchronizedStatement statement = (SynchronizedStatement) synchronizedStatement.makeRecursiveCopy();
            statement.setExpression(outter.getExpression());
            mutated_line = line_num;
            writeLog(removeNewline("synchronized block parameter has been changed"));
            line_num++;
            super.visit(statement);
        } else if (isSame(synchronizedStatement, outter)) {
            SynchronizedStatement statement = (SynchronizedStatement) synchronizedStatement.makeRecursiveCopy();
            statement.setExpression(inner.getExpression());
            mutated_line = line_num;
            writeLog(removeNewline("synchronized block parameter has been changed"));
            line_num++;
            super.visit(statement);
        } else {
            super.visit(synchronizedStatement);
        }
    }

    private boolean isSame(SynchronizedStatement s1, SynchronizedStatement s2) {
        return isSameObject(s1, s2) || s1.toString().equals(s2.toString());
    }
}