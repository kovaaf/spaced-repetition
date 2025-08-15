package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {
    private final AppProperties appProperties;
    private final UsernamePasswordCredentialsProvider credentials;

    public Git getGitInstanceWithHardReset() throws GitAPIException {
        Git git = getGitInstance();
        resetToOriginBranch(git);
        return git;
    }

    private Git getGitInstance() throws GitAPIException {
        Path repoPath = Paths.get(appProperties.getDefaultDeck()
                        .getRepo()
                        .getPath())
                .toAbsolutePath();
        File repoDir = repoPath.toFile();
        String repoUrl = appProperties.getDefaultDeck()
                .getRepo()
                .getUrl();

        if (!repoDir.exists()) {
            return cloneRepository(repoUrl, repoDir);
        }

        // Попытка открыть репозиторий с обработкой возможных ошибок
        try {
            Git git = Git.open(repoDir);
            git.fetch()
                    .setCredentialsProvider(credentials)
                    .setRemoveDeletedRefs(true)
                    .call();
            return git;
        } catch (RepositoryNotFoundException e) {
            log.warn("Repository not found or invalid, recreating: {}", repoDir);
        } catch (Exception e) {
            log.error("Error opening existing repository, recreating: {}", repoDir, e);
        }

        // Удаляем поврежденную директорию и клонируем заново
        deleteDirectoryRecursively(repoDir.toPath());
        return cloneRepository(repoUrl, repoDir);
    }

    private Git cloneRepository(String repoUrl, File repoDir) throws GitAPIException {
        log.info("Cloning repository: {}", repoUrl);
        return Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(repoDir)
                .setCredentialsProvider(credentials)
                .call();
    }

    private void deleteDirectoryRecursively(Path path) {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.error("Failed to delete: {}", p, e);
                            }
                        });
            } catch (IOException e) {
                log.error("Failed to delete directory: {}", path, e);
            }
        }
    }

    private void resetToOriginBranch(Git git) throws GitAPIException {
        String branch = appProperties.getDefaultDeck()
                .getRepo()
                .getBranch();
        git.reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef("origin/" + branch)
                .call();
    }

    public String getLatestCommit(Git git) throws IOException {
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            ObjectId head = git.getRepository()
                    .resolve(Constants.HEAD);
            return walk.parseCommit(head)
                    .getName();
        }
    }
}
