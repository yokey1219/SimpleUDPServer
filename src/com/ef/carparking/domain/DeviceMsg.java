package com.ef.carparking.domain;

public class DeviceMsg {
	protected byte[] msgbuffer;
	protected int msgno; //2-byte,0~65535
	protected int cmdid; //2-byte,0~65535
	protected int seriallen;//1-byte,0~255
	protected String serialno;//n-byte,n=seriallen
	protected int datalen;//2-byte,0~65535
	protected byte[] databuffer;
	protected int dataidx;
	protected boolean avilable;
	

	public byte[] getMsgbuffer() {
		return msgbuffer;
	}

	public void setMsgbuffer(byte[] msgbuffer) {
		this.msgbuffer = msgbuffer;
	}

	public int getMsgno() {
		return msgno;
	}

	public void setMsgno(int msgno) {
		this.msgno = msgno;
	}

	public int getCmdid() {
		return cmdid;
	}

	public void setCmdid(int cmdid) {
		this.cmdid = cmdid;
	}

	public int getSeriallen() {
		return seriallen;
	}

	public void setSeriallen(int seriallen) {
		this.seriallen = seriallen;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public int getDatalen() {
		return datalen;
	}

	public void setDatalen(int datalen) {
		this.datalen = datalen;
	}

	public byte[] getDatabuffer() {
		return databuffer;
	}

	public void setDatabuffer(byte[] databuffer) {
		this.databuffer = databuffer;
	}

	public int getDataidx() {
		return dataidx;
	}

	public void setDataidx(int dataidx) {
		this.dataidx = dataidx;
	}
	
	
	
	public boolean isAvilable() {
		return avilable;
	}



	public static final int CMDID_LOGIN=0;
	public static final int CMDID_BINDPARKING=1;
	public static final int CMDID_REPORTCARPARKING=2;
	public static final int CMDID_REPORTSTATUS=3;
	
	protected static final int MINLEN=10;
	
	public DeviceMsg(byte[] buffer)
	{
		this.avilable=false;
		msgbuffer=buffer.clone();
		if(msgbuffer.length>=MINLEN)
		{
			msgno=(int)(msgbuffer[0]<<8)+msgbuffer[1];
			cmdid=(int)(msgbuffer[2]<<8)+msgbuffer[3];
			seriallen=(int)(msgbuffer[4]);
			
			serialno=new String(msgbuffer,5,seriallen);
			
			datalen=(int)(msgbuffer[5+seriallen]<<8)+msgbuffer[6+seriallen];
			dataidx=7+seriallen;
			databuffer=new byte[datalen];
			if(datalen!=(msgbuffer.length-seriallen-7))
			{
				
			}
			System.arraycopy(msgbuffer,dataidx, databuffer, 0, datalen);
			this.avilable=true;
		}
	}
	
	public byte[] makeACK()
	{
		byte[] ack=new byte[4];
		System.arraycopy(msgbuffer, 0, ack, 0, ack.length);
		return ack;
	}
	
	
}
