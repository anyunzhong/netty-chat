package net.datafans.netty.chat.common.entity.response;

public enum ResponseStatus {
	STATUS_OK(1),STATUS_ERROR(0);
	
	private int status;
	ResponseStatus(int status){
		this.setStatus(status);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
