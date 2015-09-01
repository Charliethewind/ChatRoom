package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import Chatdb.JDBC;
import Chatdb.MD5;
import DataStructure.ChatRoom;
import DataStructure.User;


public class ChatServer extends JFrame{
	
	JTextArea showChat;
//	JPanel showChatArea;
	JPanel showChatArea;
	
	BorderLayout showChatAreaborderlayout;
	JList roomList;
//	JList userList;
	JTextArea showInfo;
	JScrollPane roomListPane;
	ServerSocket ss = null;				//总的serversocket
	ServerSocket clientLogin = null;  	//检查8000端口在用户登录时发来的核对密码请求
	boolean started = false;
	public boolean server_is_on = false;
	List<RecvClient> clients= new ArrayList<RecvClient>(); //chatroom监控list的句柄，可以用来指向不同chatroom的list
	RecvClient c = null;
	private JDBC jdbc;
	List<UserOfServer> user_Info_List = new ArrayList<UserOfServer>();
	List<ChatRoom> chatRoomListOfServer = new ArrayList<ChatRoom>();
	
	/**
	 * @author Oliver
	 * @此构造方法设置布局
	 */
	public ChatServer(){

		setTitle("服务器端");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int x = (int) screenSize.getWidth();
		int y = (int) screenSize.getHeight();
		this.setBounds((x - 500), (y - 600) / 2, 450, 600);

		
		showChat = new JTextArea();
//		showChat.setBackground(Color.CYAN);;
		showChat.setEditable(false);
//		showChat.setBackground(Color.blue);
		showChat.setBounds(0, 0, 430, 250);
		
		showChatArea = new JPanel();
		showChatArea.setBounds(5, 30, 433, 250);
//		showChatArea.setBackground(Color.RED);
		showChatArea.setLayout(null);
//		showChatAreaborderlayout = new BorderLayout();
		showChatArea.add(showChat);
		
		// 设置聊天房间列表属性
//		roomList = new JList();
//		roomList.setBounds(5, 300, 165, 295);
		roomList = new JList();
		roomList.setBounds(5, 300, 165, 295);
		
		// 设置显示用户区域属性
//		userList = new JList();
//		userList.setBounds(190, 300, 235, 295);
		showInfo = new JTextArea();
		showInfo.setBounds(190, 300, 235, 295);
		
		// 设置聊天室列表JScrollPane的属性
		roomListPane = new JScrollPane(roomList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		roomListPane.setBounds(170, 300, 15, 295);
		

		//测试
		User user1 = new User("测试用户1");
		user1.connected = true;
		user1.online = true;
		User user2 = new User("测试用户2");
		user2.connected = true;
		User user3 = new User("测试用户3");
		user2.connected = true;
		ChatRoom room1 = new ChatRoom("默认聊天室1");
		ChatRoom room2 = new ChatRoom("默认聊天室2");
		room1.usrListOfChatRoom.add(user1);
		room1.usrListOfChatRoom.add(user2);
		room2.usrListOfChatRoom.add(user3);
		chatRoomListOfServer.add(room1);
		chatRoomListOfServer.add(room2);
		roomList.setListData(chatRoomListOfServer.toArray());
		// 设置双击聊天室事件
		roomList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
						String getroomName = ChatServer.this.roomList.getSelectedValue().toString();
						ChatRoom showUsrChatRoom = Manager.getChatRoom_From_chatRoomList(ChatServer.this, getroomName);
//						userList.setListData(showUsrChatRoom.usrListOfChatRoom.toArray());
						showInfo.setText("\t**聊天室信息**\n"+"创建者："+showUsrChatRoom.roomOwner+"\n"+"人数："+showUsrChatRoom.usrListOfChatRoom.size());
				}
			}
		});
		
		start();
//		add(userList);
		add(showInfo);
		add(roomList);
		add(roomListPane);
		add(showChatArea);
