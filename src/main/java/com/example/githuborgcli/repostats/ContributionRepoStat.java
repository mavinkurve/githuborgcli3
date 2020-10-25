package com.example.githuborgcli.repostats;

import com.example.githuborgcli.IRepoStat;
import com.example.githuborgcli.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContributionRepoStat implements IRepoStat {

    List<String> stats;

    public ContributionRepoStat() {
        this.stats = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Top Repositories By Contributions";
    }

    @Override
    public List<String> getStats() {
        return stats;
    }

    @Override
    public void generateStats(List<Repository> repositories, int count) {
        repositories.stream().sorted(new ContributionComparator()).limit(count).forEach(r -> stats.add(r.getName()));
    }

    public class ContributionComparator implements Comparator<Repository> {

        @Override
        public int compare(Repository o1, Repository o2) {
            return Double.compare(o2.getContribution(), o1.getContribution());
        }
    }
}