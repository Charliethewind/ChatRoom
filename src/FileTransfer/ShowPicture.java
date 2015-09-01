package FileTransfer;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShowPicture {
	private JDialog showPicDialog;
	private JPanel imagePanel;
	private ImageIcon pic;
	JButton closeButton;
	public ShowPicture(int x, int y, String path) {
		showPicDialog = new JDialog();
		pic = new ImageIcon(path);// 背景图片
System.out.println("加载背景图片完毕");
		JLabel label = new JLabel(pic);// 把背景图片显示在一个标签里面
		showPicDialog.setBounds(x, y, pic.getIconWidth(),pic.getIconHeight()+30);
		showPicDialog.setUndecorated(true);
		//按钮设置
		closeButton = new JButton("关闭");
		closeButton.setOpaque(true);
		closeButton.setContentAreaFilled(false);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPicDialog.dispose();
//				showPicDialog = null;
				System.gc();
			}
		});
		
		// 把标签的大小位置设置为图片刚好填充整个面板
		label.setBounds(0, 0, pic.getIconWidth(),pic.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		imagePanel = (JPanel) showPicDialog.getContentPane();
		imagePanel.setOpaque(false);
		// 内容窗格默认的布局管理器为BorderLayout
		imagePanel.setLayout(new FlowLayout());
		imagePanel.add(closeButton);

		showPicDialog.getLayeredPane().setLayout(null);
		// 把背景图片添加到分层窗格的最底层作为背景
		showPicDialog.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		showPicDialog.setSize(pic.getIconWidth(), pic.getIconHeight());
		showPicDialog.setResizable(false);
		showPicDialog.setVisible(true);
System.out.println("显示为true");
	}
}
