package com.bfd.download.plugin;

import java.util.List;
import java.util.Map;

public class AjaxRequestEntity {

	private int num;

	private boolean status;

	private List<Map<String, Object>> ajax;

	private int code;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<Map<String, Object>> getAjax() {
		return ajax;
	}

	public void setAjax(List<Map<String, Object>> ajax) {
		this.ajax = ajax;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "AjaxRequestEntity [num=" + num + ", status=" + status + ", ajax=" + ajax + ", code=" + code + "]";
	}

	public AjaxRequestEntity() {
		// TODO Auto-generated constructor stub
	}

}
