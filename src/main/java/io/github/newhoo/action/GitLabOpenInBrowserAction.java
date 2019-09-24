package io.github.newhoo.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import io.github.newhoo.util.AppUtils;
import io.github.newhoo.util.GitlabUtils;
import io.github.newhoo.util.NotificationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static io.github.newhoo.common.AppConstant.CANNOT_OPEN_IN_BROWSER;

/**
 * GitLabOpenInBrowserAction
 *
 * @author huzunrong
 * @since 1.0
 */
public class GitLabOpenInBrowserAction extends DumbAwareAction {

    @Override
    public void update(final AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (project == null || project.isDefault()) {
            setVisibleEnabled(e, false, false);
            return;
        }

        final Collection<GitRepository> repositories = GitUtil.getRepositories(project);
        // 找不到git仓库信息
        if (repositories.isEmpty()) {
            setVisibleEnabled(e, false, false);
            return;
        }

        // 未选择文件，取git仓库地址
        if (virtualFile == null) {
            setVisibleEnabled(e, true, true);
            return;
        }

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        if (changeListManager.isUnversioned(virtualFile)) {
            setVisibleEnabled(e, true, false);
            return;
        }

        Change change = changeListManager.getChange(virtualFile);
        if (change != null && change.getType() == Change.Type.NEW) {
            setVisibleEnabled(e, true, false);
            return;
        }
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        final VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (project == null || project.isDisposed()) {
            return;
        }

        GitRepository repository;

        // 未选择文件，取第一个git仓库地址
        if (virtualFile == null) {
            Optional<GitRepository> firstRepo = GitUtil.getRepositories(project).stream().findFirst();
            if (!firstRepo.isPresent()) {
                return;
            }
            repository = firstRepo.get();
        } else {
            GitRepositoryManager manager = GitUtil.getRepositoryManager(project);
            repository = manager.getRepositoryForFile(virtualFile);
            if (repository == null) {
                StringBuilder details = new StringBuilder("file: " + virtualFile.getPresentableUrl() + "; Git repositories: ");
                for (GitRepository repo : manager.getRepositories()) {
                    details.append(repo.getPresentableUrl()).append("; ");
                }
                NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER, "Can't find git repository: " + details.toString());
                return;
            }
        }

        final String rootPath = repository.getRoot().getPath();
        final String path = virtualFile != null ? virtualFile.getPath() : rootPath;

        List<AnAction> remoteSelectedActions = new ArrayList<>();

        for (GitRemote remote : repository.getRemotes()) {
            remoteSelectedActions.add(new RemoteSelectedAction(project, repository, editor, remote, rootPath, path));
        }

        if (remoteSelectedActions.size() > 1) {
            DefaultActionGroup remotesActionGroup = new DefaultActionGroup();
            remotesActionGroup.addAll(remoteSelectedActions);
            DataContext dataContext = e.getDataContext();
            final ListPopup popup = JBPopupFactory.getInstance()
                                                  .createActionGroupPopup(
                                                          "Select remote",
                                                          remotesActionGroup,
                                                          dataContext,
                                                          JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                                          true);

            popup.showInBestPositionFor(dataContext);
        } else if (remoteSelectedActions.size() == 1) {
            remoteSelectedActions.get(0).actionPerformed(null);
        } else {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER, "Can't find gitlab remote");
        }
    }

    private void setVisibleEnabled(AnActionEvent e, boolean visible, boolean enabled) {
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(enabled);
    }
}

class RemoteSelectedAction extends AnAction {

    private final Editor editor;

    private final GitRemote remote;

    private final String rootPath;

    private final String path;

    private final Project project;

    private final GitRepository repository;

    public RemoteSelectedAction(@NotNull Project project, @NotNull GitRepository repository, @Nullable Editor editor,
                                @NotNull GitRemote remote, @NotNull String rootPath, @NotNull String path) {
        super(remote.getName());
        this.project = project;
        this.repository = repository;
        this.editor = editor;
        this.remote = remote;
        this.rootPath = rootPath;
        this.path = path;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        setEnabledInModalContext(true);
        e.getPresentation().setEnabled(true);
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent anActionEvent) {
        if (!path.startsWith(rootPath)) {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER,
                    "File is not under repository root: " + rootPath + ", file: " + path);
            return;
        }

        String branch = GitlabUtils.getBranchNameOnRemote(project, repository);
        if (branch == null) {
            return;
        }

        String remoteUrl = remote.getFirstUrl();

        if (remoteUrl == null) {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER, "Can't obtain url for remote: " + remote.getName());
            return;
        }

        String relativePath = path.substring(rootPath.length());
        String urlToOpen = GitlabUtils.makeUrlToOpen(editor, relativePath, branch, remoteUrl);
        if (urlToOpen == null) {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER, "Can't create properly url: " + remote.getFirstUrl());
            return;
        }

        AppUtils.copyToClipboard(project, urlToOpen);
        BrowserUtil.browse(urlToOpen);
    }
}
