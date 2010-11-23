package com.madgag.agit.operations;

public class OpNotification {
	private final int icon;
	private final String tickerText, eventTitle, eventDetail;
	
	public OpNotification(int icon, String tickerText, String eventTitle, String eventDetail) {
		this.icon = icon;
		this.tickerText = tickerText;
		this.eventTitle = eventTitle;
		this.eventDetail = eventDetail;
	}

	public int getDrawable() {
		return icon;
	}

	public String getTickerText() {
		return tickerText;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public String getEventDetail() {
		return eventDetail;
	}
}
