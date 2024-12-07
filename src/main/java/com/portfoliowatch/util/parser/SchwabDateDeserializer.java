package com.portfoliowatch.util.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchwabDateDeserializer extends JsonDeserializer<Date> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String dateStr = p.getText();
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            throw new IOException("Invalid date format: " + dateStr, e);
        }
    }
}
