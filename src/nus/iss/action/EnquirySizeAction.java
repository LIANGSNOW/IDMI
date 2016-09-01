package nus.iss.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.*;

import nus.iss.service.IEnquiryImageService;

public class EnquirySizeAction {
	
	private IEnquiryImageService enquiryImageService;
	private String imageNames;
	
	public void getTotalSizeByImageNames() throws IOException, JSONException{
		if(this.imageNames == null){
			return;
		}
	    HttpServletResponse response=ServletActionContext.getResponse();  
	    response.setContentType("text/html;charset=utf-8");  
	    PrintWriter out = response.getWriter();  
	    ArrayList<String> images = new ArrayList<String>();
	    String[] imageNamesArray = imageNames.split(",");
	    for(int i = 0; i< imageNamesArray.length; i++){
	    	images.add(imageNamesArray[i]);
	    }
	    int size = this.enquiryImageService.getTotalSize(images);
	    out.println(size);  
	    out.flush();  
	    out.close(); 
	}

	public String getImageNames() {
		return imageNames;
	}

	public void setImageNames(String imageNames) {
		this.imageNames = imageNames;
	}

	public IEnquiryImageService getEnquiryImageService() {
		return enquiryImageService;
	}

	public void setEnquiryImageService(IEnquiryImageService enquiryImageService) {
		this.enquiryImageService = enquiryImageService;
	}

}
