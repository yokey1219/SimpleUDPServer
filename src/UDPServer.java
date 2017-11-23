import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UDPServer {

	static DatagramSocket  serverlogin;
	static DatagramSocket  server;
	static List<LoginItem> lists;
	
	
	static Boolean flag;
    
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		serverlogin=new DatagramSocket(8082);
		server = new DatagramSocket(8081);
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
		        byte[] recvlogin=new byte[100];
		        DatagramPacket recvPacket 
		            = new DatagramPacket(recvBuf , recvBuf.length);
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
		        do
		        {
		        try {
					server.receive(recvPacket);
					
					String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
			        String recvfmt=String.format("%s\t%s",sdf.format(new Date()),recvStr);
					System.out.println(recvfmt);
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
			        server.send(sendPacket);
			        
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
					
					e.printStackTrace();
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
