package com.example.githuborgcli.utils;


import org.apache.logging.log4j.LogManager;

class GithubOrgCliException extends Exception {
    GithubOrgCliException(String errorMessage) {
        super(errorMessage);
        LogManager.getLogger().fatal(errorMessage);
    }
}
