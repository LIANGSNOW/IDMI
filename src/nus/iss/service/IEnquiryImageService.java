package nus.iss.service;

import java.util.List;

public interface IEnquiryImageService {
	
	/*
	 * return the images' url
	 */
	public String enquiryImages(double latitude, double longitude);
	
	/*
	 * return the images'url with coordinate
	 */
	public String enquiryImagesWithCoordinate(double latitude, double longitude);
	/*
	 * return the total size of the enquired images, the argument should be a list of image name
	 */
	public int getTotalSize(List<String> images);
}
