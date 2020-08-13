package com.safeatwork.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safeatwork.R;
import com.safeatwork.adapters.CubeAdapter;
import com.safeatwork.bluetooth.App;
import com.safeatwork.bluetooth.DeviceListAdapter;
import com.safeatwork.bluetooth.DiscoverService;
import com.safeatwork.bluetooth.ListAdapterModel;
import com.safeatwork.common.Common;
import com.safeatwork.common.NetworkStateReceiverListener;
import com.safeatwork.common.PrefSession;
import com.safeatwork.common.TimeAndDate;
import com.safeatwork.data.AsyncTaskDbOperation;
import com.safeatwork.data.ExcelSeatDataWork;
import com.safeatwork.data.LocalDataBaseHelper;
import com.safeatwork.data.ParseRawSeatsToListFormat;
import com.safeatwork.location.LocationFinder;
import com.safeatwork.location.receiver.GeofenceBroadcastReceiver;
import com.safeatwork.model.CubeModel;
import com.safeatwork.model.EmployeeModel;
import com.safeatwork.model.RowModel;
import com.safeatwork.model.SeatModel;
import com.safeatwork.model.dbformat.Seat;
import com.safeatwork.reminder.activities.AlarmActivity;
import com.safeatwork.ui.login.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.safeatwork.common.Common.isEmpty;
import static com.safeatwork.data.AsyncTaskDbOperation.DB_OPER_UPDATE_SEAT_SWIPEOUT;
import static com.safeatwork.model.Constants.ACTIVITY_RESULT_FINE_LOCATION_PERMISSION;
import static com.safeatwork.model.Constants.ACTIVITY_RESULT_READ_FILE;
import static com.safeatwork.model.Constants.ACTIVITY_RESULT_READ_PERMISSION;
import static com.safeatwork.model.Constants.ADMIN_ACCESS;
import static com.safeatwork.model.Constants.APP_NAME;
import static com.safeatwork.model.Constants.DATE_FORMAT;
import static com.safeatwork.model.Constants.DIALOG_DATA_RESET;
import static com.safeatwork.model.Constants.DIALOG_EXCEL_UPLOAD;
import static com.safeatwork.model.Constants.DIALOG_LOG_OUT;
import static com.safeatwork.model.Constants.DIALOG_SWIPE_OUT;
import static com.safeatwork.model.Constants.SEAT_AVAILABLE;
import static com.safeatwork.model.Constants.SEAT_BLOCKED_BY_ADMIN;
import static com.safeatwork.model.Constants.SEAT_USER_BOOKED;
import static com.safeatwork.model.Constants.SEAT_USER_NOT_BOOKED;

public class DashBoardActivity extends AppCompatActivity implements NetworkStateReceiverListener, AdapterView.OnItemSelectedListener {

    String TAG = APP_NAME + "_DashBoardActivity";
    List<RowModel> rowModelList = new ArrayList<>();
    ViewGroup recycler_layout;
    public static boolean isAllowedToBookNewSeat = false;
    public static int userSeatStatus = SEAT_USER_NOT_BOOKED;
    public static TextView textview_status, textview_user_seatcode, textview_seat_available, textview_seat_blocked, textview_seat_booked;
    public static Button user_swipe_out;
    public static TextView textview_emp_name, textview_emp_id;
    String emp_email;
    public static TextView textview_date;
    public static int seat_available_count = 0, seat_blocked_count = 0, seat_booked_count = 0;
    public static SeatModel swipe_out_booked_seat_model;
    public static EmployeeModel user_emp_model;
    ProgressBar progressBar;
    TextView retry;
    TextView reset_all_data, upload_excell;
    private NetworkStateReceiver networkStateReceiver;

    boolean isOfflineData = false, isCloudDbEmpty = false;

