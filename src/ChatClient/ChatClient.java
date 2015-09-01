package ChatClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch19_list.BEListUI;
import org.jb2011.lnf.beautyeye.ch20_filechooser.BEFileChooserUIWin;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import DataStructure.ChatRoom;
import DataStructure.Time;
import DataStructure.User;
import FileTransfer.RecvFileThread;
import FileTransfer.SendFileThread;
import PaintBoard.PaintBoard;
import Voice.Voice;

/**
 * the interface of client
 * @author Oliver
 */
public class ChatClient extends JFrame{
	
	JLabel titleBar;
	JLabel chatRoomJLabel;
	JLabel chatUsrJLabel;
	JPanel buttonBetweenTextJPanel;
	JPanel buttonInChatUsrAreaJPanel;
	JPanel chatRoomPanel;
	JPanel chatUsrPanel;
	JPanel chatContentPanel;
	
	Voice voice;
	
	TextField txSend;	//文本输入区域
	public TextArea txShow;	//消息显示区
	JList userList;		//当前在线用户列表
	JScrollPane userListJScrollPane;
	JList roomList;		//当前聊天室列表
	JScrollPane roomListJScrollPane;
	JButton btClear;
	JButton btClosed;
	JButton btOnline;
	JButton btOffline;
	JButton btSendFile;
	JButton btNewRoom;
	JButton btDelRoom;
	JButton btDelUsr;
	JButton btPaintBoard;
	JButton btRecord;
	
	ChatRoom currentChatRoom;//当前聊天室
	List<ChatRoom> chatRoomList;
	static boolean cOnline = false;
	boolean bConnect = false;
	public boolean paintboard = false;
	public boolean voiceRecord = false;
	public String usrName;		//接受登录对话框传入的用户名
	public String talkto;		//私聊对象名字
	String getNewRoomName;
	public Point myLocation;
	public String paintBoardFilePath;
	public String voicePath;
	public static Integer paintBoardCount = 1;
//	String chatRoom;	//当前聊天室名称
//	Controller controller = new Controller();
	
	GridBagLayout gridBagLayoutMain;
	GridBagLayout gridBagLayoutTextContentArea;
	BorderLayout chatRoomPanelBorderLayout;
	BorderLayout chatUsrPanelBorderLayout;
	BorderLayout chatContentPanelBorderLayout;
	GridLayout buttonInChatUsrAreaGridLayout;
	public ServerSocket server_socket_getFile = null;
	public Socket FileTransferSocket = null;
	public Socket FileReceiveSocket = null;
	Socket s = null;
	public DataOutputStream dos = null;
	public DataInputStream dis = null;
	
	Thread tRecv = new Thread(new Recv());
	
