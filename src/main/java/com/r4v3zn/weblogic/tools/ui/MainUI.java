package com.r4v3zn.weblogic.tools.ui;

import javax.swing.*;

/**
 * Title: MainUI
 * Desc: TODO
 * Date:2020/4/1 23:44
 * Email:woo0nise@gmail.com
 * Company:www.r4v3zn.com
 * @author R4v3zn
 * @version 1.0.0
 */
public class MainUI {

    private JButton checkButton;
    private JPanel MainPanel;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
