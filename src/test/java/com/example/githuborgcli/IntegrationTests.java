package com.example.githuborgcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class IntegrationTests {

    // Integration test PAT
    static String INTEG_TEST_PAT = "e023ac9abb0b939aadc5a73bd8ac390ab98a0d5a";

    static String INVALID_PAT = "abcd";


    // Integration test org
    static String ORG_NAME = "psm-test";

    // Integration test org
    static String INVALID_ORG_NAME = "1968177137";

    @Test
    void testHappyPath() {
        Assertions.assertEquals(0,
                new CommandLine(new Main()).execute("-a", INTEG_TEST_PAT, "-o", ORG_NAME));
    }

    @Test
    void testInvalidAuth() {
        Assertions.assertNotEquals(0,
                new CommandLine(new Main()).execute("-o", ORG_NAME, "-a", INVALID_PAT));
    }

    @Test
    void testNonExistingOrg() {
        Assertions.assertNotEquals(0,
                new CommandLine(new Main()).execute("-o", INVALID_ORG_NAME, "-a", INTEG_TEST_PAT));
    }

    @Test
    void testNegativeLimitCount() {
        Assertions.assertTrue(
                new CommandLine(new Main()).execute("-o", ORG_NAME, "-a", INTEG_TEST_PAT, "-n", "-2") > 0);
    }

    @Test
    void testLimitCount() {
        Assertions.assertEquals(0,
                new CommandLine(new Main()).execute("-o", ORG_NAME, "-a", INTEG_TEST_PAT, "-n", "200"));
    }
}