    @Override
    protected void onDestroy() {

        try {
            if (mRec1Status)
                unregisterReceiver(mBroadcastReceiver1);
            if (mAddListRecStatus)
                unregisterReceiver(addListReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }


        user_emp_model = null;
        seat_available_count = 0;
        seat_blocked_count = 0;
        seat_booked_count = 0;
        swipe_out_booked_seat_model = null;
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean isChecked = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user_emp_model = new EmployeeModel();

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        textview_emp_name = findViewById(R.id.emp_name);
        textview_emp_id = findViewById(R.id.emp_code);
        textview_user_seatcode = findViewById(R.id.user_seatcode);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //pos= extras.getInt("pos");
            emp_email = extras.getString("emp_email");
            LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(getApplicationContext());
            localDataBaseHelper.dbInit(localDataBaseHelper);
            user_emp_model = localDataBaseHelper.getUserDetails(emp_email, null);
            localDataBaseHelper.dbDeInit(localDataBaseHelper);
            if (user_emp_model != null) {
                setUserDetailsToUi();
            }
        }
        if (user_emp_model != null) {
            setUiControls();
        }
        //createSeatTables(textview_date.getText().toString());
        //new AsyncTaskDbSeatOperation(textview_date.getText().toString()).execute();
        firebaseInit();

        initLocationUiWorks(isChecked);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check?
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTIVITY_RESULT_FINE_LOCATION_PERMISSION);
            } else {
                initBluetoothUiWorks();
            }
        }

    }

    void setUiControls() {
        TextView location = findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                startActivity(intent);
            }
        });
        textview_seat_available = findViewById(R.id.seat_available_count);
        textview_seat_blocked = findViewById(R.id.seat_blocked_count);
        textview_seat_booked = findViewById(R.id.seat_booked_count);
        textview_status = findViewById(R.id.status);

        recycler_layout = findViewById(R.id.recycler_layout);
        user_swipe_out = findViewById(R.id.user_swipe_out);
        user_swipe_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog(getApplicationContext(), "Swipe Out for Today", "Confirm Swipe out when you complete the work and leave office for the day", "Confirm", DIALOG_SWIPE_OUT);
            }
        });

        textview_date = findViewById(R.id.date);
        TimeAndDate timeAndDate = new TimeAndDate();
        textview_date.setText(timeAndDate.getTodaysDate(DATE_FORMAT));
        /*textview_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDatePickerDialog();
            }
        });*/

        progressBar = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                firebaseInit();
            }
        });
    }

    void firebaseInit() {
        if (Common.isOnline(getApplicationContext())) {
            retry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            //addNewSeat();
            //updateSeat();
            //mDatabase.child("seat").child("45").removeValue();
            mEventsQuery = mDatabase.child("seat");/*.child("0");*/
            //mEventsQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());
            loadDataFromCloud();
        } else {
            new AsyncTaskDbSeatOperation(false).execute();
            Toast.makeText(getApplicationContext(), "Please connect to Internet and Refresh", Toast.LENGTH_SHORT).show();
            retry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                reset_all_data.setVisibility(View.GONE);
            }
        }
    }

    void setUserDetailsToUi() {
        textview_emp_name.setText(user_emp_model.getEmp_name());
        textview_emp_id.setText(user_emp_model.getEmp_id());
        textview_user_seatcode.setText(user_emp_model.getSeat_code());
        if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
            TextView is_admin = findViewById(R.id.is_admin);
            is_admin.setVisibility(View.VISIBLE);
            reset_all_data = findViewById(R.id.reset_all_data);
            reset_all_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog(getApplicationContext(), "Reset All Data", "Are you sure you want to erase all user seat data from databases?", "Yes", DIALOG_DATA_RESET);
                }
            });
            upload_excell = findViewById(R.id.upload_excell);
            upload_excell.setVisibility(View.VISIBLE);
            upload_excell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, " upload_excell onclick");
                    createDialog(getApplicationContext(), "Upload Seat Data", "Upload new seat data excel file from you device will replace current data. Do you want to continue?", "Yes", DIALOG_EXCEL_UPLOAD);
                }
            });

        }
    }

    void addNewSeat() {
        Seat seat = new Seat("1", "54203937", "Sarath", "sarath@hcl.com",
                "1", "4", "1", "3", "09-07-2020", "09-07-2020 12:10:00", "09-07-2020",
                "1", "0", "empty", "empty", "empty", "1");

        mDatabase.child("seat").child("45").setValue(seat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });
        //(String cube_num, String default_user_empid, String default_user_name, String default_user_rm_email,
        // String floor_num, String id, String odc_num, String row_num, String seat_booked_for, String seat_booking_time,
        // String seat_date, String seat_num, String seat_status, String temp_user_name, String temp_user_id, String temp_user_rm_name, String tower_num) {
    }


    public void updateSeat(String id, String seat_status) {
        Log.e(TAG, " firebase updateSeat" + id);
        Seat seatToUpdate = null;
        for (Seat seat : firebaseSeatList) {
            if (seat.id.equals(id)) {
                seatToUpdate = seat;
                seatToUpdate.seat_status = seat_status;
            }
        }
        Map<String, Object> seatValues = seatToUpdate.toMap();
        mDatabase.child("seat").child(id).updateChildren(seatValues);
    }

    public void updateBookedDefaultSeat(String id, String seatStatus, String
            booking_time, String booked_date) {
        Log.e(TAG, "firebase updateBookedDefaultSeat" + id);
        Seat seatToUpdate = null;
        for (Seat seat : firebaseSeatList) {
            if (seat.id.equals(id)) {
                seatToUpdate = seat;
                seatToUpdate.seat_status = seatStatus;
                seatToUpdate.seat_booking_time = booking_time;
                seatToUpdate.seat_booked_for = booked_date;
            }
        }
        Map<String, Object> seatValues = seatToUpdate.toMap();
        mDatabase.child("seat").child(id).updateChildren(seatValues);
    }

    public void updateSwipeOut(String id, boolean isUser) {
        Log.e(TAG, "firebase updateBookedDefaultSeat" + id + "boolean isUser " + isUser);
        Seat seatToUpdate = null;
        for (Seat seat : firebaseSeatList) {
            if (seat.id.equals(id)) {
                seatToUpdate = seat;
                if (isUser) {
                    seatToUpdate.seat_booking_time = "empty";
                    seatToUpdate.seat_booked_for = "empty";
                } else {
                    seatToUpdate.temp_user_id = "empty";
                    seatToUpdate.temp_user_name = "empty";
                    seatToUpdate.temp_user_rm_name = "empty";
                    seatToUpdate.seat_booking_time = "empty";
                    seatToUpdate.seat_booked_for = "empty";
                }
                seatToUpdate.seat_status = String.valueOf(SEAT_AVAILABLE);
            }
        }
        Map<String, Object> seatValues = seatToUpdate.toMap();
        mDatabase.child("seat").child(id).updateChildren(seatValues);
    }

    public void resetFirebaseSeats(String date, List<Seat> completeList) {
        Log.e(TAG, "firebase resetFirebaseSeats" + date);
        Seat seatToUpdate = null;
        for (Seat seat : completeList) {
            seatToUpdate = seat;
            seatToUpdate.temp_user_id = "empty";
            seatToUpdate.temp_user_name = "empty";
            seatToUpdate.temp_user_rm_name = "empty";
            seatToUpdate.seat_booking_time = "empty";
            seatToUpdate.seat_booked_for = "empty";
            seatToUpdate.seat_status = String.valueOf(SEAT_BLOCKED_BY_ADMIN);
            Map<String, Object> seatValues = seatToUpdate.toMap();
            mDatabase.child("seat").child(seatToUpdate.id).updateChildren(seatValues);
        }
    }

    void addNewFirebaseSeat(List<Seat> completeList) {
        Log.e(TAG, "firebase addNewFirebaseSeat " + completeList.size());
        Seat seatToUpdate = null;
        for (Seat seat : completeList) {
            seatToUpdate = seat;
            seatToUpdate.temp_user_id = "empty";
            seatToUpdate.temp_user_name = "empty";
            seatToUpdate.temp_user_rm_name = "empty";
            seatToUpdate.seat_booking_time = "empty";
            seatToUpdate.seat_booked_for = "empty";
            seatToUpdate.seat_date = "empty";
            seatToUpdate.seat_status = String.valueOf(SEAT_BLOCKED_BY_ADMIN);
            //Map<String, Object> seatValues = seatToUpdate.toMap();
            mDatabase.child("seat").child(seatToUpdate.id).setValue(seat);
        }
         /*mDatabase.child("seat").child("45").setValue(seat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                    }
                });*/
    }

    public void updateBookedTempSeat(String id, String status, String temp_emp_name, String
            temp_emp_id, String temp_emp_rm_email, String booking_time, String booked_date) {
        Log.e(TAG, "firebase updateBookedDefaultSeat" + id);
        Seat seatToUpdate = null;
        for (Seat seat : firebaseSeatList) {
            if (seat.id.equals(id)) {
                seatToUpdate = seat;
                seatToUpdate.seat_status = status;
                seatToUpdate.temp_user_name = temp_emp_name;
                seatToUpdate.temp_user_id = temp_emp_id;
                seatToUpdate.temp_user_rm_name = temp_emp_rm_email;
                seatToUpdate.seat_booking_time = booking_time;
                seatToUpdate.seat_booked_for = booked_date;
            }
        }
        Map<String, Object> seatValues = seatToUpdate.toMap();
        mDatabase.child("seat").child(id).updateChildren(seatValues);
    }

    private Query mEventsQuery;
    public static List<Seat> firebaseSeatList;
    private DatabaseReference mDatabase;

    private void loadDataFromCloud() {
        Log.e(TAG, " firebase loadDataFromCloud");
        //addValueEventListener
        if (mEventsQuery == null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            //addNewSeat();
            //updateSeat();
            //mDatabase.child("seat").child("45").removeValue();
            mEventsQuery = mDatabase.child("seat");/*.child("0");*/
            //mEventsQuery = mDatabase.child(DateHelper.getCurrentYear()).orderByChild("date").startAt(DateHelper.getCurrentDate());
        }
        mEventsQuery.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reset();
                Log.e(TAG, "firebase loadDataFromCloud");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                firebaseSeatList = new ArrayList<Seat>();

                if (dataSnapshot.exists()) {
                    Log.e(TAG, "firebase dataSnapshot.exists()");
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        //Log.e(TAG, "firebase loop");
                        Seat seat = eventSnapshot.getValue(Seat.class);
                        //String default_user_name = eventSnapshot.child("default_user_name").getValue(String.class);
                        firebaseSeatList.add(seat);
                        //Log.e(TAG, "firebase name"+default_user_name);
                        //Log.e(TAG, "firebase name" + seat.default_user_name);
                        ;
                    }
                    Log.e(TAG, "firebase firebaseSeatList " + firebaseSeatList.size());
                    // Set Adapter

                } else {
                    isCloudDbEmpty = true;
                    if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                        upload_excell.setVisibility(View.VISIBLE);
                        reset_all_data.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Please upload seat data excel file.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "firebase else admin");
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "firebase else user");
                        Toast.makeText(getApplicationContext(), "Request Admin to prepare Seat Data.", Toast.LENGTH_SHORT).show();
                    }
                }

                //even though no json data in cloud,
                //sometimes cloud sends single entry of empty seat model
                //check for value 1 is to avoid that case.
                Log.e(TAG, "firebase firebaseSeatList size " + firebaseSeatList.size());
                if (firebaseSeatList.size() > 1) {
                    isOfflineData = false;
                    ParseRawSeatsToListFormat parseRawSeatsToListFormat = new ParseRawSeatsToListFormat();
                    List<RowModel> rowModelListTemp = parseRawSeatsToListFormat.convertRawSeatsToList(firebaseSeatList);
                    Log.e(TAG, "firebase rowModelList size " + rowModelList.size());
                    if (rowModelListTemp.size() > 0) {
                        Log.e(TAG, "firebase rowModelList size >0 ");
                        rowModelList.addAll(rowModelListTemp);
                        createSeatLocationLayout();
                        if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                            if (reset_all_data != null) {
                                reset_all_data.setVisibility(View.VISIBLE);
                            } else {
                                reset_all_data = findViewById(R.id.reset_all_data);
                                reset_all_data.setVisibility(View.VISIBLE);
                            }
                            upload_excell.setVisibility(View.VISIBLE);
                        }
                        new AsyncTaskDbSeatOperation(true).execute();
                        if (retry.getVisibility() == View.VISIBLE) {
                            retry.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG, "firebase rowModelList NOT size >0 ");
                        if (progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                        retry.setVisibility(View.VISIBLE);
                    }
                    isCloudDbEmpty = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load Seat. Connect to internet and Refresh", Toast.LENGTH_SHORT).show();
                retry.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                reset_all_data.setVisibility(View.GONE);
                //upload_excell.setVisibility(View.GONE);
                Log.w(TAG, "firebase Failed to read value.", error.toException());
            }
        });

