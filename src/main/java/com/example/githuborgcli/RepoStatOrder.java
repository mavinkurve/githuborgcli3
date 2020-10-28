package com.example.githuborgcli;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum RepoStatOrder {

    STAR_GAZER_COUNT(Comparator.comparing(Repository::getStars, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()),
    FORKS(Comparator.comparing(Repository::getForks, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()),
    PULL_REQUESTS(Comparator.comparing(Repository::getPullRequestCount, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()),
    CONTRIBUTION(Comparator.comparing(Repository::getContribution, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

    private Comparator<Repository> cmp;

    RepoStatOrder(Comparator<Repository> c) {
        cmp = c;
    }

    public List<String> getTopN(List<Repository> list, int count) {
        return list.stream()
                .sorted(cmp)
                .limit(count)
                .map(Repository::getName)
                .collect(toList());
    }

}