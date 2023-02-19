package ati.player.rest.api.exception;

/**
 * Error codes
 * 
 * @author bright_zheng
 *
 */
public enum ErrorCode {

	//SUCCESS CODES
	SUCCESS(			"0", "Successfully Submitted");
	
	public String code;
	public String message;
	public String errorMessage;

	private ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public boolean equals(ErrorCode c) {
		return this.code == c.code;
	}

	public boolean equals(String code) {
		return this.code == code;
	}

	public static ErrorCode fromCode(String code) {
		for (ErrorCode v : ErrorCode.values()) {
			if (v.code.equals(code)) {
				return v;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
