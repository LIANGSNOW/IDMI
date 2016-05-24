package nus.iss.action;

public class JNIInterface {
	
	public native void convertImg(String inputPath, String outPath);
	 
	 static{
		 System.loadLibrary("IcubeJNI");
	 }
}
