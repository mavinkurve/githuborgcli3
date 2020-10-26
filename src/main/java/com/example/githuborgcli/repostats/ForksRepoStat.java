package com.example.githuborgcli.repostats;

import com.example.githuborgcli.IRepoStat;
import com.example.githuborgcli.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ForksRepoStat implements IRepoStat {

    List<String> stats;

    public ForksRepoStat() {
        this.stats = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Top Repositories By Forks Count";
    }

    @Override
    public List<String> getStats() {
        return stats;
    }

    @Override
    public void generateStats(List<Repository> repositories, int count) {
        repositories.stream().sorted(new ForkCountComparator()).limit(count).forEach(r -> stats.add(r.getName()));
    }

    public class ForkCountComparator implements Comparator<Repository> {
        //ToDo: Handle null repos after timeout is reached for rogue repo
        @Override
        public int compare(Repository o1, Repository o2) {
            return Integer.compare(o2.getForks(), o1.getForks());
        }
    }
}
