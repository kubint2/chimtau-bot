package ati.player.rest.api.exception;

/**
 * @author binhlv
 *
 */
public class BotServiceException extends RuntimeException {

	private static final long serialVersionUID = 8317790885199050056L;
	private ErrorCode errorCode;

	public BotServiceException() {
		super();
	}

	public BotServiceException(String message) {
		super(message);
	}

	public BotServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BotServiceException(Throwable cause) {
		super(cause);
	}
	
	public BotServiceException(ErrorCode errorCode) {
		super("ErrorCode: " + errorCode.code + ", ErrorMessage: " +  errorCode.message);
		this.errorCode = errorCode;
	}

	public BotServiceException(ErrorCode errorCode, Throwable cause) {
		super("ErrorCode: " + errorCode.code + ", ErrorMessage: " +  errorCode.message, cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
