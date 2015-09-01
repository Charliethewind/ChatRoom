package ChatClient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.ch6_textcoms.BETextFieldUI;import org.jb2011.lnf.beautyeye.widget.border.BEShadowBorder;

import Chatdb.MD5;


public class Login extends JFrame{
		
		private static boolean able_login = false; //是否可以登录，当为true时离开login视图，进入client
		private static String Name = null;
		private static String Password = null;
		private static Socket checklogin;
		static DataInputStream dis;
		static DataOutputStream dos;
		
		private JFrame jf = new JFrame("登录");
		private JPanel jp =new JPanel();
		private JLabel Uname = new JLabel("账号:");
		private JLabel Upassword = new JLabel("密码:");
		
		static JTextField t_usrname = new JTextField(15);
		static JPasswordField t_password = new JPasswordField(15);
		//static JTextField t_password = new JTextField(15);
		private JButton ok = new JButton("ok");
		private JButton cancel = new JButton("cancel");
		private JButton regist = new JButton("regist");
		
		
		public Login()
		{
			try {
				jf.setDefaultLookAndFeelDecorated( true );
				BeautyEyeLNFHelper.frameBorderStyle=
				BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
				org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			ok.addActionListener(new OkListener());
			ok.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		   cancel.addActionListener(new cancelListener());
		   cancel.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		   regist.addActionListener(new registListener());
		   regist.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
//		   t_usrname.setUI(new BETextFieldUI());
		   loginConnect();
			jp.add(Uname);
			jp.add(t_usrname);
			jp.add(Upassword);
			jp.add(t_password);
			jp.add(ok);
			jp.add(cancel);
			jp.add(regist);
			jf.add(jp);
			jf.setUndecorated(true);
			jf.setVisible(true);
			jf.setSize(170,130);
			jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
			jf.setLocation(500,250);
			jf.setResizable(false);
			int i = 0;
			while(!able_login){
				try {
					Thread.sleep(3000);
					System.out.println("i = "+i++);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			jf.dispose();
		}
		
		
	class OkListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Name = t_usrname.getText();
			Password = new MD5().MD5Digest(t_password.getText().toString().trim());
//			System.out.println("@@@@@\n"+Password);
			check();
		}
	}
	
	class cancelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}
	
	class registListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			regist(Login.this);
		}
		
	}
	
	public void loginConnect(){
		try {//使用socket连接服务端，在服务器端jdbc验证用户名密码是否正确
			checklogin = new Socket("127.0.0.1", 8000);
			if(checklogin != null)
				System.out.println("====login 连接服务端成功");
			this.dis = new DataInputStream(checklogin.getInputStream());
			this.dos = new DataOutputStream(checklogin.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void check(){
System.out.println("进入check");
		if(t_usrname.getText().equals(""))
			{	JOptionPane.showMessageDialog(null, "请输入用户名!","error",JOptionPane.ERROR_MESSAGE);	}
		else //if(t_password.getPassword().equals(""))
			if(t_password.getPassword().equals(""))
				{	JOptionPane.showMessageDialog(null, "请输入密码","error",JOptionPane.ERROR_MESSAGE);	}
			else 
			{
					try {
						String s = new String("登录:"+Name+" "+Password+"*");
System.out.println("====待传送的s的值为 :"+s);
						dos.writeUTF(s);
						dos.flush();
						s = dis.readUTF();
System.out.println("====传送回s的值:"+s);
						//处理s
						if(s.equals("ok"))
							{
								dos.writeUTF("退出");
								dos.flush();
								able_login = true;
								dos.close();
								dis.close();
								checklogin.close();
							}
						if(s.equals("isOnline"))
							JOptionPane.showMessageDialog(null, "用户已在线","error",JOptionPane.INFORMATION_MESSAGE);
						if(s.equals("PasswordWrong"))
							JOptionPane.showMessageDialog(null, "密码错误","error",JOptionPane.INFORMATION_MESSAGE);
						if(s.equals("NameNotExist"))
							JOptionPane.showMessageDialog(null, "用户不存在","error",JOptionPane.INFORMATION_MESSAGE);
						
						
					}catch (IOException e) {
						JOptionPane.showMessageDialog(null, "JDBC连接错误","IOException",JOptionPane.INFORMATION_MESSAGE);
						e.printStackTrace();
					} finally{
					}
					//JOptionPane.showMessageDialog(null, "登陆成功","success",JOptionPane.INFORMATION_MESSAGE);
			}
	}
	
	public void regist(Login login){
		Register frame = new Register(login);
		frame.setVisible(true);
	}
	
	public String getName(){
		return Name;
	}
	public String getPassword(){
		return Password;
	}
	
	public static void main(String[] args)
		    {
		    new Login();
		    }
}
