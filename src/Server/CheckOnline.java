package Server;

import ChatClient.ChatClient;
import Server.ChatServer.RecvClient;

/**
 * @author Oliver
 *此线程的功能就是维护Server端的用户列表更新
 */
public class CheckOnline implements Runnable {

	boolean started;
	ChatServer chatserver;
	String s;
	String getI;
	public CheckOnline(ChatServer chatserver){
		started = chatserver.started;
		this.chatserver = chatserver;
	}
	
	
	@Override
	public void run() {
		while(started){
			//尝试每隔十五秒发送消息“监听连接消息”来维护Client端的用户列表更新
				Manager.Send_to_everyone(chatserver, "监听连接消息");
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
