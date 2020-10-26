package com.example.githuborgcli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;

import java.io.IOException;

public class Repository {

    static Logger log = (Logger) LogManager.getLogger();
    int pullRequestCount;
    Double contribution;
    private GHRepository ghRepository;

    public Repository(GHRepository ghRepo) {

        ghRepository = ghRepo;

        int forks = ghRepo.getForksCount();

        try {
            pullRequestCount = forks > 0 ? ghRepo.getPullRequests(GHIssueState.OPEN).size() : 0;
        } catch (IOException e) {
            log.error("Failed to fetch pull requests for {}", ghRepo.getName(), e);

            pullRequestCount = 0;
        }
        contribution = (pullRequestCount > 0.0) ? pullRequestCount / forks : 0.0;
    }

    public Double getStars() {
        return (double) ghRepository.getStargazersCount();
    }

    public int getPullRequestCount() {
        return pullRequestCount;
    }

    public Double getContribution() {
        return contribution;
    }

    public String getName() {
        return ghRepository.getName();
    }

    public int getForks() {
        return ghRepository.getForksCount();
    }

}
