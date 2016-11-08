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

public class StringHelper {

    private StringHelper() {}

    public static boolean containsExtension(String filePath, String extension) {
        if (extension.length() > filePath.length()) {
            return false;
        }
        int indexOf = filePath.lastIndexOf('.');
        return indexOf != -1 && filePath.substring(indexOf + 1).equalsIgnoreCase(extension);
    }

    public static String insert(String orig, String inserting, int index) {
        if (index < 0 || index > orig.length() - 1 || orig.length() < inserting.length()) {
            return orig;
        }
        return orig.substring(0, index) + inserting + orig.substring(index);
    }

    public static boolean parseBoolean(String s) {
        if (s != null && s.length() > 0) {
            if (s.charAt(0) == '0' || s.equalsIgnoreCase("false")) {
                return false;
            } else if (s.charAt(0) == '1' || s.equalsIgnoreCase("true")) {
                return true;
            }
        }
        throw new NumberFormatException("Invalid boolean: " + s);
    }

}