	//===========================================================================================//
	public ChatClient(){
		
		//******Login，跳出登录界面 ******
		Login login = new Login();
		usrName = login.getName();
		
//**************一系列初始化********************************************************
		//初始化3个Panel
		chatRoomPanel = new JPanel();
		chatUsrPanel = new JPanel();
		chatContentPanel = new JPanel();
		buttonBetweenTextJPanel = new JPanel();
		buttonInChatUsrAreaJPanel = new JPanel();
		// 初始化按钮
		btClear = new JButton("清空历史消息");
		btClosed = new JButton("关闭");
		btOnline = new JButton("上线");
		btOffline = new JButton("隐身");
		btSendFile = new JButton("发送文件");
		btNewRoom = new JButton("新建房间");
		btDelRoom = new JButton("删除房间");
		btDelUsr = new JButton("删除用户");
		btPaintBoard = new JButton("画板");
		btRecord = new JButton("录音");
		
		btClear.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btSendFile.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btPaintBoard.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btOnline.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btOffline.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btNewRoom.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btDelRoom.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btDelUsr.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btClosed.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btRecord.setUI(new BEButtonUI().setNormalColor(NormalColor.lightBlue));
		
		//初始化两个文本区域
		txSend = new TextField();
		txShow = new TextArea();
		//初始化用户显示列表
		userList = new JList();
		userList.setUI(new BEListUI() {
		});
		//初始化聊天室显示列表
		roomList = new JList();
		//初始化整体布局管理器
		gridBagLayoutMain = new GridBagLayout();
		//初始化文本显示区布局管理器
		gridBagLayoutTextContentArea = new GridBagLayout();
		//初始化聊天室显示区域布局管理器
		chatRoomPanelBorderLayout = new BorderLayout();
		//初始化用户显示区域布局管理器
		chatUsrPanelBorderLayout = new BorderLayout();
		//初始化聊天室List
		chatRoomList = new ArrayList<ChatRoom>();
		//初始化用户区域的按钮布局管理器
		buttonInChatUsrAreaGridLayout = new GridLayout(6, 1);
		// 初始化用户列表滚动条
		userListJScrollPane = new JScrollPane(userList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// 初始化聊天室列表滚动条
		roomListJScrollPane = new JScrollPane(roomList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);		
		//初始化整体布局参数类
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		//初始化文本显示区布局参数类
		GridBagConstraints gridBagConstraintsTextArea = new GridBagConstraints();
		//初始化顶部栏
		titleBar = new JLabel("TitleBar",JLabel.CENTER);
		//初始化显示房间列表的标签
		chatRoomJLabel = new JLabel("聊天室房间");
		//初始化显示用户列表的标签
		chatUsrJLabel = new JLabel("当前用户");
		
		
//**************设置Client界面****************************************************
		this.setTitle("欢迎您:" + usrName);
		//setUndecorated(true);//设置窗体没有边框
		setIconImage(Toolkit.getDefaultToolkit().getImage(ChatClient.class.getResource("/img/ic1.png")));
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int x = (int) screenSize.getWidth();
		int y = (int) screenSize.getHeight();
		this.setBounds((x - 600) / 2, (y - 600) / 2, 720, 600);
		
		//发送框相关设置
		txSend.addActionListener(new TFListener());
		txSend.setPreferredSize(getPreferredSize());
		txSend.setText("");
//		txSend.setBackground(Color.black);
		
		//显示框相关设置
		txShow.setPreferredSize(getPreferredSize());
		txShow.setText("\n******进入聊天室*默认聊天室******\n\n");
		txShow.setEditable(false);
		
		// 设置用户列表JList属性
		userList.setPreferredSize(getPreferredSize());
		// 设置聊天房间列表属性
		roomList.setPreferredSize(getPreferredSize());
		//开始设置整体布局
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagLayoutMain.setConstraints(titleBar, gridBagConstraints);
		add(titleBar);
		
		chatRoomPanelBorderLayout.setHgap(1);
		chatRoomPanelBorderLayout.setVgap(5);
		chatUsrPanelBorderLayout.setHgap(1);
		chatUsrPanelBorderLayout.setVgap(5);
		buttonInChatUsrAreaGridLayout.setHgap(10);
		buttonInChatUsrAreaGridLayout.setVgap(15);
		chatRoomPanel.setLayout(chatRoomPanelBorderLayout);
		chatUsrPanel.setLayout(chatUsrPanelBorderLayout);
		chatContentPanel.setLayout(gridBagLayoutTextContentArea);
		buttonInChatUsrAreaJPanel.setLayout(buttonInChatUsrAreaGridLayout);

		//管理按钮的两个JPanel
		buttonBetweenTextJPanel.add(btSendFile);
		buttonBetweenTextJPanel.add(btRecord);
		buttonBetweenTextJPanel.add(btPaintBoard);
		buttonBetweenTextJPanel.add(btClear);
		buttonInChatUsrAreaJPanel.add(btOnline);
		buttonInChatUsrAreaJPanel.add(btOffline);
		buttonInChatUsrAreaJPanel.add(btNewRoom);
		buttonInChatUsrAreaJPanel.add(btDelRoom);
		buttonInChatUsrAreaJPanel.add(btDelUsr);
		buttonInChatUsrAreaJPanel.add(btClosed);
		
		//设置聊天室列表显示面板
		chatRoomPanel.add(chatRoomJLabel, BorderLayout.NORTH);
		chatRoomPanel.add(roomList);
		chatRoomPanel.add(roomListJScrollPane, BorderLayout.EAST);
		
		//设置用户列表显示面板
		chatUsrPanel.add(chatUsrJLabel, BorderLayout.NORTH);
		chatUsrPanel.add(userList, BorderLayout.CENTER);
		chatUsrPanel.add(userListJScrollPane, BorderLayout.EAST);
		chatUsrPanel.add(buttonInChatUsrAreaJPanel, BorderLayout.SOUTH);
		
		//设置聊天内容显示面板
		gridBagConstraintsTextArea.fill = GridBagConstraints.BOTH;
		gridBagConstraintsTextArea.weightx = 1;
		gridBagConstraintsTextArea.weighty = 3;
		gridBagConstraintsTextArea.gridwidth = GridBagConstraints.REMAINDER;
		gridBagLayoutTextContentArea.setConstraints(txShow, gridBagConstraintsTextArea);
		chatContentPanel.add(txShow);
		gridBagConstraintsTextArea.weighty = 0.05;
		gridBagConstraintsTextArea.weightx = GridBagConstraints.LINE_START;
		gridBagLayoutTextContentArea.setConstraints(buttonBetweenTextJPanel, gridBagConstraintsTextArea);
		chatContentPanel.add(buttonBetweenTextJPanel);
		gridBagConstraintsTextArea.weighty = 1;
		gridBagLayoutTextContentArea.setConstraints(txSend, gridBagConstraintsTextArea);
		chatContentPanel.add(txSend);
		
		gridBagConstraints.weightx = 2;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weighty = 10;
		gridBagLayoutMain.setConstraints(chatRoomPanel, gridBagConstraints);
		add(chatRoomPanel);
		gridBagConstraints.weightx = 1;
		gridBagLayoutMain.setConstraints(chatUsrPanel, gridBagConstraints);
		add(chatUsrPanel);
		gridBagConstraints.weightx = 4;
		gridBagLayoutMain.setConstraints(chatContentPanel, gridBagConstraints);
		add(chatContentPanel);
		
//**************绑定一些列事件****************************************		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				String clientOffLine = "断线通知:" + "(" + currentChatRoom.chatRoomName + ")" +usrName + "*";
				Controller.sendMessage(ChatClient.this, clientOffLine);
				Controller.disconnect(ChatClient.this);
				System.exit(0);
			}
		});

		Controller.connect(ChatClient.this);
		setLayout(gridBagLayoutMain);
		setVisible(true);
		tRecv.start();
		
		
		//绑定<btPaintBoard>按钮事件
		btPaintBoard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paintboard = true;
