package ca.uoit.AllenKaganovsky.mobile_a3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.media.ExifInterface;

public class GeoAppController {

	static ArrayList<GeoPhoto> photos;
	
	GeoAppController(File mediaStorageDir) {
		photos = new ArrayList<GeoPhoto>();
		// get all the files contained within the mediaStorageDir
		File[] files = mediaStorageDir.listFiles();
		for(File file : files) {
			try { // for each file found, read and populate each GeoPhoto with its
				// GeoTag information, date, and image path
				ExifInterface exif = new ExifInterface(file.getPath());
				String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
				String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
				String date = file.getName().replace(".jpg", "");
				String path = file.getPath();
				
				photos.add(new GeoPhoto(path, longitude, latitude, date));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void addPhoto(String path, String longitude, String latitude, String date) {
		photos.add(new GeoPhoto(path, longitude, latitude, date));
	}
	
}

// GeoPhoto object stores an image object
// contains each image path, geo location, and date taken.
class GeoPhoto {
	public String imgPath;
	public String longitude;
	public String latitude;
	public String date;
	
	GeoPhoto(String imgPath, String longitude, String latitude, String date) {
		this.imgPath = imgPath;
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
	}
	
	public String toString() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.valueOf(date));
		return c.getTime().toString();
	}
}
