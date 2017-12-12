import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ef.carparking.app.domain.AppCallBackData;
import com.ef.carparking.app.domain.AppClientInfo;
import com.ef.carparking.app.domain.AppClientMsg;
import com.ef.carparking.app.domain.AppCommand;
import com.ef.carparking.app.domain.AppCommandUtil;
import com.ef.carparking.app.domain.AppMsgLog;
import com.ef.carparking.domain.DeviceInfo;
import com.ef.carparking.util.UtilTools;


public class APPMsgHandler extends Thread {

protected Queue<AppClientMsg> msgqueue;
	
	public APPMsgHandler()
	{
		this.msgqueue=new ConcurrentLinkedQueue<AppClientMsg>();
	}
	
	public boolean AddMsg(AppClientMsg msg)
	{
		return this.msgqueue.offer(msg);
	}
	
	public void run() {

		do {
			try {
				AppClientMsg msg=this.msgqueue.poll();
				
				if(msg!=null)
				{
					String sn=msg.getSn();
					String token=msg.getToken();
					AppClientInfo cliinfo=DbHelper.GetAppInfo(sn);
					if(cliinfo==null) continue;
					
					AppMsgLog log=new AppMsgLog();
					log.setInfo(cliinfo);
					log.setMsg(msg);
					log.setMsgdate(new Date());
					
					String cmdname=msg.getCmd();
					AppCommand appcmd=AppCommandUtil.getCommand(cmdname);
					AppCallBackData data=null;
					if(appcmd!=null)
					{
						switch(appcmd.cmdid)
						{
							case AppCommandUtil.APPCMD_LOGIN://登录注册
								data=new AppCallBackData();
								data.rsltcode=0;
								data.data=null;
								UDPServer.PrepareAppSendPacket(UtilTools.sglobalGson.toJson(data), msg.getRemoteaddr(), msg.getRemoteport());
								break;
							case AppCommandUtil.APPCMD_LISTDEVS://列出设备
								List<DeviceInfo> list=DbHelper.GetDeviceInfoList();
								data=new AppCallBackData();
								data.rsltcode=0;
								data.data=list;
								UDPServer.PrepareAppSendPacket(UtilTools.sglobalGson.toJson(data), msg.getRemoteaddr(), msg.getRemoteport());
								break;
							case AppCommandUtil.APPCMD_GETDEV://设备信息
								
								break;
							case AppCommandUtil.APPCMD_OPDEV://操作设备
								
								break;
							case AppCommandUtil.APPCMD_WATCHDEV://关注设备
								if(msg.getArgs()!=null&&msg.getArgs().size()>0)
								{	
									if(msg.getArgs().get(0).argname=="imei")
									{
										String imei=msg.getArgs().get(0).argvalue;
										DevWatchControl.AddWatch(imei, msg.getRemoteaddr(),msg.getRemoteport());
									}
								}
								break;
							case AppCommandUtil.APPCMD_DEWATCHDEV://取消关注设备
								if(msg.getArgs()!=null&&msg.getArgs().size()>0)
								{	
									if(msg.getArgs().get(0).argname=="imei")
									{
										String imei=msg.getArgs().get(0).argvalue;
										DevWatchControl.RemoveWatch(imei, msg.getRemoteaddr(),msg.getRemoteport());
									}
								}
								break;
							default:
								break;
						}
						
						
					}
					
					DbHelper.Insert(log);
					
				}
				DevWatchControl.ClearOutofdateWatch();//清理一下过期的关注
				Thread.sleep(100);
			} catch (Exception e) {

			}
		} while (true);

	}
}