/*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String default_user_name = ds.child("default_user_name").getValue(String.class);
                    Log.e(TAG, "firebase name"+default_user_name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mEventsQuery.addListenerForSingleValueEvent(eventListener);
*/
    }

/*    String getTodaysDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        return df.format(c);
    }*/

    void resetValues() {
        viewList.clear();
        rowModelList.clear();
        firebaseSeatList = new ArrayList<>();
        firebaseSeatList.clear();
        isAllowedToBookNewSeat = false;
        userSeatStatus = SEAT_USER_NOT_BOOKED;
        swipe_out_booked_seat_model = null;
        //user_emp_model = new EmployeeModel();
        //userSeatLocation = "";
        user_swipe_out.setVisibility(View.GONE);
        seat_available_count = 0;
        seat_blocked_count = 0;
        seat_booked_count = 0;
        textview_seat_available.setText(String.valueOf(seat_available_count));
        textview_seat_blocked.setText(String.valueOf(seat_blocked_count));
        textview_seat_booked.setText(String.valueOf(seat_booked_count));
        textview_status.setText("Blocked");
        textview_status.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
    }

    void compareDate(Date date) {
        int result = Calendar.getInstance().getTime().compareTo(date);
        /*if (result == 0) {
            Toast.makeText(getApplicationContext(), "Please select another date", Toast.LENGTH_SHORT).show();
        } else */
        if (result == -1 || result == 0) {
            //toggleUseCaseNumeber();

            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
            textview_date.setText(dateFormatter.format(date.getTime()));
            reset();
            //createSeatTables(textview_date.getText().toString());

            //new AsyncTaskDbSeatOperation(textview_date.getText().toString()).execute();

            //  1 comes when date1 is higher then date2
        } else if (result == 1) {
            Toast.makeText(getApplicationContext(), "Please select a future date", Toast.LENGTH_SHORT).show();
            // -1 comes when date1 is lower then date2
        }
    }

    public void reset() {
        for (View v : viewList) {
            ((ViewGroup) v.getParent()).removeView(v);
            Log.e(TAG, "inside if");
        }
        resetValues();
        //createSeatTables(useCase);
    }

    void launchDatePickerDialog() {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                compareDate(newDate.getTime());
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    List<View> viewList = new ArrayList<>();


    void createDialog(final Context context, String title, String message, String
            okButtonName, final int condition) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoardActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButtonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case DIALOG_LOG_OUT:
                        PrefSession session = new PrefSession(context);
                        session.setUserEmailLoginSession("0");
                        if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                            session.setUserName("0");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("admin");
                        }
                        Toast.makeText(context, "You are Logged out.", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    case DIALOG_SWIPE_OUT:
                        //store the data
                        if (Common.isOnline(getApplicationContext())) {
                            user_swipe_out.setVisibility(View.GONE);
                            LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(getApplicationContext());
                            localDataBaseHelper.dbInit(localDataBaseHelper);
                            if (swipe_out_booked_seat_model.getDefaultEmployeeSeatModel().getEmp_name().equals(user_emp_model.getEmp_name())) {
                                new AsyncTaskDbOperation(DB_OPER_UPDATE_SEAT_SWIPEOUT, swipe_out_booked_seat_model.getId(), true,
                                        getApplicationContext()).execute();
                                updateSwipeOut(String.valueOf(swipe_out_booked_seat_model.getId()), true);
                                //localDataBaseHelper.updateSeatStatusAfterSwipeOut(swipe_out_booked_seat_model.getId(), true);
                            } else {
                                new AsyncTaskDbOperation(DB_OPER_UPDATE_SEAT_SWIPEOUT, swipe_out_booked_seat_model.getId(), false,
                                        getApplicationContext()).execute();
                                updateSwipeOut(String.valueOf(swipe_out_booked_seat_model.getId()), false);
                                //localDataBaseHelper.updateSeatStatusAfterSwipeOut(swipe_out_booked_seat_model.getId(), false);
                            }
                            if (progressBar.getVisibility() == View.GONE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            localDataBaseHelper.dbDeInit(localDataBaseHelper);
                            //reset();
                            //new AsyncTaskDbSeatOperation(textview_date.getText().toString()).execute();
                            //createSeatTables(textview_date.getText().toString());
                            //loadDataFromCloud();
                            Toast.makeText(context, "You are Swiped Out for Today.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DIALOG_DATA_RESET:
                        if (Common.isOnline(getApplicationContext())) {
                            progressBar.setVisibility(View.VISIBLE);
                            reset_all_data.setVisibility(View.GONE);
                            TimeAndDate timeAndDate = new TimeAndDate();
                            resetFirebaseSeats(timeAndDate.getTodaysDate(DATE_FORMAT), firebaseSeatList);
                            reset();
                            //loadDataFromCloud();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DIALOG_EXCEL_UPLOAD:
                        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                Log.e(TAG, " launch permission checker");
                                ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        ACTIVITY_RESULT_READ_PERMISSION);
                            } else {
                                Log.e(TAG, " read permission granted");
                                ExcelSeatDataWork excelSeatDataWork = new ExcelSeatDataWork();
                                //excelSeatDataWork.initExcelWork(getApplicationContext());
                                excelSeatDataWork.launchFileSelector(DashBoardActivity.this);
                            }
                        } else {
                            Log.e(TAG, " below android marshmellow");
                            ExcelSeatDataWork excelSeatDataWork = new ExcelSeatDataWork();
                            //excelSeatDataWork.initExcelWork(getApplicationContext());
                            excelSeatDataWork.launchFileSelector(DashBoardActivity.this);
                        }
                        break;
                }
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //finish();
            }
        });

        alertDialog.show();
    }

    void checkUserTemperorySeat(List<RowModel> rowModelList) {
        for (RowModel r : rowModelList) {
            for (CubeModel c : r.getCubeModelList()) {
                for (SeatModel s : c.getSeatModelList()) {
                    String tempEmpId = s.getTemporaryEmployeeSeatModel().getEmp_id();
                    Log.e(TAG, "seat code --" + s.getSeat_code() + " tempEmpId " + tempEmpId + "user_emp_model.getEmp_id()" + user_emp_model.getEmp_id() + "");
                    if (!isEmpty(tempEmpId) && tempEmpId.equals(user_emp_model.getEmp_id())) {
                        Log.e(TAG, "Found Booked user seat ---- " + tempEmpId + " " + s.getId() + " " + c.getRow_number() + " " + s.getCube_id() + " " + s.getSeat_code() + " status" + s.getSeat_status());
                        userSeatStatus = SEAT_USER_BOOKED;
                        swipe_out_booked_seat_model = s;
                        textview_status.setText("Confirmed");
                        textview_status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                        Log.e(TAG, "swipe out!!!!!");
                        user_swipe_out.setVisibility(View.VISIBLE);
                        isAllowedToBookNewSeat = false;
                    }
                }
            }
        }
    }


    public class AsyncTaskDbSeatOperation extends AsyncTask<Void, Void, Boolean> {
        private String date;
        /*//List<RowModel> rowModelList;
        public AsyncTaskDbSeatOperation(String date) {
            this.date = date;
        }*/

        boolean isInsert = false;

        public AsyncTaskDbSeatOperation(boolean isInsert) {
            this.isInsert = isInsert;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(getApplicationContext());
            localDataBaseHelper.dbInit(localDataBaseHelper);

            Log.e(TAG, "rowModelList size" + rowModelList.size());
            if (isInsert) {
                if (localDataBaseHelper.isTableExists(LocalDataBaseHelper.SEAT_TABLE_NAME)) {
                    //localDataBaseHelper.deleteSeatTable();
                    localDataBaseHelper.insertSeatData(rowModelList, false);
                } else {
                    localDataBaseHelper.insertSeatData(rowModelList, true);
                }
                // if (!localDataBaseHelper.isSeatDataExistForSpecificDate(date)) {
                Log.e(TAG, "db insert seat data to sqlite");
                //localDataBaseHelper.insertSeatData(rowModelList);
            } else if (rowModelList.size() == 0) {
                isOfflineData = true;
                Log.e(TAG, "db query seat data from sqlite");
                rowModelList.addAll(localDataBaseHelper.getAllSeatsData());
            }

            /*for (RowModel r : rowModelList) {
                for (CubeModel c : r.getCubeModelList()) {
                    for (SeatModel s : c.getSeatModelList()) {
                        Log.e(TAG, "seat after db " + s.getId() + " " + c.getRow_number() + " " + s.getCube_id() + " " + s.getSeat_code() + " status" + s.getSeat_status());
                    }
                }
            }*/

            //new AsyncTaskDbOperation(DB_OPER_INSERT_SEATS,rowModelList,context);
            /*} else {
                Log.e(TAG, "db seat data Exist for date:" + date);
            }*/

            //DummyDataProvider dummyDataProvider = new DummyDataProvider();
            //dummyDataProvider.createDummyData(getApplicationContext(), date);
            //rowModelList.addAll(dummyDataProvider.createDummyData( getApplicationContext(), textview_date.getText().toString()));

            //List<RowModel> completeList = new ArrayList<>();
            /*Log.e(TAG, "rowModelList lize before" + String.valueOf(rowModelList.size()));
            localDataBaseHelper.dbInit(localDataBaseHelper);
            if (localDataBaseHelper.isTableExists(LocalDataBaseHelper.SEAT_TABLE_NAME)) {
                rowModelList.addAll(localDataBaseHelper.getAllSeatsData(date));
            }*/
            localDataBaseHelper.dbDeInit(localDataBaseHelper);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!isInsert) {
                if (rowModelList.size() > 0)
                    createSeatLocationLayout();
            }

        }

    }

    void createSeatLocationLayout() {
        checkUserTemperorySeat(rowModelList);
        for (RowModel r : rowModelList) {
            List<CubeModel> cubeModelListDynamic = r.getCubeModelList();
            String rowNumber = "T" + r.getTower_number() + "-F" + r.getFloor_number() + "-ODC" + r.getOdc_number() + "-R" + r.getRow_number();

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater != null ? inflater.inflate(R.layout.recyclerview_custom, recycler_layout, false) : null;
            RecyclerView dynamicRecyclerView = view.findViewById(R.id.dynamicRecyclerView);
            TextView row_number = view.findViewById(R.id.row_num);
            row_number.setText(rowNumber);

            StaggeredGridLayoutManager mStaggeredLayoutManager_Category = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            if (dynamicRecyclerView != null)
                dynamicRecyclerView.setLayoutManager(mStaggeredLayoutManager_Category);

            CubeAdapter cubeAdapter = new CubeAdapter(getApplicationContext(), DashBoardActivity.this, cubeModelListDynamic, rowNumber, mDatabase);
            Log.e("cubeModelListD size", String.valueOf(cubeModelListDynamic.size()));
            dynamicRecyclerView.setAdapter(cubeAdapter);
            recycler_layout.addView(view);
            viewList.add(view);
        }
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            createDialog(getApplicationContext(), "Log Out", " Are you sure you want to log out of the account?", "Yes", DIALOG_LOG_OUT);
            //return true;
        }
        /*if (id == R.id.action_notification) {
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(item);
    }

    public static class NetworkStateReceiver extends BroadcastReceiver {

        protected Set<NetworkStateReceiverListener> listeners;
        protected Boolean connected;

        public NetworkStateReceiver() {
            listeners = new HashSet<NetworkStateReceiverListener>();
            connected = null;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getExtras() == null)
                return;

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();

            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                connected = false;
            }

            notifyStateToAll();
        }

        private void notifyStateToAll() {
            for (NetworkStateReceiverListener listener : listeners)
                notifyState(listener);
        }

        private void notifyState(NetworkStateReceiverListener listener) {
            if (connected == null || listener == null)
                return;

            if (connected == true) {

                listener.networkAvailable();
            } else
                listener.networkUnavailable();
        }

        public void addListener(NetworkStateReceiverListener l) {
            listeners.add(l);
            notifyState(l);
        }

        public void removeListener(NetworkStateReceiverListener l) {
            listeners.remove(l);
        }

        /*public interface NetworkStateReceiverListener {
            public void networkAvailable();
            public void networkUnavailable();
        }*/
    }

    @Override
    public void networkAvailable() {
        if (isOfflineData) {
            progressBar.setVisibility(View.VISIBLE);
            retry.setVisibility(View.GONE);
            //reset();
            loadDataFromCloud();
            isOfflineData = false;
        }
        //Toast.makeText(getApplicationContext(), "network connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkUnavailable() {
        //Toast.makeText(getApplicationContext(), "network disconnected", Toast.LENGTH_SHORT).show();
    }


    //Location related APIs


    private static final String KEY_NAME_NOTIFY = "NOTIFICATION";
    private static final String KEY_WORK_LOCATION = "WORK_LOCATION";
