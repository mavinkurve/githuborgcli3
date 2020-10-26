package com.example.githuborgcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class MainTest {

    // Integration test PAT
    static String INTEG_TEST_PAT = "e023ac9abb0b939aadc5a73bd8ac390ab98a0d5a";

    @Test
    void testOrgName() {
        Assertions.assertEquals(2,
                new CommandLine(new Main()).execute("-a", INTEG_TEST_PAT));
    }
}