System.out.println("Client的位置"+ChatClient.this.getLocation());
				ChatClient.this.myLocation = ChatClient.this.getLocation();
				PaintBoard pb = new PaintBoard(ChatClient.this);
				
			}
		});
		
		//绑定<NewRoom>事件
		btNewRoom.addActionListener(
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
System.out.println("Client的位置"+ChatClient.this.getLocation());
					ChatClient.this.myLocation = ChatClient.this.getLocation();
						new NewChatRoom(ChatClient.this);
					}
				});
		
		//绑定<DeleteRoom>事件
		btDelRoom.addActionListener(
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
System.out.println("进入删除房间事件");
						String deleteName = roomList.getSelectedValue().toString();
System.out.println("删除的房间:"+deleteName);						
						if(!deleteName.equals("默认聊天室"))  
						{			
							  	if(deleteName!= null 
										  && Controller.getChatRoom_From_chatRoomList(ChatClient.this, deleteName).roomOwner.equals(usrName)){
									  //向每个client发送删除消息,并在roomList中移除此聊天室
									  String deleteRoom = "删除聊天室:"+deleteName+"*";
									  System.out.println("=====删除的房间:"+deleteRoom+" ====");
									  Controller.sendMessage(ChatClient.this, deleteRoom);
								  }else {
									  System.out.println("未选中或没有权利删除");							 
									  if(!Controller.getChatRoom_From_chatRoomList(ChatClient.this, deleteName).roomOwner.equals(usrName))
										  JOptionPane.showMessageDialog(null, "没有权利删除", "Error", JOptionPane.ERROR_MESSAGE);
									  else
										  JOptionPane.showMessageDialog(null, "未选中聊天室", "Error", JOptionPane.ERROR_MESSAGE);
								  }
						  }
					}
					
				}
				);
		
		//绑定<DeleteUsr>事件
		btDelUsr.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						  if(userList.getSelectedValue()!= null){
System.out.println("进入删除用户事件");
								
							  if(currentChatRoom.roomOwner.equals(usrName)){
									  //向每个client发送删除消息,并在roomList中移除此聊天室
									  String deleteName = userList.getSelectedValue().toString();
									  String deleteUsr = "删除用户:"+ "(" + currentChatRoom.chatRoomName + ")" +deleteName+"*";
										System.out.println("====删除的消息:===="+deleteUsr);
										System.out.println("====删除的用户:"+deleteName);
										
										Controller.sendMessage(ChatClient.this, deleteUsr);
							  }else
								  JOptionPane.showMessageDialog(null, "没有权利删除", "Error", JOptionPane.ERROR_MESSAGE);
						  }else {
							  //弹出未选择聊天室对话框
							  JOptionPane.showMessageDialog(null, "未选中用户", "Error", JOptionPane.ERROR_MESSAGE);
						  }
					}
				}
				);
		
		// 设置双击用户列表事件
		userList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (ChatClient.this.userList.getSelectedValue()
							.toString().equals(usrName)) {
						JOptionPane.showMessageDialog(null, "不能和 自己聊天的");
					} else {
						talkto = ChatClient.this.userList.getSelectedValue().toString();
						System.out.println("====talkto is: " + talkto);
						ChatClient.this.txShow.append("\n******与"+talkto+"对话******\n\n");
						ChatClient.this.setTitle("*"+usrName+"*"+"与"+talkto+"对话中");
					}
				}
			}
		});
		
		// 设置双击聊天室事件
		roomList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(cOnline){if (e.getClickCount() == 2) {
						String getroomName = ChatClient.this.roomList.getSelectedValue().toString();
						String previousRoomName = currentChatRoom.chatRoomName;
						currentChatRoom = Controller.getChatRoom_From_chatRoomList(ChatClient.this, getroomName);
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
						talkto = null;
						ChatClient.this.txShow.append("\n******进入聊天室*"+getroomName+"******\n\n");
						ChatClient.this.setTitle("*"+usrName+"*"+"在聊天室" + getroomName + "中");
						String changeRoom = "更换聊天室"+"("+ previousRoomName
								+"*"+ getroomName +"):"+ChatClient.this.usrName;
System.out.println("更换聊天室发出的信息: "+ changeRoom);	
						Controller.sendMessage(ChatClient.this, changeRoom);
				}}
			}
		});
		
		//绑定清空消息按钮事件
		btClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChatClient.this.txShow.setText("");
			}
		});

		
		//绑定<录音>按钮事件
		btRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		            if (btRecord.getText().startsWith("录音")) {
System.out.println("按下了录音键！！！！！");
		            	voiceRecord = true;
		            	if( talkto != null ){
							
							voice  = null;
			            	voice = new Voice();
			                voice.file = null;
			                voice.capture.start(btRecord);  
			                voice.fileName = "untitled";
			                btRecord.setText("停止");
			                txShow.append("\t正在录音...\n");
						}else
							JOptionPane.showMessageDialog(ChatClient.this, "请选择用户", "Error", JOptionPane.ERROR_MESSAGE);
					

		            } else {
System.out.println("按下了停止键！！！！！");
		            	if(voice != null){
		            	voiceRecord = true;
		            	voicePath = System.getProperty("user.dir") + "\\record.wav";
		                voice.lines.removeAllElements();  
		                voice.capture.stop();
		                btRecord.setText("录音");
		                txShow.append("\t发送了一段录音...\n");
		              //向服务端请求被选中用户的port号或者是ip 接受服务端发回信息
						try {
							String request = "请求:" + talkto+ "*" + usrName+"(";
System.out.println("按下录音键后发送的请求消息:"+request);
							dos.writeUTF(request);
							dos.flush();
						} catch (IOException e1) {e1.printStackTrace();}
		                voiceRecord = false;
		                //调用send
		                }else
		                	System.out.println("按下停止键后出现错误，voice为null");
		        }  
		    
			}
		});
		
		
		//绑定<发送文件>按钮事件
		btSendFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paintboard = false;
				if( talkto != null ){
					ChatClient.this.myLocation = ChatClient.this.getLocation();
					//向服务端请求被选中用户的port号或者是ip 接受服务端发回信息
					try {
						String request = "请求:" + talkto+ "*" + usrName+"(";
System.out.println("选了:"+request);
						dos.writeUTF(request);
						dos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else
					JOptionPane.showMessageDialog(ChatClient.this, "请选择用户", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		//绑定<关闭>按钮的事件
		btClosed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String clientOffLine = "断线通知:" + "(" + currentChatRoom.chatRoomName + ")" +usrName + "*";
				Controller.sendMessage(ChatClient.this, clientOffLine);
				Controller.disconnect(ChatClient.this);
				System.exit(0);
			}
		});
		
		//绑定<上线>按钮的事件
		btOnline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.upline(ChatClient.this);
			}
		});
		
		//绑定<隐身>按钮的事件
		btOffline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.offline(ChatClient.this);
			}
		});
	
	}


	//绑定回车事件（发送消息）
	private class TFListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
