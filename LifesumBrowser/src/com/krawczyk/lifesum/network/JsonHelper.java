/*
 * Copyright (C) 2014 Pawel Krawczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.krawczyk.lifesum.network;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krawczyk.lifesum.Food;

/**
 * @class JsonHelper
 * @author Pawel Krawczyk
 * @since 23-05-2014
 */
public class JsonHelper {

    private JsonHelper() {
    }

    public static String serialize(Object object) {
        return new Gson().toJson(object);
    }

    public static <T> Object deserialize(String jsonStr, Class<?> targetClass) {
        return new Gson().fromJson(jsonStr, targetClass);
    }

    @SuppressWarnings("unchecked")
    public static List<Food> deserializeFoodList(String jsonStr) {
        Type t = new TypeToken<List<Food>>() {
        }.getType();
        return (List<Food>) new Gson().fromJson(jsonStr, t);
    }
}
