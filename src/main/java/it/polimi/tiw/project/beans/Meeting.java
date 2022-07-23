package it.polimi.tiw.project.beans;

import java.sql.Time;
import java.sql.Date;

/**
 * This class represents online meetings.
 */
public class Meeting {
	private int meetingID;
	private String title;
	private Date date;
	private Time time;
	private int duration;
	private int maxPart;
	private int creatorID;
	private String creatorUsername;
	
	
	/**
	 * Class constructor.
	 */
	public Meeting() {
	}
	
	
	/* The following methods are setters for this class' attributes */
	
	public void setID(int id) {
		this.meetingID = id;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	public void setDate(Date d) {
		this.date = d;
	}
	
	public void setTime(Time t) {
		this.time = t;
	}
	
	public void setDuration(int d) {
		this.duration = d;
	}
	
	public void setMaxPart(int max) {
		this.maxPart = max;
	}
	
	public void setCreatorID(int id) {
		this.creatorID = id;
	}
	
	public void setCreatorUsername(String cUsername) {
		this.creatorUsername = cUsername;
	}


	/* The following methods are getters for this class' attributes */
	
	public int getID() {
		return meetingID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Time getTime() {
		return time;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getMaxPart() {
		return maxPart;
	}
	
	public int getCreatorID() {
		return creatorID;
	}
	
	public String getCreatorUsername() {
		return creatorUsername;
	}
	
	
	public String toString() {
		StringBuffer buff = new StringBuffer("Meeting");
		
		buff.append(" id: ");
		buff.append(meetingID);
		buff.append(" title: ");
		buff.append(title);
		buff.append(" date: ");
		buff.append(date);
		buff.append(" time: ");
		buff.append(time);
		buff.append(" duration: ");
		buff.append(duration);
		buff.append(" maxPart: ");
		buff.append(maxPart);
		buff.append(" creatorUsername: ");
		buff.append(creatorUsername);
		
		return buff.toString();
	}
	
}