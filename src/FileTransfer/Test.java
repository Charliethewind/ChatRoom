package FileTransfer;

import java.io.File;

import javax.swing.*;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
	JFileChooser jfc = new JFileChooser();
	jfc.showOpenDialog(null);
	File s = jfc.getSelectedFile();
	System.out.println(s);
	
    }

}

//JOptionPanelz的实验
/*//TODO Auto-generated method stub
	if(JOptionPane.showConfirmDialog(null, "are you sure"+"?","really",JOptionPane.OK_CANCEL_OPTION)
		== JOptionPane.OK_OPTION)
	    System.out.println("ok");
	else
	    System.out.println("not ok");
	if(JOptionPane.showConfirmDialog(null, "are you sure?","really",JOptionPane.OK_CANCEL_OPTION)
		== JOptionPane.CANCEL_OPTION)
	    System.out.println("cancel");*/