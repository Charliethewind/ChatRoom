package FileTransfer;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import ChatClient.ChatClient;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class RecvFileThread implements Runnable {
    private ChatClient chatclient;
    public RecvFileThread(ChatClient chatclient) {
	this.chatclient = chatclient;
    }

    @Override
    public void run() {
System.out.println("接收方开始ReceiveFileThread run");
        DataOutputStream dosMessage = null;
        DataInputStream    disFile = null;
        DataOutputStream outputFileStream = null;
	try {
		chatclient.FileReceiveSocket = chatclient.server_socket_getFile.accept();
System.out.println("接收socket成功");
		dosMessage = new DataOutputStream(chatclient.FileReceiveSocket.getOutputStream());
		disFile = new DataInputStream(chatclient.FileReceiveSocket.getInputStream());
		String filename = disFile.readUTF();
		if(filename.startsWith("文件名"))filename = filename.substring(filename.indexOf(":")+1);
		/*System.out.println("ERROR!!!第一次读到的不是以文件名三个字开头的消息");*/
		if(filename.startsWith("音乐文件"))
		{
		    dosMessage.writeUTF("agree");
		    dosMessage.flush();
System.out.println("读到的文件名是:"+filename);    
		    File file = new File( "G:\\Receive" + File.separator + "record.wav");
System.out.println("成功创建文件句柄");
		    byte[] buff = new byte[1024];
		    outputFileStream = new DataOutputStream(new FileOutputStream(file));
		    int len = -1;//每次读到的字节数
		    int receivedLen = -1;//总共读到的字节数
		    
		  //开始接收文件并写入
		    while(true){
			if(disFile != null)
			    {
				len = disFile.read(buff);
				receivedLen += len;
			    }
			if(len != -1)outputFileStream.write(buff, 0, len);
			else break;
			outputFileStream.flush();
		    }
		//播放收到的声音文件
		    chatclient.txShow.append("\t"+chatclient.talkto+"对我说话...\n");
		    try {
	            // 1.wav 文件放在java project 下面  
	            FileInputStream fileau = new FileInputStream("G:\\Receive" + File.separator + "record.wav");
	            System.out.println("打开声音文件");
	            System.out.println(new File(System.getProperty("user.dir") + "\\record.wav").getName());
	            AudioStream as = new AudioStream(fileau);
	            AudioPlayer.player.start(as);
	            System.out.println("播放完毕");
	        } catch (Exception e) {
	            e.printStackTrace();  
	        }  
		}
		else
			if(filename.substring(filename.indexOf(".")-3, filename.indexOf(".")).equals("888"))
			{
				chatclient.txShow.append("\t"+chatclient.talkto+"向您发送了画板");
			    dosMessage.writeUTF("agree");
			    dosMessage.flush();
	System.out.println("读到的文件名是:"+filename);		    
			    File file = new File("G:\\Receive" + File.separator + filename);
	System.out.println("成功创建文件句柄");
			    byte[] buff = new byte[1024];
			    outputFileStream = new DataOutputStream(new FileOutputStream(file));
			    int len = -1;//每次读到的字节数
			    int receivedLen = -1;//总共读到的字节数
			    
			  //开始接收文件并写入
	System.out.println("开始接收文件并写入");
			    while(true){
				if(disFile != null)
				    {
					len = disFile.read(buff);
					receivedLen += len;
				    }
				if(len != -1)outputFileStream.write(buff, 0, len);
				else break;
				outputFileStream.flush();
			    }
			
			}
			else
			
		if(JOptionPane.showConfirmDialog(chatclient, filename+"?", "要接受文件",JOptionPane.OK_CANCEL_OPTION)
			== JOptionPane.OK_OPTION){
		    dosMessage.writeUTF("agree");
		    dosMessage.flush();
System.out.println("读到的文件名是:"+filename);		    
		    File file = new File("G:\\Receive" + File.separator + filename);
System.out.println("成功创建文件句柄");
		    byte[] buff = new byte[1024];
		    outputFileStream = new DataOutputStream(new FileOutputStream(file));
		    int len = -1;//每次读到的字节数
		    int receivedLen = -1;//总共读到的字节数
		    
		  //开始接收文件并写入
System.out.println("开始接收文件并写入");
		    while(true){
			if(disFile != null)
			    {
				len = disFile.read(buff);
				receivedLen += len;
			    }
			if(len != -1)outputFileStream.write(buff, 0, len);
			else break;
			outputFileStream.flush();
		    }
		}
		else
		    {dosMessage.writeUTF("disagree");
		    dosMessage.flush();}
		
		//判断如果是图片则显示出来
		String filetype = filename.substring(filename.indexOf(".")+1);
		if(filetype.equals("jpg")){
			if(filename.substring(filename.indexOf("8"), filename.indexOf(".")).equals("888")){
//				System.out.println("*****equals to 123.jpg*****");
				new ShowPicture(chatclient.myLocation.x+330,  chatclient.myLocation.y+220,  "G:\\Receive" + File.separator + filename);
			}else{
			System.out.println("*****equals to jpg*****");
			new ShowPicture(chatclient.myLocation.x+50,  chatclient.myLocation.y+50,  "G:\\Receive" + File.separator + filename);
			}
		}
		
	} catch (IOException e) {
	    e.printStackTrace(); 
	}finally{
		try {
		    if(outputFileStream != null)outputFileStream.close();
		    if(disFile != null)disFile.close();
		    if(dosMessage != null)dosMessage.close();
		} catch (IOException e) {e.printStackTrace();}
	}
    }

}
