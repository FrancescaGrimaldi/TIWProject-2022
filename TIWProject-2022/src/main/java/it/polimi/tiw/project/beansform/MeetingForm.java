package it.polimi.tiw.project.beansform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Time;

/**
* This class provides methods to check if the input inserted in 
* the createMeeting form is correct and displays potential errors.
*/
public class MeetingForm {
	private String title;
	private Date date; // in SQL date format
	private Time time; // in SQL time format
	private int duration;
	private int maxPart;
	private String titleError;
	private String dateError;
	private String timeError;
	private String durationError;
	private String maxPartError;

	public MeetingForm() {
		super();
	}

	public MeetingForm(String title, String date, String time, int duration, int maxPart) {
		super();
		this.setTitle(title);
		this.setDate(date);
		this.setTime(time);
		this.setDuration(duration);
		this.setMaxPart(maxPart);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if (title == null || title.isEmpty()) {
			this.titleError = "You didn't enter a title!";
		} else {
			this.titleError = null;
		}
	}

	public Date getDate() {
		return date;
	}

	/**
	 * Sets date in a proper format for SQL queries and checks the validity of the
	 * inserted parameter.
	 * 
	 * @param date the date inserted.
	 */
	public void setDate(String date) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		// checks if date format matches a regexp
		if (date.matches("[0-9]{2}[/]{1}[0-9]{2}[/]{1}[0-9]{4}")) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.setLenient(false);

			// parses the String date in order to have a (sql.)Date object
			Date d = null;
			try {
				d = (java.sql.Date) sdf.parse(date);
				this.date = d;

				// checks if date is in the past
				if (d.before(c.getTime())) {
					this.dateError = "Date cannot be in the past";
				} else {
					this.dateError = null;
				}

			} catch (ParseException exc) {
				exc.printStackTrace();
			}
		} else {
			this.dateError = "Please insert date in the format dd/MM/yyyy";
		}

	}

	public Time getTime() {
		return time;
	}

	/**
	 * Sets time in a proper format for SQL queries and checks the validity of the
	 * inserted parameter.
	 * 
	 * @param time the time inserted.
	 */
	public void setTime(String time) {
		Calendar c = Calendar.getInstance();

		// checks if time format matches the regexp
		if (time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {

			SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
			stf.setLenient(false);

			// parses the String time in order to have a Time object
			Time t = null;
			try {
				t = (Time) stf.parse(time);
				this.time = t;

				// if date == today's date, checks if time is in the past
				if (this.date.equals((Date) c.getTime()) && t.before((Time) c.getTime())) {
					this.timeError = "Time cannot be in the past";
				} else {
					this.timeError = null;
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} else {
			this.timeError = "Please insert time in the format hh:mm";
		}

	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
		if (duration == 0) {
			this.durationError = "Duration can't be zero minutes!";
		} else {
			this.durationError = null;
		}
	}

	public int getMaxPart() {
		return maxPart;
	}

	public void setMaxPart(int maxPart) {
		this.maxPart = maxPart;
		if (maxPart == 0) {
			this.maxPartError = "Participants can't be zero!";
		} else if (maxPart > 50) {
			this.maxPartError = "You can't invite more than 50 people!";
		} else {
			this.maxPartError = null;
		}
	}

	public String getTitleError() {
		return titleError;
	}

	public String getDateError() {
		return dateError;
	}

	public String getTimeError() {
		return timeError;
	}

	public String getDurationError() {
		return durationError;
	}

	public String getMaxPartError() {
		return maxPartError;
	}

	public boolean isValid() {
		return (this.titleError == null && this.dateError == null && this.timeError == null
				&& this.durationError == null && this.maxPartError == null);
	}

}

