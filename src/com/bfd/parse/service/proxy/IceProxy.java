package com.bfd.parse.service.proxy;

public interface IceProxy {

	public static final int STATUS_OK = 0;

	public static final int STATUS_INITFAILED = 1;

	public static final int STATUS_REFUSE = 2;

	public static final int STATUS_RELEASE = 3;

	public void release();

	public Integer getStatus();

	public String name();

}
