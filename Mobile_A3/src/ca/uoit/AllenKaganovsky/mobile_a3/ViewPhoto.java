package ca.uoit.AllenKaganovsky.mobile_a3;

import java.io.*;
import java.util.*;

import android.net.*;
import android.os.*;
import android.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class ViewPhoto extends Activity {

	String imgPath = "";
	String longitude1 = "";
	String latitude1 = "";
	String date = "";
	int position = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// retrieve image information passed through intent
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
		} else {
			 imgPath = extras.getString("imgPath");
			 longitude1 = extras.getString("longitude");
			 latitude1 = extras.getString("latitude");
			 date = extras.getString("date");
			 position = extras.getInt("position");
		}
		
		setContentView(R.layout.activity_view_photo);
		
		// set view object data
		
		File imagePath = new File(imgPath);
		Uri uri = Uri.fromFile(imagePath);
		ImageView preview = (ImageView) findViewById(R.id.imageView1);
		preview.setImageURI(uri);
		
		TextView title = (TextView) findViewById(R.id.textView3);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(date));
		title.setText(c.getTime().toString());
		TextView latitude = (TextView) findViewById(R.id.textView1);
		TextView longitude = (TextView) findViewById(R.id.textView2);
		latitude.setText("Lat: " + latitude1);
		longitude.setText("Long: " + longitude1);
		
		Button deleteBtn = (Button) findViewById(R.id.button1);
		deleteBtn.setOnClickListener(deleteButtonListener);
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// deletes the image from the image path
	// removes the item from the array adapter
	// update the array adapter and return.
	private OnClickListener deleteButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			File f = new File(imgPath);
			f.delete();
			MainActivity.adapter.remove(MainActivity.adapter.getItem(position));
			MainActivity.adapter.notifyDataSetChanged();
			finish();
		}
		
	};

}
