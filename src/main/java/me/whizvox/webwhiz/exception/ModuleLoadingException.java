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
package me.whizvox.webwhiz.exception;

public class ModuleLoadingException extends Exception {

    public ModuleLoadingException() {
    }

    public ModuleLoadingException(String message) {
        super(message);
    }

    public ModuleLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleLoadingException(Throwable cause) {
        super(cause);
    }

    public ModuleLoadingException(String format, Object... args) {
        super(String.format(format, args));
    }

}
