package edu.seaaddicts.brockbutler.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import edu.seaaddicts.brockbutler.R;
import edu.seaaddicts.brockbutler.coursemanager.Course;
import edu.seaaddicts.brockbutler.coursemanager.CourseHandler;

public class AddTaskActivity extends Activity {
	private static final int DATE_DIALOG_ID = 0;

	private Button mDueDateButton;
	private Button mSaveButton;
	private Button mCancelButton;
	
	private ArrayList<Course> mRegCourses;
	private EditText mCourseTitle;
	private TextView mDueDateTextView;

	private Spinner mCourseSpinner;
	private Spinner mPrioritySpinner;

	private CourseHandler mCourseHandle = null;

	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init();
	}

	/*
	 * Initialize all views and sets Button OnClickListeners.
	 */
	private void init() {
		mCourseHandle = new CourseHandler(this);
		mDueDateButton = (Button) findViewById(R.id.add_task_due_date_button);
		mSaveButton = (Button) findViewById(R.id.add_task_save_button);
		mCancelButton = (Button) findViewById(R.id.add_task_cancel_button);
		mDueDateTextView = (TextView) findViewById(R.id.add_task_date_textview);
		mPrioritySpinner = (Spinner) findViewById(R.id.add_task_priority_spinner);
		mCourseTitle = (EditText) findViewById(R.id.add_task_title);

		mCourseSpinner = (Spinner) findViewById(R.id.add_task_course_spinner);

		mRegCourses = mCourseHandle.getRegisteredCourses();
		ArrayList<String> cs = new ArrayList<String>();

		// Create ArrayList of Course Strings in format: SUBJECT CODE
		for (int i = 0; i < mRegCourses.size(); i++) {
			cs.add(mRegCourses.get(i).mSubject + " " + mRegCourses.get(i).mCode);
		}
		// Set the course Spinner
		mCourseSpinner.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, cs));		

		// DatePicker stuff.
		final Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);

		// OnClickListener for DatePicker
		mDueDateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Task t = new Task();
				t.mSubj = mRegCourses.get(mCourseSpinner.getSelectedItemPosition()).mSubject;
				t.mCode = mRegCourses.get(mCourseSpinner.getSelectedItemPosition()).mCode;
				t.mName = mCourseTitle.getText().toString();
				// Lower == higher priority
				t.mPriority = mPrioritySpinner.getSelectedItemPosition();
				t.mDueDate = mDueDateTextView.getText().toString();
				
				// Get current date
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				Date date = new Date();
				t.mCreationDate = dateFormat.format(date);
				mCourseHandle.addTask(t);
				onBackPressed();
			}
		});

		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Do nothing.
				onBackPressed();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 * 
	 * Overridden method to handle multiple dialogs that may be created from
	 * this activity.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDatePickerListener, mYear,
					mMonth, mDay);
		}
		return null;
	}

	/*
	 * Retrieves date set from DatePickerDialog and sets TextView in this
	 * activity.
	 */
	private DatePickerDialog.OnDateSetListener mDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			mYear = selectedYear;
			mMonth = selectedMonth+1;	// Since index starts at 0 and there is no 0 month.
			mDay = selectedDay;

			// Set TextView in this activity.
			if (mMonth < 10) {
				if (mDay < 10) {
			mDueDateTextView.setText(new StringBuilder().append(mYear)
					.append("/").append(0).append(mMonth).append("/").append(0).append(mDay));
				} else
					mDueDateTextView.setText(new StringBuilder().append(mYear)
							.append("/").append(0).append(mMonth).append("/").append(mDay));
			} else if (mMonth > 9) {
				if (mDay < 10) {
				mDueDateTextView.setText(new StringBuilder().append(mYear)
						.append("/").append(mMonth).append("/").append(0).append(mDay));
				} else 
					mDueDateTextView.setText(new StringBuilder().append(mYear)
							.append("/").append(mMonth).append("/").append(mDay));
			}

		}
	};

}
