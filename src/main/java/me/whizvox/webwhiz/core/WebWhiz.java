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

import me.whizvox.webwhiz.helper.JsonHelper;
import me.whizvox.webwhiz.module.ModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class WebWhiz {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebWhiz.class);

    private ModuleLoader loader;
    private WebWhizConfiguration config;

    public WebWhiz(WebWhizConfiguration config) {
        init(config);
    }

    public WebWhiz(File configFile) throws IOException {
        WebWhizConfiguration config = null;
        boolean setDefault = true;
        if (configFile.exists()) {
            try {
                config = WebWhizConfiguration.build(JsonHelper.readTree(configFile));
                setDefault = false;
            } catch (IOException e) {
                LOGGER.error("Could not load configuration file from " + configFile, e);
            }
        }
        if (setDefault) {
            config = WebWhizConfiguration.DEFAULT;
            configFile.createNewFile();
            JsonHelper.writeValueToFile(configFile, config);
        }
        init(config);
    }

    public WebWhiz() throws IOException {
        this(new File("config.json"));
    }

    private void init(WebWhizConfiguration config) {
        this.config = config;
        loader = new ModuleLoader(this, config.modulesDirectory);

        if (!config.modulesDirectory.exists()) {
            config.modulesDirectory.mkdirs();
        }
    }

    public ModuleLoader getModuleLoader() {
        return loader;
    }

    public WebWhizConfiguration getConfig() {
        return config;
    }

}
