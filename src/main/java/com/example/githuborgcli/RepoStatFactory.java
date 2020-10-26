package com.example.githuborgcli;

import com.example.githuborgcli.repostats.*;

class RepoStatFactory {

    IRepoStat getRepoStat(RepoStatType repoStatType) {
        if (repoStatType == null)
            return null;

        if (RepoStatType.STAR_GAZER_COUNT == repoStatType)
            return new StarsRepoStat();

        if (RepoStatType.FORKS == repoStatType)
            return new ForksRepoStat();

        if (RepoStatType.PULL_REQUESTS == repoStatType)
            return new PullRequestRepoStat();

        if (RepoStatType.CONTRIBUTION == repoStatType)
            return new ContributionRepoStat();

        return null;
    }
}
