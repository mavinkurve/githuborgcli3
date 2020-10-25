package com.example.githuborgcli;

import com.sun.accessibility.internal.resources.accessibility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

class GithubClient {

    static Logger log = LogManager.getLogger();

    static GitHub getClient(String accessToken, String username, char[] password) throws IOException {
        if (accessToken != null) {
            log.debug("Initializing GitHub client with provided personal access token {} ", accessToken );
            return new GitHubBuilder().withOAuthToken(accessToken).build();
        }

        if (username != null && password != null) {
            log.debug("Initializing GitHub client with provided username {} ", username );
            return new GitHubBuilder().withPassword(username,String.valueOf(password)).build();
        }

        String usernameProp = PropertyManager.get(Constants.USER_NAME);
        String passwordProp = PropertyManager.get(Constants.PASSWORD);
        String accessTokenProp = PropertyManager.get(Constants.ACCESS_TOKEN);

        if (accessTokenProp != null) {
            log.debug("Initializing GitHub client with personal access token {} in config properties", accessTokenProp);
            return new GitHubBuilder().withOAuthToken(accessTokenProp).build();
        }

        if (usernameProp != null && passwordProp != null) {
            log.debug("Initializing GitHub client with user name {} in config properties", usernameProp);
            return new GitHubBuilder().withPassword(usernameProp,passwordProp).build();
        }

        log.fatal("Could not initialize GitHub client");
        return null;
    }
}