//    private static final String KEY_HOME_LOCATION = "HOME_LOCATION";
//    private static final int LAUNCH_MAPS_ACTIVITY = 1;
//    private double mCurLatitude;
//    private double mCurLongitude;

//    private AddressResultReceiver mResultReceiver;
//    private Address address;
//    private int fetchType = Constants.USE_ADDRESS_NAME;

    //    private LocationFinder mHomeLocationFinder;
    private LocationFinder mWorkLocationFinder;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
//    private static boolean mIsCurrentLocation;

    private Context mContext;

    //    private EditText addressEdit;
    private Spinner mWorkLocation;
    private SwitchCompat mAllowNotification;

    private SharedPreferences mAppPref;
    private SharedPreferences.Editor mEditor;

    void initLocationUiWorks(Boolean isChecked) {
//        Boolean isChecked = false;

//        mResultReceiver = new AddressResultReceiver(null);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mContext = getApplicationContext();

        mAppPref = getApplicationContext().
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        mEditor = mAppPref.edit();

        //Toggle button to allow notifications for this app
        mAllowNotification = (SwitchCompat) findViewById(R.id.allowNotification);
        mAllowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerGeofenceBroadcastReceiver();
                } else {
                    unregisterGeofenceBroadcastReceiver();
                }
                mEditor.putBoolean(KEY_NAME_NOTIFY, isChecked);
                mEditor.commit();
            }
        });

        isChecked = mAppPref.getBoolean(KEY_NAME_NOTIFY, false);
        Log.d(TAG, "isChecked: " + isChecked);
        mAllowNotification.setChecked(isChecked);
        if (!isChecked) {
            Log.d(TAG, "Allow notification off");
            unregisterGeofenceBroadcastReceiver();
        }

        // User address locator
        mWorkLocationFinder = (LocationFinder) new LocationFinder(mContext, geofencingClient);
