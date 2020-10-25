package com.example.githuborgcli.repostats;

import com.example.githuborgcli.IRepoStat;
import com.example.githuborgcli.Repository;

import java.util.*;

public class ContributionRepoStat  implements IRepoStat {

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

    List<String> stats;

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