//			if(talkto == null)
//			Controller.upline(ChatClient.this);
			String s = Controller.generate(ChatClient.this);
			Controller.sendMessage(ChatClient.this, s);
		}
		
	}
	
	//Client的接收消息线程
	public class Recv implements Runnable {
		
		@Override
		public void run() {
			try {
				bConnect = true;
System.out.println("====进入Recv Run方法 ");
				while(bConnect){
					String str = dis.readUTF();
					
					//处理请求
					if( str.startsWith("请求:") 
							&& str.substring(str.indexOf(":")+1 , str.indexOf("*")).equals(usrName)){
System.out.println("读到请求:");
					String requestFromUsr = str.substring( str.indexOf("*")+1, str.indexOf("(") );
System.out.println("请求来源于: "+ requestFromUsr);
//					txShow.append("\t****"+requestFromUsr+"*发来图片****\n");
System.out.println("localport:"+s.getLocalPort());
					int port = s.getLocalPort();
System.out.println("get localport ok");
					if(server_socket_getFile == null)
						server_socket_getFile = new ServerSocket(port+1);
					else
						{
							server_socket_getFile.close();
							server_socket_getFile = new ServerSocket(port+1);
						}
System.out.println("建立起ServerSocket");
					ChatClient.this.myLocation = ChatClient.this.getLocation();
					new Thread(new RecvFileThread(ChatClient.this)).start();
System.out.println("ReceiveThread start之后");
					}
					
					//处理返回的端口信息
					if( str.startsWith("端口:") && str.substring(str.indexOf("*")+1, str.indexOf("(")).equals(talkto) ){
System.out.println("读到服务端发来端口信息");

						String StringPort = str.substring(  str.indexOf(":")+1  ,  str.indexOf("*"));
						String PortOwner = str.substring(str.indexOf("*")+1, str.indexOf("("));
						if(paintboard)
//						    paintBoardFilePath = "G:\\123.jpg";
							System.out.println("paintBoardFilePath:::::::::::::::"+paintBoardFilePath);
						else
						    paintBoardFilePath = null;
						
						int IntPort = Integer.valueOf(StringPort);	
System.out.println("端口:"+IntPort);
						if(PortOwner.equals(talkto)){
System.out.println("equals to talkto");
							/*try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}*/
//							FileTransferSocket = new Socket("127.0.0.1", IntPort+1);
//System.out.println("发送方new socket成功");
							new Thread(new SendFileThread(ChatClient.this ,IntPort)).start();
System.out.println("SendThread之后");							
						}
					}
					
					//处理删除聊天室通知
					if( str.startsWith("删除聊天室:") ){
						String deleteRoomName = str.substring(str.indexOf(":")+1, str.indexOf("*"));
System.out.println("读到了："+deleteRoomName);
						if(currentChatRoom.chatRoomName.equals(deleteRoomName)){
//							chatRoomList.get(0).usrListOfChatRoom.add(currentChatRoom.getUser(usrName));	//把自己添加到默认聊天室中
							currentChatRoom = chatRoomList.get(0);		//回到默认聊天室
							
							txShow.append("\t聊天室*"+deleteRoomName+"*被删除，您已回到默认聊天室"+"\n");
							setTitle("欢迎您：" + usrName);
						}else{
							txShow.append("\t聊天室*"+deleteRoomName+"*被删除\n");
						}
						ChatRoom roombeingdeleted = Controller.getChatRoom_From_chatRoomList(ChatClient.this, deleteRoomName);
						for(int i = 0; i < roombeingdeleted.usrListOfChatRoom.size(); i++)
							chatRoomList.get(0).usrListOfChatRoom.add(roombeingdeleted.usrListOfChatRoom.get(i));
						Controller.deleteChatRoom_In_chatRoomList(ChatClient.this, deleteRoomName);
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
					
					//处理新增聊天室通知
					if( str.startsWith("新增聊天室:") ){
						String addRoomName = str.substring(str.indexOf(")")+1, str.indexOf("*"));
						String Owner = str.substring(str.indexOf("(")+1, str.indexOf(")"));
							txShow.append("\t聊天室*"+addRoomName+"*新加入"+"\n");
							ChatRoom newChatRoom = new ChatRoom(addRoomName);
							newChatRoom.roomOwner = Owner;
							chatRoomList.add(newChatRoom);
							roomList.setListData(chatRoomList.toArray());
							userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
					
					//处理删除用户通知
					if( str.startsWith("删除用户:")){
System.out.println("读到删除用户通知: "+str);
						String deleteUsrName = str.substring(str.indexOf(")")+1, str.indexOf("*"));
						String deleteUsrRoom = str.substring(str.indexOf("(")+1, str.indexOf(")"));
						if(usrName.equals(deleteUsrName)){
							txShow.append("\t您已被迫退出当前聊天室\n");
							setTitle("默认聊天室");
							txShow.append("\t您已回到默认聊天室"+"\n");
//							roomList.setListData(chatRoomList.toArray());
							User tmp = Controller.getChatRoom_From_chatRoomList(ChatClient.this,deleteUsrRoom).getUser(deleteUsrName);
							currentChatRoom = chatRoomList.get(0);
							chatRoomList.get(0).usrListOfChatRoom.add(tmp);
							Controller.getChatRoom_From_chatRoomList(ChatClient.this,deleteUsrRoom ).removeUsr(deleteUsrName);
						}else{
							//同在一个聊天室能收到通知
							if(currentChatRoom.chatRoomName.equals(deleteUsrRoom))
								txShow.append("\t用户*"+deleteUsrName+"*已被踢出聊天室\n");
							User tmp = Controller.getChatRoom_From_chatRoomList(ChatClient.this,deleteUsrRoom).getUser(deleteUsrName);
							chatRoomList.get(0).usrListOfChatRoom.add(tmp);
							Controller.getChatRoom_From_chatRoomList(ChatClient.this,deleteUsrRoom ).removeUsr(deleteUsrName);
						}
						
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
				
					//处理断线通知
					if( str.startsWith("断线通知:") ){
System.out.println("读到断线通知: "+str);
						String RoomName = str.substring(str.indexOf("(")+1, str.indexOf(")"));
						String listname = str.substring(str.indexOf(")")+1,str.indexOf("*"));
						//同在一个聊天室能收到通知
						if(currentChatRoom.chatRoomName.equals(RoomName))
							txShow.append("\t用户*"+listname+"*已离开\n");
						Controller.getChatRoom_From_chatRoomList(ChatClient.this,RoomName ).removeUsr(listname);
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
					
					//处理下线通知
					if( str.startsWith("下线通知:") )
					{
System.out.println("读到下线通知: "+str);
						String RoomName = str.substring(str.indexOf("(")+1, str.indexOf(")"));
						String listname = str.substring(str.indexOf(")")+1,str.indexOf("*"));
						//在同一个聊天室内
						if(currentChatRoom.chatRoomName.equals(RoomName))
							txShow.append("\t用户*"+listname+"*已离开\n");
						Controller.getChatRoom_From_chatRoomList(ChatClient.this,RoomName ).removeUsr(listname);
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
					
					//处理上线通知
					if( str.startsWith("上线通知:") )
					{System.out.println("上线通知:  "+str);
						String RoomName = str.substring(str.indexOf("(")+1, str.indexOf(")"));
						String listname = str.substring(str.indexOf(")")+1,str.indexOf("*"));
System.out.println("====上线的名字:"+listname+" 聊天室名字:"+RoomName);
						//用户处于隐身状态
						/*if(Controller.isUsr_Connected(ChatClient.this, RoomName, listname))
						{
							System.out.println("用户是隐身的");
							Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).getUser(listname).online = true; 
							for(int i = 0; i < Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.size(); i++)
								{System.out.println(Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.get(i).name);
							System.out.println(Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.get(i).connected);
							System.out.println(Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.get(i).online);
//							System.out.println(Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.get(i).);
							}
						}
						else//用户刚刚连接
*/						
						if(!Controller.isUsr_Connected(ChatClient.this, RoomName, listname))
						{	System.out.println("用户刚连接");
						Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomName).usrListOfChatRoom.add(new User(listname, true, false));  }
						
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
					
					//处理收到的字符串中与当前聊天室名称匹配的消息
					if(str.startsWith("聊天室")){
						
						int hour = new Date().getHours();
						int mininute = new Date().getMinutes();
						int sec = new Date().getSeconds();
						
						String s = "\n  "+String.valueOf(hour)+":"+String.valueOf(mininute)
								+":"+String.valueOf(sec)+"\n ";
						String MessagechatRoomName = str.substring(str.indexOf("(")+1, str.indexOf(")"));
						if(MessagechatRoomName.equals(currentChatRoom.chatRoomName))
							txShow.append(s+str.substring( str.indexOf(")")+1, str.indexOf(":") ) + "说: " +
									str.substring( str.indexOf(":")+1, str.indexOf("*")) + "\n");
					}
					 //处理私聊消息
					if( str.startsWith("私聊") && str.substring(str.indexOf("(")+1, str.indexOf(")")).equals(currentChatRoom.chatRoomName)){
						String privateTalkSender   = str.substring(str.indexOf(")")+1, str.indexOf("*"));//私聊消息发送者
						String privateTalkReceiver = str.substring(str.indexOf("*")+1, str.indexOf(":"));	//私聊消息接收者
//						 privateTalkReceiver.equals(usrName) && !str.substring(str.indexOf(":")+1).equals("")
//							|| privateTalkSender.equals(usrName) && !str.substring(str.indexOf(":")+1).equals("")
						if(  !str.substring(str.indexOf(":")+1).equals("") /*&&  str.substring(str.indexOf("(")+1, str.indexOf(")")).equals(ChatClient.this.currentChatRoom)*/)
						{	//符合条件，显示消息于面板上
							int hour = new Date().getHours();
							int mininute = new Date().getMinutes();
							int sec = new Date().getSeconds();
System.err.println("当前聊天室为："+ChatClient.this.currentChatRoom
		+"当前聊天对象："+ChatClient.this.talkto);
							String s = "\n  "+String.valueOf(hour)+":"+String.valueOf(mininute)
									+":"+String.valueOf(sec)+"\n ";
							if(privateTalkReceiver.equals(usrName))
							talkto = privateTalkSender;
							ChatClient.this.setTitle("*"+usrName+"*"+"与"+talkto+"对话中");
							txShow.append(s+privateTalkSender + "对" + privateTalkReceiver + "说: " + str.substring(str.indexOf(":")) + "\n");
						}
					}
					
					//处理更换聊天室
					if( str.startsWith("更换聊天室") ){
						String RoomBefore = str.substring(str.indexOf("(")+1, str.indexOf("*"));
						String Roomafter  = str.substring(str.indexOf("*")+1, str.indexOf(")"));
						String ChangeusrName = str.substring(str.indexOf(":")+1);
						User changeUser = Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomBefore).getUser(ChangeusrName);
						Controller.getChatRoom_From_chatRoomList(ChatClient.this, RoomBefore).removeUsr(ChangeusrName);
						Controller.getChatRoom_From_chatRoomList(ChatClient.this, Roomafter).usrListOfChatRoom.add(changeUser);
						roomList.setListData(chatRoomList.toArray());
						userList.setListData(currentChatRoom.usrListOfChatRoom.toArray());
					}
				}
			}
			catch (SocketException e){
				System.out.println(" run exit!");
			}
			catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	public static void main(String[] args) {
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			BeautyEyeLNFHelper.frameBorderStyle=
			BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
			UIManager.put("RootPane.setupButtonVisible", false);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		ChatClient chatclient = new ChatClient();
	}
	
}
