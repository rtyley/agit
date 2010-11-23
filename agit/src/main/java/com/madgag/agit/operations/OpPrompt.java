package com.madgag.agit.operations;

public class OpPrompt<T> {
	private final OpNotification opNotification;
	private final Class<T> requiredResponseType;
	
	
	public OpPrompt(OpNotification opNotification, Class<T> requiredResponseType) {
		this.opNotification = opNotification;
		this.requiredResponseType = requiredResponseType;
	}
	
	public OpNotification getOpNotification() {
		return opNotification;
	}
	
	public Class<T> getRequiredResponseType() {
		return requiredResponseType;
	}
}
