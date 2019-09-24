package io.github.newhoo.common.jr.plugin;

import com.intellij.openapi.project.Project;

import java.util.Map;

/**
 * CustomJrPlugin
 *
 * @author huzunrong
 * @since 1.0
 */
public interface CustomJrPlugin {

    boolean enabled(Project project);

    String getLocation();

    Map<String, String> getVmParameter(Project project);
}