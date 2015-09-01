package ChatClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import DataStructure.ChatRoom;

/**
 * @author Oliver
 *	此工具类中包含connect(ChatClient),disconnect(ChatClient), upline(CharClient)三个方法
 */
public abstract class Controller {

	public static void upline(ChatClient chatclient){
		if(!chatclient.cOnline){
			chatclient.btOnline.setEnabled(false);
			chatclient.btOffline.setEnabled(true);
			chatclient.cOnline = true;
			try {
				chatclient.dos.writeUTF("上线通知:"+"("+chatclient.currentChatRoom.chatRoomName+")"+chatclient.usrName+"*");
System.out.println("====执行upline");
				chatclient.dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void offline(ChatClient chatclient){
		if(chatclient.cOnline){
			chatclient.btOnline.setEnabled(true);
			chatclient.btOffline.setEnabled(false);
			chatclient.cOnline = false;
			try {
				chatclient.dos.writeUTF("下线通知:"+"("+chatclient.currentChatRoom.chatRoomName+")"+chatclient.usrName+"*");
System.out.println("====执行offline");
				chatclient.dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void disconnect(ChatClient chatclient){
		try {
			chatclient.dos.close();
			chatclient.dis.close(); 
			chatclient.s.close();
			System.out.println("====ChatClient disconnected!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//建立连接，同时接收Server发来的所有已经连接上Server的Client名字，显示在在线用户中
	public static void connect(ChatClient chatclient){
		try {
			chatclient.s = new Socket("127.0.0.1",8888);
//System.out.println("\n++++\n"+"getLocalPort():"+chatclient.s.getLocalPort()+"\n++++\n"+"getPort():"+chatclient.s.getPort());			
			chatclient.dos = new DataOutputStream(chatclient.s.getOutputStream());
			chatclient.dis = new DataInputStream(chatclient.s.getInputStream());
			
			//发送此Client的名字，Server端会在给Client发送聊天室列表用户列表的之前
			//先构造一个RecvClient()，构造函数里首先读Stream，将读到的name作为新连接上Client的name
			chatclient.dos.writeUTF(chatclient.usrName);
			chatclient.dos.flush();
			
			String strRead = chatclient.dis.readUTF();
System.out.println("聊天室数量: "+strRead);
			int num = Integer.valueOf(strRead.substring(strRead.indexOf(":")+1, strRead.indexOf("*")));
			// 获得当前聊天室名称列表
			if( strRead.startsWith("聊天室数量:"))
			{
				ObjectInputStream objInput= new ObjectInputStream(chatclient.dis);
				ChatRoom chatroom;
				for(int i = 0; i < num; i++)
				{
					chatroom = (ChatRoom) objInput.readObject();
					chatclient.chatRoomList.add(chatroom);
					if(i == 0)
						chatclient.currentChatRoom = chatroom;
				}
				chatclient.roomList.setListData(chatclient.chatRoomList.toArray());
				chatclient.userList.setListData(chatclient.currentChatRoom.usrListOfChatRoom.toArray());
			}
			upline(chatclient);
		} catch (UnknownHostException e) {
			System.out.println("=E=Cilent:(connect) Unknown hosts!!!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("=E=Client:(connect) IO exception!");
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		chatclient.bConnect = true;
	}//connect end
	
	
	/**用于在client端发送消息
	 * @param chatclient
	 * @param message
	 */
	public static void sendMessage(ChatClient chatclient, String message){
		String s = message;
		try {
			if(!s.isEmpty()){
				chatclient.dos.writeUTF(s);
				chatclient.dos.flush();
			}
		} catch (IOException e1) {
//System.out.println("====7");
			e1.printStackTrace();
		}//catch ends
	}
	
	
	/**主要用于监听send按钮和回车键按下时产生发送消息
	 * @param chatclient
	 * @return
	 */
	public static String generate(ChatClient chatclient){
		String s = null;
		if(chatclient.talkto == null && !chatclient.txSend.getText().isEmpty()){
			s = "聊天室" + "("+chatclient.currentChatRoom.chatRoomName + ")" + chatclient.usrName + ":" 
					+ chatclient.txSend.getText()+"*";
//			chatclient.txShow.append(chatclient.usrName + "说" + chatclient.txSend.getText() + "\n");
		}
		if(chatclient.talkto != null && !chatclient.txSend.getText().isEmpty()){
			s = "私聊" + "("+chatclient.currentChatRoom.chatRoomName + ")" + chatclient.usrName 
					+"*" +chatclient.talkto + ":" + chatclient.txSend.getText();
//			chatclient.txShow.append(chatclient.usrName + "对" + chatclient.talkto + "说" + chatclient.txSend.getText() + "\n");
		}
		chatclient.txSend.setText("");
		return s;
	}
	
	//在制定房间中是否存在某个用户
	public static boolean isUsr_Connected(ChatClient chatclient, String RoomName, String UsrName){
		if(getChatRoom_From_chatRoomList(chatclient, RoomName).containUsrOrNot(UsrName))
			return true;
		else
			return false;
	}
	
	//获取某个特定的聊天室
	public static ChatRoom getChatRoom_From_chatRoomList(ChatClient chatclient, String Name){
		for(int i = 0; i < chatclient.chatRoomList.size(); i++)
			
			if(chatclient.chatRoomList.get(i).chatRoomName.equals(Name))
				{System.out.println("***i***="+i);return chatclient.chatRoomList.get(i);}
System.out.println("getChatRoom_From_chatRoomList未获取到聊天室");
		return null;
	}
	
	//删除某个特定的聊天室
	public static void deleteChatRoom_In_chatRoomList(ChatClient chatclient, String Name){
		for(int i = 1; i < chatclient.chatRoomList.size(); i++)
			if(chatclient.chatRoomList.get(i).chatRoomName.equals(Name))
			{	chatclient.chatRoomList.remove(i);
				System.out.println("remove语句执行完");
				break;
			}
	}
	
	public static int getIndexOfVector(Vector v, String s){
		for(int i =0; i < v.size(); i++)
			if(v.get(i).equals(s))return i;
		return 65535;
	}
}
