
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ef.carparking.app.domain.AppClientInfo;
import com.ef.carparking.app.domain.AppClientMsg;
import com.ef.carparking.app.domain.AppMsgLog;
import com.ef.carparking.domain.DeviceInfo;
import com.ef.carparking.domain.DeviceLog;
import com.mysql.jdbc.*;

public class DbHelper {
	public static Connection getConn()
	{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://119.23.12.86:3306/CarParking?useSSL=false";
		String username="user1";
		String password="abc.1234";
		Connection conn=null;
		try
		{
			Class.forName(driver);
			conn=(Connection) DriverManager.getConnection(url,username,password);
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	
	public static int Insert(DeviceInfo deviceinfo)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into t_deviceinfo (imei,registerat,lastlogin,lastip,state) values(?,?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,deviceinfo.getImei());
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(deviceinfo.getRegisterat().getTime()));
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(deviceinfo.getLastlogin().getTime()));
 	        pstmt.setString(col++,deviceinfo.getLastip());
 	        pstmt.setInt(col++, deviceinfo.getState());
 	        i=pstmt.executeUpdate();
 	        if(i>0)
 	        {
 	        	i= (int)pstmt.getLastInsertID();
 	        	deviceinfo.setDeviceid(i);
 	        }
 	        else
 	        	i= -1;
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
	
	
	
