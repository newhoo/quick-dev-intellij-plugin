package io.github.newhoo.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.newhoo.setting.PluginProjectSetting;
import io.github.newhoo.setting.PluginGlobalSetting;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 插件配置信息
 *
 * @author huzunrong
 * @since 1.0
 */
public class AppSettingDialog extends DialogWrapper {
    private final PluginGlobalSetting globalSetting = PluginGlobalSetting.getInstance();
    private final PluginProjectSetting apiProjectSetting;
    private final AppSettingPanel apiSettingPanel;

    public AppSettingDialog(@NotNull final Project project) {
        super(project, true);
        this.apiProjectSetting = new PluginProjectSetting(project);
        this.apiSettingPanel = new AppSettingPanel();

        apiSettingPanel.invokeEnableCheckBox.setSelected(apiProjectSetting.getEnableQuickInvoke());

        apiSettingPanel.mysqlExplainEnableCheckBox.setSelected(apiProjectSetting.getEnableMySQLExplain());
        apiSettingPanel.mysqlShowSqlCheckBox.setSelected(apiProjectSetting.getMysqlShowSql());
        apiSettingPanel.mysqlFilterField.setText(apiProjectSetting.getMysqlFilter());
        apiSettingPanel.mysqlTypes.setText(apiProjectSetting.getMysqlTypes());
        apiSettingPanel.mysqlExtras.setText(apiProjectSetting.getMysqlExtras());

        apiSettingPanel.apolloUrlTextField.setText(globalSetting.getApolloBaseUrl());

        setTitle("设置插件配置信息");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        return apiSettingPanel.mainPanel;
    }

    @Override
    protected String getHelpId() {
        return null;
    }

    @Override
    protected String getDimensionServiceKey() {
        return "quick-dev.AppSettingDialog";
    }

    @Override
    protected void doOKAction() {
        apiProjectSetting.setEnableQuickInvoke(apiSettingPanel.invokeEnableCheckBox.isSelected());

        apiProjectSetting.setEnableMySQLExplain(apiSettingPanel.mysqlExplainEnableCheckBox.isSelected());
        apiProjectSetting.setMysqlShowSql(apiSettingPanel.mysqlShowSqlCheckBox.isSelected());
        apiProjectSetting.setMysqlFilter(apiSettingPanel.mysqlFilterField.getText());
        apiProjectSetting.setMysqlTypes(apiSettingPanel.mysqlTypes.getText());
        apiProjectSetting.setMysqlExtras(apiSettingPanel.mysqlExtras.getText());

        globalSetting.setApolloBaseUrl(apiSettingPanel.apolloUrlTextField.getText());

        super.doOKAction();
    }
}
