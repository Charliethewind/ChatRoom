package PaintBoard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import ChatClient.ChatClient;


class Palette extends Panel implements MouseListener, MouseMotionListener {
	
	private int mouseX = 0;
	private int oldMouseX = 0;
	private int mouseY = 0;
	private int oldMouseY = 0;
	private Color color = null;
	// 画笔样式
	private BasicStroke stroke = null;
	// 缓存图形
	private BufferedImage image = null;
	private ChatClient chatclient;
	public Palette(ChatClient chatclient) {
		this.chatclient = chatclient;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		// 默认黑色画笔
		color = new Color(0, 0, 0);
		// 设置默认画笔样式
		stroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		image = new BufferedImage(293, 138, BufferedImage.TYPE_INT_RGB);
		image.getGraphics().setColor(Color.white);
		image.getGraphics().fillRect(0, 0, 1280, 1024);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Graphics2D bg = image.createGraphics();
		bg.setColor(color);
		bg.setStroke(stroke);
		bg.drawLine(oldMouseX, oldMouseY, mouseX, mouseY);
		g2d.drawImage(image, 0, 0, this);
	}

	// 设置画笔粗细
	public void setStrokeWidth(float width) {
		this.stroke = new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin());
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void mouseClicked(MouseEvent e){System.out.println("鼠标点击");}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		this.oldMouseX = this.mouseX = mouseEvent.getX();
		this.oldMouseY = this.mouseY = mouseEvent.getY();
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent mouseEvent) {System.out.println("鼠标释放");}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		this.oldMouseX = this.mouseX;
		this.oldMouseY = this.mouseY;
		this.mouseX = mouseEvent.getX();
		this.mouseY = mouseEvent.getY();
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent mouseEvent) {/*System.out.println("鼠标移动");*/}

	public void getBufferedImage(){//BufferedImage
		String paintBoardPicName = (chatclient.paintBoardCount++).toString()+"888.jpg";
		File file = new File("G:\\"+paintBoardPicName);
		chatclient.paintBoardFilePath = "G:\\"+paintBoardPicName;
//		imagemini = BufferedImage);
		try {
			ImageIO.write(image, "JPG", file);
System.out.println("写入成功");
//读取本地保存的画板画然后发送
			try {
				if(chatclient.talkto != null)
				{String request = "请求:" + chatclient.talkto+ "*" + chatclient.usrName+"(&*)";
System.out.println("保存了画板内容之后发出请求消息:"+request);
				chatclient.dos.writeUTF(request);
				chatclient.dos.flush();
				}else
					JOptionPane.showMessageDialog(null, "请选择用户", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		return image;
	}


}
