package com.ef.carparking.domain;

import java.util.Date;

public class DeviceInfo {
	public int deviceid;
	public String imei;
	public Date registerat;
	public Date lastlogin;
	public String lastip;
	public int state;//0-新状态；1-正常工作；2-低电量；3-离线；4-异常；5-故障；
	public int getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(int deviceid) {
		this.deviceid = deviceid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public Date getRegisterat() {
		return registerat;
	}
	public void setRegisterat(Date registerat) {
		this.registerat = registerat;
	}
	public Date getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}
	public String getLastip() {
		return lastip;
	}
	public void setLastip(String lastip) {
		this.lastip = lastip;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	
}
