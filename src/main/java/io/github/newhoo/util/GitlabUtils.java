package io.github.newhoo.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import git4idea.GitLocalBranch;
import git4idea.GitRemoteBranch;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.newhoo.common.AppConstant.CANNOT_OPEN_IN_BROWSER;

/**
 * GitlabUtils
 *
 * @author huzunrong
 * @since 1.0
 */
public class GitlabUtils {

    private static String makeRepoUrlFromRemoteUrl(@NotNull String remoteUrl) {
        String cleanedFromDotGit = StringUtil.trimEnd(remoteUrl, ".git");

        if (remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://")) {
            return cleanedFromDotGit;
        } else if (remoteUrl.startsWith("git@")) {
            String cleanedFromGitAt = StringUtil.trimStart(cleanedFromDotGit, "git@");

            return "http://" + StringUtil.replace(cleanedFromGitAt, ":", "/");
        } else {
            throw new IllegalStateException("Invalid remote Gitlab url: " + remoteUrl);
        }
    }

    @Nullable
    public static String makeUrlToOpen(@Nullable Editor editor,
                                       @NotNull String relativePath,
                                       @NotNull String branch,
                                       @NotNull String remoteUrl) {
        final StringBuilder builder = new StringBuilder();
        final String repoUrl = makeRepoUrlFromRemoteUrl(remoteUrl);
        if (repoUrl == null) {
            return null;
        }
        builder.append(repoUrl).append("/blob/").append(branch).append(relativePath);

        if (editor != null && editor.getDocument().getLineCount() >= 1) {
            // lines are counted internally from 0, but from 1 on gitlab
            SelectionModel selectionModel = editor.getSelectionModel();
            final int begin = editor.getDocument().getLineNumber(selectionModel.getSelectionStart()) + 1;
            final int selectionEnd = selectionModel.getSelectionEnd();
            int end = editor.getDocument().getLineNumber(selectionEnd) + 1;
            if (editor.getDocument().getLineStartOffset(end - 1) == selectionEnd) {
                end -= 1;
            }
            builder.append("#L").append(begin).append('-').append(end);
        }

        return builder.toString();
    }

    @Nullable
    public static String getBranchNameOnRemote(@NotNull Project project, @NotNull GitRepository repository) {
        GitLocalBranch currentBranch = repository.getCurrentBranch();
        if (currentBranch == null) {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER,
                    "Can't open the file on GitLab when repository is on detached HEAD. Please checkout a branch.");
            return null;
        }

        GitRemoteBranch tracked = currentBranch.findTrackedBranch(repository);
        if (tracked == null) {
            NotificationUtils.errorBalloon(project, CANNOT_OPEN_IN_BROWSER,
                    "Can't open the file on GitLab when current branch doesn't have a tracked branch." +
                            "Current branch: " + currentBranch + ", tracked info: " + repository.getBranchTrackInfos());
            return null;
        }

        return tracked.getNameForRemoteOperations();
    }
}
