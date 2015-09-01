package Server;

import DataStructure.User;

public class UserOfServer {
	public String name;
	public String sex;
	public String password;
	public int port;
	public boolean connected = false;
	public boolean online = false;
	
	
	public UserOfServer(String name, String password, String sex){
		this.name = name;
		this.password = password;
		this.sex = sex;
	}
	
	public UserOfServer(){}
	public String isConnected(){
		if(connected)return "Connected";
		else return "notConnected";
	}
	public String isOnLine(){
		if(online)
			return "online";
		else
			return "offline";
}
	@Override
	public String toString() {
		return name+"  "+isConnected()+"  "+isOnLine();
	}
	public int getPort() {
		return port;
	}
}
