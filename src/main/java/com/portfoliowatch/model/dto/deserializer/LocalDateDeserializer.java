package com.portfoliowatch.model.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
  @Override
  public LocalDate deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      return null;
    }
    String dateStr = parser.getText();
    String dateStrBeforeT = dateStr.split("T")[0];
    return LocalDate.parse(dateStrBeforeT);
  }
}
