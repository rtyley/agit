package com.madgag.agit;


public class Time {
	public static String timeSinceSeconds(int epochTimeInSeconds) {
		return timeSinceMS(epochTimeInSeconds*1000L); 
	}
	
	public static String timeSinceMS(long epochTimeInMS) {
		long ms = System.currentTimeMillis() - epochTimeInMS;
		long sec = ms / 1000;
		long min = sec / 60;
		long hour = min / 60;
		long day = hour / 24;
		String end;
		if (day > 0) {
			if (day == 1) {
				end = " day ago";
			} else {
				end = " days ago";
			}
			return (day + end);
		}
		if (hour > 0) {
			if (hour == 1) {
				end = " hour ago";
			} else {
				end = " hours ago";
			}
			return (hour + end);
		} 
		if (min > 0) {
			if (min == 1) {
				end = " minute ago";
			} else {
				end = " minutes ago";
			}
			return (min + end);
		} 
		if (sec == 1) {
			end = " second ago";
		} else {
			end = " seconds ago";
		}
		return (sec + end);
	}
}
