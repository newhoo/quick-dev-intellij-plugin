//package io.github.newhoo.setting;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.CommonDataKeys;
//import com.intellij.openapi.project.Project;
//import io.github.newhoo.ui.AppSettingDialog;
//import org.jetbrains.annotations.NotNull;
//
///**
// * 项目配置
// *
// * @author huzunrong
// * @since 1.0
// */
//public class PluginSettingAction extends AnAction {
//
//    @Override
//    public void update(AnActionEvent event) {
//        final Project project = event.getData(CommonDataKeys.PROJECT);
//        event.getPresentation().setEnabledAndVisible(project != null && !project.isDefault());
//    }
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent event) {
//        final Project project = event.getData(CommonDataKeys.PROJECT);
//        if (project == null || project.isDefault()) {
//            return;
//        }
//
//        final AppSettingDialog dialog = new AppSettingDialog(project);
//        if (!dialog.showAndGet()) {
//            return;
//        }
//    }
//}
