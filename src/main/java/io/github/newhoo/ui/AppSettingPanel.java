package io.github.newhoo.ui;

import javax.swing.*;

public class AppSettingPanel {

    public JPanel mainPanel;

    /*
     * 快速调用配置
     */
    public JCheckBox invokeEnableCheckBox;

    /*
     * mysql explain配置
     */
    public JCheckBox mysqlExplainEnableCheckBox;
    public JCheckBox mysqlShowSqlCheckBox;
    public JTextField mysqlFilterField;
    public JTextField mysqlTypes;
    public JTextField mysqlExtras;

    /*
     * Apollo配置中心配置
     */
    public JTextField apolloUrlTextField;
}
