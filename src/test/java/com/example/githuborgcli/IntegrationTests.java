package com.example.githuborgcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import picocli.CommandLine;

public class IntegrationTests {

    // Integration test PAT
    static String INTEG_TEST_PAT = System.getenv("GITHUB_ACCESS_TOKEN");

    static String INVALID_PAT = "abcd";

    // Integration test org
    static String ORG_NAME = "psm-test";

    // Integration test org
    static String INVALID_ORG_NAME = "1968177137";

    @Test
    @EnabledIfEnvironmentVariable(named = "GITHUB_ACCESS_TOKEN", matches = "^(?!\\s*$).+")
    void testHappyPath() {
        Assertions.assertEquals(0,
                new CommandLine(new Main()).execute("--accessToken", INTEG_TEST_PAT, "-o", ORG_NAME));
    }

    @Test
    void testInvalidAuth() {
        Assertions.assertNotEquals(0,
                new CommandLine(new Main()).execute("-o", ORG_NAME, "--accessToken", INVALID_PAT));
    }

    @Test
    void testNonExistingOrg() {
        Assertions.assertNotEquals(0,
                new CommandLine(new Main()).execute("-o", INVALID_ORG_NAME, "--accessToken", INTEG_TEST_PAT));
    }

    @Test
    void testNegativeLimitCount() {
        Assertions.assertTrue(
                new CommandLine(new Main()).execute("-o", ORG_NAME, "--accessToken", INTEG_TEST_PAT, "-n", "-2") > 0);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GITHUB_ACCESS_TOKEN", matches = "^(?!\\s*$).+")
    void testLimitCount() {
        assert (INTEG_TEST_PAT != null);
        Assertions.assertEquals(0,
                new CommandLine(new Main()).execute("-o", ORG_NAME, "--accessToken", INTEG_TEST_PAT, "-n", "200"));
    }

    @Test
    void testOrgName() {
        Assertions.assertEquals(2,
                new CommandLine(new Main()).execute());
    }
}
