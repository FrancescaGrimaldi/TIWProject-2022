package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Meeting;
import it.polimi.tiw.project.beans.User;

/**
 * This class manages the access to the database containing created meetings.
 */
public class MeetingDAO {
	private Connection connection;
	
	public MeetingDAO(Connection c) {
		this.connection = c;
	}
	
	//Finds the meetings the given user has created
	public List<Meeting> findCreatedMeetings(User u) throws SQLException {
		List<Meeting> meetings = new ArrayList<Meeting>();
		
		String query = "SELECT * FROM tiwproject.meeting WHERE creator = ?";
		try(PreparedStatement pstat = connection.prepareStatement(query);) {
			pstat.setInt(1, u.getID());
			try(ResultSet result = pstat.executeQuery();){
				while (result.next()) {
					Meeting m = new Meeting();
					m.setID(result.getInt("meetingID"));
					m.setTitle(result.getString("title"));
					m.setDate(result.getDate("date"));
					m.setTime(result.getTime("time"));
					m.setDuration(result.getInt("duration"));
					m.setMaxPart(result.getInt("maxPart"));
					m.setCreator(result.getInt("creator"));
					meetings.add(m);
				}
			}
		}
		
		return meetings;
	}

	//Finds the meetings the given user is invited to
	public List<Meeting> findInvitedMeetings(User u) throws SQLException {
		List<Meeting> meetings = new ArrayList<Meeting>();
		
		String query = "SELECT * FROM tiwproject.meeting M JOIN tiwproject.participation P"
				+ "on M.meetingID = P.meetingID WHERE P.userID = ?";
		try(PreparedStatement pstat = connection.prepareStatement(query);){
			pstat.setInt(1, u.getID());
			try(ResultSet result = pstat.executeQuery();){
				while (result.next()) {
					Meeting m = new Meeting();
					m.setID(result.getInt("meetingID"));
					m.setTitle(result.getString("title"));
					m.setDate(result.getDate("date"));
					m.setTime(result.getTime("time"));
					m.setDuration(result.getInt("duration"));
					m.setMaxPart(result.getInt("maxPart"));
					m.setCreator(result.getInt("creator"));
					meetings.add(m);
				}
			}
		}
		
		return meetings;
	}
	
	//Adds a new row to the meeting table
	public int createMeeting(String title, Date date, Time time, int duration, int maxPart, int creator) throws SQLException {
		int code = 0;
		
		String query = "INSERT into tiwproject.meeting (title, date, time, duration, maxPart, creator) VALUES(?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstat = connection.prepareStatement(query);){
			pstat.setString(1, title);
			pstat.setDate(2, date);
			pstat.setTime(3, time);
			pstat.setInt(4, duration);
			pstat.setInt(5, maxPart);
			pstat.setInt(6,  creator);
			
			code = pstat.executeUpdate();
		}

		return code;
	}
	
}

