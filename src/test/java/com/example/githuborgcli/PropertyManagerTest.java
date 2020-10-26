package com.example.githuborgcli;

import com.example.githuborgcli.utils.PropertyManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PropertyManagerTest {

    @Test
    void get() {
        String non_existing_key = PropertyManager.get("non existing key");
        Assertions.assertNull(non_existing_key, "Key for non existing value is null");
    }
}