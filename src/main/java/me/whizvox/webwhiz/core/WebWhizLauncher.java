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

import org.apache.commons.cli.*;
import spark.Spark;

import java.io.File;

public class WebWhizLauncher {

    private WebWhizLauncher() {}

    public static final String
            KEY_CONFIG_FILE = "configFile",
            KEY_ROOT_DIRECTORY = "rootDirectory",
            OPTION_CONFIG_PATH = "config.json",
            OPTION_ROOT_DIRECTORY = ".";

    public static void main(String[] args) {

        Options ops = new Options();
        ops.addOption(KEY_CONFIG_FILE, true, "The configuration file to be used");
        ops.addOption(KEY_ROOT_DIRECTORY, true, "The directory in which WebWhiz runs");
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine cmd = parser.parse(ops, args);
            String configFilePath = cmd.getOptionValue(KEY_CONFIG_FILE, OPTION_CONFIG_PATH);
            String rootPath = cmd.getOptionValue(KEY_ROOT_DIRECTORY, OPTION_ROOT_DIRECTORY);
            WebWhiz app = new WebWhiz(new File(configFilePath));
            Spark.init();
            app.getModuleLoader().load();
        } catch (ParseException e) {
            System.err.println("Could not parse arguments");
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("A fatal error occurred when running the application!");
            t.printStackTrace();
        }

    }

}
