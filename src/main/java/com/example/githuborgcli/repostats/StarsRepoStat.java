package com.example.githuborgcli.repostats;

import com.example.githuborgcli.IRepoStat;
import com.example.githuborgcli.Repository;

import java.util.*;

public class StarsRepoStat implements IRepoStat {

    public StarsRepoStat() {
        this.stats = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Top Repositories By Star Gazer Count";
    }

    @Override
    public List<String> getStats() {
        return stats;
    }

    List<String> stats;

    @Override
    public void generateStats(List<Repository> repositories, int count) {
        repositories.stream().sorted(new StarCountComparator()).limit(count).forEach(r -> stats.add(r.getName()));
    }

    public class StarCountComparator implements Comparator<Repository> {

        @Override
        public int compare(Repository o1, Repository o2) {
            return Double.compare(o2.getStars(), o1.getStars());
        }
    }
}
