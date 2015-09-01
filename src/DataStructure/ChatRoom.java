package DataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ChatRoom implements Serializable{
	public String chatRoomName ;
	public String roomOwner;
	public List<User> usrListOfChatRoom;
	
	public ChatRoom(String chatRoomName){
		this.chatRoomName = chatRoomName;
		this.usrListOfChatRoom = new ArrayList<User>();
	}
	
	public ChatRoom(String chatRoomName, String roomOwner){
		this.roomOwner = roomOwner;
		this.chatRoomName = chatRoomName;
		this.usrListOfChatRoom = new ArrayList<User>();
	}
	
	//设定聊天室boss
	public void setOwner(String Owner){
		this.roomOwner = Owner;
	}
	
	//房间中是否存在某个特定的用户
	public boolean containUsrOrNot(String usrName){
		for(int i = 0; i < this.usrListOfChatRoom.size(); i++){
			if(usrListOfChatRoom.get(i).name.equals(usrName))
				return true;
		}
		return false;
	}
	
	//获得某个特定的用户
	public User getUser(String usrName){
		for(int i = 0; i < this.usrListOfChatRoom.size(); i++){
			if(usrListOfChatRoom.get(i).name.equals(usrName))
				return usrListOfChatRoom.get(i);
		}
		System.out.println("没有找到制定用户");
		return null;
	}

	//删除某个特定的用户
	public void removeUsr(String usrName){
System.out.println("进入删除用户方法");		
		for(int i = 0; i < this.usrListOfChatRoom.size(); i++){
			if(usrListOfChatRoom.get(i).name.equals(usrName))
				usrListOfChatRoom.remove(i);
		}
System.out.println("删除用户方法执行完毕");
	}
	
	@Override
	public String toString() {
		return chatRoomName;
	}

	
}
