package com.sensoro.common.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListDeserializer implements JsonDeserializer<List<?>> {
    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() == 0) {
                return Collections.EMPTY_LIST;
            }
            List<?> resultList = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                resultList.add(context.deserialize(element, typeOfT));
            }
            return resultList;
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
