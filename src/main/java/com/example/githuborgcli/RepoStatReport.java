package com.example.githuborgcli;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class RepoStatReport {

    static Logger log = LogManager.getLogger();

    static void generate(Map<String,List<String>> stats, String orgName, String resultFile) throws IOException {
        File outputFile = new File(resultFile);
        if (outputFile.createNewFile()) {
            log.debug("Created new output file {} for logging result", outputFile.getName());
        } else
            log.debug("Output file {} already exists", outputFile.getName());

        FileWriter fileWriter = new FileWriter(outputFile.getAbsolutePath());

        try {
            fileWriter.write("******* Repository Stats for " + StringUtils.capitalize(orgName) + " ********\n");
        } catch (IOException e) {
            log.error("Error writing {} stats to output file {}", orgName, outputFile.getName(), e);
        }
        for (Map.Entry entry : stats.entrySet()) {
            try {
                fileWriter.write("\t" + entry.getKey() + "\n");
            } catch (IOException e) {
                log.error("Error writing {} to output file {}", entry.getKey(), outputFile.getName(), e);
            }
            log.debug(entry.getKey());
            for(String repoName : (List<String>) entry.getValue()) {
                String s = "\t\t" + repoName + "\n";
                log.debug(s);
                try {
                    fileWriter.write(s);
                } catch (IOException e) {
                    log.error("Error writing {} to output file {}", s, outputFile.getName(), e);
                }
            }
        }

        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error flushing output file {}", outputFile.getName(), e);
        }
        log.info("Repo stats can be found in file " + outputFile.getAbsolutePath());
    }
}
