package com.example.githuborgcli;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface IRepoStat {

    String getName();

    void generateStats(List<Repository> repositories, int count);

    List<String> getStats();
}
