package ChatClient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalBorders.TextFieldBorder;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import ChatClient.ChatClient;
import DataStructure.ChatRoom;

public class NewChatRoom extends JDialog {
	private JTextField getNewRoomName;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel lable;
	ChatClient chatclient;

	public NewChatRoom(ChatClient chatclient) {
		this.chatclient = chatclient;
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int x = chatclient.myLocation.x;
		int y = chatclient.myLocation.y;
		this.setBounds(x+280, y+260 , 220, 175);
		okButton = new JButton("确定");
		okButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getNameFromTextFiled();
				dispose();
			}
		});
		
		cancelButton = new JButton("取消");
		cancelButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		lable = new JLabel("请输入房间名");
		getNewRoomName = new JTextField(15);
		lable.setBounds(50, 5, 100, 30);
		getNewRoomName.setBounds(30, 35, 150 , 30);
		okButton.setBounds(10, 75, 70, 30);
		cancelButton.setBounds(120, 75, 70, 30);
		setLayout(null);
		add(lable);
		add(getNewRoomName);
		add(okButton);
		add(cancelButton);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	public void getNameFromTextFiled(){
		chatclient.getNewRoomName = getNewRoomName.getText().trim();
		if(!chatclient.getNewRoomName.equals("")){
			ChatRoom newChatRoom = new ChatRoom(chatclient.getNewRoomName, chatclient.usrName);
			
			String previousRoomName = chatclient.currentChatRoom.chatRoomName;
			String getroomName = newChatRoom.chatRoomName;
					
					
			chatclient.currentChatRoom = newChatRoom;
			chatclient.setTitle("*"+chatclient.usrName+"*"+"在聊天室" + chatclient.currentChatRoom.chatRoomName + "中");
			String message = "新增聊天室:"+ "(" + chatclient.usrName + ")" +chatclient.getNewRoomName+"*";
			Controller.sendMessage(chatclient, message);
			String changeRoom = "更换聊天室"+"("+ previousRoomName
					+"*"+ getroomName +"):"+chatclient.usrName;
System.out.println("新建聊天室then更换聊天室发出的信息: "+ changeRoom);	
			Controller.sendMessage(chatclient, changeRoom);
		}
	}
}
