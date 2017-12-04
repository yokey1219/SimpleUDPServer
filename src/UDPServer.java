import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ef.carparking.domain.DeviceMsg;

public class UDPServer {

	static DatagramSocket  serverlogin;
	static DatagramSocket  server;
	static List<LoginItem> lists;
	
	
	static Boolean flag;
    
	public static void main(String[] args) throws IOException {
		
		int port1=8081;
		int port2=8082;
		if(args.length>1)
		{
			for(String arg:args)
			{
				if(arg.startsWith("-p1"))
				{
					
					port1=Integer.valueOf(arg.replace("-p1","")).intValue();
				}
				else if(arg.startsWith("-p2"))
				{
					port2=Integer.valueOf(arg.replace("-p2", "")).intValue();
				}
			}
		}
		System.out.println(String.format("p1:%d",port1));
		System.out.println(String.format("p2:%d",port2));
		serverlogin=new DatagramSocket(port2);
		server = new DatagramSocket(port1);
		lists=new ArrayList<LoginItem>();
        flag=true;
        MyThread thread1=new MyThread();
        MyThreadlogin thread2=new MyThreadlogin();
        thread1.start();
        thread2.start();
        
        do
        {
        /*server.receive(recvPacket);
        String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
        System.out.println("Hello World!" + recvStr);
        int port = recvPacket.getPort();
        InetAddress addr = recvPacket.getAddress();
        String sendStr;
        if(recvStr.equals("close"))
        {
        	sendStr = "ByeBye";
        	flag=false;
        }
        else
        {
        	sendStr = String.format("Hello ! I'm Server,I got your words: %s",recvStr);
        }
        byte[] sendBuf;
        sendBuf = sendStr.getBytes();
        DatagramPacket sendPacket 
            = new DatagramPacket(sendBuf , sendBuf.length , addr , port );
        server.send(sendPacket);*/
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        while(flag);
        server.close();
        serverlogin.close();
	}
	
	
	static class MyThread extends Thread
	{
		public void run(){
			    byte[] recvBuf = new byte[100];
		        DatagramPacket recvPacket 
		            = new DatagramPacket(recvBuf , recvBuf.length);
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        System.out.println("Server is started!");
		        InetAddress theaddr=null;
		        int theport=-1;
		        try {
					server.setSoTimeout(5000);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        do
		        {
		        try {
					server.receive(recvPacket);
					
					String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
					
			        
			        int port = recvPacket.getPort();
			        InetAddress addr = recvPacket.getAddress();
			        String recvfmt=String.format("%s\t%s\t%d\t%s",sdf.format(new Date()),addr.getHostAddress(),port,recvStr);
					System.out.println(recvfmt);
			        theaddr=addr;
			        theport=port;
					String sendStr;
			        byte[] sendBuf=null;
			        
			        DeviceMsg devmsg=new DeviceMsg(recvPacket.getData());
			        if(devmsg.isAvilable())
			        {
			        	//sendStr = String.format("Hello ! I'm Server,I got your words: %s",recvStr);
			        	sendBuf =devmsg.makeACK();
			        	
			        }
			        
			        
			        DatagramPacket sendPacket 
			            = new DatagramPacket(sendBuf , sendBuf.length , addr , port );
			        server.send(sendPacket);
			        
			        
			        
			        if(true)
			        {
			        	byte[] tmpbuf=recvfmt.getBytes();
			        DatagramPacket _pack
	            	= new DatagramPacket(tmpbuf,tmpbuf.length,addr,port);
			        
			        serverlogin.send(_pack);
			        server.send(_pack);
			        }
			        
			        synchronized(lists)
			        {
			        	for(LoginItem item :lists)
			        	{
			        		byte[] tmpbuf=recvfmt.getBytes();
			        		//DatagramSocket datagramSocket=new DatagramSocket();
			        		DatagramPacket _pack
				            	= new DatagramPacket(tmpbuf,tmpbuf.length,item.addr,item.port);
			        		serverlogin.send(_pack);
			        		//datagramSocket.receive(_pack);
			        		//datagramSocket.close();
			        	}
			        }
			        
				} catch (IOException e) {
					
					//e.printStackTrace();
					/*if(theaddr!=null)
					{
						System.out.println("sending...");
						DatagramPacket _pack
		            	= new DatagramPacket(new byte[]{0x00,0x00,0x00},3,theaddr,theport);     
				        
				        try {
							server.send(_pack);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}*/
				}
		        }
		        while(flag);
		        
	    }
	}
	
	
	static class MyThreadlogin extends Thread
	{
		public void run(){
			    byte[] recvBuf = new byte[100];
		        
		        DatagramPacket recvPacket 
		            = new DatagramPacket(recvBuf , recvBuf.length);
		        
		        do
		        {
		        try {
					serverlogin.receive(recvPacket);
					String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
					String[] recvs=recvStr.split(":");
					int port1=Integer.valueOf(recvs[1]).intValue();
			        int port = recvPacket.getPort();
			        InetAddress addr = recvPacket.getAddress();
					if(recvs.length<=1) continue;
					if(recvs[0].equals("attach"))
					{
						System.out.println("Hello login:" + recvStr);
						
				        String sendStr="hello!";
				        byte[] sendBuf;
				        sendBuf = sendStr.getBytes();
				        DatagramPacket sendPacket 
				            = new DatagramPacket(sendBuf , sendBuf.length , addr , port );
				        serverlogin.send(sendPacket);
				        LoginItem item=new LoginItem();
				        item.addr=addr;
				        item.port=port;
				        synchronized(lists)
				        {
				        	if(!lists.contains(item))
				        			lists.add(item);
				        }
					}
					else if(recvs[0].equals("dettach"))
					{
						String sendStr="byebye!";
				        byte[] sendBuf;
				        sendBuf = sendStr.getBytes();
				        DatagramPacket sendPacket 
				            = new DatagramPacket(sendBuf , sendBuf.length , addr , port );
				        serverlogin.send(sendPacket);
						LoginItem item=new LoginItem();
				        item.addr=addr;
				        item.port=port;
				        synchronized(lists)
				        {
				        	if(lists.contains(item))
				        			lists.remove(item);
				        }
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        }
		        while(flag);
		        
	    }
	}
	
	static class LoginItem
	{
		public InetAddress addr;
		public int port;
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof LoginItem)
			{
				LoginItem item=(LoginItem) obj;
				return item.addr.toString().equals(this.addr.toString())&&item.port==this.port;
			}
			else
				return false;
		}
	}

}
