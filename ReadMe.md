# Github Organization Repo Stats

This is a simple commandline application to gather and print repository stats for a 
Github organization. 

## Running the app 

```$shell
java -cp github-org-cli-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.githuborgcli.Main -o twitter
```

## Usage
```$shell
Usage: getRepoStats [-hV] [-p]... [--accessToken=<accessToken>] [--accessToken:
                    env=<accessTokenSysEnv>] [-g=<githubTimeout>] [-n=<count>]
                    -o=<orgName> [-r=<resultFile>] [-t=<threadPoolSize>]
                    [-u=<username>]
gets repo popularity stats for a github org
      --accessToken=<accessToken>
                   GitHub personal access token
      --accessToken:env=<accessTokenSysEnv>
                   GitHub personal access token read from System Env variable
  -g, --githubTimeout=<githubTimeout>
                   Github API timeout in seconds
  -h, --help       Show this help message and exit.
  -n, --numberOfResults=<count>
                   Number of results to include for stats
  -o, --organization=<orgName>
                   Organization to get repo stats on
  -p, --password   GitHub password
  -r, --resultFile=<resultFile>
                   File to print results in
  -t, --threadPoolSize=<threadPoolSize>
                   Thread pool size
  -u, --username=<username>
                   GitHub username
  -V, --version    Print version information and exit.

```

## Authentication
This app can run in authenticated or unauthenticated mode. When running unauthenticated, API rate limit will be 
restricted to 60 per hour. 
Authentication can be provided via personal access token specified on commandline or provided via a system environment 
variable. 
Personal access token directions: https://github.blog/2013-05-16-personal-api-tokens

Alternately, you can also specify username and password on commandline. If you are providing `-u` option, specify `-p` 
flag on commandline to enter password in interactive mode.   

## Adding new stat 
The app can be extended to report a new stat for repositories by specifying the name of the stat and its comparator on 
a list of repositories in RepoStatType.java as follows. Adding a new enum in this file will automatically add the stat 
to the generated report. 
```$java
STAR_GAZER_COUNT(Comparator.comparing(Repository::getStars, Comparator.nullsFirst(Comparator.naturalOrder())).reversed()),
```

## Preemptive Rate Limiting
Github limits API queries to 60 per hour for unauthenticated user and 5000 per hour for authenticated user. To avoid 
running into this limit, app will check the remaining rate limit for user before performing any API call intensive 
operations. It will preemptively exit if such an operation cannot be completed and will provide the rate limit reset time.  

## App Config 

*Output File*

*Thread pool* 

Repository data is fetched from github server using parallel threads to speed up the task. This config property can be used to adjust the number of threads being spawned in parallel. 
User can adjust thread pool size to optimize for appropriate value. 

*Github API Timeout* 









