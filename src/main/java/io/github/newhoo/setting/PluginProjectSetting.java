package io.github.newhoo.setting;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import io.github.newhoo.common.jr.plugin.JrHelper;

/**
 * PluginProjectSetting
 *
 * @author huzunrong
 * @since 1.0
 */
public class PluginProjectSetting {

    private static final String KEY_SPRING_INVOKE_ENABLE = "quick-dev.enableQuickInvoke";
    private static final String KEY_INVOKE_PORT = "quick-dev.invokePort";

    private static final String KEY_MYSQL_EXPLAIN_ENABLE = "quick-dev.enableMySQLExplain";
    private static final String KEY_MYSQL_SHOW_SQL = "quick-dev.mysql.showSQL";
    private static final String KEY_MYSQL_FILTER = "quick-dev.mysql.filter";
    private static final String KEY_MYSQL_TYPES = "quick-dev.mysql.types";
    private static final String KEY_MYSQL_EXTRAS = "quick-dev.mysql.extras";

    private static final String KEY_JMV_PARAMETER = "quick-dev.jvmParameter";
    private static final String KEY_JMV_PARAMETER_LIST = "quick-dev.jvmParameterList";

    private final PropertiesComponent propertiesComponent;

    public PluginProjectSetting(Project project) {
        this.propertiesComponent = PropertiesComponent.getInstance(project);
    }

    // spring

    public boolean getEnableQuickInvoke() {
        return propertiesComponent.getBoolean(KEY_SPRING_INVOKE_ENABLE, Boolean.TRUE);
    }

    public void setEnableQuickInvoke(boolean enableQuickInvoke) {
        propertiesComponent.setValue(KEY_SPRING_INVOKE_ENABLE, enableQuickInvoke, Boolean.TRUE);
    }

    public int getSpringInvokePort() {
        return propertiesComponent.getInt(KEY_INVOKE_PORT, JrHelper.getDefaultInvokePort());
    }

    public void setSpringInvokePort(int port) {
        propertiesComponent.setValue(KEY_INVOKE_PORT, port, JrHelper.getDefaultInvokePort());
    }

    // mysql explain

    public boolean getEnableMySQLExplain() {
        return propertiesComponent.getBoolean(KEY_MYSQL_EXPLAIN_ENABLE, Boolean.TRUE);
    }

    public void setEnableMySQLExplain(boolean enableMySQLExplain) {
        propertiesComponent.setValue(KEY_MYSQL_EXPLAIN_ENABLE, enableMySQLExplain, Boolean.TRUE);
    }

    public boolean getMysqlShowSql() {
        return propertiesComponent.getBoolean(KEY_MYSQL_SHOW_SQL, Boolean.FALSE);
    }

    public void setMysqlShowSql(boolean mysqlShowSql) {
        propertiesComponent.setValue(KEY_MYSQL_SHOW_SQL, mysqlShowSql, Boolean.FALSE);
    }

    public String getMysqlFilter() {
        return propertiesComponent.getValue(KEY_MYSQL_FILTER, "QRTZ_,COUNT(0)");
    }

    public void setMysqlFilter(String mysqlFilter) {
        propertiesComponent.setValue(KEY_MYSQL_FILTER, mysqlFilter);
    }

    public String getMysqlTypes() {
        return propertiesComponent.getValue(KEY_MYSQL_TYPES, "ALL");
    }

    public void setMysqlTypes(String mysqlTypes) {
        propertiesComponent.setValue(KEY_MYSQL_TYPES, mysqlTypes);
    }

    public String getMysqlExtras() {
        return propertiesComponent.getValue(KEY_MYSQL_EXTRAS, "Using filesort,Using temporary");
    }

    public void setMysqlExtras(String mysqlExtras) {
        propertiesComponent.setValue(KEY_MYSQL_EXTRAS, mysqlExtras);
    }

    public String getJvmParameter() {
        return propertiesComponent.getValue(KEY_JMV_PARAMETER);
    }

    public void setJvmParameter(String jvmParameter) {
        propertiesComponent.setValue(KEY_JMV_PARAMETER, jvmParameter);
    }

    public String getJvmParameterList() {
        return propertiesComponent.getValue(KEY_JMV_PARAMETER_LIST);
    }

    public void setJvmParameterList(String jvmParameterList) {
        propertiesComponent.setValue(KEY_JMV_PARAMETER_LIST, jvmParameterList);
    }
}