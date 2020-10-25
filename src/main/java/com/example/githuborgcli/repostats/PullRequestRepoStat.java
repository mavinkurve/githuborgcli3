package com.example.githuborgcli.repostats;

import com.example.githuborgcli.IRepoStat;
import com.example.githuborgcli.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PullRequestRepoStat implements IRepoStat {

    List<String> stats;

    public PullRequestRepoStat() {
        this.stats = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Top Repositories By Pull Requests";
    }

    @Override
    public List<String> getStats() {
        return stats;
    }

    @Override
    public void generateStats(List<Repository> repositories, int count) {
        repositories.stream().sorted(new PullRequestComparator()).limit(count).forEach(r -> stats.add(r.getName()));
    }

    public class PullRequestComparator implements Comparator<Repository> {

        @Override
        public int compare(Repository o1, Repository o2) {
            return Double.compare(o2.getPullRequestCount(), o1.getPullRequestCount());
        }
    }
}