package com.melnykov.dashclock.mailruextension.net;

public class MailRuApiException extends Exception {

    public static int UNKNOWN_ERROR = 1;
    public static int AUTHORIZATION_FAILED = 102;

	private int errorCode;
	
	public MailRuApiException(int errorCode){
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}


