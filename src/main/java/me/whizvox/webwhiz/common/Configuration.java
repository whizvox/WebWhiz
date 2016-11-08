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
package me.whizvox.webwhiz.common;

import me.whizvox.webwhiz.helper.JsonHelper;
import me.whizvox.webwhiz.helper.StringHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {

    private final Map<String, String> map;
    private final File file;

    public Configuration(Map<String, String> map, File file) {
        this.map = map;
        this.file = file;
    }

    public Configuration(File file) {
        this(new HashMap<>(), file);
    }

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public <T> T get(String key, T defaultValue, Parser<T> parser) {
        if (has(key)) {
            try {
                return parser.parse(map.get(key));
            } catch (Exception e) {}
        }
        map.put(key, String.valueOf(defaultValue));
        return defaultValue;
    }

    // it's assumed that the default value is within the bounds
    public <T extends Number> T getWithBoundsCheck(String key, T defaultValue, Parser<T> parser, double min, double max) {
        if (has(key)) {
            try {
                T val = parser.parse(map.get(key));
                double valDouble = val.doubleValue();
                if (valDouble >= min && valDouble <= max) {
                    return val;
                }
            } catch (Exception e) {}
        }
        map.put(key, String.valueOf(defaultValue));
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        return get(key, defaultValue, s -> s);
    }

    public String getString(String key, String defaultValue, Pattern pattern) {
        String s = getString(key, defaultValue);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            return s;
        }
        map.put(key, defaultValue);
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key, defaultValue, StringHelper::parseBoolean);
    }

    public char getChar(String key, char defaultValue) {
        return get(key, defaultValue, s -> s.charAt(0));
    }

    public byte getByte(String key, byte defaultValue) {
        return get(key, defaultValue, Byte::parseByte);
    }

    public byte getByte(String key, byte defaultValue, byte min, byte max) {
        return getWithBoundsCheck(key, defaultValue, Byte::parseByte, min, max);
    }

    public byte getUnsignedByte(String key, byte defaultValue) {
        return (byte) (getInt(key, defaultValue, 0, 1 + (Byte.MAX_VALUE << 1)) & 0xff);
    }

    public short getShort(String key, short defaultValue) {
        return get(key, defaultValue, Short::parseShort);
    }

    public short getShort(String key, short defaultValue, short min, short max) {
        return getWithBoundsCheck(key, defaultValue, Short::parseShort, min, max);
    }

    public short getUnsignedShort(String key, short defaultValue) {
        return (short) (getInt(key, defaultValue, 0, 1 + (Short.MAX_VALUE << 1)));
    }

    public int getInt(String key, int defaultValue) {
        return get(key, defaultValue, Integer::parseInt);
    }

    public int getInt(String key, int defaultValue, int min, int max) {
        return getWithBoundsCheck(key, defaultValue, Integer::parseInt, min, max);
    }

    public int getUnsignedInt(String key, int defaultValue) {
        return get(key, defaultValue, Integer::parseUnsignedInt);
    }

    public long getLong(String key, long defaultValue) {
        return get(key, defaultValue, Long::parseLong);
    }

    public long getLong(String key, long defaultValue, long min, long max) {
        return getWithBoundsCheck(key, defaultValue, Long::parseLong, min, max);
    }

    public long getUnsignedLong(String key, long defaultValue) {
        return get(key, defaultValue, Long::parseUnsignedLong);
    }

    public float getFloat(String key, float defaultValue) {
        return get(key, defaultValue, Float::parseFloat);
    }

    public float getFloat(String key, float defaultValue, float min, float max) {
        return getWithBoundsCheck(key, defaultValue, Float::parseFloat, min, max);
    }

    public double getDouble(String key, double defaultValue) {
        return get(key, defaultValue, Double::parseDouble);
    }

    public double getDouble(String key, double defaultValue, double min, double max) {
        return getWithBoundsCheck(key, defaultValue, Double::parseDouble, min, max);
    }

    public void save() throws IOException {
        JsonHelper.writeValueToFile(file, map);
    }

    public void load() throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        } else {
            Map<String, String> value = JsonHelper.readValueWithTypeReference(file);
            map.clear();
            map.putAll(value);
        }
    }

    public interface Parser<T> {
        T parse(String s) throws Exception;
    }

}
