package io.github.newhoo.extension;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import io.github.newhoo.common.jr.plugin.JrHelper;
import io.github.newhoo.setting.PluginProjectSetting;
import io.github.newhoo.util.NotificationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.github.newhoo.util.LogUtils.LOG;

/**
 * MyPreRunChecks
 *
 * @author huzunrong
 * @since 1.0
 */
public class MyPreRunChecks extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (configuration instanceof RunConfiguration) {
            RunConfiguration runConfiguration = (RunConfiguration) configuration;

            LOG.info("检查jr插件启用状态");

            if (!javaParameters.getEnv().isEmpty()) {
                NotificationUtils.infoEcho(runConfiguration.getProject(), "getEnv", javaParameters.getEnv().toString());
            }

            Map<String, String> enabledPluginVmParameter = JrHelper.getEnabledPluginVmParameter(runConfiguration.getProject());
            enabledPluginVmParameter.forEach((k, v) -> {
                javaParameters.getVMParametersList().addNotEmptyProperty(k, v);
            });

            // 增加自定义jvm参数
            String jvmParameter = new PluginProjectSetting(runConfiguration.getProject()).getJvmParameter();
            if (StringUtils.isNotBlank(jvmParameter)) {
                javaParameters.getVMParametersList().addParametersString(jvmParameter);
            }
        }
    }
}