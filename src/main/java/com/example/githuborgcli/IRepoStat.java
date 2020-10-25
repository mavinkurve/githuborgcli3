package com.example.githuborgcli;

import java.util.List;

public interface IRepoStat {

    String getName();

    void generateStats(List<Repository> repositories, int count);

    List<String> getStats();
}
