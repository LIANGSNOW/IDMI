package nus.iss.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import nus.iss.dao.IImageDao;
import nus.iss.model.Image;
import nus.iss.service.IEnquiryImageService;

public class EnquiryImageServiceImpl implements IEnquiryImageService {
	
	private IImageDao imageDao;
	private double distanceScope = 30;
	private String imageServerURL = "https://s3-ap-southeast-1.amazonaws.com/idmdevelopment/";

	@Override
	public String enquiryImages(double latitude, double longitude) {
		// TODO Auto-generated method stub

		JSONArray jsonArray = new JSONArray();
		List<Image> images = this.imageDao.getAllImages();
		if(images != null){
			for(Image image:images){
				double distance = this.distance(latitude, image.getLatitude(), longitude, image.getLongitude());
				if(distance < this.distanceScope){
					jsonArray.put(this.imageServerURL + image.getImgName());
				}
			}
		}

		return jsonArray.toString();
	}
	
	@Override
	public String enquiryImagesWithCoordinate(double latitude, double longitude) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		List<Image> images = this.imageDao.getAllImages();
		if(images != null){
			for(Image image:images){
				double distance = this.distance(latitude, image.getLatitude(), longitude, image.getLongitude());
				if(distance < this.distanceScope){
					HashMap<String, String> map = new HashMap<>();
					map.put("imageName", this.imageServerURL + image.getImgName());
					map.put("latitude", image.getLatitude() + "");
					map.put("longitude", image.getLongitude() + "");
					map.put("size", image.getSize() + "");
					jsonArray.put(map);
				}
			}
		}

		return jsonArray.toString();
	}
	
	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * @returns Distance in Meters
	 */
	public double distance(double lat1, double lat2, double lon1, double lon2) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(lat2 - lat1);
	    Double lonDistance = Math.toRadians(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    return distance;
	}

	public IImageDao getImageDao() {
		return imageDao;
	}

	public void setImageDao(IImageDao imageDao) {
		this.imageDao = imageDao;
	}

	@Override
	public int getTotalSize(List<String> images) {
		// TODO Auto-generated method stub
		
		return this.imageDao.getTotalSize(images);
	}

}
