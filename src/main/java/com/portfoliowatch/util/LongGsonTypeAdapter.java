package com.portfoliowatch.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LongGsonTypeAdapter extends TypeAdapter<Long> {
    @Override
    public void write(JsonWriter jsonWriter, Long number) throws IOException {
        if (number == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(number);
    }

    @Override
    public Long read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String stringValue = jsonReader.nextString();
        try {
            Long value = Long.valueOf(stringValue);
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
