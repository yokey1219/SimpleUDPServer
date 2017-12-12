import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ef.carparking.app.domain.AppClientMsg;
import com.ef.carparking.domain.DeviceMsg;
import com.ef.carparking.util.UtilTools;

public class UDPServer {

	static DatagramSocket serverapp;
	static DatagramSocket serverdev;
	//static List<LoginItem> lists;

	static DevMsgHandler handler;
	static APPMsgHandler apphandler;
	static Boolean flag;

	protected static Queue<DatagramPacket> devqueue;
	protected static Queue<DatagramPacket> appqueue;
	
	public static void main(String[] args) throws IOException {

		int port1 = 8081;
		int port2 = 8082;
		if (args.length > 1) {
			for (String arg : args) {
				if (arg.startsWith("-p1")) {

					port1 = Integer.valueOf(arg.replace("-p1", "")).intValue();
				} else if (arg.startsWith("-p2")) {
					port2 = Integer.valueOf(arg.replace("-p2", "")).intValue();
				}
			}
		}
		System.out.println(String.format("p1:%d", port1));
		System.out.println(String.format("p2:%d", port2));
		serverapp = new DatagramSocket(port2);
		serverdev = new DatagramSocket(port1);
		
		devqueue=new ConcurrentLinkedQueue<DatagramPacket>();
		appqueue=new ConcurrentLinkedQueue<DatagramPacket>();
		
		flag = true;
		DevRecvThread thread1 = new DevRecvThread();
		AppRecvThread thread2 = new AppRecvThread();
		DevSendThread thread3 = new DevSendThread();
		AppSendThread thread4 = new AppSendThread();
		handler = new DevMsgHandler();
		apphandler=new APPMsgHandler();
		thread1.start();
		thread2.start();
		handler.start();
		thread3.start();
		thread4.start();
		apphandler.start();

		do {
			/*
			 * server.receive(recvPacket); String recvStr = new
			 * String(recvPacket.getData() , 0 , recvPacket.getLength());
			 * System.out.println("Hello World!" + recvStr); int port =
			 * recvPacket.getPort(); InetAddress addr = recvPacket.getAddress();
			 * String sendStr; if(recvStr.equals("close")) { sendStr = "ByeBye";
			 * flag=false; } else { sendStr =
			 * String.format("Hello ! I'm Server,I got your words: %s",recvStr);
			 * } byte[] sendBuf; sendBuf = sendStr.getBytes(); DatagramPacket
			 * sendPacket = new DatagramPacket(sendBuf , sendBuf.length , addr ,
			 * port ); server.send(sendPacket);
			 */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (flag);
		serverdev.close();
		serverapp.close();
	}

	static class DevRecvThread extends Thread {
		public void run() {
			byte[] recvBuf = new byte[100];
			DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("Server is started!");
			
			try {
				serverdev.setSoTimeout(5000);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			do {
				try {
					serverdev.receive(recvPacket);

					String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());

					int port = recvPacket.getPort();
					InetAddress addr = recvPacket.getAddress();
					String recvfmt = String.format("%s\t%s\t%d\t%s", sdf.format(new Date()), addr.getHostAddress(),
							port, recvStr);
					System.out.println(recvfmt);
					
					
					byte[] sendBuf = null;

					DeviceMsg devmsg = new DeviceMsg(recvPacket.getData(), recvPacket.getLength());
					devmsg.setRemote(addr,port);
					if (devmsg.isAvilable()) {
						
						sendBuf = devmsg.makeACK();
						handler.AddMsg(devmsg);
					}

					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
					serverdev.send(sendPacket);

				} catch (IOException e) {

				}
			} while (flag);

		}
	}
	
	
	static class DevSendThread extends Thread {
		public void run() {
			
			do {
				try {
					DatagramPacket tosend=devqueue.poll();
					if(tosend!=null)
					{
						serverdev.send(tosend);
					}

				} catch (IOException e) {

				}
				
				
			} while (flag);

		}
	}

	static class AppRecvThread extends Thread {
		public void run() {
			byte[] recvBuf = new byte[100];

			DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

			do {
				try {
					serverapp.receive(recvPacket);
					String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
//					String[] recvs = recvStr.split(":");
					System.out.println(recvStr);
					int port = recvPacket.getPort();
					InetAddress addr = recvPacket.getAddress();
					AppClientMsg msg=UtilTools.sglobalGson.fromJson(recvStr, AppClientMsg.class);
					msg.setRemote(addr, port);
					apphandler.AddMsg(msg);
//					if (recvs.length <= 1)
//						continue;
//					if (recvs[0].equals("attach")) {
//						System.out.println("Hello login:" + recvStr);
//
//						String sendStr = "hello!";
//						byte[] sendBuf;
//						sendBuf = sendStr.getBytes();
//						DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
//						serverapp.send(sendPacket);
//					
//					} else if (recvs[0].equals("dettach")) {
//						String sendStr = "byebye!";
//						byte[] sendBuf;
//						sendBuf = sendStr.getBytes();
//						DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
//						serverapp.send(sendPacket);
////						LoginItem item = new LoginItem();
////						item.addr = addr;
////						item.port = port;
////						synchronized (lists) {
////							if (lists.contains(item))
////								lists.remove(item);
////						}
//
//					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (flag);

		}
	}
	
	static void PrepareDevSendPacket(byte[] data,InetAddress addr,int port)
	{
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr,port);
		devqueue.offer(sendPacket);
	}
	
	static void PrepareAppSendPacket(String data,InetAddress addr,int port)
	{
		byte[] sendBuf;
		sendBuf = data.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr,port);
		appqueue.offer(sendPacket);
	}
	
	static class AppSendThread extends Thread {
		public void run() {
			
			do {
				try {
					DatagramPacket tosend=appqueue.poll();
					if(tosend!=null)
					{
						serverapp.send(tosend);
					}

				} catch (IOException e) {

				}
				
				
			} while (flag);

		}
	}

//	static class LoginItem {
//		public InetAddress addr;
//		public int port;
//
//		@Override
//		public boolean equals(Object obj) {
//			if (obj instanceof LoginItem) {
//				LoginItem item = (LoginItem) obj;
//				return item.addr.toString().equals(this.addr.toString()) && item.port == this.port;
//			} else
//				return false;
//		}
//	}

}
