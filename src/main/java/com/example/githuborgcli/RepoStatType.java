package com.example.githuborgcli;

import java.util.*;

import static java.util.stream.Collectors.toList;

public enum RepoStatType {

    TOP_REPOSITORIES_BY_STARS(Comparator.comparing(Repository::getStars, Comparator.nullsLast(Comparator.naturalOrder())).reversed()),
    TOP_REPOSITORIES_BY_FORKS(Comparator.comparing(Repository::getForks, Comparator.nullsLast(Comparator.naturalOrder())).reversed()),
    TOP_REPOSITORIES_BY_PULL_REQUESTS(Comparator.comparing(Repository::getPullRequestCount, Comparator.nullsLast(Comparator.naturalOrder())).reversed()),
    TOP_REQUESTS_BY_CONTRIBUTION(Comparator.comparing(Repository::getContribution, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

    private Comparator<Repository> cmp;

    RepoStatType(Comparator<Repository> c) {
        cmp = c;
    }

    public List<String> getTopN(List<Repository> list, int count) {
        return Optional.ofNullable(list)
                .orElseGet(Collections::emptyList).stream()
                .sorted(cmp)
                .limit(count)
                .map(Repository::getName)
                .filter(Objects::nonNull)
                .collect(toList());
    }

}