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

package org.mind.soundtrackmanager.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.mind.soundtrackmanager.capper.SoundTrackCapper;
import org.mind.soundtrackmanager.database.MySQLManager;
import org.mind.soundtrackmanager.utils.PropertiesLauncher;



/***
 * Manage a {@link SoundTrackManager}
 * @author Sh1fT
 *
 */
public class SoundTrackManager {
	private PropertiesLauncher propertiesLauncher;
	private String rootDirectory;
	private File rootDirectoryFile;
	private ArrayList<SoundTrackCapper> cappers;
	private MySQLManager mySQLManager;

	/**
	 * Create a new {@link SoundTrackManager} instance
	 */
	public SoundTrackManager() {
		this.setPropertiesLauncher(new PropertiesLauncher("SoundTrackManager.properties"));
		this.setRootDirectory(this.getCappersDirectory()+System.getProperty("file.separator"));
		this.setRootDirectoryFile(new File(this.getRootDirectory()));
		this.setCappers(new ArrayList<SoundTrackCapper>());
		this.setMySQLManager(new MySQLManager(this));
		for (File capper : this.getCappers(this.getRootDirectoryFile())) {
			SoundTrackCapper stc = new SoundTrackCapper(this, capper.getName());
			this.getCappers().add(stc);
		}
	}

	public PropertiesLauncher getPropertiesLauncher() {
		return propertiesLauncher;
	}

	public void setPropertiesLauncher(PropertiesLauncher propertiesLauncher) {
		this.propertiesLauncher = propertiesLauncher;
	}

	public String getCappersDirectory() {
		return this.getPropertiesLauncher().getProperties().getProperty("CappersDirectory");
	}

	public String getCompletedDirectory() {
		return this.getPropertiesLauncher().getProperties().getProperty("CompletedDirectory");
	}

	public String getMySQLHost() {
		return this.getPropertiesLauncher().getProperties().getProperty("MySQLHost");
	}

	public Integer getMySQLPort() {
		return Integer.parseInt(this.getPropertiesLauncher().getProperties().getProperty("MySQLPort"));
	}

	public String getMySQLDatabase() {
		return this.getPropertiesLauncher().getProperties().getProperty("MySQLDatabase");
	}

	public String getMySQLTable() {
		return this.getPropertiesLauncher().getProperties().getProperty("MySQLTable");
	}

	public String getMySQLUsername() {
		return this.getPropertiesLauncher().getProperties().getProperty("MySQLUsername");
	}

	public String getMySQLPassword() {
		return this.getPropertiesLauncher().getProperties().getProperty("MySQLPassword");
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public File getRootDirectoryFile() {
		return rootDirectoryFile;
	}

	public void setRootDirectoryFile(File rootDirectoryFile) {
		this.rootDirectoryFile = rootDirectoryFile;
	}

	public ArrayList<SoundTrackCapper> getCappers() {
		return cappers;
	}

	public void setCappers(ArrayList<SoundTrackCapper> cappers) {
		this.cappers = cappers;
	}

	public MySQLManager getMySQLManager() {
		return mySQLManager;
	}

	public void setMySQLManager(MySQLManager mySQLManager) {
		this.mySQLManager = mySQLManager;
	}

	/**
	 * Return a list of cappers directory
	 * @param rootDirectory
	 * @return
	 */
	public File [] getCappers(File rootDirectory) {
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		if (rootDirectory.isDirectory())
			return rootDirectory.listFiles(fileFilter);
		return null;
	}

	/**
	 * Launch the {@link SoundTrackManager}
	 */
	public void launch() {
		for (SoundTrackCapper stc : this.getCappers())
			stc.start();
	}

	/**
	 * Main of {@link SoundTrackManager} 
	 * @param args
	 */
	public static void main(String[] args) {
       new SoundTrackManager().launch();
    }
}