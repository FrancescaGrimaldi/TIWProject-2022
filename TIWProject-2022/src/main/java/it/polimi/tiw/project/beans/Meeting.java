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
	private int creator;
	
	
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
	
	public void setCreator(int id) {
		this.creator = id;
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
	
	public int getCreator() {
		return creator;
	}
	
}
