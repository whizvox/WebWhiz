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
package me.whizvox.webwhiz.module;

import com.fasterxml.jackson.databind.JsonNode;
import me.whizvox.webwhiz.exception.ModuleLoadingException;
import me.whizvox.webwhiz.helper.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ModuleMetadata {

    /** The alphanumeric ID of the module */
    public final String id;
    /** The version string of the module */
    public final String version;
    /** The class containing the initialization methods */
    public final String loadFrom;
    /** Unmodifiable list of dependencies */
    public final Map<String, Pattern> dependencies;

    public ModuleMetadata(String id, String version, String loadFrom, Map<String, Pattern> dependencies) {
        this.id = id;
        this.version = version;
        this.loadFrom = loadFrom;
        this.dependencies = dependencies;
    }

    public boolean hasDependencies() {
        return dependencies.size() > 0;
    }

    public Set<String> getDependencies() {
        return dependencies.keySet();
    }

    public boolean doesDependencyVersionMatch(String moduleId) {
        if (dependencies.containsKey(moduleId)) {
            Pattern pattern = dependencies.get(moduleId);
            if (pattern != null) {
                Matcher matcher = pattern.matcher(version);
                if (!matcher.matches()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static final char DEPENDENCY_VERSION_SEPARATOR = ':';

    public static ModuleMetadata build(String jsonString) throws ModuleLoadingException {
        try {
            return _build(JsonHelper.readTree(jsonString));
        } catch (IOException e) {
            throw new ModuleLoadingException("Could not read the following as a json string", e);
        }
    }

    public static ModuleMetadata build(File jsonFile) throws ModuleLoadingException {
        try {
            return _build(JsonHelper.readTree(jsonFile));
        } catch (IOException e) {
            throw new ModuleLoadingException("Could not read the following as a json file: " + jsonFile.getAbsolutePath(), e);
        }
    }

    public static ModuleMetadata build(URL jsonUrl) throws ModuleLoadingException {
        try {
            return _build(JsonHelper.readTree(jsonUrl));
        } catch (IOException e) {
            throw new ModuleLoadingException("Could not read the following as a json URL: " + jsonUrl);
        }
    }

    private static ModuleMetadata _build(JsonNode node) throws ModuleLoadingException {
        if (!node.has(KEY_ID)) {
            throw new ModuleLoadingException("Required configuration field is not defined: %s", KEY_ID);
        }
        String id = node.get(KEY_ID).asText();
        if (!node.has(KEY_VERSION)) {
            throw new ModuleLoadingException("Required configuration field is not defined: %s", KEY_VERSION);
        }
        String version = node.get(KEY_VERSION).asText();
        if (!node.has(KEY_ENTRY_CLASS)) {
            throw new ModuleLoadingException("Required configuration field is not defined: %s", KEY_ENTRY_CLASS);
        }
        String accessClass = node.get(KEY_ENTRY_CLASS).asText();
        Map<String, Pattern> dependencies = new HashMap<>();
        if (node.has(KEY_DEPENDENCIES)) {
            JsonNode depsNode = node.get(KEY_DEPENDENCIES);
            if (depsNode.isArray()) {
                for (JsonNode n : depsNode) {
                    String text = n.asText();
                    int indexOf = text.indexOf(DEPENDENCY_VERSION_SEPARATOR);
                    String moduleId = null;
                    Pattern pattern = null;
                    if (indexOf != -1) {
                        try {
                            pattern = Pattern.compile(text.substring(indexOf + 1));
                            moduleId = text.substring(0, indexOf).trim();
                        } catch (PatternSyntaxException e) {}
                    }
                    if (moduleId == null) {
                        moduleId = text.trim();
                    }
                    dependencies.put(moduleId, pattern);
                }
            }
        }
        return new ModuleMetadata(id, version, accessClass, Collections.unmodifiableMap(dependencies));
    }

    public static final String
            KEY_ID = "id",
            KEY_ENTRY_CLASS = "entryClass",
            KEY_VERSION = "version",
            KEY_DEPENDENCIES = "dependencies";

}
