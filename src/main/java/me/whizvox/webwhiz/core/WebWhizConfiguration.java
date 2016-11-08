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
package me.whizvox.webwhiz.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebWhizConfiguration {

    /**
     * Webmaster-defined home directories for individual modules. Also supports basic variable setting. For example,
     * for the following custom directories<br>
     * <pre>
     * "module1": "/htdocs",
     * "module2": "{module1}/assets"</pre>
     * <br>
     * module2's home directory will be resolved as "/htdocs/assets". This doesn't support forward declaration, so this
     * <br>
     * <pre>
     * "module1": "{module2}",
     * "module2": "/htdocs"</pre>
     * <br>
     * will not work. module1's home directory will be resolved as "{module2}". A warning will appear in the sysout if
     * this is detected. The same thing will happen with variables that don't exist. It will be resolved as plain text,
     * and a warning will be printed. However, if you want to use brackets in your file path, using C#-inspired syntax
     * will do the trick. Just put an AT symbol (@) at the front of the string to have it treated as plain text.
     * <br>
     * <pre>
     * "module1": "@{module1}"
     * "module2": "@module2/{anotherthingwithbrackets}"</pre>
     * <br>
     * That being said, no paths can start with an AT symbol (@).
     * */
    public final Map<String, String> customHomeDirectories;
    public final File modulesDirectory;

    private WebWhizConfiguration(Map<String, String> customHomeDirectories, File modulesDirectory) {
        Map<String, String> resolvedHomeDirectories = new HashMap<>();
        customHomeDirectories.keySet().forEach(key -> resolvedHomeDirectories.put(key, resolve(customHomeDirectories, key)));
        this.customHomeDirectories = Collections.unmodifiableMap(resolvedHomeDirectories);
        this.modulesDirectory = modulesDirectory;
    }

    public String getCustomDirectory(String moduleId) {
        for (Map.Entry<String, String> entry : customHomeDirectories.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(moduleId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static final WebWhizConfiguration DEFAULT = new WebWhizConfiguration(new HashMap<>(), new File("modules"));

    private static String resolve(Map<String, String> map, String id) {
        return resolve(map, id, id, 0);
    }

    private static String resolve(Map<String, String> map, String moduleId, String origModuleId, int iteration) {
        if (iteration > 12) {
            return getCleanPath(map.get(origModuleId));
        }
        String path = getCleanPath(map.get(moduleId));
        if (path.startsWith("@")) {
            return path.substring(1);
        }
        Matcher matcher = PATTERN_VAR.matcher(path);
        if (!matcher.find()) {
            return path;
        }
        String otherModuleId = matcher.group(1);
        return matcher.replaceFirst(resolve(map, otherModuleId, origModuleId, ++iteration));
    }

    public static WebWhizConfiguration build(JsonNode json) {
        Map<String, String> customHomeDirectories = new HashMap<>();
        if (json.has(NAME_HOME_DIRS)) {
            JsonNode homeDirs = json.get(NAME_HOME_DIRS);
            Iterator<Map.Entry<String, JsonNode>> iter = homeDirs.fields();
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> field = iter.next();
                customHomeDirectories.put(field.getKey(), field.getValue().asText());
            }
        }
        String modulesDirectory;
        if (json.has(NAME_MODULES_DIRECTORY)) {
            modulesDirectory = json.get(NAME_MODULES_DIRECTORY).asText();
        } else {
            modulesDirectory = "modules";
        }
        return new WebWhizConfiguration(Collections.unmodifiableMap(customHomeDirectories), new File(modulesDirectory));
    }

    private static final String
            NAME_HOME_DIRS = "homeDirectories",
            NAME_MODULES_DIRECTORY = "modulesDirectory";

    private static Pattern PATTERN_VAR = Pattern.compile("\\{(.+?)}");

    private static String getCleanPath(String path) {
        path = path.trim();
        if (path.length() > 0) {
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }
            if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
                path = path.substring(0, path.length() - 2);
            }
        }
        return path;
    }

}
