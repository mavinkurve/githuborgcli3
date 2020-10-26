package com.example.githuborgcli.utils;

import picocli.CommandLine;

public class AuthArgs {
    @CommandLine.Option(names = {"-a", "--accesstoken"}, description = "GitHub personal access token", required = true)
    String accessToken = null;
    @CommandLine.Option(names = {"-u", "--username"}, description = "GitHub username", required = true)
    String username = null;
}
