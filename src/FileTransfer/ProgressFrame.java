package FileTransfer;

import java.awt.HeadlessException;

import javax.swing.*;

public class ProgressFrame extends JFrame {
    private JProgressBar jprogressbar= null;
    public ProgressFrame(int x,int y) throws HeadlessException {
	this.setLocation(x, y);
	this.setSize(200, 100);
	jprogressbar = new JProgressBar();
	this.add(jprogressbar);
    }
    public void updateProgressbar(int  l){
	this.jprogressbar.setValue(l*10-1);
    }
    public void shutdown(){
	this.dispose();
    }
/*    public static void main(String[] args){
	ProgressFrame pf = new ProgressFrame(300,200);
	pf.updateProgressbar(10);
	pf.setVisible(true);
	JOptionPane.showMessageDialog(null, "对方拒收文件呢", "Error", JOptionPane.OK_CANCEL_OPTION);

    }*/
    
}
