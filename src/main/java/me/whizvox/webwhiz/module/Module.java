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

import java.io.File;

public abstract class Module {

    String id;
    String version;
    File home;
    Configuration config;

    public Module() {

    }

    public final String getId() {
        return id;
    }

    public final String getVersion() {
        return version;
    }

    public final File getHome() {
        return home;
    }

    protected Configuration getConfig() {
        return config;
    }

    public void preInit() {}

    public void init() {}

    public void postInit() {}

}
