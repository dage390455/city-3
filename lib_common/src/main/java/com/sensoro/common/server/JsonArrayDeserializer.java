package com.sensoro.common.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by tangrisheng on 2016/5/6.
 * number element.if "" or an exception appears , return 0;
 */
public class JsonArrayDeserializer implements JsonDeserializer<JsonArray> {
    @Override
    public JsonArray deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        try {
            if (typeOfT.equals(JsonArray.class)) {
                return json.getAsJsonArray();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
