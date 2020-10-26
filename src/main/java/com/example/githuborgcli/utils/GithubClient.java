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

    GitHub client;

    public GithubClient(AuthArgs args, char[] password) throws IOException {
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

        String accessTokenEnvVar = System.getenv(Constants.GITHUB_ACCESS_TOKEN);

        if (accessTokenEnvVar != null) {
            log.debug("Initializing GitHub client with personal access token {} from sys env variable", Constants.GITHUB_ACCESS_TOKEN);
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

    public List<Repository> getRepositories(String orgName) throws Exception {
        GHOrganization organization;
        try {
            organization = client.getOrganization(orgName);
        } catch (GHFileNotFoundException ex) {
            log.fatal("Could not query {}. Check organization name.", orgName, ex);
            return null;
        }

        List<GHRepository> ghRepositories = listRepositories(organization);

        GHRateLimit rateLimit = client.getRateLimit();
        log.debug("Currently at {} remaining rate limit", rateLimit.getRemaining());
        if (ghRepositories.size() > rateLimit.getRemaining()) {
            throw new GithubOrgCliException("The remaining GitHub API rate limit is not sufficient to process " +
                    "repository data  for " + orgName + ". Please retry later at " + rateLimit.getResetDate());
        }

        List<Repository> repositories = new ArrayList<>();
        ProgressBar repoStatProgress = new ProgressBar("Gathering repository stats", ghRepositories.size()).start();
        ExecutorService executor = Executors.newFixedThreadPool(PropertyManager.getAsInteger(Constants.THREAD_POOL_SIZE));
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (final GHRepository ghRepository : ghRepositories) {
            Callable<Boolean> c = () -> {
                boolean b = repositories.add(new Repository(ghRepository));
                repoStatProgress.step();
                return b;
            };
            tasks.add(c);
        }

        executor.invokeAll(tasks, PropertyManager.getAsInteger(Constants.GITHUB_API_TIMEOUT), TimeUnit.SECONDS);
        repoStatProgress.stop();

        return repositories;

    }
}
