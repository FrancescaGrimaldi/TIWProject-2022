package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Meeting;
import it.polimi.tiw.project.beans.User;

/**
 * This class manages the access to the database containing the meetings.
 */
public class MeetingDAO {
	private Connection connection;

	/**
	 * Class constructor.
	 * @param c				the Connection.
	 */
	public MeetingDAO(Connection c) {
		this.connection = c;
	}


	/**
	 * Finds the meetings that the given user has created, excluding those
	 * that started in the past.
	 * @param u				the given User.
	 * @return				a List containing the created Meetings.
	 * @throws SQLException	if an error occurs while accessing the database.
	 */
	public List<Meeting> findCreatedMeetings(User u) throws SQLException {
		List<Meeting> meetings = new ArrayList<>();
		
		String query = "SELECT * FROM meeting WHERE creator = ? AND addtime(date,time)>now()";
		try(PreparedStatement pstat = connection.prepareStatement(query)) {
			pstat.setInt(1, u.getID());
			try(ResultSet result = pstat.executeQuery()){
				while (result.next()) {
					Meeting m = new Meeting();
					m.setID(result.getInt("meetingID"));
					m.setTitle(result.getString("title"));
					m.setDate(result.getDate("date"));
					m.setTime(result.getTime("time"));
					m.setDuration(result.getInt("duration"));
					m.setMaxPart(result.getInt("maxPart"));
					m.setCreatorID(result.getInt("creator"));
					meetings.add(m);
				}
			}
		}
		
		return meetings;
	}


	/**
	 * Finds the meetings the given user is invited to, excluding those that
	 * started in the past.
	 * @param u				the given User.
	 * @return				a List containing the Meetings they are invited to.
	 * @throws SQLException if an error occurs while accessing the database.
	 */
	public List<Meeting> findInvitedMeetings(User u) throws SQLException {
		List<Meeting> meetings = new ArrayList<>();
		
		String query = "SELECT * FROM meeting M JOIN participation P on M.meetingID = P.meetingID WHERE P.participantID = ? AND addtime(date,time)>now()";
		try(PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setInt(1, u.getID());
			try(ResultSet result = pstat.executeQuery()){
				while (result.next()) {
					Meeting m = new Meeting();
					m.setID(result.getInt("M.meetingID"));
					m.setTitle(result.getString("M.title"));
					m.setDate(result.getDate("M.date"));
					m.setTime(result.getTime("M.time"));
					m.setDuration(result.getInt("M.duration"));
					m.setMaxPart(result.getInt("M.maxPart"));
					m.setCreatorID(result.getInt("M.creator"));
					meetings.add(m);
				}
			}
		}
		
		return meetings;
	}

	
	/**
	 * Adds a new row to the meeting table with the information in the parameters.
	 * @param title			the title of the Meeting.
	 * @param date			the date.
	 * @param time			the time.
	 * @param duration		the duration.
	 * @param maxPart		the maximum number of participants.
	 * @param creator		the username of the user who creates the meeting.
	 * @return				the generated key.
	 * @throws SQLException if an error occurs while accessing the database.
	 */
	public int createMeeting(String title, Date date, Time time, int duration, int maxPart, String creator) throws SQLException {
		int creatorID;
		
		String query = "INSERT into meeting (title, date, time, duration, maxPart, creator) VALUES(?, ?, ?, ?, ?, ?)";
		
		UserDAO uDAO = new UserDAO(connection);
		creatorID = uDAO.getIDByNick(creator);
		
		int key;
		
		try (PreparedStatement pstat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			pstat.setString(1, title);
			pstat.setDate(2, date);
			pstat.setTime(3, time);
			pstat.setInt(4, duration);
			pstat.setInt(5, maxPart);
			pstat.setInt(6, creatorID);
			
			pstat.executeUpdate();
			
			ResultSet keys = pstat.getGeneratedKeys();    
			keys.next();  
			key = keys.getInt(1);
		}
		
		return key;
	}


	/**
	 * Adds a new row to the participation table that pairs a meeting and a user
	 * through their IDs.
	 * @param mID			Meeting's ID.
	 * @param participant	participant's username.
	 * @throws SQLException if an error occurs while accessing the database.
	 */
	public void sendInvitation(int mID, String participant) throws SQLException {
		
		String query = "INSERT into participation (meetingID, participantID) VALUES(?, ?)";
		
		UserDAO uDAO = new UserDAO(connection);
		int uID = uDAO.getIDByNick(participant);
		
		try (PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setInt(1, mID);
			pstat.setInt(2, uID);
			
			pstat.executeUpdate();
		}
	}
	
}