package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Meeting;
import it.polimi.tiw.project.beans.User;

public class MeetingsDAO {
	private Connection connection;
	
	public MeetingsDAO(Connection c) {
		this.connection = c;
	}
	
	public List<Meeting> findCreatedMeetings(User u) throws SQLException {
		List<Meeting> meetings = new ArrayList<Meeting>();
		
		try(PreparedStatement pstat = connection.prepareStatement("SELECT * FROM tiwproject.meeting WHERE creator = ?");) {
			pstat.setInt(1, u.getID());
			try(ResultSet result = pstat.executeQuery();){
				while (result.next()) {
					m = new Meeting();
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
		
	}

}

