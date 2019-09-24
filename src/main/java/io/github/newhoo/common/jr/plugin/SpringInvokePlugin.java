package io.github.newhoo.common.jr.plugin;

import com.example.plugin.spring.common.Constant;
import com.intellij.openapi.project.Project;
import io.github.newhoo.setting.PluginProjectSetting;
import io.github.newhoo.util.AppUtils;

import java.util.HashMap;
import java.util.Map;

import static io.github.newhoo.util.AppUtils.findAvailablePort;

/**
 * SpringInvokePlugin
 *
 * @author huzunrong
 * @since 1.0
 */
public class SpringInvokePlugin implements CustomJrPlugin {

    @Override
    public boolean enabled(Project project) {
        return new PluginProjectSetting(project).getEnableQuickInvoke()
                && AppUtils.findPsiClass("org.springframework.context.support.AbstractApplicationContext", project) != null;
    }

    @Override
    public String getLocation() {
        return JrHelper.getJrPluginPath(com.example.plugin.spring.common.Constant.class);
    }

    @Override
    public Map<String, String> getVmParameter(Project project) {
        Map<String, String> vmParameter = new HashMap<>();
        vmParameter.put(Constant.PROPERTIES_KEY_INVOKE_PORT, String.valueOf(findAvailablePort(project)));

        return vmParameter;
    }
}