package com.portfoliowatch.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class DoubleGsonTypeAdapter extends TypeAdapter<Double> {
  @Override
  public void write(JsonWriter jsonWriter, Double number) throws IOException {
    if (number == null) {
      jsonWriter.nullValue();
      return;
    }
    jsonWriter.value(number);
  }

  @Override
  public Double read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return 0.0;
    }
    String stringValue = jsonReader.nextString();
    try {
      return Double.valueOf(stringValue);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }
}
