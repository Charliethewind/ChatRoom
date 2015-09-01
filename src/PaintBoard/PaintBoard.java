package PaintBoard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChatClient.ChatClient;

/**
 * 简单画图板程序
 */
public class PaintBoard extends JDialog {
	private JPanel toolBarPane;//颜色和大小的总Panel
	private JPanel paintColorSelectPanel;//颜色Panel
	private JPanel paintWidthSelectPanel;//粗细Panel
	private JPanel showCurrentColor = null;//显示当前选择颜色，一小格
	private JPanel buttonPanel = null;
	
	private Palette palette = null;//画板功能
	private JLabel drawWidthLabel = null;//显示当前大小
	private JLabel paintpoint = null;
	private JButton SendPaint;
	private JButton ClosePaint;
	JButton [] buttonColor;
	JButton [] buttonWidth;
	private ButtonColorAction buttonColorAction = null;
	
	ChatClient chatclient;

	public PaintBoard(ChatClient chatclient) {
		palette = new Palette(chatclient);
		
		toolBarPane = new JPanel(new GridLayout(2, 1)); 
		showCurrentColor = new JPanel();
		drawWidthLabel = new JLabel();
		paintpoint = new JLabel();
		paintColorSelectPanel = new JPanel(new GridLayout(1, 5));
		paintWidthSelectPanel = new JPanel(new GridLayout(1, 5));
		buttonColor = new JButton[5];
		buttonWidth = new JButton[5];
		SendPaint = new JButton("发送画板");
		ClosePaint = new JButton("关闭画板");
		buttonPanel = new JPanel();
		buttonPanel.add(ClosePaint/*, BorderLayout.WEST*/);
		buttonPanel.add(SendPaint/*, BorderLayout.EAST*/);
		
		Color [] color = { Color.blue, Color.cyan, Color.red, Color.pink, Color.green};
		paintWidthSelectPanel.add(drawWidthLabel);
		
		//绑定发送事件
		SendPaint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//把文件保存到本地然后发送文件并关闭
				palette.getBufferedImage();
				PaintBoard.this.dispose();
//				destorymyself(PaintBoard.this);
				System.gc();
			}

//			private void destorymyself(PaintBoard paintboard) {
//				paintboard = null;
//			}
		});
		
		//绑定关闭事件
		ClosePaint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PaintBoard.this.dispose();
			}
		});
		
		// 显示当前颜色
		showCurrentColor.setBackground(Color.black);
		paintColorSelectPanel.add(showCurrentColor);
		buttonColorAction = new ButtonColorAction();
		for(int i = 0; i < buttonColor.length; i++){
			buttonColor[i] = new JButton();
//System.out.println("创建了第"+i+"个Button");
			buttonColor[i].setBackground(color[i]);
			buttonColor[i].addActionListener(buttonColorAction);
			//buttonColor[i].addMouseListener(buttonMouseOn);
			paintColorSelectPanel.add(buttonColor[i]);
		}
		
		toolBarPane.add(paintColorSelectPanel);
		buttonWidth[0] = new JButton("1");
		buttonWidth[1] = new JButton("3");
		buttonWidth[2] = new JButton("5");
		buttonWidth[3] = new JButton("7");
		buttonWidth[4] = new JButton("9");
		for(int i = 0; i < buttonWidth.length; i++){
			paintWidthSelectPanel.add(buttonWidth[i]);
			//buttonWidth[i].addMouseListener(buttonMouseOn);
			buttonWidth[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton button_temp = (JButton) e.getSource();
					String name = button_temp.getName();
					if(name.equalsIgnoreCase("width")){
						drawWidthLabel.setText(button_temp.getLabel());
						palette.setStrokeWidth(Float.parseFloat(button_temp.getLabel()));
					}
				
				}
			});
			buttonWidth[i].setName("width");
		}
		toolBarPane.add(paintWidthSelectPanel);
		
		this.setUndecorated(true);
		this.setTitle("画板");
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(toolBarPane, BorderLayout.NORTH);
		this.add(palette);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setIconImage(Toolkit.getDefaultToolkit().createImage("images/palette.png"));
		
//		// 设置窗口的大小
		this.setSize(new Dimension(300, 230));
		// 设置窗口位置
		int x = chatclient.myLocation.x;
		int y = chatclient.myLocation.y;
		this.setLocation(x+300, y+200);
		this.setVisible(true);
	}

	/**
	 * 选取颜色按钮的监听事件类
	 */
	class ButtonColorAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Color color_temp = ((JButton)e.getSource()).getBackground();
			showCurrentColor.setBackground(color_temp);
			palette.setColor(color_temp);
		}
	}
}

