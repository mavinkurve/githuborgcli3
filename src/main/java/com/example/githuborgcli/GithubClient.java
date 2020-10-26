package com.example.githuborgcli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpException;

import java.io.IOException;

class GithubClient {

    static Logger log = LogManager.getLogger();

    static GitHub getClient(Main.Dependent args, char[] password) throws IOException {
        if (args == null) {
            String accessTokenEnvVar = System.getenv(Constants.GITHUB_ACCESS_TOKEN);

            if (accessTokenEnvVar != null) {
                log.debug("Initializing GitHub client with personal access token {} from sys env variable", Constants.GITHUB_ACCESS_TOKEN);
                return new GitHubBuilder().withOAuthToken(accessTokenEnvVar).build();
            }
            return null;
        }

        if (args.accessToken != null) {
            log.debug("Initializing GitHub client with provided personal access token {} ", args.accessToken);
            try {
                return new GitHubBuilder().withOAuthToken(args.accessToken).build();
            }
            catch (HttpException ex) {
                log.fatal("Failed to authenticate with GitHub API. Check credentials.");
                return null;
            }
        }

        if (args.username != null && password != null) {
            log.debug("Initializing GitHub client with provided username {} ", args.username);
            return new GitHubBuilder().withPassword(args.username, String.valueOf(password)).build();
        }

        log.fatal("Could not initialize GitHub client for access token {}, username {}, password {}",args.accessToken, args.username, password);
        return null;
    }
}
