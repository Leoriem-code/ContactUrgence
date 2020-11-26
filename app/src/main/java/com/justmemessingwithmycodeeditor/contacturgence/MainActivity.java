package com.justmemessingwithmycodeeditor.contacturgence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
	LocationManager locationManager;
	double longitudeGPS, latitudeGPS, longitudeNet, latitudeNet;
	TextView longitudeValueGPS, latitudeValueGPS, longitudeValueNet, latitudeValueNet;
	String Url;
	Button position, copy_paste;
	ClipData clip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		longitudeValueGPS = findViewById(R.id.longitudeGPS);
		latitudeValueGPS = findViewById(R.id.latitudeGPS);
		longitudeValueNet = findViewById(R.id.longitudeNet);
		latitudeValueNet = findViewById(R.id.latitudeNet);
		position = findViewById(R.id.position);
		copy_paste = findViewById(R.id.copy);
		copy_paste.setVisibility(View.INVISIBLE);
		
	}
	
	public void click_position(View view) {
		Toast.makeText(MainActivity.this, Url, Toast.LENGTH_SHORT).show();
		copy_paste.setVisibility(View.VISIBLE);
	}
	public void click_copy(View view) {
		ClipboardManager clipboard = (ClipboardManager)
				getSystemService(Context.CLIPBOARD_SERVICE);
		clip = ClipData.newPlainText("simple text", Url);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(MainActivity.this, "Url copied", Toast.LENGTH_SHORT).show();
	}
	
	private void requestPermission(){
		ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
	}
	private boolean checkLocation() {
		if(isLocationEnabled())
			showAlert();
		return isLocationEnabled();
	}
	
	private void showAlert() {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Enable Location")
				.setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
				.setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(myIntent);
				})
				.setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
				});
		dialog.show();
	}
	
	private boolean isLocationEnabled() {
		return !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public void toggleGPSUpdates(View view) {
		if(checkLocation())
			return;
		Button button = (Button) view;
		if(button.getText().equals(getResources().getString(R.string.pause))) {
			locationManager.removeUpdates(locationListenerGPS);
			button.setText(R.string.resumeGPS);
		}
		else {
			if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 60 * 1000, 10, locationListenerGPS);
				button.setText(R.string.pause);
			}
			else {
				requestPermission();
			}
		}
	}
	
	public void toggleNetworkUpdates(View view) {
		if(checkLocation())
			return;
		Button button = (Button) view;
		if(button.getText().equals(getResources().getString(R.string.pause))) {
			locationManager.removeUpdates(locationListenerNet);
			button.setText(R.string.resumeNet);
		}
		else {
			if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 60 * 1000, 10, locationListenerNet);
				button.setText(R.string.pause);
			}
		}
	}
	
	private final LocationListener locationListenerGPS = new LocationListener() {
		public void onLocationChanged(Location location) {
			longitudeGPS = location.getLongitude();
			latitudeGPS = location.getLatitude();
			
			runOnUiThread(() -> {
				Url = "https://www.google.com/maps/@"  + latitudeGPS + "," + longitudeGPS + ",18z";
				longitudeValueGPS.setText(longitudeGPS + "");
				latitudeValueGPS.setText(latitudeGPS + "");
				Toast.makeText(MainActivity.this, "GPS position update", Toast.LENGTH_SHORT).show();
			});
		}
		
		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) { }
		@Override
		public void onProviderEnabled(String s) { }
		@Override
		public void onProviderDisabled(String s) { }
	};
	
	private final LocationListener locationListenerNet = new LocationListener() {
		public void onLocationChanged(Location location) {
			longitudeNet = location.getLongitude();
			latitudeNet = location.getLatitude();
			
			runOnUiThread(() -> {
				Url = "https://www.google.com/maps/@"  + latitudeNet + "," + longitudeNet + ",18z";
				longitudeValueNet.setText(longitudeNet + "");
				latitudeValueNet.setText(latitudeNet + "");
				Toast.makeText(MainActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
			});
		}
		
		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) { }
		@Override
		public void onProviderEnabled(String s) { }
		@Override
		public void onProviderDisabled(String s) { }
	};
}