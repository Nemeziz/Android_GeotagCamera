package ca.uoit.AllenKaganovsky.mobile_a3;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	
	private int TAKE_PICTURE = 1;
	private Uri uri;
	double longitude, latitude = 0.0;
	GeoAppController gac;
	File mediaStorageDir;
	public static PhotoAdapter<GeoPhoto> adapter;
	public static int dateSortOrder = 1;
	public static int geoSortOrder = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// check for application read/save folder. 
		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GeoPhotoApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				System.out.println("ERROR: Could not create image directory.");
			}
		}
		// get GeoAppController object and pass the DIR of where our images are to be read and stored.
		gac = new GeoAppController(mediaStorageDir);
		// set custom array adapter to show custom cell.
		adapter = new PhotoAdapter<GeoPhoto>(this, android.R.layout.simple_list_item_1, GeoAppController.photos);
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		// get the location from the NetworkProvider
		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
			}

			@Override
			public void onProviderDisabled(String provider) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status,Bundle extras) {}
			
		};
		// update location listener
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		Button takePhotoBtn = (Button) findViewById(R.id.button1);
		Button sortGeo = (Button) findViewById(R.id.button2);
		Button sortDate = (Button) findViewById(R.id.button3);
		sortGeo.setOnClickListener(geoSortListener);
		sortDate.setOnClickListener(dateSortListener);
		takePhotoBtn.setOnClickListener(new OnClickListener() { // start camera activity
			@Override
			public void onClick(View v) {
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						System.out.println("ERROR: Could not create image directory.");
					}
				}
				// instruct camera activity to save file to uri
				uri = Uri.fromFile(new File(mediaStorageDir,  System.currentTimeMillis() + ".jpg"));
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(takePhotoIntent, TAKE_PICTURE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// creates a new intent to view the photo.
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 
		System.out.println(adapter.getItem(position));
		Intent intent = new Intent(this, ViewPhoto.class);
		intent.putExtra("imgPath", adapter.getItem(position).imgPath);
		intent.putExtra("longitude", adapter.getItem(position).longitude);
		intent.putExtra("latitude", adapter.getItem(position).latitude);
		intent.putExtra("date", adapter.getItem(position).date);
		intent.putExtra("position", position);
	    startActivityForResult(intent, 2);
	}
	
	// run when Sort By Geo button is pressed.
	protected OnClickListener geoSortListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			geoSortOrder *= -1;
			adapter.sort(new GeoComparer());
		}
	};
	
	// run when Sort By Date button is pressed.
	protected OnClickListener dateSortListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dateSortOrder *= -1;
			adapter.sort(new DateComparer());
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){ // returned from taking a photo and took a photo.
			
			// Calculate longitude and latitude in the form of time.
			// hours, minutes, seconds.
			// use Exif to assign the Geotagging attributes to the image file.
			int num1Lon = 0, num2Lon = 0, num1Lat = 0, num2Lat = 0;
			double num3Lon = 0.0, num3Lat = 0.0;
			
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(uri.getPath());
				num1Lon = (int) Math.floor(longitude);
				num2Lon = (int) Math.floor((longitude - num1Lon)*60);
				num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;
				num1Lat = (int) Math.floor(latitude);
				num2Lat = (int) Math.floor((latitude - num1Lat)*60);
				num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"',"+num2Lon+"'',"+(int) num3Lon/1000 + "'''");
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"',"+num2Lat+"'',"+(int) num3Lat/1000 + "'''");
				
				if (longitude > 0)
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "N");
				else
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "S");
				
				if (latitude > 0)
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "E");
				else
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "W");
				
				exif.saveAttributes();
					
			} catch (IOException e) {
				System.out.println("Error: Could not assign GeoTag information.");
			}
			
			
			// Add new photo to adapter photo list
			gac.addPhoto(uri.getPath(), exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE), exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE), uri.getLastPathSegment().replace(".jpg", ""));
			adapter.notifyDataSetChanged();
		}
	}
}

// Sort array adapter by Latitude Geo Location
class GeoComparer implements Comparator<GeoPhoto> {
	public int compare(GeoPhoto o1, GeoPhoto o2) {
		return MainActivity.geoSortOrder*o1.latitude.compareTo(o2.latitude);
	}
}

// Sort array adapter by date
class DateComparer implements Comparator<GeoPhoto> {
	public int compare(GeoPhoto o1, GeoPhoto o2) {
		return MainActivity.dateSortOrder*o1.date.compareTo(o2.date);
	}
}
