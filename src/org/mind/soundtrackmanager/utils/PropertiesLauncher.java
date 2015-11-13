/**
 * SoundTrackManager
 *
 * Copyright (C) 2013 Sh1fT
 *
 * This file is part of SoundTrackManager.
 *
 * SoundTrackManager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * SoundTrackManager is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundTrackManager; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.mind.soundtrackmanager.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * Manage a {@link PropertiesLauncher}
 * @author Sh1fT
 */
public final class PropertiesLauncher {
    private Properties properties;

    /**
     * Creates a new PropertiesLauncher instance
     * @param filename 
     */
    public PropertiesLauncher(String filename) {
        this.setProperties(new Properties());
        this.loadProperties(filename);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Load the properties file
     * @param filename 
     */
    public void loadProperties(String filename) {
        try {
            this.getProperties().load(ClassLoader.getSystemResourceAsStream(filename));
        } catch(IOException ex) {
            System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}