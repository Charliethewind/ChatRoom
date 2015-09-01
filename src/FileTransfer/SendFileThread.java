package FileTransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import ChatClient.ChatClient;

public class SendFileThread implements Runnable {
	private String selectedFilePath;
	private ChatClient chatclient;
	private int port;
	static int i = 0;

	public SendFileThread(ChatClient chatclient, int port) {
		this.chatclient = chatclient;
		this.port = port;
	}

	@Override
    public void run() {
	
	// 获取要传输的文件路径
	if(chatclient.paintBoardFilePath != null)
	{
		selectedFilePath = chatclient.paintBoardFilePath;
	}else{
		System.out.println("进入sendfile线程，voiceRecord值为："+chatclient.voiceRecord);
//		if(chatclient.voiceRecord == true)
		if(chatclient.voicePath != null)
			selectedFilePath = System.getProperty("user.dir") + "\\record.wav";
			else{
			ChooseFile choosefile = new ChooseFile();
System.out.println("成功new choosefile");
		selectedFilePath = choosefile.getSelectedFile().toString();
			}
	}
	chatclient.voiceRecord = false;
	String selectedFileName = selectedFilePath.substring(selectedFilePath.lastIndexOf(File.separator)+1);
	System.out.println("选择的文件名字是:"+selectedFileName);
	
	//建立传输socket
    	try {  Thread.sleep(300);  } catch (InterruptedException e) {e.printStackTrace();}
    	
	try {
	    chatclient.FileTransferSocket = new Socket("127.0.0.1", port+1);
	} catch (UnknownHostException e1) { e1.printStackTrace();
	} catch (IOException e1) { e1.printStackTrace(); }
System.out.println("成功创建socket");	
	

	/*//测试
	DataOutputStream outputStream = null;
	try {
	    outputStream = new DataOutputStream(new FileOutputStream("G:\\Receive\\1.jpg"));
	} catch (FileNotFoundException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}*/

	 //获得客户机之间的向外传输流
	DataOutputStream dosFile = null;
	DataInputStream  disMessage = null;
	try {
	    
	    dosFile = new DataOutputStream(chatclient.FileTransferSocket.getOutputStream());
	    disMessage = new DataInputStream(chatclient.FileTransferSocket.getInputStream());
	    DataInputStream inputFileStream = new DataInputStream(new FileInputStream(selectedFilePath));
//	    boolean agreeOrNot = false;
	    if(selectedFileName.endsWith("wav"))
	    {
	    	dosFile.writeUTF("音乐文件:"+selectedFileName);
		    dosFile.flush();
//		    agreeOrNot = true;
	    }else{
		    dosFile.writeUTF("文件名:"+selectedFileName);
		    dosFile.flush();
		 }
	    System.out.println("准备接受对方返回的同意与否的消息...");
	    String receiveMessage = disMessage.readUTF();
	    System.out.println("接受完毕："+receiveMessage);
	    if(receiveMessage.equals("disagree")  ){
		JOptionPane.showMessageDialog(chatclient, "对方拒收文件呢", "Error", JOptionPane.OK_CANCEL_OPTION);
	    }
	    
	    if(receiveMessage.equals("agree")){
	    	
		byte[] buff = new byte[1024];
		int len = -1;//每次读到的字节数
		long deleveredLen = 0;//总共发送的字节数
		long totallen = new File(selectedFilePath).length();
		long totallenPer = totallen / 100;
		int count = 0;
System.out.println("总长:"+totallen);
System.out.println("FileToSend Total Length:"+totallen);
		ProgressFrame progressframe = new ProgressFrame(chatclient.getX()+200,chatclient.getY()+150);
		progressframe.setVisible(true);
		while(true){
		    if(inputFileStream != null)
		    {
			len = inputFileStream.read(buff);
			deleveredLen += len;
		    }
		    if(len == -1){ progressframe.shutdown();break;}
//		    progressframe.updateProgressbar(deleveredLen/totallen);
		    if( deleveredLen > totallenPer && count < 10  )
		    {  count++;  deleveredLen = 0;  progressframe.updateProgressbar(count);}
//		    System.out.println(((int)deleveredLen/totallen)*100+"   目前长度:"+deleveredLen/1024+"    总长:"+totallen/1024);
//		    System.out.println("len="+len);
		    
//		    System.out.println("count:"+(++i));
		    
		   /* //测试马上写入
		    outputStream.write(buff, 0, len);
		    outputStream.flush();
		    */
		    dosFile.write(buff, 0, len);
		    dosFile.flush();
			}
	    }
	    
	    
	} catch (IOException e) {
	    e.printStackTrace();
	}finally{
		chatclient.paintBoardFilePath = null;
		chatclient.voicePath = null;
		try {
		    if(dosFile != null)dosFile.close();
		    if(disMessage != null)disMessage.close();
		} catch (IOException e) { e.printStackTrace();}
	}
    }// run
																																																							// ends
}
