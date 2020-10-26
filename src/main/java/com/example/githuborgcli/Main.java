package com.example.githuborgcli;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@CommandLine.Command(name = "getRepoStats", mixinStandardHelpOptions = true, version = "version 1.0",
        description = "gets repo popularity stats for a github org")
public class Main implements Callable<Integer> {
    static Logger log = LogManager.getLogger();

    @CommandLine.Option(names = {"-o", "--organization"}, required = true, description = "Organization to get repo stats on")
    String orgName;

    @CommandLine.Option(names = {"-n", "--numberOfResults"}, description = "Number of results to include for stats")
    Integer count = 10;

    @CommandLine.ArgGroup(heading = "\nProvide one of these authentication options. " +
            "Personal access token directions: https://github.blog/2013-05-16-personal-api-tokens/ \n")
    Dependent group;
    @CommandLine.Option(names = {"-p", "--password"}, description = "GitHub password", arity = "0..1", interactive = true)
    private char[] password = null;

    public static void main(String[] args) {
        System.out.println(log.getName());

        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public Integer call() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();

        AtomicInteger status = new AtomicInteger();

        log.info("Getting stats on " + this.orgName + " for n: " + count);

        GithubClient client = new GithubClient(group, password);

        List<Repository> repositories = client.getRepositories(orgName);

        RepoStatFactory statFactory = new RepoStatFactory();
        List<IRepoStat> repoStats = new ArrayList<>();

        for (RepoStatType repoStatType : RepoStatType.values()) {
            repoStats.add(statFactory.getRepoStat(repoStatType));
        }
        repoStats.forEach(rs -> {
            try {
                rs.generateStats(repositories, count);
            } catch (Exception ex) {
                log.error("Failed to generate {} stats for organization {}", rs.getName(), orgName, ex);
                status.addAndGet(1);
            }
        });

        RepoStatReport.generate(repoStats, orgName);

        watch.stop();
        long result = watch.getTime(TimeUnit.SECONDS);
        log.debug("Total time for execution: {} seconds", result);

        return status.get();
    }

    static class Dependent {
        @CommandLine.Option(names = {"-a", "--accesstoken"}, description = "GitHub personal access token", required = true)
        String accessToken = null;
        @CommandLine.Option(names = {"-u", "--username"}, description = "GitHub username", required = true)
        String username = null;
    }
}
