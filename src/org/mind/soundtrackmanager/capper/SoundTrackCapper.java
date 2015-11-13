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

package org.mind.soundtrackmanager.capper;

import java.io.File;
import java.io.FileFilter;

import org.mind.soundtrackmanager.main.SoundTrackManager;


/**
 * Manage a {@link SoundTrackCapper}
 * @author Sh1fT
 *
 */
public class SoundTrackCapper extends Thread {
	private SoundTrackManager parent;
	private String capper;
	private String capperDirectoryPath;
	private File capperDirectoryFile;
	private Boolean pendingFile;

	/**
	 * Create a new {@link SoundTrackCapper} instance
	 * @param parent
	 * @param capper
	 */
	public SoundTrackCapper(SoundTrackManager parent, String capper) {
		this.setParent(parent);
		this.setCapper(capper);
		this.setCapperDirectoryPath(this.getParent().getRootDirectory()+
				this.getCapper()+System.getProperty("file.separator"));
		this.setCapperDirectoryFile(new File(this.getCapperDirectoryPath()));
		this.setPendingFile(true);
	}

	public SoundTrackManager getParent() {
		return parent;
	}

	public void setParent(SoundTrackManager parent) {
		this.parent = parent;
	}

	public String getCapper() {
		return capper;
	}

	public void setCapper(String capper) {
		this.capper = capper;
	}

	public String getCapperDirectoryPath() {
		return capperDirectoryPath;
	}

	public void setCapperDirectoryPath(String capperDirectoryPath) {
		this.capperDirectoryPath = capperDirectoryPath;
	}

	public File getCapperDirectoryFile() {
		return capperDirectoryFile;
	}

	public void setCapperDirectoryFile(File capperDirectoryFile) {
		this.capperDirectoryFile = capperDirectoryFile;
	}

	public Boolean getPendingFile() {
		return pendingFile;
	}

	public void setPendingFile(Boolean pendingFile) {
		this.pendingFile = pendingFile;
	}

	/**
	 * Retrieve all the sound tracks from a directory
	 * @param soundTracksDirectory
	 * @return
	 */
	public File [] getSoundTracks(File soundTracksDirectory) {
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		    	String filename = file.getName();
		    	String extension = filename.substring(filename.lastIndexOf('.')+1);
		    	if (extension.compareToIgnoreCase("ac3") == 0 || extension.compareToIgnoreCase("mp3") == 0)
		    		return true;
		    	return false;
		    }
		};
		if (soundTracksDirectory.isDirectory())
			return soundTracksDirectory.listFiles(fileFilter);
		return null;
	}

	@Override
	public void run() {
		try {
			while (!this.isInterrupted()) {
				for (File st : this.getSoundTracks(this.getCapperDirectoryFile())) {
					String filename = st.getName();
					String soundTrack = filename.substring(0, filename.lastIndexOf('.'));
					String extension = filename.substring(filename.lastIndexOf('.')+1);
					Integer result = this.getParent().getMySQLManager().insertSoundTrack(soundTrack, this.getCapper(), extension.toUpperCase());
					if (result > 0) {
						new SoundTrackFinished(this, st).start();
			            synchronized (this) {
			                while (this.getPendingFile()) {
			                    try {
			                        wait();
			                    } catch (InterruptedException ex) {
			                    	System.out.println("Error: " + ex.getLocalizedMessage());
			            			this.getParent().getMySQLManager().close();
			            			System.exit(1);
			                    }
			                }
			            }
					}
				}
				sleep(1000);
			}
		} catch (InterruptedException ex) {
			System.out.println("Error: " + ex.getLocalizedMessage());
			this.getParent().getMySQLManager().close();
			System.exit(1);
		}
	}
}