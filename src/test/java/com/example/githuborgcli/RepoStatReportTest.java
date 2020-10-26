package com.example.githuborgcli;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class RepoStatReportTest {

    @Test
    void generate() throws IOException {
        RepoStatReport.generate(TestDataGenerator.getEmptyRepoStatsList(), "OrgName");
    }
}