package com.example.githuborgcli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepoStatFactoryTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getRepoStat() {
        RepoStatFactory factory = new RepoStatFactory();
        IRepoStat repoStat = factory.getRepoStat(null);
        Assertions.assertNull(repoStat, "Factory instance cannot be created for null type");
    }
}