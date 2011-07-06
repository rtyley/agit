/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.util;


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
