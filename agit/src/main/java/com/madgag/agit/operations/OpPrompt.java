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

    public static OpPrompt<Boolean> promptYesOrNo(OpNotification opNotification) {
        return prompt(Boolean.class, opNotification);
    }

    public static <T> OpPrompt<T> prompt(Class<T> requiredResponseType, OpNotification opNotification) {
        return new OpPrompt<T>(opNotification, requiredResponseType);
    }
}
