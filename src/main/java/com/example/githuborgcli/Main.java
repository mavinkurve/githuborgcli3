package com.example.githuborgcli;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GitHub;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "getRepoStats", mixinStandardHelpOptions = true, version = "version 1.0",
        description = "gets repo popularity stats for a github org")
public class Main implements Callable<Integer> {
    static Logger log = LogManager.getLogger();

    @CommandLine.Option(names = {"-o", "--organization"}, required = true, description = "Organization to get repo stats on")
    private String orgName;

    @CommandLine.Option(names = {"-n", "--numberOfResults"}, description = "Number of results to include for stats")
    private Integer count = 10;

    @CommandLine.Option(names = {"-u", "--username"}, description = "GitHub username")
    private String username = null;

    @CommandLine.Option(names = {"-a", "--accesstoken"}, description = "GitHub personal access token")
    private String accessToken = null;

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

        log.info("Getting stats on " + this.orgName + " for n: " + count);

        GitHub client = GithubClient.getClient(accessToken, username, password);

        if (client == null) {
            return -1;
        }

        GHOrganization organization = client.getOrganization(orgName);
        List<Repository> repositories = new ArrayList<>();
        ProgressBar pb = new ProgressBar("Gather repository information", organization.getRepositories().size()).start(); // name, initial max

        organization.getRepositories().values().parallelStream().forEach(r -> {
            repositories.add(new Repository(r));
            pb.step(); // step by 1
        });

        pb.stop(); // stops the progress bar

        log.debug("Gathered {} repositories", repositories.size());

        log.debug("Initializing stat factory");
        RepoStatFactory statFactory = new RepoStatFactory();
        List<IRepoStat> repoStats = new ArrayList<>();

        for (RepoStatType repoStatType : RepoStatType.values()) {
            repoStats.add(statFactory.getRepoStat(repoStatType));
        }
        repoStats.forEach(rs -> rs.generateStats(repositories, count));

        RepoStatReport.generate(repoStats, orgName);

        watch.stop();
        long result = watch.getTime(TimeUnit.SECONDS);
        log.info("Total time for execution: {} seconds", result);

        return 0;
    }
}
