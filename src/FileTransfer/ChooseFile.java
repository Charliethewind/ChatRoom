package FileTransfer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ChooseFile extends JFileChooser{
	public ChooseFile(){
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "PNG", "jpg", "gif","mkv","png","zip","doc","docx","mp4");
		this.setFileFilter(filter);
//		this.setLocation(0,0);
		
		int returnVal = this.showOpenDialog(null);
	}
/*	public static void main(String[] args){
	    ChooseFile choosefile = new ChooseFile();
//	    System.out.println(File.separator);
	    String s = choosefile.getSelectedFile().toString();
//	    String s = "D:\123.jpg";
//	    System.out.println(s.substring(s.lastIndexOf(File.separator)+1)  );
//	    System.out.println(s);
//	    File file = new File(s);
	    try {
		DataInputStream dis = new DataInputStream(new FileInputStream(new File(s)));
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("ok");
	}*/
}
