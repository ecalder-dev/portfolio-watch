package com.portfoliowatch.util.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SchwabDateDeserializer extends JsonDeserializer<LocalDate> {

  private static final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern as per your date format

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext context) throws IOException {
    String dateStr = p.getText();
    try {
      return LocalDate.parse(dateStr, dateFormatter);
    } catch (Exception e) {
      throw new IOException("Invalid date format: " + dateStr, e);
    }
  }
}
