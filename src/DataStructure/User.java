package DataStructure;

import java.io.Serializable;

public class User implements Serializable{
	public String name;
	public boolean connected = false;
	public boolean online = false;
	
	public User(String name){
		this.name = name;
	}
	public User(String name, boolean connected, boolean online){
		this.name = name;
		this.connected = connected;
		this.online = online;
	}

	public boolean  isConnected(){
		if(connected)return true;
		else return false;
	}
	
	public boolean isOnLine(){
		if(online)
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return name;
//		if(online)
//			return name;
//		else
//			return "*user*";
	}
}
