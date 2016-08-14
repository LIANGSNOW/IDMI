package nus.iss.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.*;

import nus.iss.service.IEnquiryImageService;

public class EnquiryImagesAction {
	
	private String latitude;
	private String longitude;
	
	private IEnquiryImageService enquiryImageService;
	
	public void enquiryImages() throws IOException, JSONException{
		if(this.latitude == null && this.longitude == null){
			return;
		}
	    HttpServletResponse response=ServletActionContext.getResponse();  
	    response.setContentType("text/html;charset=utf-8");  
	    PrintWriter out = response.getWriter();  
	    String jsonString = this.enquiryImageService.enquiryImages(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude));
	    out.println(jsonString);  
	    out.flush();  
	    out.close();  
	}
	
	public void enquiryImagesWithCoordinate() throws IOException, JSONException{
		if(this.latitude == null && this.longitude == null){
			return;
		}
	    HttpServletResponse response=ServletActionContext.getResponse();  
	    response.setContentType("text/html;charset=utf-8");  
	    PrintWriter out = response.getWriter();  
	    String jsonString = this.enquiryImageService.enquiryImagesWithCoordinate(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude));
	    out.println(jsonString);  
	    out.flush();  
	    out.close(); 
	}
	
	public IEnquiryImageService getEnquiryImageService() {
		return enquiryImageService;
	}

	public void setEnquiryImageService(IEnquiryImageService enquiryImageService) {
		this.enquiryImageService = enquiryImageService;
	}

	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	

}
