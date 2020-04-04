package com.weblogic.framework.ui;

import com.weblogic.framework.entity.MyException;
import com.weblogic.framework.payloads.VulTest;
import com.weblogic.framework.utils.StringUtils;
import com.weblogic.framework.utils.CallUtils;
import com.weblogic.framework.utils.VulUtils;
import lombok.SneakyThrows;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static com.weblogic.framework.config.CharsetConfig.defaultCharsetName;
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
    private JComboBox callComboBox;
    private JComboBox charsetComboBox;
    private JLabel charsetLabel;
    private JLabel callText;
    private JTextField ldapUrlText;

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
        text = header + VulUtils.getVulInfo() + "\n" + text;
        serverInfoText.setText(text);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        /**
         * set call class
         */
        for (String className: CallUtils.CALL_NAMES) {
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
    }

    /**
     * 漏洞验证
     */
    private void validateVul(){
        vulMap.clear();
        serverInfoText.setText("");
        cmdRspTextArea.setText("");
        String javascriptUrl = javascriptText.getText();
        javascriptUrl = javascriptUrl.trim();
        String callName = callComboBox.getSelectedItem().toString();
        String ldapUrl = ldapUrlText.getText();
        ldapUrl = ldapUrl.trim();
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if("请选择".equals(charsetName)){
            charsetName = null;
        }
        String vulName = vulComboBox.getSelectedItem().toString();
        String host = targetText.getText().trim();
        host = host.replace("\\", "/");
        if (host.lastIndexOf("/") != -1) {
            host = host.substring(0, host.lastIndexOf("/"));
        }
        // 探测所有POC
        final List<Class<? extends VulTest>> vulClasses = new ArrayList<>(VulTest.Utils.getVulTest());
        Collections.sort(vulClasses, new StringUtils.ToStringComparator());
        serverInfoText.setText("漏洞验证中.....");
        validateVul(host,javascriptUrl,ldapUrl,charsetName,callName,vulClasses,vulName);
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
    public void validateVul(String host, String javascriptUrl, String ldapUrl,String charsetName,String callName, List<Class<? extends VulTest>> vulClasses, String vulName){
        String tmp = "";
        // 反射执行 vulClasses
        for (Class<? extends VulTest> clazz : vulClasses) {
            String simpleName = clazz.getSimpleName();
            vulName = vulName.replace("-", "_");
            if(!simpleName.equals(vulName) && !"请选择".equals(vulName)){
                continue;
            }
            VulTest vulTest = null;
            Boolean flag = false;
            String msg = "";
            try {
                vulTest = clazz.newInstance();
                flag = vulTest.vulnerable(host,javascriptUrl,ldapUrl,charsetName,callName);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }catch (Exception e) {
                if(e instanceof MyException){
                    msg = e.getMessage();
                }
            }
            String token = "";
            if (flag) {
                try {
                    Field bindNameField = clazz.getDeclaredField("bindName");
                    bindNameField.setAccessible(true);
                    token = bindNameField.get(vulTest).toString();
                    vulMap.put(token, vulTest);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // TODO:
                }
            }
            String tmpStr = "";
            if(!isBlank(msg)){
                tmpStr = clazz.getSimpleName() + "  " + host+ "  "+msg;
            }else{
                if(flag){
                    tmpStr = clazz.getSimpleName() + "  " + host+ "  漏洞存在  " + " token : " + token + "\n\n";
                }else{
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
    public void exploit(){
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
        cmdRspTextArea.setText(cmd+" 命令执行中.....");
        VulTest vul = vulMap.get(token);
        String charsetName = charsetComboBox.getSelectedItem().toString();
        if("自动".equals(charsetName)){
            charsetName = null;
        }
        if (vul != null) {
            try {
                String rsp = vul.exploit(host,cmd,charsetName);
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

}
