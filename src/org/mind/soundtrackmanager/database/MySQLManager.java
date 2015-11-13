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

package org.mind.soundtrackmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.mind.soundtrackmanager.main.SoundTrackManager;


/**
 * Manage a {@link MySQLManager}
 * @author Sh1fT
 *
 */
public class MySQLManager {
	private SoundTrackManager parent;
	private Connection mySQLConnection;
	private Statement mySQLStatement;

	/**
	 * Create a new {@link MySQLManager} instance
	 * @param parent
	 */
	public MySQLManager(SoundTrackManager parent) {
		this.setParent(parent);
		this.setMySQLConnection(null);
		this.setMySQLStatement(null);
		this.init();
	}

	public SoundTrackManager getParent() {
		return parent;
	}

	public void setParent(SoundTrackManager parent) {
		this.parent = parent;
	}

	public Connection getMySQLConnection() {
		return mySQLConnection;
	}

	public void setMySQLConnection(Connection mySQLConnection) {
		this.mySQLConnection = mySQLConnection;
	}

	public Statement getMySQLStatement() {
		return mySQLStatement;
	}

	public void setMySQLStatement(Statement mySQLStatement) {
		this.mySQLStatement = mySQLStatement;
	}

	/** 
     * Initialize the connection with the MySQL Database
     */
	public void init() {
		String url = "jdbc:mysql://" + this.getParent().getMySQLHost() + ":" +
				this.getParent().getMySQLPort() + "/" + this.getParent().getMySQLDatabase();
		Properties p = new Properties();
		p.setProperty("user", this.getParent().getMySQLUsername());
		p.setProperty("password", this.getParent().getMySQLPassword());
		p.setProperty("autoReconnect", "true");
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            this.setMySQLConnection(DriverManager.getConnection(url, p));
            this.getMySQLConnection().setAutoCommit(true);
            this.setMySQLStatement(this.getMySQLConnection().createStatement(
            		ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        } catch (SQLException | ClassNotFoundException ex) {
        	System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
     }

	/** 
     * Close the connection with the MySQL Database
     */
	public void close() {
        try {
            this.getMySQLStatement().close();
            this.getMySQLConnection().close();
        } catch (SQLException ex) {
        	System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

	/**
	 * Insert a SoundTrack into the Database
	 * @param soundTrack
	 * @param capper
	 * @param format
	 * @return
	 */
	public Integer insertSoundTrack(String soundTrack, String capper, String format) {
		try {
    		String query = "INSERT INTO soundtracks(soundtrack, capper, format, completed, announced, size, speed) VALUES(?, ?, ?, FALSE, FALSE, 0, 0);";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setString(1, soundTrack);
			ps.setString(2, capper);
			ps.setString(3, format);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			if (ex.getErrorCode() == 1062)
				return 0;
			else {
				System.out.printf("Error: " + ex.getLocalizedMessage());
				this.close();
				System.exit(1);
			}
		}
		return 0;
	}

	/**
	 * Update a SoundTrack into the Database
	 * @param soundTrack
	 * @param completed
	 * @param size
	 * @param avgSpeed
	 * @return
	 */
	public Integer updateSoundTrack(String soundTrack, Boolean completed, long size, Integer avgSpeed) {
		try {
    		String query = "UPDATE soundtracks SET completed = ?, announced = FALSE, size = ?, speed = ? WHERE soundtrack LIKE ?;";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setBoolean(1, completed);
			ps.setLong(2, size);
			ps.setInt(3, avgSpeed);
			ps.setString(4, soundTrack);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return 0;
	}
}