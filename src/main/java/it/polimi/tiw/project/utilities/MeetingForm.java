package it.polimi.tiw.project.utilities;

import java.sql.Date;
import java.sql.Time;

/**
* This class provides methods to check if the input inserted in 
* the SignUp.html form is correct and sets the messages for
* potential errors.
*/
public class MeetingForm {
	private String title;
	private Date date; //in SQL date format
	private Time time; //in SQL time format
	private int duration;
	private int maxPart;
	private String titleError;
	private String dateError;
	private String timeError;
	private String durationError;
	private String maxPartError;

	/**
	 * Class constructor.
	 */
	public MeetingForm() {
		super();
	}


	/**
	 * Class constructor specifying the parameters got from user input.
	 * @param title		the String containing the title of the meeting.
	 * @param date		the Date of the meeting.
	 * @param time		the Time of the meeting.
	 * @param duration	the duration of the meeting (in minutes).
	 * @param maxPart	the maximum number of participants (chosen by the meeting creator).
	 */
	public MeetingForm(String title, String date, String time, int duration, int maxPart) {
		super();
		this.setTitle(title);
		this.setDate(date);
		this.setTime(time);
		this.setDuration(duration);
		this.setMaxPart(maxPart);
	}
	

	/**
	 * Sets the title of the meeting checking that it's a string (not empty)
	 * only made up of alphanumeric characters.
	 * @param title		the title inserted.
	 */
	public void setTitle(String title) {
		this.title = title;
		
		if (title == null || title.isBlank()) {
			this.titleError = "A title must be inserted (it can't only contain spaces).";
		} else if ( !title.matches("[a-zA-Z0-9 ]+") ){
			this.titleError = "The title can only contain alphanumeric characters and spaces.";
		} else {
			this.titleError = null;
		}
	}


	/**
	 * Sets date in a proper format for SQL queries and checks the validity of
	 * the inserted parameter.
	 * @param date 		the date inserted.
	 */
	public void setDate(String date) {
		DateChecker dc = new DateChecker();
		
		if (date!=null && date.matches("[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}")) {
			
			Integer dayNumber = Integer.parseInt(date.substring(8,10));
			Integer monthNumber = Integer.parseInt(date.substring(5,7));
			Integer yearNumber = Integer.parseInt(date.substring(0,4));
			//NumberFormatException is never thrown because the regex checks that the string only contains numbers and -
					
			this.dateError = dc.checkDate(dayNumber, monthNumber, yearNumber);
			if (this.dateError != null) return;		
		
			this.date = dc.fromStrToDate(date);
			
			if (dc.isPastDate(this.date)) {
				this.dateError = "Date cannot be in the past.";
			}

		} else {
			this.dateError = "Date must be in the format yyyy-MM-dd.";
		}

	}


	/**
	 * Sets time in a proper format for SQL queries and checks the validity of the
	 * inserted parameter.
	 * @param time 		the time inserted.
	 */
	public void setTime(String time) {
		DateChecker dc = new DateChecker();

		if (time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {

			this.time = dc.fromStrToTime(time);
			
			//we need the date to confront it with today, so if it wasn't set, time can't be set either
			if(this.date == null) return;
			
			//if date == today's date, checks if time is in the past
			if (dc.isToday(this.date) && dc.isPastTime(this.time)) {
				this.timeError = "Time cannot be in the past.";
			} else {
				this.timeError = null;
			}

		} else {
			this.timeError = "Time must be in the format hh:mm.";
		}
	}


	/**
	 * Sets the duration of the meeting checking that the number inserted
	 * is >0 and <=720.
	 * @param duration	the duration inserted.
	 */
	public void setDuration(int duration) {
		this.duration = duration;
		
		if (duration == 0) {
			this.durationError = "Duration can't be zero minutes.";
		} else if (duration > 720) {
			this.durationError = "Duration can't be more than 12 hours.";
		} else {
			this.durationError = null;
		}
	}


	/**
	 * Sets the maximum number of participants for the meeting checking that
	 * the number inserted is >0 and <=50.
	 * @param maxPart	the number inserted.
	 */
	public void setMaxPart(int maxPart) {
		this.maxPart = maxPart;
		
		if (maxPart == 0) {
			this.maxPartError = "Participants can't be zero.";
		} else if (maxPart > 50) {
			this.maxPartError = "You can't invite more than 50 people.";
		} else {
			this.maxPartError = null;
		}
	}

	
	/* The following methods are getters for the class' attributes */
	
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


	/**
	 * This method is a getter for all the error strings.
	 *
	 * @return			a String containing the errors.
	 */
	public String getErrors(){
		String error = " ";

		if(titleError!=null) error=error.concat(titleError+" ");
		if(dateError!=null) error=error.concat(dateError+" ");
		if(timeError!=null) error=error.concat(timeError+" ");
		if(durationError!=null) error=error.concat(durationError+" ");
		if(maxPartError!=null) error=error.concat(maxPartError+" ");

		return error;
	}

	
	/**
	 * Checks the validity of the user input for the meeting creation.
	 * If all the error strings == null -> there are no errors -> the form is
	 * valid -> the user can proceed with the creation of the meeting, selecting
	 * the participants.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if all the input is valid;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	public boolean isValid() {
		return (this.titleError == null && this.dateError == null && this.timeError == null
				&& this.durationError == null && this.maxPartError == null);
	}

}