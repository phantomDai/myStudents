/*
 * @author: GuYouda
 * @date: 2018/03/23
 * @time: 01:43
 * @des:
 */

package mujava.gui.util;

/**
 * <p>Template containing summary of concurrent mutants generated</p>
 * @author GuYouda
 * @version 1.0
 */
public class CONMSummaryTableModel extends SummaryTableModel {
    private static final long serialVersionUID = 203L;

    int getOperatorType() {
        return CONMO;
    }
}
