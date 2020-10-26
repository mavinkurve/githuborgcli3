package com.example.githuborgcli;

import org.kohsuke.github.GHRepository;

import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    Repository repository = new Repository(new GHRepository());

    static List<IRepoStat> getEmptyRepoStatsList() {
        List<IRepoStat> emptyRepoStats = new ArrayList<>();
        return emptyRepoStats;
    }

    static List<IRepoStat> getFiveValidRepoStats() {
        List<IRepoStat> repoStats = new ArrayList<>();

        return repoStats;
    }

    class ValidRepoStat implements IRepoStat {

        @Override
        public String getName() {
            return "Test Repo Stat";
        }

        @Override
        public void generateStats(List<Repository> repositories, int count) {

        }

        @Override
        public List<String> getStats() {
            ArrayList<String> list = new ArrayList<String>();
            list.add("One");
            list.add("Two");
            list.add("Three");
            return list;
        }
    }

    class InvalidRepoStat_NullName implements IRepoStat {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void generateStats(List<Repository> repositories, int count) {

        }

        @Override
        public List<String> getStats() {
            ArrayList<String> list = new ArrayList<String>();
            list.add("One");
            list.add("Two");
            list.add("Three");
            return list;
        }
    }

    class InvalidRepoStat_NullStats implements IRepoStat {

        @Override
        public String getName() {
            return "Null Stats";
        }

        @Override
        public void generateStats(List<Repository> repositories, int count) {

        }

        @Override
        public List<String> getStats() {
            return null;
        }
    }
}
