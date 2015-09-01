package Chatdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import DataStructure.User;
import Server.UserOfServer;

import com.mysql.jdbc.Statement;

public class JDBC {
	String url = "jdbc:mysql://localhost:3306/mydatabase";
	private static Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet result = null;
	private String selectall = "select * from usr";
	private String name;
	private String sex;
	private String password;
	java.sql.Statement stmt = null;
	ResultSet rs = null;
	List<User> userListener = null;//监听用户是否在线

	/**
	 * 建立con,连接数据库并在JDBC初始化时把数据库中已经存在的用户信息读到userListener中
	 */
	public JDBC(List<UserOfServer> userListener){
		//this.userListener = userListener;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "19940217");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			pstmt = con.prepareStatement(selectall);
			rs = pstmt.executeQuery();
			UserOfServer userofserver;
			System.out.println("Start to get data from database....");
			while(rs.next()){
				userofserver = new UserOfServer();
				userofserver.name = rs.getString("name");
				System.out.println("Name= "+userofserver.name);
//System.out.println("=====userListener Read:"+ userofserver.name);
				userofserver.password = rs.getString("password");
				userofserver.sex = rs.getString("sex");
				System.out.println(userofserver.password+"\t"+userofserver.sex);
				userListener.add(userofserver);
			}
			System.out.println("data get finished...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
/*	public JDBC(String url, String usr, String password){
	}*/
	
	public void closeConnection(){
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insert(String name, String password, String sex){
		this.name = name;
		this.sex = sex;
		this.password = new MD5().MD5Digest(password);
		String sql = "insert into usr values (?,?,?)";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, this.name);
			pstmt.setString(2, this.password);
			pstmt.setString(3, this.sex);
			int n = pstmt.executeUpdate();
			if(n == 1)
			System.out.println("数据库插入成功");
			else
				System.out.println("数据库插入失败");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}

}