//		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setVisible(true);
	}
	

	
	/**
	 * 启动服务端，建立ss来接受client来的socket请求
	 */
	public void start(){
		try {
			ss = new ServerSocket(8888);
		} 
		catch (BindException e){
			System.out.println("====Server: this port is being used now...====");
			System.exit(0);
		}
		catch (IOException e) {
			e.printStackTrace();System.exit(0);
		}
		
		getLoginCheck();
		System.out.println("====getLoginCheck executed!    ====");
		jdbc = new JDBC(user_Info_List);
		System.out.println("====JDBC constructor executed! ====");
		new Thread(new getNewClientAccepted()).start();
	}  //start ends
	
	
	
	/**
	 * @author Oliver
	 *	在CheckLogin成功返回之后此线程用来接收新连接上的Client发来的连接请求和一系列的连接初始化
	 */
	public class getNewClientAccepted implements Runnable{
		@Override
		public void run() {
			try{
				started = true;
				while(started){
					boolean bConnected = false;
					Socket s = ss.accept();
					if(s != null)System.out.println("==== 成功accept了一个socket ====");
					c = new RecvClient(s);
					int indexOfUsr = Manager.containOrNot_user_Info_List(ChatServer.this, c.clientName);
					if(indexOfUsr != 65535)
						user_Info_List.get(indexOfUsr).port = s.getPort();
					User user = new User(c.clientName);
					user.connected = true;
					chatRoomListOfServer.get(0).usrListOfChatRoom.add(user);
					try {
						/**
						 * 将聊天室列表发送到刚连接上的客户端
						 */
						DataInputStream dis = new DataInputStream(s.getInputStream());
						DataOutputStream dos = new DataOutputStream(s.getOutputStream());
						String outPutRoomListInfo = new String("聊天室数量:"+chatRoomListOfServer.size()+"*");
						dos.writeUTF(outPutRoomListInfo);
						dos.flush();
System.out.println("建立objOutput");	
						ObjectOutputStream objOutput = new ObjectOutputStream(dos);
System.out.println("成功建立objOutput");	
						for(int i = 0; i < chatRoomListOfServer.size(); i++)
						{System.out.println("进入for");
							objOutput.writeObject(ChatServer.this.chatRoomListOfServer.get(i));
							objOutput.flush();
						}
System.out.println("传输完成");
					} catch (Exception e) {
						e.printStackTrace();
					}
					
System.out.println("====a client has connected!====");
					new Thread(c).start();
					clients.add(c);
					//dis.close();
				}
			} 
			catch (EOFException e){
				System.out.println("====Server: EOFExveption , Client closed!====");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					ss.close();
				} 
				catch (IOException e) {
					System.out.println("====Server: start方法中  finally error ====");
					e.printStackTrace();
				}
			}
		
		} //run ends
	}
	
	/**
	 * getLoginCheck检查从8000端口进来的Logincheck请求
	 */
	public void getLoginCheck(){
		try {
			clientLogin = new ServerSocket(8000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread checkLoginThread = new Thread(new CheckLogin());
		checkLoginThread.start();
	}
	
	

	
	/**
	 * @author Oliver
	 *	此进程在方法getLoginCheck中被调用，循环检查8000端口客户登录时的核对密码请求
	 */
	class CheckLogin implements Runnable{
		private DataInputStream disL = null;
		private DataOutputStream dosL =null;

		@Override
		public void run() {
			server_is_on = true;
			Socket checkLoginSocket = null;

			while(server_is_on){
				try{
					checkLoginSocket = clientLogin.accept();
System.out.println("accept了一个登录请求socket: "+checkLoginSocket.getRemoteSocketAddress()+
		"\n*** "+checkLoginSocket.getPort()+"\n*** "+checkLoginSocket.getInetAddress().getHostAddress());
					disL = new DataInputStream(checkLoginSocket.getInputStream());
					dosL = new DataOutputStream(checkLoginSocket.getOutputStream());
					while(true){
						String loginin = disL.readUTF();
						if(loginin.startsWith("退出"))break;
					if(loginin.startsWith("登录")){
						String[] check = loginin.substring( loginin.indexOf(":")+1,loginin.indexOf("*") ).split(" ");
						if(check[0]!=null && check[1]!=null)
							System.out.println("====A client is ready to check...\nClientName:" +check[0]+"  ClientPassword:"+check[1]+"====");
						else
							System.out.println("空");
						
System.out.println("Printing All UserInfo in the local list...");
for(int i = 0; i < user_Info_List.size(); i++)
	System.out.println("Name:"+user_Info_List.get(i).name
		+"\tPaassword:"+user_Info_List.get(i).password  +"\tsex"+user_Info_List.get(i).sex);

						int existOrNot = Manager.containOrNot_user_Info_List(ChatServer.this, check[0]);
						if( existOrNot != 65535)
						{System.out.println("Name: "+check[0]+"  Exists in the locallist!");
						 System.out.println("Local Password:"+user_Info_List.get(existOrNot).password);
							if( user_Info_List.get(existOrNot).password.equals(check[1]) )
								if( user_Info_List.get(existOrNot).connected == false )
									dosL.writeUTF("ok");		//向客户端发送成功信号
								else
									dosL.writeUTF("isOnline");	//向客户端发送以在线信号
							else
								{dosL.writeUTF("PasswordWrong");System.out.println("密码错误");}	//向客户端发送密码错误信号
						}else
							dosL.writeUTF("NameNotExist");//向客户端发送失败信号
					}//登录if结束
					
					
					if(loginin.startsWith("注册:")){
						String[] check = loginin.substring( loginin.indexOf(":")+1,loginin.indexOf("*") ).split(" ");
						int existOrNot = Manager.containOrNot_user_Info_List(ChatServer.this, check[0]);
						if( existOrNot == 65535)
						{
//for(int i = 0; i < user_Info_List.size(); i++)
//	System.out.println(">>>>"+user_Info_List.get(i).name
//			+">>"+user_Info_List.get(i).sex+">>"+user_Info_List.get(i).password);
							UserOfServer registNew = new UserOfServer(check[0], new MD5().MD5Digest(check[1]), check[2]);
							ChatServer.this.user_Info_List.add(registNew);
System.out.println(">>>>>>>>>>>>>>>>>>>>>add在user_Info_List 成功");
for(int i = 0; i < user_Info_List.size(); i++)
	System.out.println(">>>>"+user_Info_List.get(i).name
			+">>"+user_Info_List.get(i).sex+">>"+user_Info_List.get(i).password);
							ChatServer.this.jdbc.insert(check[0], check[1], check[2]);
							showChat.append("\t用户"+check[0]+"插入成功\n");
							dosL.writeUTF("ok");
						}else
							dosL.writeUTF("NameExist");//向客户端发送失败信号
					}
					}//while ends
				}catch(IOException e){
					System.out.println("====IOException! in Client Constructer ====");
				}
			}//while ends
		}
		
	}
	
	
	/**
	 * @author Oliver
	 * @此内部线程类用来管理服务端与每一个Client的流.每一个Client都有对应的RecvClient对象
	 */
	class RecvClient implements Runnable{
			
			private Socket s = null;
			private DataInputStream dis = null;
			private DataOutputStream dos =null;
			boolean bConnected = false;
			public  String clientName;
			int i;
			public RecvClient(Socket s){
				this.s = s;
				try{
						dis = new DataInputStream(s.getInputStream());
						dos = new DataOutputStream(s.getOutputStream());
						this.clientName = dis.readUTF();
						showChat.append("\t"+clientName+"\t"+"连接上"+"\n");
						//方法containOrNot可以返回存在于user_Info_List中的名为clientname的元素位置i
						i= Manager.containOrNot_user_Info_List(ChatServer.this, clientName);
						if(i != 65535)
							user_Info_List.get(i).connected = true;//该用户上线，状态置为true
System.out.println("====Read clientName: " + clientName + "====");
						bConnected = true;
				}catch(IOException e){
					System.out.println("====IOException! in server:RecvClient Constructer ====");
				}
			}
			
			
			
			@Override
			public String toString() {
				return clientName;
			}

			void send(String str){
				try {
					dos.writeUTF(str);
				} catch (SocketException e){
					
					//客户端异常退出，发消息到其余所有client刷新在线用户列表
					String clientOffLineRoom = Manager.RoomOfUsr(ChatServer.this, this.clientName).chatRoomName;
					Manager.deleteUsrInChatRoom_In_chatRoomListOfServer(ChatServer.this, clientOffLineRoom, this.clientName);
					String clientOffLine = "断线通知:" + "("+clientOffLineRoom+")"+ this.clientName + "*";
					showChat.append("\t"+clientName+"\t"
					+"异常退出"+"\n");
System.out.println("====生成clientOffLine: "+clientOffLine+"====");
					int position = Manager.containOrNot_user_Info_List(ChatServer.this, this.clientName);//返回在列表中位置
					if( position != 65535 )
					{
						user_Info_List.get(position).connected = false;
						user_Info_List.get(position).online = false;
					}
					clients.remove(this);
					Manager.Send_to_everyone(ChatServer.this, clientOffLine);
					System.out.println("====一个Client从RecvClient中的send 退出了====");
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			

			
			@Override
			public void run() {
				try {
					while(bConnected){
						String strRead = dis.readUTF();
System.out.println("     这一次读到了:"+strRead+"   #\n");
						/* 
						 * 处理请求
						 */
						if(strRead.startsWith("请求:")){
							String requestedName = strRead.substring(  strRead.indexOf(":")+1, strRead.indexOf("*") );
							
							int indexOfUsr = Manager.containOrNot_user_Info_List(ChatServer.this, requestedName);
							if( indexOfUsr != 65535){
								int outputPortInt = user_Info_List.get(indexOfUsr).getPort();
								String outputPortString;
								if(strRead.contains("(&*)"))
									 outputPortString = "端口:"+String.valueOf(outputPortInt)+"*"+requestedName+"(&*)";
								else
									 outputPortString = "端口:"+String.valueOf(outputPortInt)+"*"+requestedName+"(";
								dos.writeUTF(outputPortString);
								dos.flush();

System.out.println("向客户端写了端口 : "+outputPortString);
							}
						}
						
						
						/* 
						 * 处理删除聊天室通知
						 */
						if( strRead.startsWith("删除聊天室:") ){
							String deleteRoomName = strRead.substring(strRead.indexOf(":")+1, strRead.indexOf("*"));
	System.out.println("删除聊天室的名字："+deleteRoomName);
							showChat.append("\t聊天室 * "+deleteRoomName+" * \t"+"解散"+"\n");
							ChatRoom roombeingdeleted = Manager.getChatRoom_From_chatRoomList(ChatServer.this, deleteRoomName);
							for(int i = 0; i < roombeingdeleted.usrListOfChatRoom.size(); i++)
								chatRoomListOfServer.get(0).usrListOfChatRoom.add(roombeingdeleted.usrListOfChatRoom.get(i));
							Manager.deleteChatRoom_In_chatRoomList(ChatServer.this, deleteRoomName);
							roomList.setListData(chatRoomListOfServer.toArray());
						}
						
						
						//处理新增聊天室通知
						if( strRead.startsWith("新增聊天室:") ){
							String addRoomName = strRead.substring(strRead.indexOf(")")+1, strRead.indexOf("*"));
							String Owner = strRead.substring(strRead.indexOf("(")+1, strRead.indexOf(")"));
							showChat.append("\t新增聊天室 * "+addRoomName+" * \t房主: "+Owner+"\n");
								ChatRoom newChatRoom = new ChatRoom(addRoomName);
								newChatRoom.roomOwner = Owner;
								chatRoomListOfServer.add(newChatRoom);
								roomList.setListData(chatRoomListOfServer.toArray());
						}
						
						//处理删除用户通知
						if( strRead.startsWith("删除用户:")){
	System.out.println("读到删除用户通知: "+strRead);
							String deleteUsrName = strRead.substring(strRead.indexOf(")")+1, strRead.indexOf("*"));
							String deleteUsrRoom = strRead.substring(strRead.indexOf("(")+1, strRead.indexOf(")"));
								User tmp = Manager.getChatRoom_From_chatRoomList(ChatServer.this,deleteUsrRoom).getUser(deleteUsrName);
//								currentChatRoom = chatRoomListOfServer.get(0);
								showChat.append("\t用户 "+deleteUsrName+" 被踢出聊天室 "+deleteUsrRoom+"\n");
								chatRoomListOfServer.get(0).usrListOfChatRoom.add(tmp);
								Manager.getChatRoom_From_chatRoomList(ChatServer.this,deleteUsrRoom ).removeUsr(deleteUsrName);
								roomList.setListData(chatRoomListOfServer.toArray());
						}
						
						/*
						 * 处理断线通知
						 */
						if(strRead.startsWith("断线通知:")){
System.out.println("读到断线通知: "+strRead);							
							String LeaveLineClientName = strRead.substring(  strRead.indexOf(")")+1, strRead.indexOf("*") );
							String LeaveLineClientRoomName = strRead.substring(  strRead.indexOf("(")+1, strRead.indexOf(")") );
System.out.println("断线用户: "+LeaveLineClientName);							
							showChat.append("\t"+clientName+"\t"+"下线了"+"\n");
							//==将此用户移出user_Info_List
							int position = Manager.containOrNot_user_Info_List(ChatServer.this, this.clientName);//返回在列表中位置
							if( position != 65535 )
								{
									user_Info_List.get(position).connected = false;
									user_Info_List.get(position).online = false;
								}
							Manager.remove_from_user_Info_List(ChatServer.this, LeaveLineClientName);
System.out.println("delete前");
int index1 = Manager.indexOfRoom_In_chatRoomListOfServer(ChatServer.this, LeaveLineClientRoomName);
System.out.println("index的值外:"+index1);
for(int i = 0; i < chatRoomListOfServer.get(index1).usrListOfChatRoom.size(); i++){
System.out.println("用户所在房间的usrN: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).name );
System.out.println("用户所在房间的usrC: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).connected );
System.out.println("用户所在房间的usrO: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).online +"\n******");
	}
							Manager.deleteUsrInChatRoom_In_chatRoomListOfServer(ChatServer.this, LeaveLineClientRoomName, LeaveLineClientName);
for(int i = 0; i < chatRoomListOfServer.get(index1).usrListOfChatRoom.size(); i++){
System.out.println("用户所在房间的usrN: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).name );
System.out.println("用户所在房间的usrC: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).connected );
System.out.println("用户所在房间的usrO: "+ chatRoomListOfServer.get(index1).usrListOfChatRoom.get(i).online +"\n******");
}
System.out.println("delete后");
						}
						
						/* 
						 * 处理下线通知
						 */
						if(strRead.startsWith("下线通知")){
							String RoomName = strRead.substring(strRead.indexOf("(")+1, strRead.indexOf(")"));
							String listname = strRead.substring(strRead.indexOf(")")+1, strRead.indexOf("*"));
							showChat.append("\t"+clientName+"\t"+"隐身了"+"\n");
							//==将此用户移出user_Info_List
							int position = Manager.containOrNot_user_Info_List(ChatServer.this, this.clientName);//返回在列表中位置
							if( position != 65535 )
								{
									user_Info_List.get(position).online = false;
								}

						Manager.getChatRoom_From_chatRoomList(ChatServer.this,RoomName ).getUser(listname).online = false;
						roomList.setListData(chatRoomListOfServer.toArray());
						}
						/* 
						 * 处理上线通知
						 */
						if(strRead.startsWith("上线通知")){
							int position = Manager.containOrNot_user_Info_List(ChatServer.this, this.clientName);//返回在列表中位置
							showChat.append("\t"+clientName+"\t"+"上线了"+"\n");
							//将此用户在user_Info_List中的状态设置为在线
							if( position != 65535 )
								user_Info_List.get(position).online = true;
							
							String RoomName = strRead.substring(strRead.indexOf("(")+1, strRead.indexOf(")"));
							String listname = strRead.substring(strRead.indexOf(")")+1, strRead.indexOf("*"));
	System.out.println("====上线的名字:"+listname+" 聊天室名字:"+RoomName);
							//用户处于隐身状态
							if(Manager.isUsr_Connected(ChatServer.this, RoomName, listname))
							{
								System.out.println("用户是隐身的");
								Manager.getChatRoom_From_chatRoomList(ChatServer.this, RoomName).getUser(listname).online = true;
							}
							else//用户刚刚连接
							{	System.out.println("用户刚连接");Manager.getChatRoom_From_chatRoomList(ChatServer.this, RoomName).usrListOfChatRoom.add(new User(listname, true, false));  }
							
							roomList.setListData(chatRoomListOfServer.toArray());
						}
						
						
						//处理更换聊天室
						if( strRead.startsWith("更换聊天室") ){
							String RoomBefore = strRead.substring(strRead.indexOf("(")+1, strRead.indexOf("*"));
							String Roomafter  = strRead.substring(strRead.indexOf("*")+1, strRead.indexOf(")"));
							String ChangeusrName = strRead.substring(strRead.indexOf(":")+1);
							User changeUser = Manager.getChatRoom_From_chatRoomList(ChatServer.this, RoomBefore).getUser(ChangeusrName);
							Manager.getChatRoom_From_chatRoomList(ChatServer.this, RoomBefore).removeUsr(ChangeusrName);
							Manager.getChatRoom_From_chatRoomList(ChatServer.this, Roomafter).usrListOfChatRoom.add(changeUser);
							roomList.setListData(chatRoomListOfServer.toArray());
						}
						
						
System.out.println("====Server RecvClient run Read : \" "+strRead+" \" ====");
						for(int i = 0; i < clients.size() ; i++){
							RecvClient c = clients.get(i);
System.out.println("群发给:"+c.clientName);
							c.send(strRead);
						}
					}
				} 
				catch (SocketException e){
					if(c != null){
System.out.println("****c.clientName:"+c.clientName);
						//clients.remove(c);
					}
System.out.println("====A Client quit!====");
				}
				catch (EOFException e){
System.out.println("====Server: Client closed!====");
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						if(s   != null)  s.close();
						if(dos != null)  dos.close();
						if(dis != null)  dis.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
				
		 }//Class Client end
	
	
	public static void main(String[] args){
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			BeautyEyeLNFHelper.frameBorderStyle=
			BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
			UIManager.put("RootPane.setupButtonVisible", false);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		ChatServer chatserver = new ChatServer();
		new Thread(new CheckOnline(chatserver)).start();
	}
}
