package nus.iss.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class UploadAction extends ActionSupport{

	 private File file; 
	 private String fileFileName; 
	 private String fileContentType;
	 private String dataUrl;
	 public static String imgname  = "Tom Clancy's The .jpg";
	 public String location;
	 
	 
	 public  String execute() throws Exception {
				
		if(file == null){
			return "false";
		}
		System.out.println( System.getProperty("java.library.path"));
		JNIInterface test = new JNIInterface();	
		
		

		String imgpath = "upload/";

		InputStream is = new FileInputStream(file);
		
		
		String path = ServletActionContext.getServletContext().getRealPath("/");
		
		System.out.println(path);
		
		dataUrl = path+imgpath+this.getFileFileName();
		
		//dataUrl = "C:\\Users\\Snow\\Desktop\\test\\"+this.getFileFileName();
		
		//imgname =  this.getFileFileName();
		
		//imgname = "test1.jpg";
		
		String name = this.getFileFileName();
		
		StringTokenizer st=new StringTokenizer(name,".");
		
		String nameUpdate = st.nextToken()+"update"+"."+st.nextToken();
		
		imgname = nameUpdate;
		
		System.out.println(imgname);
		
		File destFile = new File(path+imgpath, this.getFileFileName());
		 
	    OutputStream os = new FileOutputStream(destFile);
	    
	    byte[] buffer = new byte[400];
	    int length = 0;
	    
	    while ((length = is.read(buffer)) > 0) {
	    	os.write(buffer, 0, length);
	    }
	    

	    is.close();
	    
	    os.close();
		
	    test.convertImg(dataUrl,
	    		 path+imgpath+imgname);
	    //location ="hhh";
//	  
//	    initClinet(dataUrl);
	    
		return SUCCESS;
		
			 
	}
//
//	
//	public void initClinet(String url) throws UnknownHostException, IOException{
//		Socket socket = new Socket("172.23.22.163", 34567);
//		String output = new String(url);
//		socket.getOutputStream().write(output.getBytes());
//		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));          
//		String s;  
//		while ((s = in.readLine()) != null) {  	
//			location = s;
//		    System.out.println("Reveived: " + s);  
//		}
//		socket.close();
//	}

	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public String getDataUrl() {
		return dataUrl;
	}


	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}


	public String getFileFileName() {
		return fileFileName;
	}


	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}


	public String getFileContentType() {
		return fileContentType;
	}


	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}


	public String getImgname() {
		return imgname;
	}


	public void setImgname(String imgname) {
		this.imgname = imgname;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
