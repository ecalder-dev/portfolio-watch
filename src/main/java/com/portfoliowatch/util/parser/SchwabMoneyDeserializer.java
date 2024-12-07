package com.portfoliowatch.util.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class SchwabMoneyDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String bigDecimalStr = p.getText().replace("$", "").trim();
        try {
            if (bigDecimalStr.isEmpty()) {
                return null;
            } else {
                return new BigDecimal(bigDecimalStr);
            }
        } catch (Exception e) {
            throw new IOException("Invalid format: " + bigDecimalStr, e);
        }
    }
}
