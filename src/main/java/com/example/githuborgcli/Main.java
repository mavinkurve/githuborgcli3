package com.example.githuborgcli;

import com.example.githuborgcli.repostats.RepoStatType;
import com.example.githuborgcli.utils.AuthArgs;
import com.example.githuborgcli.utils.GithubClient;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHOrganization;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
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

    @CommandLine.ArgGroup(heading = "\nProvide one of these authentication options.\n")
    AuthArgs group;

    @CommandLine.Option(names = {"-r", "--resultFile"}, description = "File to print results in")
    String resultFile = "logs/output.log";

    @CommandLine.Option(names = {"-t", "--threadPoolSize"}, description = "Thread pool size")
    int threadPoolSize = 25;

    @CommandLine.Option(names = {"-g", "--githubTimeout"}, description = "Github API timeout in seconds")
    int githubTimeout = 100;

    @CommandLine.Option(names = {"-p", "--password"}, description = "GitHub password", arity = "0..1", interactive = true)
    private char[] password = null;

    public static void main(String[] args) {
        StopWatch watch = new StopWatch();
        watch.start();

        int exitCode = new CommandLine(new Main()).execute(args);

        watch.stop();
        long result = watch.getTime(TimeUnit.SECONDS);
        log.debug("Total time for execution: {} seconds", result);

        System.exit(exitCode);
    }

    public Integer call() throws Exception {
        AtomicInteger status = new AtomicInteger();

        log.info("Getting stats on " + this.orgName + " for n: " + count);

        GithubClient client = new GithubClient(group, password, threadPoolSize, githubTimeout);

        GHOrganization organization = client.getOrganization(orgName);

        if (organization != null) {
            List<Repository> repositories = client.getRepositories(organization);

            if (repositories.size() > 0) {
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

                RepoStatReport.generate(repoStats, orgName, resultFile);

                return status.get();
            } else
                status.getAndSet(-1);
        } else
            status.getAndSet(-1);

        return status.get();
    }
}
