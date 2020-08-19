/*
 * @author: GuYouda
 * @date: 2018/03/23
 * @time: 01:15
 * @des: 并发变异体查看界面
 */

package mujava.gui;

import mujava.MutationSystem;
import mujava.gui.util.CONMSummaryTableModel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * <p>Template for viewing concurrent-level mutants: show original source code,
 * source code of the selected mutants, summary of mutants </p>
 *
 * @author GuYouda
 * @version 1.0
 */

public class ConcurrentMutantsViewerPanel extends MutantsViewerPanel {
    private static final long serialVersionUID = 103L;

    public ConcurrentMutantsViewerPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Initialization ConcurrentMutantsViewerPanel
     *
     * @see MutantsViewerPanel#jbInit()
     */
    void jbInit() throws Exception {
        this.setLayout(new FlowLayout());

        StyleConstants.setForeground(red_attr, Color.red);
        StyleConstants.setForeground(blue_attr, Color.blue);
        StyleConstants.setForeground(black_attr, Color.black);

        /* summary table: containing the numbers of mutants by each
         *  concurrent-level mutation operators along with the total number
         *  of mutants generated
         */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        JLabel summaryL = new JLabel("* Summary *");
        leftPanel.add(summaryL);
        initSummaryTable();
        summaryTable.setEnabled(false);
        summaryPanel.getViewport().add(summaryTable);
        setSummaryTableSize();
        leftPanel.add(summaryPanel);
        leftPanel.add(totalLabel);
        summaryPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

        /*
        * ComboBox for class (in which mutants will be generated) selection
        */
        JPanel selectClassPanel = new JPanel();
        selectClassPanel.setLayout(new FlowLayout());
        JLabel selectClassLabel = new JLabel("   Select a class : ");
        selectClassPanel.add(selectClassLabel);
        //refreshEnv();
        classCB.setEditable(false);
        selectClassPanel.add(classCB);
        classCB.setPreferredSize(new Dimension(550, 25));
        classCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateClassComboBox();
            }
        });

        /* show a list of mutants to be selected for viewing --
         *  click on the mutant to display the original source and
         *  mutated code
         */
        JPanel contentPanel = new JPanel();
        JScrollPane leftContentSP = new JScrollPane();
        leftContentSP.getViewport().add(mList, null);
        leftContentSP.setPreferredSize(new Dimension(120, 800));
        contentPanel.add(leftContentSP);
        mList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mList_mouseClicked(e);
            }
        });

        JPanel rightContentPanel = new JPanel();
        rightContentPanel.setLayout(new BoxLayout(rightContentPanel, BoxLayout.PAGE_AXIS));

        /*
         * show the line mutated
         */
        changeTF.setPreferredSize(new Dimension(550, 40));
        rightContentPanel.add(changeTF);

        /*
         * show the source code of the original file and the mutant
         */
        originalSP.setPreferredSize(new Dimension(1000, 400));
        mutantSP.setPreferredSize(new Dimension(1000, 400));
        originalSP.setBorder(new TitledBorder("Original"));
        mutantSP.setBorder(new TitledBorder("Mutant"));
        mutantSP.getViewport().add(mutantTP, null);
        originalSP.getViewport().add(originalTP, null);
        rightContentPanel.add(originalSP);
        rightContentPanel.add(mutantSP);
        contentPanel.add(rightContentPanel);

        rightPanel.add(selectClassPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        rightPanel.add(contentPanel);

        contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        leftPanel.setPreferredSize(new Dimension(100, 800));
        this.add(leftPanel);
        this.add(rightPanel);

        refreshEnv();
    }

    /**
     * Change contents for the newly selected class
     */
    void updateClassComboBox() {
        Object item = classCB.getSelectedItem();
        if (item == null)
            return;

        target_dir = item.toString();
        if (target_dir == null)
            return;

        if (isProperClass(target_dir)) {
            MutationSystem.setJMutationPaths(target_dir);
            updateContents();
        } else {
            clearSourceContents();
            mList.setListData(new Vector());
            mList.repaint();
            showGeneratedMutantsNum(null);
        }
        this.repaint();
    }

    void setMutationType() {
        MutationSystem.MUTANT_PATH = MutationSystem.CONCURRENT_MUTANT_PATH;
    }

    void setSummaryTableSize() {
        int temp = MutationSystem.conm_operators.length * 20;
        summaryPanel.setPreferredSize(new Dimension(250, temp));
        summaryPanel.setMaximumSize(new Dimension(250, temp));
    }

    void initSummaryTable() {
        CONMSummaryTableModel conmodel = new CONMSummaryTableModel();
        summaryTable = new JTable(conmodel);
        adjustSummaryTableSize(summaryTable, conmodel);
    }

    int getMutantType() {
        return MutationSystem.CONM;
    }

    /**
     * Set a location where concurrent-level mutants will be stored
     */
    void setMutantPath() {
        MutationSystem.MUTANT_PATH = MutationSystem.CONCURRENT_MUTANT_PATH;
    }

    /**
     * Retrieve a path containing concurrent-level mutants
     */
    String getMutantPath() {
        return MutationSystem.CONCURRENT_MUTANT_PATH;
    }

    void printGeneratedMutantNum(String[] operators, int[] num) {
        int total = 0;
        CONMSummaryTableModel myModel = (CONMSummaryTableModel) (summaryTable.getModel());
        for (int i = 0; i < operators.length; i++) {
            myModel.setValueAt(new Integer(num[i]), i, 1);
            total = total + num[i];
        }
        totalLabel.setText("Total : " + total);
    }
}

