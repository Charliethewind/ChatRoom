package Server;

import ChatClient.ChatClient;
import DataStructure.ChatRoom;
import Server.ChatServer.RecvClient;

public abstract class Manager {
	
	/**
	 * @发送消息s给在clients列表的每个客户端
	 * @param chatserver
	 * @param s
	 */
	public static void Send_to_everyone(ChatServer chatserver,String s){
		for(int i = 0; i < chatserver.clients.size() ; i++){
			RecvClient c = chatserver.clients.get(i);
			c.send(s);
		}
	}
	
	//删除某个聊天室的特定用户
	public static void deleteUsrInChatRoom_In_chatRoomListOfServer(ChatServer chatserver, String roomName, String usrName){
		int index = indexOfRoom_In_chatRoomListOfServer(chatserver, roomName);
		chatserver.chatRoomListOfServer.get(index).removeUsr(usrName);
	}
	
	//	判断用户所属房间
	public static ChatRoom RoomOfUsr(ChatServer chatserver, String usrname){
		for( int i = 0; i < chatserver.chatRoomListOfServer.size(); i++){
			for(int j = 0; j < chatserver.chatRoomListOfServer.get(i).usrListOfChatRoom.size(); j++)
				if(chatserver.chatRoomListOfServer.get(i).usrListOfChatRoom.get(j).name.equals(usrname))
					return chatserver.chatRoomListOfServer.get(i);
				
		}
			
			return null;
	}
	
	//在服务端的聊天室内列表中找到指定的聊天室
	public static int indexOfRoom_In_chatRoomListOfServer(ChatServer chatserver, String roomName){
		for(int i = 0; i < chatserver.chatRoomListOfServer.size(); i++){
			if( roomName.equals(chatserver.chatRoomListOfServer.get(i).chatRoomName) )
				return i;
		}
		return 65535;
	}
	
	
	/**
	 * @param s
	 * @return判断user_Info_List中是否存在 name 为 s 的在线用户
	 */
	public static int containOrNot_user_Info_List(ChatServer chatserver, String s){
		for(int i=0; i < chatserver.user_Info_List.size(); i++){
			if(chatserver.user_Info_List.get(i).name.equals(s))
				return i;
		}
		return 65535;
	}
	public static void remove_from_user_Info_List(ChatServer chatserver, String s){
		//==将此client移出clients
		for(int i = 0; i < chatserver.clients.size(); i++)
			if(	 s.equals( chatserver.clients.get(i).clientName )  )
				chatserver.clients.remove(i);
	}
	
	
	//在制定房间中是否存在某个用户
		public static boolean isUsr_Connected(ChatServer chatserver, String RoomName, String UsrName){
			if(getChatRoom_From_chatRoomList(chatserver, RoomName).containUsrOrNot(UsrName))
				return true;
			else
				return false;
		}
		
		
		//获取某个特定的聊天室
		public static ChatRoom getChatRoom_From_chatRoomList(ChatServer chatserver, String Name){
			for(int i = 0; i < chatserver.chatRoomListOfServer.size(); i++)
				
				if(chatserver.chatRoomListOfServer.get(i).chatRoomName.equals(Name))
					{
					return chatserver.chatRoomListOfServer.get(i);}
			return null;
		}
		
		//删除某个特定的聊天室
		public static void deleteChatRoom_In_chatRoomList(ChatServer chatserver, String Name){
			for(int i = 1; i < chatserver.chatRoomListOfServer.size(); i++)
				if(chatserver.chatRoomListOfServer.get(i).chatRoomName.equals(Name))
				{	chatserver.chatRoomListOfServer.remove(i);
					System.out.println("remove语句执行完");
					break;
				}
		}
	
}
