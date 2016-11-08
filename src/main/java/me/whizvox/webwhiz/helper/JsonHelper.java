/*
 * WebWhiz: A module-based web server manager
 * Copyright (C) 2016 Cornelius Eanes
 *
 *  This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package me.whizvox.webwhiz.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

public class JsonHelper {

    private JsonHelper() {}

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static JsonNode readTree(String stringSrc) throws IOException {
        return MAPPER.readTree(stringSrc);
    }

    public static JsonNode readTree(File fileSrc) throws IOException {
        return MAPPER.readTree(fileSrc);
    }

    public static JsonNode readTree(URL urlSrc) throws IOException {
        return MAPPER.readTree(urlSrc);
    }

    public static <T> T readValue(String stringSrc, Class<T> type) throws IOException {
        return MAPPER.readValue(stringSrc, type);
    }

    public static <T> T readValue(File fileSrc, Class<T> type) throws IOException {
        return MAPPER.readValue(fileSrc, type);
    }

    public static <T> T readValueWithTypeReference(File fileSrc) throws IOException {
        return MAPPER.readValue(fileSrc, new TypeReference<T>() {});
    }

    public static <T> T readValue(URL urlSrc, Class<T> type) throws IOException {
        return MAPPER.readValue(urlSrc, type);
    }

    public static void writeValue(Writer writer, Object obj) throws IOException {
        MAPPER.writeValue(writer, obj);
    }

    public static void writeValueToFile(File file, Object obj) throws IOException {
        writeValue(new FileWriter(file), obj);
    }

}
