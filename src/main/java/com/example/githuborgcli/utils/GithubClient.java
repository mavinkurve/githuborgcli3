package com.example.githuborgcli.utils;

import com.example.githuborgcli.Repository;
import me.tongfei.progressbar.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GithubClient {

    static Logger log = LogManager.getLogger();

    static int PAGE_SIZE = 100;

    static String GITHUB_ACCESS_TOKEN = "GITHUB_ACCESS_TOKEN";

    GitHub client;

    int githubTimeout;

    int threadPoolSize;

    public GithubClient(AuthArgs args, char[] password, int threadPoolSize, int githubTimeout) throws IOException {
        this.threadPoolSize = threadPoolSize;
        this.githubTimeout = githubTimeout;

        if (args != null) {
            if (args.accessToken != null) {
                log.debug("Initializing GitHub client with provided personal access token {} ", args.accessToken);
                try {
                    client = new GitHubBuilder().withOAuthToken(args.accessToken).build();
                    return;
                } catch (HttpException ex) {
                    log.fatal("Failed to authenticate with GitHub API. Check credentials.", ex);
                    return;
                }
            }

            if (args.username != null && password != null) {
                log.debug("Initializing GitHub client with provided username {} ", args.username);
                client = new GitHubBuilder().withPassword(args.username, String.valueOf(password)).build();
                return;
            }
        }

        String accessTokenEnvVar = System.getenv(GITHUB_ACCESS_TOKEN);

        if (accessTokenEnvVar != null) {
            log.debug("Initializing GitHub client with personal access token {} from sys env variable",
                    GITHUB_ACCESS_TOKEN);
            client = new GitHubBuilder().withOAuthToken(accessTokenEnvVar).build();
            return;
        }

        log.warn("GENERATING UNAUTHENTICATED GITHUB CLIENT WITH LOWER RATE LIMIT");
        client = new GitHubBuilder().build();
        return;
    }

    private List<GHRepository> listRepositories(GHOrganization organization) throws IOException {
        log.debug("Getting {} repositories", organization.getName());
        PagedIterator repositoryPagedIterator = organization.listRepositories(PAGE_SIZE).iterator();
        List<GHRepository> ghRepositories = new ArrayList<>();
        repositoryPagedIterator.forEachRemaining
                (r -> ghRepositories.add((GHRepository) r));
        log.debug("Resolved {} repositories", ghRepositories.size());
        return ghRepositories;
    }

    public GHOrganization getOrganization(String orgName) {
        try {
            return client.getOrganization(orgName);
        } catch (GHFileNotFoundException ex) {
            log.fatal("Could not query \"{}\". Check organization name.", orgName, ex);
        } catch (IOException e) {
            log.fatal("Could not query \"{}\". Check organization name.", orgName, e);
        }
        return null;
    }

    public List<Repository> getRepositories(GHOrganization organization) throws Exception {
        log.info("Gathering data for \"{}\" organization", organization.getName());
        List<GHRepository> ghRepositories = listRepositories(organization);

        GHRateLimit rateLimit = client.getRateLimit();
        log.debug("Currently at {} remaining rate limit", rateLimit.getRemaining());
        if (ghRepositories.size() > rateLimit.getRemaining()) {
            throw new GithubOrgCliException("The remaining GitHub API rate limit is not sufficient to process " +
                    "repository data  for " + organization.getName() + ". Please retry later at " + rateLimit.getResetDate());
        }

        List<Repository> repositories = new ArrayList<>();
        ProgressBar repoStatProgress = new ProgressBar(String.format("Gathering stats for %s's %d repositories",
                organization.getName(), ghRepositories.size()), ghRepositories.size()).start();
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (final GHRepository ghRepository : ghRepositories) {
            Callable<Void> c = () -> {
                repositories.add(new Repository(ghRepository));
                repoStatProgress.step();
                return null;
            };
            tasks.add(c);
        }

        executor.invokeAll(tasks, githubTimeout, TimeUnit.SECONDS);
        repoStatProgress.stop();

        return repositories;

    }
}