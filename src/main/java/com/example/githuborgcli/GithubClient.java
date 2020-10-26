package com.example.githuborgcli;

import me.tongfei.progressbar.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class GithubClient {

    static Logger log = LogManager.getLogger();

    GitHub client;

    GithubClient(Main.Dependent args, char[] password) throws IOException, GithubOrgCliException {
        if (args == null) {
            String accessTokenEnvVar = System.getenv(Constants.GITHUB_ACCESS_TOKEN);

            if (accessTokenEnvVar != null) {
                log.debug("Initializing GitHub client with personal access token {} from sys env variable", Constants.GITHUB_ACCESS_TOKEN);
                client = new GitHubBuilder().withOAuthToken(accessTokenEnvVar).build();
                return;
            }
        }

        assert args != null;
        if (args.accessToken != null) {
            log.debug("Initializing GitHub client with provided personal access token {} ", args.accessToken);
            try {
                client = new GitHubBuilder().withOAuthToken(args.accessToken).build();
            } catch (HttpException ex) {
                log.fatal("Failed to authenticate with GitHub API. Check credentials.",ex);
                return;
            }
        }

        if (args.username != null && password != null) {
            log.debug("Initializing GitHub client with provided username {} ", args.username);
            client = new GitHubBuilder().withPassword(args.username, String.valueOf(password)).build();
        }

        if (client == null)
            throw new GithubOrgCliException(String.format("Could not initialize GitHub client for access token %s, username %s", args.accessToken, args.username));
    }

    List<Repository> getRepositories(String orgName) throws Exception {
        GHOrganization organization;
        try {
            organization = client.getOrganization(orgName);
        } catch (GHFileNotFoundException ex) {
            log.fatal("Could not query {}. Check organization name.", orgName, ex);
            return null;
        }
        log.debug("Getting {} repositories",orgName);
        Map<String, GHRepository> ghRepositoryMap = organization.getRepositories();
        log.debug("Resolved {} repositories", ghRepositoryMap.size());

        GHRateLimit rateLimit = client.getRateLimit();
        log.debug("Currently at {} remaining rate limit",rateLimit.getRemaining());
        if (ghRepositoryMap.size() > rateLimit.getRemaining()) {
            Exception ex = new GithubOrgCliException("GITHUB API RATE LIMIT EXCEEDED. RETRY AT " + rateLimit.getResetDate());
            log.fatal(ex);
            throw ex;
        }
        List<Repository> repositories = new ArrayList<>();

        ProgressBar pb = new ProgressBar("Gathering repository data", ghRepositoryMap.size()).start();
        ExecutorService executor = Executors.newFixedThreadPool(PropertyManager.getAsInteger(Constants.THREAD_POOL_SIZE));
        //ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (final GHRepository ghRepository : ghRepositoryMap.values()) {
            Callable<Boolean> c = () -> {
                boolean b = repositories.add(new Repository(ghRepository));
                pb.step();
                return b;
            };
            tasks.add(c);
        }

        executor.invokeAll(tasks,PropertyManager.getAsInteger(Constants.GITHUB_API_TIMEOUT),TimeUnit.SECONDS);
        pb.stop();

        return repositories;

    }
}
