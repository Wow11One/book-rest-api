package com.profitsoft.restapi.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class JsonFormatException extends JsonParseException {
    public JsonFormatException(JsonParser jsonParser, String message) {
        super(jsonParser, message);
    }
}
