package edu.seaaddicts.brockbutler.maps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.seaaddicts.brockbutler.R;
import edu.seaaddicts.brockbutler.help.HelpActivity;

public class MapsActivity extends Activity {
	private static final String TAG = "MapsActivity";

	private TextView mTemp;
	private Button start;
	private Button resume;
	private Position pTest;

	private EditText mSearchEditText;

	private Handler mHandler;
	private MapsTouchImageView mMapImage;
	private MapsHandler mMapsHandler;

	private Position currentLocation;
	private Position mStartPosition;
	private Position mGoalPosition;
	private Astar school;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_map);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.maps_layout);
		layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.actionbar_bg));
		init();

		mTemp = (TextView) findViewById(R.id.txtv_count);
		mHandler = new Handler() {

			// Handles Messages sent from Thomas.
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MapsHandler.MAPS_REQUEST_UPDATE:
					Log.d("MAIN HANDLER", "YAYAAA!!!");
					break;
				case MapsHandler.THREAD_UPDATE_POSITION:
					Log.d("TEST","-----+++++ Got THREAD_UPDATE_POSITION message. +++++-----");
					break;
				default:
					Log.d(TAG,"-----+++++ Got THREAD_UPDATE_POSITION message. +++++-----");
					mTemp.setText("#" + msg.what);
					break;
				}
			}
		};
//		mMapsHandler = new MapsHandler(mHandler);
//        school = new Astar();
		mMapsHandler = new MapsHandler(mHandler,this);
        school = new Astar();
        mContext = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

	private void init() {
		mMapImage = (MapsTouchImageView) findViewById(R.id.imgv_map);
		// mMapImage.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// mMapsHandler.sendEmptyMessage(MapsHandler.MAPS_REQUEST_UPDATE);
		// }
		// });
		// start = (Button) findViewById(R.id.btnstart);
		// start.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// mMapsHandler.sendEmptyMessage(MapsHandler.THREAD_REQUEST_START);
		// }
		// });
		// stop = (Button) findViewById(R.id.btnstop);
		// stop.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// mMapsHandler.sendEmptyMessage(MapsHandler.THREAD_REQUEST_PAUSE);
		// }
		// });
		//
		// resume = (Button) findViewById(R.id.btnresume);
		// resume.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// mMapsHandler
		// .sendEmptyMessage(MapsHandler.THREAD_REQUEST_RESUME);
		// }
		// });
	}

	@Override
	public void onBackPressed() {
		mMapsHandler.sendEmptyMessage(MapsHandler.THREAD_REQUEST_PAUSE);
		mMapsHandler = null;
		super.onBackPressed();
	}

	/**
	 * 
	 * @param item
	 */
	public void exitMaps(MenuItem item) {
		onBackPressed();
	}

	/**
	 * Prompts user to search for existence of a location.
	 * 
	 * @param item
	 */
	public void displaySearchDialog(MenuItem item) {
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("MChown Location Search");
		editalert.setMessage("Enter block or room name (i.e. B203)");

		mSearchEditText = new EditText(this);
		mSearchEditText.setSingleLine(true);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mSearchEditText.setLayoutParams(lp);
		editalert.setView(mSearchEditText);

		final ListView locList = new ListView(this);

		editalert.setPositiveButton("Search",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(getApplicationContext(),
								"Thomas' search method goes here.",
								Toast.LENGTH_LONG).show();
						// Call Thomas' location search method with EditText
						// string.
						
						// TESTING
						Position p1, p2;
						p1 = school.findPosition("J01");
						p2 = school.findPosition("J23");
						pTest = school.findPosition(mSearchEditText.getText().toString());
						pTest.printCoordinates();
						
						Position[] p = school.pathGeneration(p1, pTest);
						mMapImage.drawPosition(p);
						
						//pTest = school.findPosition(mSearchEditText.getText().toString());
						//pTest.printCoordinates();
						
						
						//Log.d("SEARCH", "Location: " + mSearchEditText.getText().toString() + ", " + pTest.xPosition + "," + pTest.yPosition);
					}
				});
		editalert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing
					}
				});

		editalert.show();
	}

	/**
	 * Displays AlertDialog for user to enter destination. First the location is
	 * determined to exist, then if true path is drawn on map.
	 * 
	 * @param item
	 */
	public void displayGetDirectionsDialog(MenuItem item) {
		AlertDialog.Builder editalert = new AlertDialog.Builder(this);

		editalert.setTitle("MChown Direction Getter");
		editalert.setMessage("Enter block or room name (i.e. B203)");

		final EditText input = new EditText(this);
		input.setSingleLine(true);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		editalert.setView(input);

		final ListView locList = new ListView(this);

		editalert.setPositiveButton("Search",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Call Thomas' location search method with EditText
						// string.
						// if (locationFound) {
						// getDirections()
						// draw_directions_on_map
						// else
						// displayNoSuchLocationDialog()

						mStartPosition = school.findPosition("J01");
						mGoalPosition = school.findPosition(mSearchEditText.getText().toString());

						if (school.nodeExist(mStartPosition) && school.nodeExist(mGoalPosition)) {
							Position[] route = school.pathGeneration(mStartPosition, mGoalPosition);

							if (route != null) {
								for (int i = 0; i < route.length; i++)
									Log.i("PRINT ROUTE", route[i].nodeNumber);
								mMapImage.drawPosition(route);
							} else {
								Log.e("ROUTE ERROR", "No path generated");
							}
						}
						
						Toast.makeText(getApplicationContext(),
								"Thomas' search method goes here.",
								Toast.LENGTH_LONG).show();
						Toast.makeText(getApplicationContext(),
								"Thomas' direction getter method goes here.",
								Toast.LENGTH_LONG).show();
					}
				});
		editalert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing
					}
				});

		editalert.show();
	}

	public void showHelp(MenuItem item)
	{
		Intent intent = new Intent(MapsActivity.this,HelpActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("activity", "maps");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * Menu item onClick that calls mapping function to manual update user
	 * location in case first reported location is not accurate.
	 * 
	 * @param item
	 */
	public void updatePosition(MenuItem item) {
		Toast.makeText(getApplicationContext(), "Thomas' method goes here.",
				Toast.LENGTH_LONG).show();
		// call Thomas' method to update current position.
	}
}
