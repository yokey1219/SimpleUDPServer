import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ef.carparking.app.domain.AppCallBackData;
import com.ef.carparking.app.domain.AppCmdArg;
import com.ef.carparking.app.domain.DevMsgToAPP;
import com.ef.carparking.domain.DeviceMsg;
import com.ef.carparking.util.UtilTools;

public class DevWatchControl {
	protected static Map<String,List<Watcher>> watchmap=new HashMap<String,List<Watcher>>();
	protected static final int MAX_OUTOFDATE_MS=10*60*1000;
	
	public static void NotifyDevMsg(DeviceMsg msg)
	{
		
		if(msg.getCmdid()!=DeviceMsg.CMDID_REPORTCARPARKING) return;
		String imei=msg.getSerialno();
		DevMsgToAPP devmsg=new DevMsgToAPP();
		devmsg.imei=imei;
		devmsg.cmdid=msg.getCmdid();
		devmsg.args=new ArrayList<AppCmdArg>();
		AppCmdArg arg=new AppCmdArg();
		arg.argname="carparking";
		arg.argtype="String";
		arg.argvalue=msg.getHexContent();
		devmsg.args.add(arg);
		if(watchmap.containsKey(imei))
		{
			List<Watcher> list=new ArrayList<Watcher>();
			list.addAll(watchmap.get(imei));
			new Thread(new Runnable()
				{
					@Override
					public void run() {
						try {
							AppCallBackData data=new AppCallBackData();
							data.rsltcode=0;
							data.data=devmsg;
							data.keepthread=true;
							String send=UtilTools.sglobalGson.toJson(data);
							byte[] sendbuffer=null;
							try {
								sendbuffer = send.getBytes("utf-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							DatagramSocket socket=new DatagramSocket();
							DatagramPacket pack=new DatagramPacket(sendbuffer,0,sendbuffer.length);
							for(Watcher watcher:list)
							{
								pack.setAddress(watcher.addr);
								pack.setPort(watcher.port);
								try {
									socket.send(pack);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							socket.close();
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						list.clear();
					}
			
				}).start();
		}
	}
	
	public static void AddWatch(String imei,InetAddress remoteaddr,int remoteport)
	{
		List<Watcher> list=null;
		if(watchmap.containsKey(imei))
		{
			list=watchmap.get(imei);
		}
		else
		{
			list=new ArrayList<Watcher>();
			watchmap.put(imei, list);
		}
		Watcher watcher=null;
		for(Watcher tmp:list)
		{
			if(tmp.addr.getHostAddress().equals(remoteaddr.getHostAddress())&&tmp.port==remoteport)
			{
				watcher=tmp;
			}
		}
		if(watcher!=null)
		{
			watcher.updtime=(new Date()).getTime();
		}
		else
		{
			watcher=new Watcher();
			watcher.imei=imei;
			watcher.addr=remoteaddr;
			watcher.port=remoteport;
			watcher.updtime=(new Date()).getTime();
			list.add(watcher);
		}
	}
	
	public static void RemoveWatch(String imei,InetAddress remoteaddr,int remoteport)
	{
		List<Watcher> list=null;
		if(watchmap.containsKey(imei))
		{
			list=watchmap.get(imei);
			Watcher watcher=null;
			for(Watcher tmp:list)
			{
				if(tmp.addr.getHostAddress().equals(remoteaddr.getHostAddress())&&tmp.port==remoteport)
				{
					watcher=tmp;
				}
			}
			if(watcher!=null)
				list.remove(watcher);
		}
	}
	
	public static void ClearOutofdateWatch()
	{
		long time=(new Date()).getTime();
		for(Entry<String,List<Watcher>>  entry: watchmap.entrySet())
		{
			List<Watcher>  list=new ArrayList<Watcher>();
			for(Watcher watcher:entry.getValue())
			{
				if((time-watcher.updtime)<MAX_OUTOFDATE_MS)
				{
					list.add(watcher);
				}
			}
			entry.setValue(list);
		}
	}
	
	protected static class Watcher
	{
		public String imei;
		public InetAddress addr;
		public int port;
		public long updtime;
	}
}
