package net.datafans.netty.chat.common.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;

import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.constant.Version;
import net.datafans.netty.chat.common.entity.response.ErrorResponse;
import net.datafans.netty.chat.common.entity.response.SuccessResponse;

public class DataPackage {

	public final static DataPackage HEARTBEAT_PACKAGE = new DataPackage(Version.V1, BizType.HEARTBEAT);
	public final static DataPackage LOGIN_SUCCESS_PACKAGE = new DataPackage(Version.V1, BizType.LOGIN, JSON
			.toJSONString(new SuccessResponse()).getBytes());
	public final static DataPackage LOGIN_FAIL_PACKAGE = new DataPackage(Version.V1, BizType.LOGIN, JSON.toJSONString(
			new ErrorResponse(1001, "login failed")).getBytes());
	public final static DataPackage LOGIN_INVALID_PACKAGE = new DataPackage(Version.V1, BizType.LOGIN, JSON.toJSONString(
			new ErrorResponse(1002, "login invalid")).getBytes());
	
	
	private int size;
	private byte[] version;
	private int id;
	private byte[] type;
	private byte[] common;
	private byte[] content;

	public DataPackage() {

	}

	public DataPackage(byte[] version, byte[] type) {
		super();
		this.version = version;
		this.type = type;
	}
	
	public DataPackage(byte[] version, byte[] type, int msgId) {
		super();
		this.version = version;
		this.type = type;
	}

	public DataPackage(byte[] version, byte[] type, byte[] content) {
		super();
		this.version = version;
		this.type = type;
		this.content = content;
	}

	public byte[] getVersion() {
		return version;
	}

	public void setVersion(byte[] version) {
		this.version = version;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getType() {
		return type;
	}

	public void setType(byte[] type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getCommon() {
		return common;
	}

	public void setCommon(byte[] common) {
		this.common = common;
	}

	@Override
	public String toString() {
		return "DataPackage [size=" + size + ", version=" + version + ", id=" + id + ", type=" + type + ", common="
				+ Arrays.toString(common) + ", content=" + Arrays.toString(content) + "]";
	}

}
