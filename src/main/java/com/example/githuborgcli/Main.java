package com.example.githuborgcli;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHOrganization;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@CommandLine.Command(name = "getRepoStats", mixinStandardHelpOptions = true, version = "version 1.0",
        description = "gets repo popularity stats for a github org")
public class Main implements Callable<Integer> {
    static Logger log = LogManager.getLogger();

    @CommandLine.Option(names = {"-o", "--organization"}, required = true, description = "Organization to get repo stats on")
    String orgName;

    @CommandLine.Option(names = {"-n", "--numberOfResults"}, description = "Number of results to include for stats")
    Integer count = 10;

    @CommandLine.Option(names = "--accessToken", description = "GitHub personal access token ")
    String accessToken;

    @CommandLine.Option(names = "--accessToken:env", description = "GitHub personal access token read from " +
            "System Env variable")
    String accessTokenSysEnv;

    @CommandLine.Option(names = {"-u", "--username"}, description = "GitHub username")
    String username = null;

    @CommandLine.Option(names = {"-p", "--password"}, description = "GitHub password", interactive = true)
    char[] password = null;

    @CommandLine.Option(names = {"-r", "--resultFile"}, description = "File to print results in")
    String resultFile = "logs/output.log";

    @CommandLine.Option(names = {"-t", "--threadPoolSize"}, description = "Thread pool size")
    int threadPoolSize = 25;

    @CommandLine.Option(names = {"-g", "--githubTimeout"}, description = "Github API timeout in seconds")
    int githubTimeout = 100;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {

        StopWatch watch = new StopWatch();
        watch.start();

        int exitCode = new CommandLine(new Main()).execute(args);

        watch.stop();
        long result = watch.getTime(TimeUnit.SECONDS);
        log.debug("Total time for execution: {} seconds", result);

        System.exit(exitCode);

    }

    private GithubClient getGithubClient() throws Exception {
        if (accessToken != null) {
            return new GithubClient(accessToken, threadPoolSize, githubTimeout);
        } else if (accessTokenSysEnv != null) {
            return new GithubClient(System.getenv(accessTokenSysEnv), threadPoolSize, githubTimeout);
        } else if (username != null) {
            if (password == null) {
                throw new CommandLine.ParameterException(spec.commandLine(), "Password required for username authentication");
            }
            return new GithubClient(username, String.valueOf(password), threadPoolSize, githubTimeout);
        }
        else
            return new GithubClient(threadPoolSize, githubTimeout);
    }

    public Integer call() throws Exception {
        AtomicInteger status = new AtomicInteger();

        log.info("Getting stats on " + this.orgName + " for n: " + count);

        GithubClient client = getGithubClient();

        GHOrganization organization = client.getOrganization(orgName);

        if (organization != null) {
            List<Repository> repositories = client.getRepositories(organization);

            if (repositories.size() > 0) {

                Map<String,List<String>> repoStats = new HashMap<>();

                for (RepoStatOrder order : RepoStatOrder.values()) {
                    repoStats.put(order.name(), order.getTopN(repositories, count));
                }

                RepoStatReport.generate(repoStats, orgName, resultFile);

                return status.get();
            } else
                status.getAndSet(-1);
        } else
            status.getAndSet(-1);

        return status.get();
    }
}