	public static DeviceInfo GetDeviceInfo(String imei)
	{
		DeviceInfo info=null;
		Connection conn = getConn();
	    int i = 0;
	    String sql = "select devid,imei,registerat,lastlogin,lastip,state from  t_deviceinfo where imei=?";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,imei);
 	        ResultSet rs=pstmt.executeQuery();
 	        if(rs.first())
 	        {
 	        	info=new DeviceInfo();
 	        	info.setDeviceid(rs.getInt("devid"));
 	        	info.setImei(rs.getString("imei"));
 	        	info.setLastip(rs.getString("lastip"));
 	        	Timestamp timestmp=rs.getTimestamp("registerat");
 	        	if(timestmp!=null)
 	        		info.setRegisterat(new java.util.Date(timestmp.getTime()));
 	        	timestmp=rs.getTimestamp("lastlogin");
 	        	if(timestmp!=null)
 	        		info.setLastlogin(new java.util.Date(timestmp.getTime()));
 	        	info.setState(rs.getInt("state"));
 	        }
	        pstmt.close();
		    return info;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return info;
	}
	
	public static List<DeviceInfo> GetDeviceInfoList()
	{
		List<DeviceInfo> list=new ArrayList<DeviceInfo>();
		DeviceInfo info=null;
		Connection conn = getConn();
	    int i = 0;
	    String sql = "select devid,imei,registerat,lastlogin,lastip,state from  t_deviceinfo";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
           
 	        ResultSet rs=pstmt.executeQuery();
 	        while(rs.next())
 	        {
 	        	info=new DeviceInfo();
 	        	info.setDeviceid(rs.getInt("devid"));
 	        	info.setImei(rs.getString("imei"));
 	        	info.setLastip(rs.getString("lastip"));
 	        	Timestamp timestmp=rs.getTimestamp("registerat");
 	        	if(timestmp!=null)
 	        		info.setRegisterat(new java.util.Date(timestmp.getTime()));
 	        	timestmp=rs.getTimestamp("lastlogin");
 	        	if(timestmp!=null)
 	        		info.setLastlogin(new java.util.Date(timestmp.getTime()));
 	        	info.setState(rs.getInt("state"));
 	        	list.add(info);
 	        }
	        pstmt.close();
		    return list;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return list;
	}
	
	public static int Update(DeviceInfo deviceinfo)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "update t_deviceinfo set imei=?,registerat=?,lastlogin=?,lastip=?,state=? where devid=?";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,deviceinfo.getImei());
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(deviceinfo.getRegisterat().getTime()));
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(deviceinfo.getLastlogin().getTime()));
 	        pstmt.setString(col++,deviceinfo.getLastip());
 	        pstmt.setInt(col++, deviceinfo.getState());
 	        pstmt.setInt(col++, deviceinfo.getDeviceid());
 	        i=pstmt.executeUpdate();
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
	
	public static int Insert(DeviceLog devicelog)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into t_devicelog (deviceid,infotype,infocontent,infodate,infoip) values(?,?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setInt(col++,devicelog.getDeviceid());
            pstmt.setInt(col++,devicelog.getInfotype());
            pstmt.setString(col++, devicelog.getInfocontent());
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(devicelog.getInfodate().getTime()));
 	        pstmt.setString(col++, devicelog.infoip);
 	        i=pstmt.executeUpdate();
 	        if(i>0)
 	        {
 	        	i= (int)pstmt.getLastInsertID();
 	        	devicelog.setRid(i);
 	        }
 	        else
 	        	i= -1;
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
	
	public static List<DeviceLog> GetDeviceLogList(int deviceid)
	{
		List<DeviceLog> list=new ArrayList<DeviceLog>();
		Connection conn = getConn();
	    int i = 0;
	    String sql = "select rid,deviceid,infotype,infocontent,infodate from  into t_devicelog where deviceid=?";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setInt(col++,deviceid);
     
 	        ResultSet rs=pstmt.executeQuery();
 	        while(rs.next())
 	        {
 	        	DeviceLog log=new DeviceLog();
 	        	log.setRid(rs.getInt("rid"));
 	        	log.setDeviceid(rs.getInt("deviceid"));
 	        	log.setInfotype(rs.getInt("infotype"));
 	        	log.setInfocontent(rs.getString("infocontent"));
 	        	Timestamp timestmp=rs.getTimestamp("infodate");
 	        	if(timestmp!=null)
 	        		log.setInfodate(new java.util.Date(timestmp.getTime()));
 	        	list.add(log);
 	        }
	        pstmt.close();
		    return list;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return list;
	}
	
	public static int Insert(AppClientInfo appinfo)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into t_appinfo (sn,lastlogin,lastip) values(?,?,?)";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,appinfo.sn);
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(appinfo.lastlogin.getTime()));
 	        pstmt.setString(col++,appinfo.addr.getHostAddress());
 	        
 	        i=pstmt.executeUpdate();
 	        if(i>0)
 	        {
 	        	i= (int)pstmt.getLastInsertID();
 	        	appinfo.id=i;
 	        }
 	        else
 	        	i= -1;
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
	
	
	
	public static AppClientInfo GetAppInfo(String sn)
	{
		AppClientInfo info=null;
		Connection conn = getConn();
	    int i = 0;
	    String sql = "select id,sn,lastip,lastlogin from  t_appinfo where sn=?";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,sn);
 	        ResultSet rs=pstmt.executeQuery();
 	        if(rs.first())
 	        {
 	        	info=new AppClientInfo();
 	        	info.id=rs.getInt("id");
 	        	info.sn=rs.getString("sn");
 	        	info.lastip=rs.getString("lastip");
 	        	Timestamp timestmp=rs.getTimestamp("lastlogin");
 	        	if(timestmp!=null)
 	        		info.lastlogin=new java.util.Date(timestmp.getTime());
 	        }
	        pstmt.close();
		    return info;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return info;
	}
	
	public static List<AppClientInfo> GetAppInfoList()
	{
		List<AppClientInfo> list=new ArrayList<AppClientInfo>();
		AppClientInfo info=null;
		Connection conn = getConn();
	    int i = 0;
	    String sql = "select id,sn,lastlogin,lastip from  t_appinfo";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
           
 	        ResultSet rs=pstmt.executeQuery();
 	        while(rs.next())
 	        {
 	        	info=new AppClientInfo();
 	        	info.id=rs.getInt("id");
 	        	info.sn=rs.getString("sn");
 	        	info.lastip=rs.getString("lastip");
 	        	Timestamp timestmp=rs.getTimestamp("lastlogin");
 	        	if(timestmp!=null)
 	        		info.lastlogin=new java.util.Date(timestmp.getTime());
 	        	list.add(info);
 	        }
	        pstmt.close();
		    return list;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return list;
	}
	
	public static int Update(AppClientInfo appinfo)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "update t_appinfo set sn=?,lastlogin=?,lastip=? where id=?";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setString(col++,appinfo.sn);
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(appinfo.lastlogin.getTime()));
 	        pstmt.setString(col++,appinfo.lastip);
 	        pstmt.setInt(col++, appinfo.id);
 	        i=pstmt.executeUpdate();
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
	
	public static int Insert(AppMsgLog msglog)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into t_appmsg (appid,cmd,content,ipaddr,msgdate) values(?,?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setInt(col++,msglog.getInfo().id);
            pstmt.setString(col++,msglog.getMsg().getCmd());
            pstmt.setString(col++, msglog.getMsg().getContent());
            pstmt.setString(col++, msglog.getMsg().getRemoteaddr().getHostAddress());
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(msglog.getMsgdate().getTime()));
 	        
 	        i=pstmt.executeUpdate();
 	        if(i>0)
 	        {
 	        	i= (int)pstmt.getLastInsertID();
 	        	msglog.setRid(i);
 	        }
 	        else
 	        	i= -1;
	        pstmt.close();
		    return i;
	        //conn.close();
	    } catch (SQLException e) {
	        //e.printStackTrace();
	    	System.out.println(e.getMessage());
	    }
	    finally
	    {
	    	try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	    }
	    return 0;
	}
}
