package cn.geniusman.exception;

public class CommunicationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -335026656751093927L;

	/**
	 * @param message
	 */
	public CommunicationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}
}
