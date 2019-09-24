package io.github.newhoo.common.jr.plugin;

import com.intellij.openapi.project.Project;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.newhoo.util.LogUtils.LOG;

/**
 * JrHelper
 *
 * @author huzunrong
 * @since 1.0
 */
public class JrHelper {

    private static List<CustomJrPlugin> customJrPlugins = new ArrayList<>();

    static {
        customJrPlugins.add(new SpringInvokePlugin());
        customJrPlugins.add(new MySQLExplainPlugin());

    }

    public static Map<String, String> getEnabledPluginVmParameter(Project project) {
        Map<String, String> vmParameter = new HashMap<>();

        Set<String> pluginPath = new HashSet<>(customJrPlugins.size());

        customJrPlugins.forEach(customJrPlugin -> {
            if (customJrPlugin.enabled(project)) {
                pluginPath.add(customJrPlugin.getLocation());
                vmParameter.putAll(customJrPlugin.getVmParameter(project));
            }
        });

        if (!pluginPath.isEmpty()) {
            String collect = String.join(",", pluginPath);
            vmParameter.put("rebel.plugins", collect);
        }

        return vmParameter;
    }

    public static int getDefaultInvokePort() {
        return com.example.plugin.spring.common.Constant.DEFAULT_INVOKE_PORT;
    }

    /**
     * -Drebel.plugins=/path/your/quick-dev-jr-plugin.jar
     */
    static String getJrPluginPath(Class aClass) {
        URL resource = aClass.getResource("");
        if (resource != null && "jar".equals(resource.getProtocol())) {
            String path = resource.getPath();
            try {
                return URLDecoder.decode(path.substring("file:/".length() - 1, path.indexOf("!/")), "UTF-8");
            } catch (Exception e) {
                LOG.error("URLDecoder Exception: " + resource.getPath(), e);
            }
        }
        return "";
    }
}