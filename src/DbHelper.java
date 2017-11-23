
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ef.carpaking.domain.DeviceInfo;
import com.ef.carpaking.domain.DeviceLog;
import com.mysql.jdbc.*;

public class DbHelper {
	public static Connection getConn()
	{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://112.74.166.169:3306/DictionaryTool?useSSL=false";
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
	
	public static int Insert(DeviceLog devicelog)
	{
		Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into t_devicelog (deviceid,infotype,infocontent,infodate) values(?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
        	pstmt = (PreparedStatement) conn.prepareStatement(sql);
 	        int col=1;
            pstmt.setInt(col++,devicelog.getDeviceid());
            pstmt.setInt(col++,devicelog.getInfotype());
            pstmt.setString(col++, devicelog.getInfocontent());
 	        pstmt.setTimestamp(col++, new java.sql.Timestamp(devicelog.getInfodate().getTime()));
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
}
