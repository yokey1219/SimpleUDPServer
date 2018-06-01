import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ef.carparking.domain.DeviceCarparkInfo;
import com.ef.carparking.domain.DeviceInfo;
import com.ef.carparking.domain.DeviceLog;
import com.ef.carparking.domain.DeviceMsg;

public class DevMsgHandler extends Thread {
	protected Queue<DeviceMsg> msgqueue;
	
	public DevMsgHandler()
	{
		this.msgqueue=new ConcurrentLinkedQueue<DeviceMsg>();
	}
	
	public boolean AddMsg(DeviceMsg msg)
	{
		return this.msgqueue.offer(msg);
	}
	
	public void run() {

		do {
			try {
				DeviceMsg msg=this.msgqueue.poll();
				if(msg!=null)
				{
					String sn=msg.getSerialno();
					DeviceInfo info=DbHelper.GetDeviceInfo(sn);
					
					int cmdid=msg.getCmdid();
					switch(cmdid)
					{
						case DeviceMsg.CMDID_LOGIN://登录注册
							if(info==null)
							{
								info=new DeviceInfo();
								info.setImei(sn);
								info.setRegisterat(new Date());
								info.setLastip(msg.getRemoteaddr().getHostAddress());
								info.setLastlogin(msg.getRecvat());
								info.setState(0);
								DbHelper.Insert(info);
							}
							else
							{
								info.setLastip(msg.getRemoteaddr().getHostAddress());
								info.setLastlogin(msg.getRecvat());
								info.setState(1);
								DbHelper.Update(info);
							}
							break;
						case DeviceMsg.CMDID_STATESYNC://状态同步
							break;
						case DeviceMsg.CMDID_REPORTCARPARKING://车辆状态报告
							if(info!=null)
							{
								DeviceCarparkInfo parkinfo=new DeviceCarparkInfo();
								parkinfo.deviceid=info.getDeviceid();
								parkinfo.imei=info.getImei();
								String statehex=msg.getHexContent();
								int parstate=Integer.valueOf(statehex, 16).intValue();
								parkinfo.carparkstate=parstate;
								parkinfo.lastupdat=msg.getRecvat();
								DbHelper.Insert(parkinfo);
								//通知关注者
								//System.out.println("notify carparking");
								DevWatchControl.NotifyDevMsg(msg);
							}
							break;
						default:
							break;
					}
					
					if(info!=null)
					{
						DeviceLog log=new DeviceLog();
						log.setDeviceid(info.getDeviceid());
						log.setInfodate(msg.getRecvat());
						log.setInfotype(msg.getCmdid());
						log.setInfocontent(msg.getHexContent());
						log.setInfoip(msg.getRemoteaddr().getHostAddress());
						DbHelper.Insert(log);
					}
					
				}
				Thread.sleep(100);
			} catch (Exception e) {

			}
		} while (true);

	}
}
