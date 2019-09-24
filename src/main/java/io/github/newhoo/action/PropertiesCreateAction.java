package io.github.newhoo.action;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import io.github.newhoo.util.AppUtils;
import io.github.newhoo.util.NotificationUtils;
import io.github.newhoo.util.StrUtils;

import static io.github.newhoo.common.AppConstant.PROPERTIES_FILENAME;

/**
 * PropertiesCreateAction
 *
 * @author huzunrong
 * @since 1.0
 */
public class PropertiesCreateAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null || project.isDefault()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        e.getPresentation().setEnabled(findPropertiesFile(project) == null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null || project.isDefault()) {
            return;
        }

        VirtualFile devPropertiesFile = findPropertiesFile(project);
        if (devPropertiesFile != null) {
            NotificationUtils.errorBalloon(project, PROPERTIES_FILENAME + " 已存在", "");
            return;
        }

        ApplicationManager.getApplication().runWriteAction(() -> {
            VirtualFile propertiesDir = findPropertiesDir(project);
            if (propertiesDir == null) {
                NotificationUtils.errorBalloon(project, "resources文件夹不存在", "");
                return;
            }
            String propertiesTemplate = AppUtils.getPropertiesTemplate();
            String content = StrUtils.unicodeToString(StringUtil.convertLineSeparators(propertiesTemplate));
            PsiFile devPropertiesPsiFile = PsiFileFactory.getInstance(project).createFileFromText(
                    PROPERTIES_FILENAME, Language.findLanguageByID("Properties"), content);

            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(propertiesDir);
            if (directory == null) {
                NotificationUtils.errorBalloon(project, "resources文件夹不存在", "");
                return;
            }
            directory.add(devPropertiesPsiFile);

            FileEditorManager.getInstance(project).openFile(findPropertiesFile((project)), true);
        });
    }

    private VirtualFile findPropertiesFile(Project project) {
        return AppUtils.findProjectFile(project, PROPERTIES_FILENAME, "resources");
    }

    private VirtualFile findPropertiesDir(Project project) {
        VirtualFile projectBaseDir = project.getBaseDir();

        VirtualFile resourcesDir = projectBaseDir.findFileByRelativePath("src/main/resources");
        if (resourcesDir != null && resourcesDir.exists()) {
            return resourcesDir;
        }

        for (VirtualFile child : projectBaseDir.getChildren()) {
            VirtualFile childResourcesDir = child.findFileByRelativePath("src/main/resources");
            if (childResourcesDir != null && childResourcesDir.exists()) {
                return childResourcesDir;
            }
        }
        return null;
    }
}