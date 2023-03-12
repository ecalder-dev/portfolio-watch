package com.portfoliowatch.util.adapter;

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
            return 0L;
        }
        String stringValue = jsonReader.nextString();
        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
