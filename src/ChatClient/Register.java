package ChatClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI.NormalColor;

import Chatdb.MD5;

public class Register extends JDialog {
	
	private String getRegistName;
	private String getRegistPassword;
	private String getRegistSex = "Boy";
	
	private JLabel registWelcome;
	private JLabel registName;
	private JLabel registPassword;
	private JLabel registSex;
	private JTextField registNameField;
	private JPasswordField registPasswordField;
//	private JTextField registSexField;
	private JComboBox<String> registSexBox;
	private JButton registButton;
	private JPanel welcome;
	private JPanel NameField;
	private JPanel PasswordField;
	private JPanel SexField;
	private JPanel ButtonField;
	private Login login;


	public Register(Login login) {
		this.login = login;
		// 总体注册界面大小
//		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		setBounds(500, 230, 220, 300);
		setResizable(false);
		setLayout(new GridLayout(5,1));
		
		//各个类目
		registWelcome = new JLabel("*欢迎注册*");
		registName = new JLabel("账号:");
		registPassword = new JLabel("密码:");
		registSex = new JLabel("性别:");
		registNameField = new JTextField(15);
		registPasswordField = new JPasswordField(15);
//		registSexField = new JTextField(15);
		registSexBox = new JComboBox<String>();
		registSexBox.addItem("Boy");
		registSexBox.addItem("Girl");
		registSexBox.addItem("UnKonwn");
		registButton = new JButton("注册");
		registButton.setUI(new BEButtonUI().setNormalColor(NormalColor.lightBlue));
		registButton.addActionListener(new registListener());
		registSexBox.addActionListener(new registSexBoxListener());
		
		//五个容器
		welcome = new JPanel();
		NameField = new JPanel();
		PasswordField = new JPanel();
		SexField = new JPanel();
		ButtonField = new JPanel();
		
		//添加内容
		welcome.add(registWelcome, new FlowLayout(FlowLayout.CENTER ));
		add(welcome);
		NameField.add(registName);
		NameField.add(registNameField);
		add(NameField);
		PasswordField.add(registPassword);
		PasswordField.add(registPasswordField);
		add(PasswordField);
		SexField.add(registSex);
		SexField.add(registSexBox);
		add(SexField);
		ButtonField.add(registButton);
		add(ButtonField);
		
	}

	/**
	 * 检查注册界面中的Text是否为空，不为空则连接Server进行注册
	 */
	private void regist(){
		if(!registNameField.getText().equals("")){
			if(!registPasswordField.getText().equals("")){
				this.getRegistName = registNameField.getText().trim();
				this.getRegistPassword = registPasswordField.getText().toString().trim();
				
				String s = new String("注册:"+getRegistName+" "+getRegistPassword+" "+getRegistSex+"*");
System.out.println("====注册传送s的值为 :"+s);
										try {
											login.dos.writeUTF(s);
											login.dos.flush();
											s = login.dis.readUTF();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
System.out.println("====传送回s的值:"+s);
										//处理s
										if(s.equals("ok"))
										{
											login.t_usrname.setText(getRegistName);
											login.t_password.setText(getRegistPassword);
											Register.this.dispose();
										}
										if(s.equals("NameExist"))
											JOptionPane.showMessageDialog(null, "用户已存在","error",JOptionPane.INFORMATION_MESSAGE);
			}else
				JOptionPane.showMessageDialog(null, "请输入密码!","error",JOptionPane.ERROR_MESSAGE);
		}
		else
			JOptionPane.showMessageDialog(null, "请输入账号!","error",JOptionPane.ERROR_MESSAGE);
	}
	
	class registSexBoxListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == registSexBox){
				getRegistSex = (String)registSexBox.getSelectedItem();
			}
		}
		
	}
	
	class registListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			regist();
		}
		
	}
}
