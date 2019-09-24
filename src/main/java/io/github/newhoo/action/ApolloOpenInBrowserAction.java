package io.github.newhoo.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import io.github.newhoo.setting.PluginGlobalSetting;
import io.github.newhoo.util.AppUtils;
import io.github.newhoo.util.NotificationUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * ApolloOpenInBrowserAction
 *
 * @author huzunrong
 * @since 1.0
 */
public class ApolloOpenInBrowserAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null || project.isDefault()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setEnabledAndVisible(StringUtils.isNotBlank(findApolloAppId(project)));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null || project.isDefault()) {
            return;
        }

        String appId = findApolloAppId(project);
        if (StringUtils.isEmpty(appId)) {
            NotificationUtils.errorBalloon(project, "Cannot Find Apollo AppId", "");
            return;
        }

        String apolloBaseUrl = PluginGlobalSetting.getInstance().getApolloBaseUrl();
        if (StringUtils.isEmpty(apolloBaseUrl)) {
            NotificationUtils.errorBalloon(project, "Apollo Host not Configured", "");
            return;
        }

        String apolloUrl = apolloBaseUrl + "/config.html?#/appid=" + appId;
        if (!apolloUrl.startsWith("http")) {
            apolloUrl = "http://" + apolloUrl;
        }
        AppUtils.copyToClipboard(project, apolloUrl);
        BrowserUtil.browse(apolloUrl);
    }

    private String findApolloAppId(Project project) {
        return AppUtils.findProjectProperty(project, "app.id", "app.properties", "META-INF");
    }
}