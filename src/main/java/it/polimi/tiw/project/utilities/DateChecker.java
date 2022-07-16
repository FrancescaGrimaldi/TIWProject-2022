package it.polimi.tiw.project.utilities;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * This class contains useful methods to work with and convert date/time input.
 */
public class DateChecker {
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	
	/**
	 * Class constructor.
	 */
	public DateChecker(){
	}
	
	
	/**
	 * Converts a String into a sql.Date object.
	 * @param string	the String to convert.
	 * @return			the corresponding sql.Date object.
	 */
	public java.sql.Date fromStrToDate(String string) {
		Date javaDate = null;
		java.sql.Date sqlDate;
		
		try {
			javaDate = dateFormatter.parse(string);
		} catch (ParseException pex) {
			pex.printStackTrace();
		}
		
		sqlDate = new java.sql.Date(javaDate.getTime());
		return sqlDate;
	}
	
	
	/**
	 * Converts a String into a sql.Time object.
	 * @param string	the String to convert.
	 * @return			the corresponding sql.Time object.
	 */
	public java.sql.Time fromStrToTime(String string) {
		Date javaTime = null;
		java.sql.Time sqlTime;
		
		try {
			javaTime = timeFormatter.parse(string);
		} catch (ParseException pex) {
			pex.printStackTrace();
		}
		
		sqlTime = new java.sql.Time(javaTime.getTime());
		return sqlTime;
	}
	
	
	/**
	 * States whether the given date is in the past.
	 * @param date		the sql.Date to check.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if it is in the past;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	public boolean isPastDate(java.sql.Date date) {
		java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
		
		if (date.before(today)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * States whether the given time is in the past.
	 * @param date		the sql.Time to check.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if it is in the past;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	public boolean isPastTime(java.sql.Time time) {
		Date todayTime;
		
		try {
			todayTime = timeFormatter.parse(java.time.LocalTime.now().toString());
			if(time.before(todayTime)) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException pex) {
			pex.printStackTrace();
			return false;
		}

	}
	
	
	/**
	 * States whether the given date matches today's date.
	 * @param date		the sql.Date to check.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if it is today;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	public boolean isToday(java.sql.Date date) {
		Date todayDate;
	
		try {
			todayDate = dateFormatter.parse(java.time.LocalDate.now().toString());
			if(date.compareTo(todayDate) == 0) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException pex) {
			pex.printStackTrace();
			return false;
		}
	}
	
}
