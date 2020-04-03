package com.r4v3zn.weblogic.tools.ui;

import com.r4v3zn.weblogic.tools.entity.MyException;
import com.r4v3zn.weblogic.tools.payloads.VulTest;
import com.r4v3zn.weblogic.tools.utils.StringUtils;
import lombok.SneakyThrows;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static com.r4v3zn.weblogic.tools.utils.VulUtils.getVulInfo;
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
    private JComboBox<String> vulComboBox;
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

    private Map<String, VulTest> vulMap = new HashMap<>(16);

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
        text = header + getVulInfo() + "\n" + text;
        serverInfoText.setText(text);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
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
                        serverInfoText.setText("");
                        cmdRspTextArea.setText("");
                        String vulName = vulComboBox.getSelectedItem().toString();
                        String host = targetText.getText().trim();
                        host = host.replace("\\", "/").replace("https://", "").replace("http://", "");
                        if (host.lastIndexOf("/") != -1) {
                            host = host.substring(0, host.lastIndexOf("/"));
                        }
                        String ip = "";
                        Integer port = 80;
                        if (host.split(":").length == 1) {
                            ip = host.split(":")[0];
                        } else if (host.split(":").length > 1) {
                            ip = host.split(":")[0];
                            port = Integer.parseInt(host.split(":")[1]);
                        }
                        if ("请选择".equals(vulName)) {
                            // 探测所有
                            final List<Class<? extends VulTest>> vulClasses = new ArrayList<>(VulTest.Utils.getVulTest());
                            Collections.sort(vulClasses, new StringUtils.ToStringComparator());
                            String tmp = "";
                            serverInfoText.setText(tmp);
                            // 反射执行 vulClasses
                            for (Class<? extends VulTest> clazz : vulClasses) {
                                VulTest vulTest = clazz.newInstance();
                                Boolean flag = vulTest.vulnerable(ip, port);
                                String token = "";
                                if (flag) {
                                    try {
                                        Field bindNameField = clazz.getDeclaredField("bindName");
                                        bindNameField.setAccessible(true);
                                        token = bindNameField.get(vulTest).toString();
                                        vulMap.put(token, vulTest);
                                    } catch (NoSuchFieldException nosuch) {
                                        // TODO:
                                    }
                                }
                                String tmpStr = "";
                                if (flag){
                                    tmpStr = clazz.getSimpleName() + "  " + ip + ":" + port + "  " + flag + " token : " + token + "\n\n";
                                }else{
                                    tmpStr = clazz.getSimpleName() + "  " + ip + ":" + port + "  " + flag + " token : " + token + "\n\n";
                                }
                                tmp += tmpStr;
                                serverInfoText.setText(tmp);
                            }
                        } else {
                            // 探测指定版本
                        }
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
                VulTest vul = vulMap.get(token);
                if (vul != null) {
                    try {
                        String rsp = vul.exploit(null, null, new String[]{cmd});
                        cmdRspTextArea.setText(rsp);
                    } catch (Exception ex) {
                        if (ex instanceof MyException) {
                            cmdRspTextArea.setText(ex.getMessage());
                        } else {
                            cmdRspTextArea.setText("执行失败");
                        }
                    }
                } else {
                    cmdRspTextArea.setText("漏洞不存在");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame main = new Main("Weblogic 检测工具 仅供安全检测使用，勿用于非法用途");
        main.setVisible(true);
    }

}
