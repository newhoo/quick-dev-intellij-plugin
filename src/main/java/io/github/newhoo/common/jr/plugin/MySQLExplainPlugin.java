package io.github.newhoo.common.jr.plugin;

import com.intellij.openapi.project.Project;
import io.github.newhoo.setting.PluginProjectSetting;
import io.github.newhoo.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

import static com.example.plugin.mysql.common.Constant.PROPERTIES_KEY_MYSQL_EXTRAS;
import static com.example.plugin.mysql.common.Constant.PROPERTIES_KEY_MYSQL_FILTER;
import static com.example.plugin.mysql.common.Constant.PROPERTIES_KEY_MYSQL_SHOW_SQL;
import static com.example.plugin.mysql.common.Constant.PROPERTIES_KEY_MYSQL_TYPES;

/**
 * MySQLExplainPlugin
 *
 * @author huzunrong
 * @since 1.0
 */
public class MySQLExplainPlugin implements CustomJrPlugin {

    @Override
    public boolean enabled(Project project) {
        return new PluginProjectSetting(project).getEnableMySQLExplain()
                && AppUtils.findPsiClass("com.mysql.jdbc.PreparedStatement", project) != null;
    }

    @Override
    public String getLocation() {
        return JrHelper.getJrPluginPath(com.example.plugin.mysql.common.Constant.class);
    }

    @Override
    public Map<String, String> getVmParameter(Project project) {
        Map<String, String> vmParameter = new HashMap<>(4);

        PluginProjectSetting pluginProjectSetting = new PluginProjectSetting(project);
        vmParameter.put(PROPERTIES_KEY_MYSQL_SHOW_SQL, String.valueOf(pluginProjectSetting.getMysqlShowSql()));
        vmParameter.put(PROPERTIES_KEY_MYSQL_FILTER, pluginProjectSetting.getMysqlFilter());
        vmParameter.put(PROPERTIES_KEY_MYSQL_TYPES, pluginProjectSetting.getMysqlTypes());
        vmParameter.put(PROPERTIES_KEY_MYSQL_EXTRAS, pluginProjectSetting.getMysqlExtras());

        return vmParameter;
    }
}