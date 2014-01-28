package ca.uoit.AllenKaganovsky.mobile_a3;

import java.util.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class PhotoAdapter<T> extends ArrayAdapter<GeoPhoto> {

	private List<GeoPhoto> photos;
	
	public PhotoAdapter(Context context, int textViewResourceId,
			List<GeoPhoto> objects) {
		super(context, textViewResourceId, objects);
		this.photos = objects;
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		View v = view;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.photo_cell_view, null);
		}
		
		// populate the custom cell view with the GeoPhoto object data
		GeoPhoto gp = photos.get(position);
		if (gp != null) {
			TextView title = (TextView) v.findViewById(R.id.textView1);
			TextView latitude = (TextView) v.findViewById(R.id.textView2);
			TextView longitude = (TextView) v.findViewById(R.id.textView3);
			title.setText(gp.toString());
			latitude.setText("Lat: " + gp.latitude);
			longitude.setText("Long: " + gp.longitude);
		}
		
		return v;
	}
	
	

}
