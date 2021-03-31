package com.portfoliowatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@Configuration
public class TDAmeritradeConfig {

    @Bean
    public File getTokenFile() throws IOException {
        File tokenFile = ResourceUtils.getFile("classpath:td_token.json");
        if (tokenFile.exists()) {
            if (tokenFile.isDirectory()) {
                throw new IOException(tokenFile.getAbsolutePath() + " is not allowed to be a directory.");
            } else {
                return tokenFile;
            }
        } else {
            if (tokenFile.createNewFile()) {
                return tokenFile;
            } else {
                throw new IOException(tokenFile.getAbsolutePath() + " could not be created.");
            }
        }
    }

}
