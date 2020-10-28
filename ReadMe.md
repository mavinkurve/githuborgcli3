#Github Organization Repo Stats

This is a simple commandline application to gather and print repository stats for a 
Github organization. 

##Running the app 

```
java -cp /Users/pmk/githuborgcli3/target/github-org-cli-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.githuborgcli.Main -o twitter
```

##Authentication
Github limits API queries to 60 per hour for unauthenticated user 
Personal access token directions: https://github.blog/2013-05-16-personal-api-tokens

##Adding new stat 

##Preemptive Rate Limiting

##App Config 

*Output File*

*Thread pool* 

Repository data is fetched form github server using parallel threads to speed up the task. This config property can be used to adjust the number of threads being spawned in parallel. 
User can adjust thread pool size to optimize for appropriate value. 

*Github API Timeout* 







