package nus.iss.service;

public interface IEnquiryImageService {
	
	/*
	 * return the images' url
	 */
	public String enquiryImages(double latitude, double longitude);
	
	/*
	 * return the images'url with coordinate
	 */
	public String enquiryImagesWithCoordinate(double latitude, double longitude);
	
}
