package net.datafans.netty.chat.common.entity.response;

public class ErrorResponse extends BaseResponse {
	private int errorCode;
	private String errorMsg;
	
	public ErrorResponse(int errorCode, String errorMsg){
		setStatus(ResponseStatus.STATUS_ERROR.getStatus());
		setErrorCode(errorCode);
		setErrorMsg(errorMsg);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
