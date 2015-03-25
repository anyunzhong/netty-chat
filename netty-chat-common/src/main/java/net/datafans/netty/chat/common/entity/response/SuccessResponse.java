package net.datafans.netty.chat.common.entity.response;

import java.util.Map;

public class SuccessResponse extends BaseResponse {

	private Map<String, Object> data;

	public SuccessResponse(Map<String, Object> data) {
		setStatus(ResponseStatus.STATUS_OK.getStatus());
		setData(data);

	}

	public SuccessResponse() {
		setStatus(ResponseStatus.STATUS_OK.getStatus());
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
