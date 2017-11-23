package com.ef.carpaking.domain;

import java.util.Date;

public class DeviceLog {
	public int rid;
	public int deviceid;
	public int infotype;
	public int getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(int deviceid) {
		this.deviceid = deviceid;
	}
	public int getInfotype() {
		return infotype;
	}
	public void setInfotype(int infotype) {
		this.infotype = infotype;
	}
	public String getInfocontent() {
		return infocontent;
	}
	public void setInfocontent(String infocontent) {
		this.infocontent = infocontent;
	}
	public Date getInfodate() {
		return infodate;
	}
	public void setInfodate(Date infodate) {
		this.infodate = infodate;
	}
	public String infocontent;
	public Date infodate;
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	
}
