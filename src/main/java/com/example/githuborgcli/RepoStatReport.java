package com.example.githuborgcli;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class RepoStatReport {

    static Logger log = LogManager.getLogger();

    static void generate(List<IRepoStat> repoStats, String orgName) throws IOException {
        File outputFile = new File(PropertyManager.get(Constants.OUTPUT_FILE));
        if (outputFile.createNewFile()) {
            log.debug("Created new output file {} for logging result", outputFile.getName());
        } else
            log.debug("Output file {} already exists", outputFile.getName());

        FileWriter fileWriter = new FileWriter(outputFile.getAbsolutePath());

        try {
            fileWriter.write("******* Repository Stats for " + StringUtils.capitalize(orgName) + "********\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        repoStats.forEach(rs -> {
            try {
                fileWriter.write("\t" + rs.getName() + "\n");
            } catch (IOException e) {
                log.error("Error writing {} to output file {}", rs.getName(), outputFile.getName(), e);
            }
            log.info(rs.getName());
            for (String name : rs.getStats()) {
                String s = "\t\t" + name + "\n";
                log.info(s);
                try {
                    fileWriter.write(s);
                } catch (IOException e) {
                    log.error("Error writing {} to output file {}", s, outputFile.getName(), e);
                }
            }
        });

        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Repo stats can be found in file " + outputFile.getAbsolutePath());
    }
}
