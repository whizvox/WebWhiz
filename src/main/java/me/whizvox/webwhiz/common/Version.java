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

public class Version implements Comparable<Version> {

    public final int major, minor, revision;

    public Version(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    @Override
    public int compareTo(Version o) {
        if (o.major > major || o.minor > minor || o.revision > revision) {
            return 1;
        } else if (equals(o)) {
            return 0;
        }
        return -1;
    }

    public boolean equals(Version o) {
        return o.major == major && o.minor == minor && o.revision == revision;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Version && equals((Version) obj);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + revision;
    }

}