//        mHomeLocationFinder = (LocationFinder) new LocationFinder(mContext, geofencingClient);

//        // Home location
//        addressEdit = (EditText) findViewById(R.id.addressEdit);
//        Log.d(TAG, "Home location: " + mAppPref.getString(KEY_HOME_LOCATION, null));
//        addressEdit.setText(mAppPref.getString(KEY_HOME_LOCATION, null));

        mWorkLocation = (Spinner) findViewById(R.id.workLocation);
        Log.d(TAG, "Work location: " + mAppPref.getString(KEY_WORK_LOCATION, null));
        mWorkLocation.setPrompt(mAppPref.getString(KEY_WORK_LOCATION, null));
        // Work location click listener
        mWorkLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                Log.d(TAG, "onItemSelected: " + item.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO
            }
        });

        // Avoid keyboard during activity start for home location
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }


    public void registerGeofenceBroadcastReceiver() {
        Log.d(TAG, "registerGeofenceBroadcastReceiver");
        PackageManager pm = DashBoardActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(DashBoardActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void unregisterGeofenceBroadcastReceiver() {
        Log.d(TAG, "unregisterGeofenceBroadcastReceiver");
        PackageManager pm = DashBoardActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(DashBoardActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    //    public void onGetCurrentLocationClicked(View view) {
//        Intent mapActivity = new Intent(this, MapsActivity.class);
//        startActivityForResult(mapActivity, LAUNCH_MAPS_ACTIVITY);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == LAUNCH_MAPS_ACTIVITY) {
//            if (resultCode == Activity.RESULT_OK) {
//                Log.d(TAG, "Lat: " + data.getDoubleExtra("Latitude", 0) + " Long: " +
//                        data.getDoubleExtra("Longitude", 0));
//                mCurLatitude = data.getDoubleExtra("Latitude", 1);
//                mCurLongitude = data.getDoubleExtra("Longitude", 1);
//                mIsCurrentLocation = true;
//            }
//        }
//    }


    public void onAlarmClicked(View view) {
        Intent alarmActivity = new Intent(this, AlarmActivity.class);
        startActivity(alarmActivity);
    }

    public void onSanitisationClicked(View view) {
        Intent alarmActivity = new Intent(this, AlarmActivity.class);
        startActivity(alarmActivity);
    }

    public void onButtonClicked(View view) {
        String workLocation = mWorkLocation.getItemAtPosition(mWorkLocation.getSelectedItemPosition()).toString();
        Log.d(TAG, "Work location: " + workLocation + " Pref: " +
                mAppPref.getString(KEY_WORK_LOCATION, null));
        if (!workLocation.equals(mAppPref.getString(KEY_WORK_LOCATION, null))) {
            //Add geofence for work location
            String location = (String) getResources().obtainTypedArray(R.array.work_location_values)
                    .getText(mWorkLocation.getSelectedItemPosition());
            String[] separated = location.split(",");
            mWorkLocationFinder.setLatitude(Double.parseDouble(separated[0]));
            mWorkLocationFinder.setLongitude(Double.parseDouble(separated[1]));
            mWorkLocationFinder.setRadius(GEOFENCE_RADIUS);
            mWorkLocationFinder.setRequestID(GEOFENCE_ID);
            Toast.makeText(mContext, "Work Location:\n" + "Latitude: " + mWorkLocationFinder.getLatitude() + "\n" +
                    "Longitude: " + mWorkLocationFinder.getLongitude(), Toast.LENGTH_LONG).show();
            mWorkLocationFinder.addGeofence();
            mEditor.putString(KEY_WORK_LOCATION, workLocation);
            mEditor.commit();
        } else {
            Toast.makeText(mContext, "Same work location as before", Toast.LENGTH_LONG).show();
        }

//        if (mIsCurrentLocation && mCurLatitude != 0 && mCurLongitude != 0) {
//            mHomeLocationFinder.setLatitude(mCurLatitude);
//            mHomeLocationFinder.setLongitude(mCurLongitude);
//            mHomeLocationFinder.setRadius(GEOFENCE_RADIUS);
//            mHomeLocationFinder.setRequestID(GEOFENCE_ID);
//            mHomeLocationFinder.addGeofence();
//        } else {
//            //Add geofence for home location
//            Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
//            intent.putExtra(Constants.RECEIVER, mResultReceiver);
//            intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
//            if (fetchType == Constants.USE_ADDRESS_NAME) {
//                String editText = addressEdit.getText().toString();
//                if (TextUtils.isEmpty(editText)) {
//                    Toast.makeText(mContext, "Please enter your home location", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (editText.equals(mAppPref.getString(KEY_HOME_LOCATION, null))) {
//                    return;
//                }
//                intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, editText);
//                mEditor.putString(KEY_HOME_LOCATION, editText);
//                mEditor.commit();
//
//                Log.e(TAG, "Starting Service");
//                mContext.startService(intent);
//            }
//        }
    }

//    class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, final Bundle resultData) {
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                address = resultData.getParcelable(Constants.RESULT_ADDRESS);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mContext, "Home Location:\n" + "Latitude: " + address.getLatitude() + "\n" +
//                                "Longitude: " + address.getLongitude(), Toast.LENGTH_LONG).show();
//                    }
//                });
//                mHomeLocationFinder.removeGeofence();
//                mHomeLocationFinder.setLatitude(address.getLatitude());
//                mHomeLocationFinder.setLongitude(address.getLongitude());
//                mHomeLocationFinder.setRadius(GEOFENCE_RADIUS);
//                mHomeLocationFinder.setRequestID(GEOFENCE_ID);
//                mHomeLocationFinder.addGeofence();
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mContext,
//                                resultData.getString(Constants.RESULT_DATA_KEY),
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }
//    }

/*
    public void registerGeofenceBroadcastReceiver() {
        Log.d(TAG, "registerGeofenceBroadcastReceiver");
        PackageManager pm = DashBoardActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(DashBoardActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void unregisterGeofenceBroadcastReceiver() {
        Log.d(TAG, "unregisterGeofenceBroadcastReceiver");
        PackageManager pm = DashBoardActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(DashBoardActivity.this, GeofenceBroadcastReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void onGetCurrentLocationClicked(View view) {
        Intent mapActivity = new Intent(this, MapsActivity.class);
        startActivityForResult(mapActivity, LAUNCH_MAPS_ACTIVITY);
    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        Log.e(TAG, " onRequestPermissionsResult requestCode" + requestCode);
        if (requestCode == ACTIVITY_RESULT_READ_PERMISSION) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(TAG, " onRequestPermissionsResult read permission granted");
                ExcelSeatDataWork excelSeatDataWork = new ExcelSeatDataWork();
                //excelSeatDataWork.initExcelWork(getApplicationContext());
                excelSeatDataWork.launchFileSelector(DashBoardActivity.this);
            }
        }
        if (requestCode == ACTIVITY_RESULT_FINE_LOCATION_PERMISSION) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initBluetoothUiWorks();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        /*if (requestCode == LAUNCH_MAPS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Lat: " + data.getDoubleExtra("Latitude", 0) + " Long: " +
                        data.getDoubleExtra("Longitude", 0));
                mCurLatitude = data.getDoubleExtra("Latitude", 1);
                mCurLongitude = data.getDoubleExtra("Longitude", 1);
                mIsCurrentLocation = true;
            }
        }*/
        if (requestCode == ACTIVITY_RESULT_READ_FILE && resultCode == RESULT_OK && data != null) {
            Log.e(TAG, "onActivityResult");
            Uri uri = data.getData();
            if (uri == null) {
                Log.e(TAG, "uri null");
                Toast.makeText(getApplicationContext(), "Unable to open file", Toast.LENGTH_SHORT).show();
            } /*else if(!uri.getPath().contains(EXCEL_FILE_NAME)){
                Log.e(TAG, "file name not correct"+uri.getPath());
                Toast.makeText(getApplicationContext(), "Incorrect Seat Data file: \n"+uri.getPath(), Toast.LENGTH_SHORT).show();
            }*/ else {
                Log.e(TAG, "uri not null " + uri.getPath());
                //Toast.makeText(getApplicationContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
                File infile = new File(uri.getPath());
                if (!(infile.exists())) {
                    Log.e(TAG, "Infile does not exist: " + infile); // This log error is seen, so the URI wasn't right.
                } /*else {*/
                Log.e(TAG, "after if " + infile);
                InputStream inputStream = null;
                try {
                    inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "FileNotFoundException: ");
                    e.printStackTrace();
                }

                ExcelSeatDataWork excelSeatDataWork = new ExcelSeatDataWork();
                Log.e(TAG, "inputStream: " + inputStream);
                List<Seat> completeList = excelSeatDataWork.initExcelWork(inputStream);
                if (completeList != null) {
                    Log.e(TAG, "completeList size: " + completeList.size());
                    if (Common.isOnline(getApplicationContext())) {
                        progressBar.setVisibility(View.VISIBLE);
                        reset_all_data.setVisibility(View.GONE);
                        upload_excell.setVisibility(View.GONE);
                        TimeAndDate timeAndDate = new TimeAndDate();
                        if (isCloudDbEmpty) {
                            addNewFirebaseSeat(completeList);
                        } else {
                            resetFirebaseSeats(timeAndDate.getTodaysDate(DATE_FORMAT), completeList);
                        }
                        reset();
                        //loadDataFromCloud();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred while opening, Please select correct file.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


//bluetooth implementations

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                }
            }
        }
    };
    public int mSelectedValue;
    public Set<ListAdapterModel> mBTDevices = new LinkedHashSet<>();
    public DeviceListAdapter mDeviceListAdapter;
    BluetoothAdapter mBluetoothAdapter;
    SwitchCompat mSwitch_ONOFF;
    SwitchCompat mSwitch_discoverDevices;
    ListView mLvNewDevices;
    Spinner mSpinner;
    ArrayList<ListAdapterModel> mBTDevicesArraylist = new ArrayList<>();
    SharedPreferences mSharedpreferences;
    public static final String PREFERENCE = "PREFERENCE";
    public static final String DURATIONKEY = "DURATION_KEY";

    public BroadcastReceiver addListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive:" + action);
            assert action != null;
            if (action.equals("com.example.ADDLIST_ACTION")) {
                mBTDevicesArraylist.clear();
                Map<String, ListAdapterModel> parcelMap = (HashMap<String, ListAdapterModel>) intent.getSerializableExtra("DATA_KEY");
                if (parcelMap != null) {
                    for (Map.Entry m : parcelMap.entrySet()) {
                        Log.d(TAG, "key in activity: " + m.getKey() + "value: " + (m.getValue().toString()));
                        mBTDevicesArraylist.add((ListAdapterModel) m.getValue());
                    }
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevicesArraylist);
                    mLvNewDevices.setAdapter(mDeviceListAdapter);
                }
            }
        }
    };
    private Boolean mRec1Status = false;
    private Boolean mAddListRecStatus = false;


    void initBluetoothUiWorks() {
        Log.d(TAG, "initBluetoothUiWorks Called");

        mSwitch_ONOFF = findViewById(R.id.bluetoothONOFF);
        mSwitch_discoverDevices = findViewById(R.id.discoverDevices);
        mLvNewDevices = findViewById(R.id.lvNewDevices);
        mSpinner = findViewById(R.id.spinner);


        mBTDevices = new LinkedHashSet<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TurnOn bluetooth switch if Bluetooth is enabled in user device.
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth already enabled by user so setting switch to ON");
            mSwitch_ONOFF.setChecked(true);
        }

        if (isMyServiceRunning(DiscoverService.class)) {
            mSwitch_discoverDevices.setChecked(true);
        }

        mSwitch_ONOFF.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                mRec1Status = true;
                registerReceiver(mBroadcastReceiver1, BTIntent);
            } else {
                mBluetoothAdapter.disable();
                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                mRec1Status = true;
                registerReceiver(mBroadcastReceiver1, BTIntent);
            }
        });

        mSwitch_discoverDevices.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (mBluetoothAdapter.isEnabled()) {
                    Log.d(TAG, "isMyServiceRunning: " + isMyServiceRunning(DiscoverService.class));
                    if (!isMyServiceRunning(DiscoverService.class)) {
                        Intent serviceIntent = new Intent(DashBoardActivity.this, DiscoverService.class);
                        serviceIntent.putExtra("DURATION", mSelectedValue);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(DashBoardActivity.this, serviceIntent);
                        } else {
                            startService(serviceIntent);
                        }
                    }
                } else {
                    Log.d(TAG, "Bluetooth enabled from discover devices");
                    mSwitch_ONOFF.setChecked(true);
                    mSwitch_discoverDevices.setChecked(true);
                }
            } else {
                Intent serviceIntent = new Intent(DashBoardActivity.this, DiscoverService.class);
                Log.d(TAG, "CancelDiscovery");
                stopService(serviceIntent);

                //clear old notifications when user disable discovery.
                if(mBTDevicesArraylist.size()>0){
                    for (ListAdapterModel listAdapterModel: mBTDevicesArraylist){
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(listAdapterModel.getNotify_id());
                    }
                }
            }

        });

        IntentFilter filter = new IntentFilter("com.example.ADDLIST_ACTION");
        mAddListRecStatus = true;
        registerReceiver(addListReceiver, filter);


        // Spinner
        mSpinner.setOnItemSelectedListener(this);

        List<Integer> categories = new ArrayList<>();
        categories.add(1);
        categories.add(2);
        categories.add(3);
        categories.add(4);
        categories.add(5);
        categories.add(6);
        categories.add(7);
        categories.add(8);
        categories.add(9);
        categories.add(10);
        categories.add(0);

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

        mSharedpreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if (mSharedpreferences.contains(DURATIONKEY)) {
            mSpinner.setSelection(mSharedpreferences.getInt(DURATIONKEY, 1));
            Log.d(TAG, "Saved duration: " + mSharedpreferences.getInt(DURATIONKEY, 1));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        SharedPreferences.Editor editor = mSharedpreferences.edit();
        editor.putInt(DURATIONKEY, position);
        editor.apply();
        mSelectedValue = Integer.parseInt(item);
        Log.d(TAG, "selectedValue: " + mSelectedValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause called");
        App.activityPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Onresume Called");
        App.activityResumed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Onstop called");

    }
}
