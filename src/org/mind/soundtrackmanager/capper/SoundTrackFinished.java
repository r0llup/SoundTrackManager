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

/**
 * Manage a {@link SoundTrackFinished}
 * @author Sh1fT
 *
 */
public class SoundTrackFinished extends Thread {
	private SoundTrackCapper parent;
	private File soundTrack;
	private String soundTrackName;
	private String soundTrackExtension;
	private Boolean finished;
	private Integer speed;
	private Integer timeout;
	private File destinationDirectory;
	private File destinationFile;

	/**
	 * Create a new {@link SoundTrackFinished} instance
	 * @param parent
	 * @param soundTrack
	 */
	public SoundTrackFinished(SoundTrackCapper parent, File soundTrack) {
		this.setParent(parent);
		this.setSoundTrack(soundTrack);
		String filename = this.getSoundTrack().getName();
		this.setSoundTrackExtension(filename.substring(filename.lastIndexOf('.')+1));
		this.setSoundTrackName(filename.substring(0, filename.lastIndexOf("."+this.getSoundTrackExtension())));
		this.setFinished(false);
		switch (this.getParent().getCapper()) {
			case "AnGeLiuS":
				this.setSpeed(5000);
				this.setTimeout(2500);
				break;
			case "Europe12":
				this.setSpeed(5000);
				this.setTimeout(2500);
				break;
			case "Scaph":
				this.setSpeed(5000);
				this.setTimeout(2500);
				break;
			case "Maat":
				this.setSpeed(5000);
				this.setTimeout(2500);
				break;
			default:
				this.setSpeed(1000);
				this.setTimeout(0);
		}
		this.setDestinationDirectory(new File(this.getParent().getCapperDirectoryPath()+
				this.getCompletedDirectory()));
		this.setDestinationFile(new File(this.getDestinationDirectory().getAbsoluteFile()
				+System.getProperty("file.separator")+this.getSoundTrack().getName()));
	}

	public SoundTrackCapper getParent() {
		return parent;
	}

	public void setParent(SoundTrackCapper parent) {
		this.parent = parent;
	}

	public File getSoundTrack() {
		return soundTrack;
	}

	public void setSoundTrack(File soundTrack) {
		this.soundTrack = soundTrack;
	}

	public String getSoundTrackName() {
		return soundTrackName;
	}

	public void setSoundTrackName(String soundTrackName) {
		this.soundTrackName = soundTrackName;
	}

	public String getSoundTrackExtension() {
		return soundTrackExtension;
	}

	public void setSoundTrackExtension(String soundTrackExtension) {
		this.soundTrackExtension = soundTrackExtension;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public File getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(File destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}

	public String getCompletedDirectory() {
		return this.getParent().getParent().getCompletedDirectory();
	}

	@Override
	public void run() {
		try {
			long firstTime = System.currentTimeMillis();
			while (!this.getFinished()) {
				long firstSize = this.getSoundTrack().length();
				sleep(this.getSpeed());
				long lastSize = this.getSoundTrack().length();
				if (firstSize == lastSize) {
					if (this.getTimeout() > 0) {
						sleep(this.getTimeout());
						firstSize = this.getSoundTrack().length();
						sleep(this.getSpeed());
						lastSize = this.getSoundTrack().length();
					}
					long lastTime = System.currentTimeMillis();
					Integer avgSpeed = (int) ((this.getSoundTrack().length() / 1024) / ((lastTime - firstTime) / 1000));
					this.getParent().getParent().getMySQLManager().updateSoundTrack(this.getSoundTrackName(),
							true, this.getSoundTrack().length(), avgSpeed);
					this.getDestinationDirectory().mkdir();
					sleep(1000);
					this.getSoundTrack().renameTo(this.getDestinationFile());
					this.setFinished(true);
					synchronized (this.getParent()) {
						this.getParent().setPendingFile(false);
						this.getParent().notify();
					}
				}
			}
		} catch (InterruptedException ex) {
			System.out.println("Error: " + ex.getLocalizedMessage());
			this.getParent().getParent().getMySQLManager().close();
			System.exit(1);
		}
	}
}