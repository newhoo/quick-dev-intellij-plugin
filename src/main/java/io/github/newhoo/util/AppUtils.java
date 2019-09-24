package io.github.newhoo.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import io.github.newhoo.common.jr.plugin.JrHelper;
import io.github.newhoo.setting.PluginProjectSetting;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static io.github.newhoo.util.LogUtils.LOG;

/**
 * AppUtils
 *
 * @author huzunrong
 * @since 1.0
 */
public final class AppUtils {

    public static void copyToClipboard(Project project, String content) {
        if (StringUtils.isNoneEmpty(content)) {
            CopyPasteManager.getInstance().setContents(new StringSelection(content));
        }
    }

    public static PsiMethod getPositionMethod(AnActionEvent anActionEvent) {
        PsiFile psiFile = anActionEvent.getData(PSI_FILE);
        if (!(psiFile instanceof PsiJavaFileImpl)) {
            return null;
        }

        Editor editor = anActionEvent.getData(EDITOR);

        PsiJavaFile psiJavaFile = (PsiJavaFileImpl) psiFile;
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                if (editor != null && psiMethod.getTextRange().containsOffset(editor.getCaretModel().getOffset())) {
                    return psiMethod;
                }
            }
        }
        return null;
    }

    /**
     * 查找类
     *
     * @param typeCanonicalText 参数类型全限定名称
     * @param project 当前project
     * @return 查找到的类
     */
    public static PsiClass findPsiClass(String typeCanonicalText, Project project) {
        String className = typeCanonicalText;
        if (className.contains("[]")) {
            className = className.replaceAll("\\[]", "");
        }
        if (className.contains("<")) {
            className = className.substring(0, className.indexOf("<"));
        }
        if (className.lastIndexOf(".") > 0) {
            className = className.substring(className.lastIndexOf(".") + 1);
        }
        PsiClass[] classesByName = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : classesByName) {
            if (typeCanonicalText.startsWith(psiClass.getQualifiedName())) {
                return psiClass;
            }
        }
        return null;
    }

    /**
     * 判断是否为Spring应用
     */
    public static boolean isSpringApp(Project project) {
        return null != AppUtils.findPsiClass("org.springframework.context.support.AbstractApplicationContext", project);
    }

    public static VirtualFile findProjectFile(Project project, @NotNull String fileName, String parentName) {
        PsiFile[] filesByName = PsiShortNamesCache.getInstance(project).getFilesByName(fileName);
        if (ArrayUtils.isEmpty(filesByName)) {
            return null;
        }
        if (StringUtils.isEmpty(parentName)) {
            return filesByName[0].getVirtualFile();
        }
        for (PsiFile psiFile : filesByName) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null && virtualFile.getParent() != null && parentName.equals(virtualFile.getParent().getName())) {
                return virtualFile;
            }
        }
        return null;
    }

    public static String findProjectProperty(Project project, String key, @NotNull String fileName, String parentName) {
        VirtualFile propertiesFile = findProjectFile(project, fileName, parentName);
        if (propertiesFile != null) {

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(propertiesFile.getInputStream(), Charset.forName("UTF-8")))) {

                Optional<String> first = br.lines()
                                           .filter(line -> !line.startsWith("#"))
                                           .filter(line -> line.startsWith(key + "=") || line.startsWith(key + " ="))
                                           .findFirst();
                if (first.isPresent()) {
                    String[] split = StringUtils.split(first.get(), '=');
                    if (ArrayUtils.isNotEmpty(split) && split.length > 1 && StringUtils.isNoneEmpty(split[1])) {
                        return split[1];
                    }
                }
            } catch (IOException ex) {
                NotificationUtils.errorBalloon(project, String.format("Cannot Find Property %s in %s", key, fileName), ex);
            }
        }
        return null;
    }

    public static String getPropertiesTemplate() {
        try {
            InputStream inputStream = AppUtils.class.getResourceAsStream("/quick-dev_template.properties");
            if (inputStream != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    return br.lines().collect(Collectors.joining("\n"));
                }
            }
        } catch (Exception e) {
            LOG.error("获取配置模版文件异常", e);
        }
        return "";
    }

    public static Integer findAvailablePort(Project project) {
        PluginProjectSetting pluginProjectSetting = new PluginProjectSetting(project);

        for (int i = 0; i < 100; i++) {
            int availablePort = JrHelper.getDefaultInvokePort() + i;
            if (!isPortUsing(availablePort)) {
                pluginProjectSetting.setSpringInvokePort(availablePort);
                return availablePort;
            }
        }
        NotificationUtils.warnBalloon(project, "没有找到可用端口，请手动设置", "");
        return null;
    }

    private static boolean isPortUsing(int port) {
        boolean flag = false;
        try {
            Socket socket = new Socket("127.0.0.1", port);
            flag = true;
        } catch (IOException ignored) {
        }
        return flag;
    }
}