package DataStructure;

import java.util.Date;

public abstract class Time {
	public static Date date = new Date();
	public static String getCurrentTime(){
		return  "\n  "+String.valueOf(date.getHours())+":"+String.valueOf(date.getMinutes())
				+":"+String.valueOf(date.getSeconds()+"\n ");
		
	}
}
