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

import me.whizvox.webwhiz.common.Configuration;
import me.whizvox.webwhiz.core.WebWhiz;
import me.whizvox.webwhiz.exception.ModuleLoadingException;
import me.whizvox.webwhiz.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

public class ModuleLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleLoader.class);

    private WebWhiz app;
    private File root;
    private Vector<Module> modules;
    private Vector<ModuleMetadata> moduleMetadata;
    private boolean loaded;
    private URLClassLoader classLoader;

    public ModuleLoader(WebWhiz app, File root) {
        assert root != null && root.isDirectory();
        this.root = root;
        this.app = app;
        modules = new Vector<>();
        moduleMetadata = new Vector<>();
        loaded = false;
        classLoader = null;
    }

    public boolean containsModuleMetadata(String id) {
        for (ModuleMetadata metadata : moduleMetadata) {
            if (metadata.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isModuleLoaded(String id) {
        for (Module module : modules) {
            if (module.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean load() throws IOException, ModuleLoadingException {
        if (!loaded) {
            File[] jars = root.listFiles(pathname -> !pathname.isDirectory() &&
                    StringHelper.containsExtension(pathname.getAbsolutePath(), "jar"));
            if (jars == null) {
                throw new IOException("Could not list files from given modules directory: " + root);
            }
            if (jars.length == 0) {
                // TODO Do something else when no modules are found
                LOGGER.warn("No modules found!");
            } else {
                URL[] urls = new URL[jars.length];
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = jars[i].toURI().toURL();
                }
                classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
                Enumeration<URL> moduleMetadataUrls = classLoader.getResources("module.json");
                while (moduleMetadataUrls.hasMoreElements()) {
                    URL moduleMetadataUrl = moduleMetadataUrls.nextElement();
                    try {
                        moduleMetadata.add(ModuleMetadata.build(moduleMetadataUrl));
                    } catch (ModuleLoadingException e) {
                        LOGGER.error("Could not load module.json at " + moduleMetadataUrl, e);
                    }
                }
                Vector<String> unloadedModules = new Vector<>();
                for (ModuleMetadata metadata : moduleMetadata) {
                    boolean load = true;
                    if (metadata.hasDependencies()) {
                        for (String dependency : metadata.getDependencies()) {
                            if (!metadata.doesDependencyVersionMatch(dependency)) {
                                unloadedModules.add(metadata.id);
                                load = false;
                                break;
                            }
                        }
                    }
                    if (load) {
                        try {
                            Class<?> cls = Class.forName(metadata.loadFrom, true, classLoader);
                            if (Module.class.isAssignableFrom(cls)) {
                                Constructor<?> constructor = cls.getConstructor();
                                Module module = (Module) constructor.newInstance();
                                module.id = metadata.id;
                                module.version = metadata.version;
                                String customDir = app.getConfig().getCustomDirectory(module.id);
                                if (customDir != null) {
                                    module.home = new File(root, resolveHomeDirectoryName(customDir));
                                } else {
                                    module.home = new File(root, module.id);
                                }
                                module.config = new Configuration(new File(module.getHome(), "config.json"));
                                modules.add(module);
                            } else {
                                LOGGER.warn(String.format(
                                        "Entry class (%s) for module (%s) is not an instance of Module class (%s)",
                                        metadata.loadFrom, metadata.id, Module.class));
                            }
                        } catch (ClassNotFoundException e) {
                            LOGGER.warn(String.format(
                                    "Entry class (%s) for module (%s) could not be found",
                                    metadata.loadFrom, metadata.id), e);
                        } catch (NoSuchMethodException e) {
                            LOGGER.warn(String.format(
                                    "Entry class (%s) for module (%s) does not have an empty constructor",
                                    metadata.loadFrom, metadata.id));
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            LOGGER.warn(String.format(
                                    "Entry class (%s) for module (%s) could not be constructed",
                                    metadata.loadFrom, metadata.id), e);
                        }
                    }
                }
                if (unloadedModules.size() > 0) {
                    LOGGER.warn("The following modules were not loaded due to having an invalid module.json file");
                    unloadedModules.forEach(s -> LOGGER.warn('\t' + s));
                }
                if (modules.size() > 0) {
                    LOGGER.info("The following modules were successfully loaded");
                    modules.forEach(module -> LOGGER.info('\t' + module.id + " " + module.version));
                    modules.forEach(Module::preInit);
                    modules.forEach(Module::init);
                    modules.forEach(Module::postInit);
                } else {
                    // TODO Do something else when no modules have been successfully loaded
                    LOGGER.warn("No modules have been successfully loaded!");
                }
            }
            loaded = true;
            return true;
        }
        return false;
    }

    private String resolveHomeDirectoryName(String homeDirectory) {
        if (homeDirectory.startsWith("$")) {
            int indexOf = homeDirectory.indexOf('/');
            String moduleId;
            if (indexOf == -1) {
                moduleId = homeDirectory.substring(1);
            } else {
                moduleId = homeDirectory.substring(1, indexOf);
            }
            String parentDir = null;
            for (Map.Entry<String, String> entry : app.getConfig().customHomeDirectories.entrySet()) {
                if (moduleId.equalsIgnoreCase(entry.getKey())) {
                    parentDir = entry.getValue();
                }
            }
            if (parentDir != null) {
                return resolveHomeDirectoryName(parentDir);
            }
        }
        return homeDirectory;
    }

    public boolean unload() throws IOException {
        if (loaded) {
            classLoader.close();
            modules.clear();
            moduleMetadata.clear();
            loaded = false;
            return true;
        }
        return false;
    }

}
