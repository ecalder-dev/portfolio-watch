package com.portfoliowatch.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateGsonTypeAdapter extends TypeAdapter<Date> {
    final private DateFormat SIMPLE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void write(JsonWriter jsonWriter, Date date) throws IOException {
        if (date == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(ISO8601Utils.format(date));
    }

    @Override
    public Date read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        String dateAsString = jsonReader.nextString();
        if (dateAsString.length() == 10) { // assumes MM/dd/yyyy format
            try {
                return SIMPLE_FORMAT.parse(dateAsString);
            } catch (ParseException e) {
                // say something
                return null;
            }
        } else {
            try {
                return ISO8601Utils.parse(dateAsString, new ParsePosition(0));
            } catch (ParseException e) {
                // say something
                return null;
            }
        }
    }
}
