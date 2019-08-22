package pt.aubay.testesproject.execptionHandling;

import java.io.Serializable;

public class AppException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;
	private int statusCode;
	
	public AppException() {
		super();
	}
	
	public AppException(String msg) {
		super(msg);
	}
	
	public AppException(String msg, int statusCode) {
		super(msg);
		this.statusCode=statusCode;
	}
	
	public AppException(String msg, Exception e) {
		super(msg, e);
	}
	
	public AppException(String msg, Exception e, int statusCode) {
		super(msg, e);
		this.setStatusCode(statusCode);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
}
