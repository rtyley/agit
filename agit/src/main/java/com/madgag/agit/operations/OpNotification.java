package com.madgag.agit.operations;

public class OpNotification {
	private final int icon;
	private final String tickerText, eventTitle, eventDetail;
    private final boolean successful;

    public OpNotification(int icon, String tickerText, String eventDetail) {
		this(icon, tickerText, tickerText, eventDetail, true);
	}

	public OpNotification(int icon, String tickerText, String eventTitle, String eventDetail) {
		this(icon, tickerText, eventTitle, eventDetail, true);
	}

    public OpNotification(int icon, String tickerText, String eventTitle, String eventDetail, boolean successful) {
		this.icon = icon;
		this.tickerText = tickerText;
		this.eventTitle = eventTitle;
		this.eventDetail = eventDetail;
        this.successful = successful;
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
