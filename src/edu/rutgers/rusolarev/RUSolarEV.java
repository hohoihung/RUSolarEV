/*
  RUSolarEV communicates with Arduino via Amarino 2.0 to control electronics onboard the EV.
  The app also receives sensor data coming from the sensors on the EV.
  last modified on 12-17-2012
  Authors:Anton Krivosheyev, Kevin Sung
*/
package edu.rutgers.rusolarev;

import edu.rutgers.rusolarev.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class RUSolarEV  extends Activity {
	
	private static final String TAG = "SensorGraph";
	
	// change this to your Bluetooth device address 
	private static final String DEVICE_ADDRESS =  "00:06:66:42:9A:4B"; //"00:06:66:03:73:7B";
	
	private TextView mValueTV1;
	private TextView mValueTV2;
	private TextView mValueTV3;
	private TextView mValueTV4;
	private TextView mValueTV5;
	
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.main);
      
      // get handles to Views defined in our layout file
      mValueTV1 = (TextView) findViewById(R.id.value1);
      mValueTV2 = (TextView) findViewById(R.id.value2);
      mValueTV3 = (TextView) findViewById(R.id.value3);
      mValueTV4 = (TextView) findViewById(R.id.value4);
      mValueTV5 = (TextView) findViewById(R.id.value5);
      
  }
  
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		
		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(this, DEVICE_ADDRESS);
	}


	@Override
	protected void onStop() {
		super.onStop();
		
		// if you connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(this, DEVICE_ADDRESS);
		
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}
	

	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;
			
			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				
				if (data != null){
					//mValueTV.setText(data + " mi/h");
					try {
						// since we know that our string value is an int number we can parse it to an integer
						int sensorReading = Integer.parseInt(data);
					
						//there are 5 sensors, data for each sensor is sent in a sequence
						if (sensorReading >= 0 && sensorReading <= 1023) 
						{
							mValueTV1.setText("X = " + (sensorReading));
						}
						else if (sensorReading >= 1024 && sensorReading <= 1024*2-1) 
						{
							sensorReading=sensorReading-1024*1;
							mValueTV2.setText("Y = " + sensorReading);	
						}
						else if (sensorReading >= 1024*2 && sensorReading <= 1024*3-1) 
						{
							sensorReading=sensorReading-1024*2;
							mValueTV3.setText("Z = " + sensorReading);	
						}
						else if (sensorReading >= 1024*3 && sensorReading <= 1024*4-1) 
						{
							sensorReading=sensorReading-1024*3;
							mValueTV4.setText("S4 = " + sensorReading);	
						}
						else if (sensorReading >= 1024*4 && sensorReading <= 1024*5-1) 
						{
							sensorReading=sensorReading-1024*4;
							mValueTV5.setText("S5 = " + sensorReading);	
						}	
						else
						{
							mValueTV1.setText("S1 DATA ERROR");	
							mValueTV2.setText("S2 DATA ERROR");	
							mValueTV3.setText("S3 DATA ERROR");	
							mValueTV4.setText("S4 DATA ERROR");	
							mValueTV5.setText("S5 DATA ERROR");	
						}
					} 
					catch (NumberFormatException e) { /* oh data was not an integer */ }
				}
			}
		}
	}

	public void updateRightBlinker(View view) {
		
		boolean on = ((ToggleButton) view).isChecked();
		int state = 0;
		
		if (on)
			state = 1;
		else
			state = 0;
		
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'r', state);
		
	}
	
	public void updateLeftBlinker(View view) {
		
		boolean on = ((ToggleButton) view).isChecked();
		int state = 0;
		
		if (on)
			state = 1;
		else
			state = 0;
		
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'l', state);
		
	}
	
	public void updateHeadLight(View view) {
		
		boolean on = ((ToggleButton) view).isChecked();
		int state = 0;
		
		if (on)
			state = 1;
		else
			state = 0;
		
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'h', state);
		
	}
	
	public void updateHazardLight(View view) {
		
		boolean on = ((ToggleButton) view).isChecked();
		int state = 0;
		
		if (on)
			state = 1;
		else
			state = 0;
		
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'z', state);
		
	}
	
}

