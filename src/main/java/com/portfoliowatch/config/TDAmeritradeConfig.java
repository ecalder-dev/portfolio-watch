package com.portfoliowatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class TDAmeritradeConfig {

    private final File TOKEN_FILE =  new File("src/main/resources/td_token.json");

    @Bean
    public File getTokenFile() throws IOException {
        if (TOKEN_FILE.exists()) {
            if (TOKEN_FILE.isDirectory()) {
                throw new IOException(TOKEN_FILE.getAbsolutePath() + " is not allowed to be a directory.");
            } else {
                return TOKEN_FILE;
            }
        } else {
            if (TOKEN_FILE.createNewFile()) {
                return TOKEN_FILE;
            } else {
                throw new IOException(TOKEN_FILE.getAbsolutePath() + " could not be created.");
            }
        }
    }

}
