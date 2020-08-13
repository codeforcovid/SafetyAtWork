package com.safeatwork.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.safeatwork.R;
import com.safeatwork.common.Common;
import com.safeatwork.common.TimeAndDate;
import com.safeatwork.data.AsyncTaskDbOperation;
import com.safeatwork.data.LocalDataBaseHelper;
import com.safeatwork.model.CubeModel;
import com.safeatwork.model.EmployeeSeatModel;
import com.safeatwork.model.SeatModel;
import com.safeatwork.model.dbformat.Seat;
import com.safeatwork.ui.DashBoardActivity;

import java.util.List;
import java.util.Map;

import static com.safeatwork.common.Common.isEmpty;
import static com.safeatwork.data.AsyncTaskDbOperation.DB_OPER_UPDATE_SEAT_STATUS;
import static com.safeatwork.data.AsyncTaskDbOperation.DB_OPER_UPDATE_SEAT_TEMP_EMP;
import static com.safeatwork.data.AsyncTaskDbOperation.DB_OPER_UPDATE_STATUS_BOOKING_TIME;
import static com.safeatwork.model.Constants.ADMIN_ACCESS;
import static com.safeatwork.model.Constants.APP_NAME;
import static com.safeatwork.model.Constants.SEAT_AVAILABLE;
import static com.safeatwork.model.Constants.SEAT_BLOCKED_BY_ADMIN;
import static com.safeatwork.model.Constants.SEAT_BOOKED;
import static com.safeatwork.model.Constants.SEAT_USER_BOOKED;
import static com.safeatwork.model.Constants.SEAT_USER_NOT_BOOKED;
import static com.safeatwork.model.Constants.SEAT_USER_NOT_BOOKED_BUT_RISK;
import static com.safeatwork.model.Constants.TIME_FORMAT;
import static com.safeatwork.ui.DashBoardActivity.firebaseSeatList;
import static com.safeatwork.ui.DashBoardActivity.isAllowedToBookNewSeat;
import static com.safeatwork.ui.DashBoardActivity.seat_available_count;
import static com.safeatwork.ui.DashBoardActivity.seat_blocked_count;
import static com.safeatwork.ui.DashBoardActivity.seat_booked_count;
import static com.safeatwork.ui.DashBoardActivity.textview_date;
import static com.safeatwork.ui.DashBoardActivity.textview_emp_id;
import static com.safeatwork.ui.DashBoardActivity.textview_emp_name;
import static com.safeatwork.ui.DashBoardActivity.textview_seat_available;
import static com.safeatwork.ui.DashBoardActivity.textview_seat_blocked;
import static com.safeatwork.ui.DashBoardActivity.textview_seat_booked;
import static com.safeatwork.ui.DashBoardActivity.textview_status;
import static com.safeatwork.ui.DashBoardActivity.textview_user_seatcode;
import static com.safeatwork.ui.DashBoardActivity.userSeatStatus;
import static com.safeatwork.ui.DashBoardActivity.user_emp_model;
import static com.safeatwork.ui.DashBoardActivity.user_swipe_out;

public class CubeAdapter extends RecyclerView.Adapter<CubeAdapter.ViewHolder> {

    private String TAG = APP_NAME+"_CubeAdapter";
    private List<CubeModel> _data;
    private Context _c;
    private OnItemClickListener mItemClickListener;
    String rowNumber;
    Activity activity;
    DatabaseReference mDatabase;
    public CubeAdapter(Context applicationContext, Activity activity, List<CubeModel> list,
                       String rowNumber, DatabaseReference mDatabase) {
        this.activity = activity;
        this.rowNumber = rowNumber;
        this.mDatabase=mDatabase;
        _data = list;
        _c = applicationContext;
        for (CubeModel c : list) {
            List<SeatModel> seatModelList = c.getSeatModelList();
            for (SeatModel seatModel : seatModelList) {
                updateSeatCount(seatModel.getSeat_status(), 1);
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout seat_layout;
        TextView seat1;
        TextView seat2;
        TextView seat3;
        TextView seat4, cube_id;

        ViewHolder(View itemView) {
            super(itemView);
            seat_layout = itemView.findViewById(R.id.seat_layout);
            seat1 = itemView.findViewById(R.id.seat1);
            seat2 = itemView.findViewById(R.id.seat2);
            seat3 = itemView.findViewById(R.id.seat3);
            seat4 = itemView.findViewById(R.id.seat4);

            cube_id = itemView.findViewById(R.id.cube_num);

            seat1.setOnClickListener(this);
            seat2.setOnClickListener(this);
            seat3.setOnClickListener(this);
            seat4.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cube, parent, false);
        return new ViewHolder(view);
    }

    void updateUserStatus(int status) {
        Log.e("testt","userSeatStatus "+userSeatStatus+" ---- "+status);
        userSeatStatus = status;
        switch (userSeatStatus) {
            case SEAT_USER_BOOKED:
                textview_status.setText("Confirmed");
                textview_status.setTextColor(_c.getResources().getColor(R.color.colorPrimary));
                Log.e("testt","swipe out!!!!!");
                user_swipe_out.setVisibility(View.VISIBLE);
                isAllowedToBookNewSeat = false;
                break;
            case SEAT_USER_NOT_BOOKED_BUT_RISK:
                textview_status.setText("Blocked");
                textview_status.setTextColor(_c.getResources().getColor(R.color.black));
                break;
            default:
                textview_status.setText("Not Confirmed");
                textview_status.setTextColor(_c.getResources().getColor(R.color.gold));
                break;
        }
    }

    void updateSeatCount(int status, int increment) {
        switch (status) {
            case SEAT_AVAILABLE:
                Log.e(TAG, "seat_available_count " + seat_available_count);
                seat_available_count = seat_available_count + increment;
                Log.e(TAG, "seat_available_count " + seat_available_count);
                textview_seat_available.setText(String.valueOf(seat_available_count));
                break;
            case SEAT_BLOCKED_BY_ADMIN:
                seat_blocked_count = seat_blocked_count + increment;
                textview_seat_blocked.setText(String.valueOf(seat_blocked_count));
                break;
            case SEAT_BOOKED:
                //Log.e(TAG,"seat_booked_count "+seat_available_count);
                seat_booked_count = seat_booked_count + increment;
                //Log.e(TAG,"seat_booked_count "+seat_available_count);
                textview_seat_booked.setText(String.valueOf(seat_booked_count));
                break;
            case SEAT_USER_BOOKED:
                seat_booked_count = seat_booked_count + increment;
                textview_seat_booked.setText(String.valueOf(seat_booked_count));
                break;
            case SEAT_USER_NOT_BOOKED:
                seat_available_count = seat_available_count + increment;
                textview_seat_available.setText(String.valueOf(seat_available_count));
                break;
            case SEAT_USER_NOT_BOOKED_BUT_RISK:
                seat_blocked_count = seat_blocked_count + increment;
                textview_seat_blocked.setText(String.valueOf(seat_blocked_count));
                break;
            default:
                /*seat_booked_count+=1;
                textview_seat_booked.setText(seat_booked_count);*/
                break;
        }
    }

    void setSeatColourBasedOnSeatStatus(CubeModel c, TextView textView, int position) {
        SeatModel seatModel = c.getSeatModelByPosition(position);
        if (seatModel != null) {
            String currentSeatCode = getCompleteSeatCode(c, position);
            int seat_status = seatModel.getSeat_status();
            textView.setText("S" + seatModel.getSeat_code());

            if (user_emp_model.getSeat_code().equals(currentSeatCode)) {
                Log.e(TAG, "user seat_status: " + seat_status);
                textView.setTextColor(_c.getResources().getColor(R.color.black));
                switch (seat_status) {
                    case SEAT_AVAILABLE:
                        Log.e(TAG, "userSeatStatus test1");
                        if (DashBoardActivity.swipe_out_booked_seat_model == null) {
                            isAllowedToBookNewSeat = true;
                            updateUserStatus(SEAT_USER_NOT_BOOKED);
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.gold));
                        } else {
                            //updateUserStatus(SEAT_AVAILABLE);
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.white));
                        }
                        break;
                    case SEAT_BOOKED:
                        EmployeeSeatModel tempEmployeeSeatModel = seatModel.getTemporaryEmployeeSeatModel();
                        if (isEmpty(tempEmployeeSeatModel.getEmp_name())) {
                            Log.e(TAG, "userSeatStatus test2");
                            DashBoardActivity.swipe_out_booked_seat_model = seatModel;
                            updateUserStatus(SEAT_USER_BOOKED);
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.colorPrimary));
                        } else {
                            Log.e(TAG, "userSeatStatus test3");
                            updateUserStatus(SEAT_USER_NOT_BOOKED_BUT_RISK);
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.green));
                        }
                        break;
                    case SEAT_BLOCKED_BY_ADMIN:
                        Log.e(TAG, "userSeatStatus test4");
                        if (userSeatStatus != SEAT_USER_BOOKED) {
                            updateUserStatus(SEAT_USER_NOT_BOOKED_BUT_RISK);
                        }
                        if (isAllowedToBookNewSeat) {
                            createOnlyOkButtonDialog(_c, "Blocked due to Safety", "Your current seat(" + currentSeatCode + ") is blocked by Admin due to Safe Distance Policy. " +
                                    "Choose another available Seat.", "OK", textView, currentSeatCode, SEAT_USER_NOT_BOOKED_BUT_RISK, SEAT_BLOCKED_BY_ADMIN);
                        }
                        textView.setBackgroundColor(_c.getResources().getColor(R.color.greylight));
                        break;
                }
                Log.e(TAG, " found user seat! userSeatLocation " + user_emp_model.getSeat_code() + " getCompleteSeatCode(m,3)" + currentSeatCode);
                //updateUserStatus(SEAT_USER_NOT_BOOKED);
            } else {
                switch (seat_status) {
                    case SEAT_AVAILABLE:
                        textView.setBackgroundColor(_c.getResources().getColor(R.color.white));
                        if (userSeatStatus == SEAT_USER_NOT_BOOKED_BUT_RISK) {
                            isAllowedToBookNewSeat = true;
                        }
                        break;
                    case SEAT_BOOKED:
                        String temporaryEmployeeID = seatModel.getTemporaryEmployeeSeatModel().getEmp_id();
                        if (temporaryEmployeeID != null && temporaryEmployeeID.equals(user_emp_model.getEmp_id())) {
                            Log.e(TAG, "userSeatStatus test5");
                            DashBoardActivity.swipe_out_booked_seat_model = seatModel;
                            updateUserStatus(SEAT_USER_BOOKED);
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.colorPrimary));

                        } else {
                            textView.setBackgroundColor(_c.getResources().getColor(R.color.green));
                        }
                        break;
                    case SEAT_BLOCKED_BY_ADMIN:
                        textView.setBackgroundColor(_c.getResources().getColor(R.color.greylight));
                        break;
                }
            }
        } else{
            textView.setText("");
        }
    }

    private void setButtonClickActionBasedOnStatus(SeatModel seatModel, TextView textView, String seat_code) {
        if(seatModel!=null) {
            int seat_status = seatModel.getSeat_status();
            EmployeeSeatModel defaultEmployeeSeatModel = seatModel.getDefaultEmployeeSeatModel();
            EmployeeSeatModel temporaryEmployeeSeatModel = seatModel.getTemporaryEmployeeSeatModel();
            int id = seatModel.getId();
            if (user_emp_model.getSeat_code().equals(seat_code)) {
                switch (seat_status) {
                    case SEAT_AVAILABLE:
                        if (isAllowedToBookNewSeat) {
                            createDialog(id, _c, "Confirm Seat", "Are you sure to book this seat?",
                                    "Confirm", textView, seat_code, SEAT_AVAILABLE);
                        } else if (!isAllowedToBookNewSeat && userSeatStatus == SEAT_USER_BOOKED) {
                            createOtherUserProfileDialog(_c, "Seat Details", "User seat is already confirmed.", "OK", textView);
                            //createOtherUserProfileDialog(_c, "Seat Details", "Book on your current seat location.", "OK", textView);
                        }
                        break;
                    case SEAT_BOOKED:
                        if (defaultEmployeeSeatModel != null) {
                            Log.e(TAG, "defaultEmployeeSeatModel:" + defaultEmployeeSeatModel.getEmp_id() +
                                    "user_emp_model:" + user_emp_model.getEmp_id());
                            if (defaultEmployeeSeatModel.getEmp_id().equals(user_emp_model.getEmp_id())) {
                                if (isEmpty(temporaryEmployeeSeatModel.getEmp_name())) {
                                    createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is Booked by User.", "OK", textView);
                                } else {
                                    createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is booked by "
                                            + temporaryEmployeeSeatModel.getEmp_name() + "(" + temporaryEmployeeSeatModel.getEmp_id() + ").\n Please choose another seat.", "OK", textView);
                                    if (userSeatStatus == SEAT_USER_NOT_BOOKED_BUT_RISK) {
                                        Log.e(TAG, "user status ---------" + userSeatStatus + " --- " + userSeatStatus);
                                        isAllowedToBookNewSeat = true;
                                    }
                                    //Log.e(TAG,"user status ---------"+userSeatStatus);
                                }
                            } else {
                                createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is booked by "
                                        + defaultEmployeeSeatModel.getEmp_name() + "(" + defaultEmployeeSeatModel.getEmp_id() + ")", "OK", textView);
                            }
                        }
                        break;
                    case SEAT_BLOCKED_BY_ADMIN:
                        if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                            //launchSelectorDialog(id, seat_code, seat_status, textView);
                            adminDialogToEnableSeat(id, seat_code, "Do you wish to enable the seat: " + defaultEmployeeSeatModel.getEmp_name() + "(" + seat_code + ") ?", seat_status, textView);
                        } else {
                            //animation and launch dialog by default
                            createOtherUserProfileDialog(_c, "Seat Details", "Your current Seat(" + seat_code + ") is Blocked by Admin due to Safe Distance Policy. " +
                                    "Choose another available Seat.", "OK", textView);
                        }
                        break;
                }
                Log.e(TAG, " found user seat! userSeatLocation " + user_emp_model.getSeat_code() + " current_seat" + seat_code);
                //updateUserStatus(SEAT_USER_NOT_BOOKED);
            } else {
                switch (seat_status) {
                    case SEAT_AVAILABLE:
                        Log.e(TAG, " userSeatStatus" + userSeatStatus);
                        if (isAllowedToBookNewSeat && userSeatStatus == SEAT_USER_NOT_BOOKED_BUT_RISK) {
                            createDialog(id, _c, "Confirm Seat", "Are you sure to book this seat?",
                                    "Confirm", textView, seat_code, SEAT_AVAILABLE);
                        } else if (isAllowedToBookNewSeat && userSeatStatus == SEAT_USER_NOT_BOOKED) {
                            // createOtherUserProfileDialog(_c, "Seat Details", "User seat is already confirmed.", "OK", textView);
                            createOtherUserProfileDialog(_c, "Seat Details", "Book on your current seat location.", "OK", textView);
                        } else if (!isAllowedToBookNewSeat && userSeatStatus == SEAT_USER_BOOKED) {
                            createOtherUserProfileDialog(_c, "Seat Details", "User seat is already confirmed.", "OK", textView);
                            //createOtherUserProfileDialog(_c, "Seat Details", "Book on your current seat location.", "OK", textView);
                        }
                        break;
                    case SEAT_BOOKED:
                        //Log.e("test","SEAT_BOOKED ");
                        String temporaryEmployeeID = temporaryEmployeeSeatModel.getEmp_id();
                        Log.e(TAG, "defaultEmployeeSeatModel:" + defaultEmployeeSeatModel.getEmp_id() +
                                "user_emp_model:" + user_emp_model.getEmp_id());

                        if (!isEmpty(temporaryEmployeeID) && temporaryEmployeeID.equals(user_emp_model.getEmp_id())) {
                            // Toast.makeText(_c, "Current Seat is Booked by User.", Toast.LENGTH_SHORT).show();
                            createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is Booked by User.", "OK", textView);
                        } else {
                            if (!isEmpty(temporaryEmployeeID)) {
                                createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is booked by "
                                        + temporaryEmployeeSeatModel.getEmp_name() + "(" + temporaryEmployeeSeatModel.getEmp_id() + ")", "OK", textView);
                            } else {
                                createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is booked by "
                                        + defaultEmployeeSeatModel.getEmp_name() + "(" + defaultEmployeeSeatModel.getEmp_id() + ")", "OK", textView);
                            }
                        }
                        break;
                    case SEAT_BLOCKED_BY_ADMIN:
                        if (user_emp_model.getAdmin_rights().equals(ADMIN_ACCESS)) {
                            //launchSelectorDialog(id, seat_code, seat_status, textView);
                            adminDialogToEnableSeat(id, seat_code, "Do you wish to enable the seat: " + defaultEmployeeSeatModel.getEmp_name() + "(" + seat_code + ") ?", seat_status, textView);
                        } else {
                            createOtherUserProfileDialog(_c, "Seat Details", "Current Seat is Blocked by Admin.", "OK", textView);
                        }
                        break;
                }
            }
        }
    }

    private void updateUserSeatConfirmationStatusInList(int id, String seat_number, int current_status, int new_status, boolean is_admin) {
        LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(_c);
        localDataBaseHelper.dbInit(localDataBaseHelper);
        EmployeeSeatModel temporaryEmployeeSeatModel = null;
        Log.e("firebase test","update new_status"+new_status+"is_admin"+is_admin);
        //if (localDataBaseHelper.isTableExists(LocalDataBaseHelper.SEAT_TABLE_NAME)) {
            TimeAndDate timeAndDate = new TimeAndDate();
            if (user_emp_model.getSeat_code().equals(seat_number) && !is_admin) {
                Log.e("firebase test","update 1");
                isAllowedToBookNewSeat = false;
                Log.e(TAG,"userSeatStatus test6");
                updateUserStatus(SEAT_USER_BOOKED);
                textview_user_seatcode.setText(seat_number);
                Log.e(TAG, "db updateSeatStatusWithBookingTime");
                /*localDataBaseHelper.updateSeatStatusWithBookingTime(id, String.valueOf(SEAT_BOOKED),
                        timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString());*/
                ((DashBoardActivity) activity).updateBookedDefaultSeat(String.valueOf(id), String.valueOf(SEAT_BOOKED),
                        timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString());
                new AsyncTaskDbOperation(DB_OPER_UPDATE_STATUS_BOOKING_TIME,id, String.valueOf(SEAT_BOOKED),
                        timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString(),_c).execute();
            } else if ((new_status == SEAT_BLOCKED_BY_ADMIN || new_status == SEAT_AVAILABLE) && is_admin) {

                Log.e(TAG, "db updateSeatStatus status" + new_status);
                //localDataBaseHelper.updateSeatStatus(id, String.valueOf(new_status));
               // if (_c instanceof DashBoardActivity) {
                    Log.e("firebase test","update call");
                    ((DashBoardActivity) activity).updateSeat(String.valueOf(id),String.valueOf(new_status));
                //}

                //updateSeat(String.valueOf(id),String.valueOf(new_status));
                new AsyncTaskDbOperation(DB_OPER_UPDATE_SEAT_STATUS,id,String.valueOf(new_status),_c).execute();
            } else /*if (current_status == SEAT_USER_NOT_BOOKED_BUT_RISK) */ {
                Log.e("firebase test","update 3");
                isAllowedToBookNewSeat = false;
                Log.e(TAG,"userSeatStatus test7");
                updateUserStatus(SEAT_USER_BOOKED);
                textview_user_seatcode.setText(seat_number);

                ((DashBoardActivity) activity).updateBookedTempSeat(String.valueOf(id),String.valueOf(SEAT_BOOKED), textview_emp_name.getText().toString(),
                        textview_emp_id.getText().toString(), user_emp_model.getEmp_email(), timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString());
                temporaryEmployeeSeatModel = new EmployeeSeatModel(user_emp_model.getEmp_name(), user_emp_model.getEmp_id(), user_emp_model.getRm_name(), user_emp_model.getRm_email());
                Log.e(TAG, "db updateTemperoryEmpSeatDetails: name " + textview_emp_name.getText().toString()
                        + "id " + textview_emp_id.getText().toString());
                /*localDataBaseHelper.updateTemperoryEmpSeatDetails(id, textview_emp_name.getText().toString(), textview_emp_id.getText().toString(),
                        user_emp_model.getEmp_email(), timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString(), String.valueOf(SEAT_BOOKED));
*/
                new AsyncTaskDbOperation(DB_OPER_UPDATE_SEAT_TEMP_EMP,id, textview_emp_name.getText().toString(), textview_emp_id.getText().toString(),
                        user_emp_model.getEmp_email(), timeAndDate.getTodaysDate(TIME_FORMAT), textview_date.getText().toString(), String.valueOf(SEAT_BOOKED),_c).execute();

                //localDataBaseHelper.updateSeatStatus(id, String.valueOf(new_status));
            }
            Log.e("firebase test","update call end");
        //}
        localDataBaseHelper.dbDeInit(localDataBaseHelper);

        /*DashBoardActivity dashBoardActivity = new DashBoardActivity();
        dashBoardActivity.reLoadData();*/

        /*if (_c instanceof DashBoardActivity) {
            ((DashBoardActivity) _c).reLoadData();
        }*/

        //List<SeatModel> allSeatsInUpdatedCubicle = null;
        for (CubeModel cubeModel : _data) {
            List<SeatModel> seatModels = cubeModel.getSeatModelList();
            for (SeatModel seat : seatModels) {
                final String seat_code = rowNumber + "-C" + seat.getCube_id() + "-S" + seat.getSeat_code();
                //Log.e("test1", "Update Search seat_code " + seat_code + " seat_number " + seat_number);
                if (seat_code.equalsIgnoreCase(seat_number)) {
                    Log.e("test1", "Before Update Search Found seat_code " + seat_code + " seat_number " + seat_number + " new_status" + new_status);
                    seat.setSeat_status(new_status);
                    if (temporaryEmployeeSeatModel != null) {
                        Log.e(TAG, "temporaryEmployeeSeatModel!=null");
                        seat.setTemporaryEmployeeSeatModel(temporaryEmployeeSeatModel);
                    } else {
                        Log.e(TAG, "temporaryEmployeeSeatModel==null");
                    }

                }
            }
        }

        notifyDataSetChanged();
    }

    public void updateSeat(String id,String seat_status) {
        Log.e("firebase test","updateSeat"+id);
        Seat seatToUpdate = null;
        for (Seat seat:firebaseSeatList){
            if(seat.id.equals(id)){
                seatToUpdate=seat;
                seatToUpdate.seat_status=seat_status;
            }
        }
        Map<String, Object> seatValues = seatToUpdate.toMap();
        mDatabase.child("seat").child(id).updateChildren(seatValues);
    }

    void launchSelectorDialog(final int id, final String seat_code, final int status, final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //int[] statusArray = {SEAT_AVAILABLE, SEAT_BLOCKED_BY_ADMIN};
        int selection = (status == SEAT_AVAILABLE) ? 0 : 1;
        CharSequence[] items = new CharSequence[]{"Available", "Blocked"};
        builder.setTitle("Select Option");
        builder.setSingleChoiceItems(items, selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int position) {
                switch (position) {
                    case 0:
                        if (status == SEAT_BLOCKED_BY_ADMIN) {
                            if(Common.isOnline(_c)){
                                updateUserSeatConfirmationStatusInList(id, seat_code, status, SEAT_AVAILABLE, true);
                                updateSeatCount(SEAT_AVAILABLE, 1);
                                if (seat_blocked_count > 0)
                                    updateSeatCount(SEAT_BLOCKED_BY_ADMIN, -1);
                                //isAllowedToBookNewSeat = false;
                                //userSeatStatus = SEAT_USER_BOOKED;
                                textView.setBackgroundColor(_c.getResources().getColor(R.color.white));
                            } else{
                                Toast.makeText(_c, "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                            }
                        }
                        //Toast.makeText(_c, "Seat is confirmed.", Toast.LENGTH_SHORT).show();
                        d.cancel();
                        break;
                    case 1:
                        if (status == SEAT_AVAILABLE) {
                            if(Common.isOnline(_c)) {
                                updateUserSeatConfirmationStatusInList(id, seat_code, status, SEAT_BLOCKED_BY_ADMIN, true);
                                updateSeatCount(SEAT_BLOCKED_BY_ADMIN, 1);
                                if (seat_available_count > 0)
                                    updateSeatCount(SEAT_AVAILABLE, -1);
                                //isAllowedToBookNewSeat = false;
                                //userSeatStatus = SEAT_USER_BOOKED;
                                textView.setBackgroundColor(_c.getResources().getColor(R.color.greylight));
                            } else {
                                Toast.makeText(_c, "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                            }
                        }
                        d.cancel();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setTitle("Change Seat Status");
        builder.show();
    }

    String getCompleteSeatCode(CubeModel cubeModel, int position) {
        return rowNumber + "-C" + cubeModel.getCube_number() + "-S" + cubeModel.getSeatModelByPosition(position).getSeat_code();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CubeModel m = _data.get(position);

        //Log.e(TAG, "seat model list size " + m.getSeatModelList().size());

        holder.cube_id.setText("C" + m.getCube_number());

        Log.e(TAG, "seat after db " + m.getRow_number() + " " + m.getCube_number() + " ");
        setSeatColourBasedOnSeatStatus(m, holder.seat1, 0);
        setSeatColourBasedOnSeatStatus(m, holder.seat2, 1);
        setSeatColourBasedOnSeatStatus(m, holder.seat3, 2);
        setSeatColourBasedOnSeatStatus(m, holder.seat4, 3);

        holder.seat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("kf", "seat1  clicked");

                setButtonClickActionBasedOnStatus(m.getSeatModelByPosition(0), holder.seat1, getCompleteSeatCode(m, 0));
                /*Intent intent = new Intent(_c.getApplicationContext(), RecipeCategories.class);
                //intent.putExtra("pos", position);
                intent.putExtra("page_title", holder.Name.getText().toString());
                intent.putExtra("category", holder.title.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _c.startActivity(intent);*/
            }
        });
        holder.seat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kf", "seat2  clicked");
                setButtonClickActionBasedOnStatus(m.getSeatModelByPosition(1), holder.seat1, getCompleteSeatCode(m, 1));
            }
        });
        holder.seat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kf", "seat3  clicked");
                setButtonClickActionBasedOnStatus(m.getSeatModelByPosition(2), holder.seat1, getCompleteSeatCode(m, 2));
            }
        });
        holder.seat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("kf", "seat4  clicked");
                setButtonClickActionBasedOnStatus(m.getSeatModelByPosition(3), holder.seat1, getCompleteSeatCode(m, 3));
            }
        });
    }

    void createDialog(final int id, final Context context, String title, String message, String okButtonName,
                      final TextView textView, final String seat_code, final int current_status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButtonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(Common.isOnline(_c)) {
                    Log.e(TAG, "Seat is confirmed --start");
                    updateUserSeatConfirmationStatusInList(id, seat_code, current_status, SEAT_BOOKED, false);
                    updateSeatCount(SEAT_USER_BOOKED, 1);
                    if (seat_available_count > 0) {
                        updateSeatCount(SEAT_AVAILABLE, -1);
                    }
                    isAllowedToBookNewSeat = false;
                    userSeatStatus = SEAT_USER_BOOKED;
                    textView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    Log.e(TAG, "Seat is confirmed. --end");
                    Toast.makeText(context, "Seat is confirmed.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(_c, "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
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

    void adminDialogToEnableSeat(final int id, final String seat_code, final String message, final int status, final TextView textView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Enable Seat");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(Common.isOnline(_c)) {
                    updateUserSeatConfirmationStatusInList(id, seat_code, status, SEAT_AVAILABLE, true);
                    updateSeatCount(SEAT_AVAILABLE, 1);
                    if (seat_blocked_count > 0)
                        updateSeatCount(SEAT_BLOCKED_BY_ADMIN, -1);
                    //isAllowedToBookNewSeat = false;
                    //userSeatStatus = SEAT_USER_BOOKED;
                    textView.setBackgroundColor(_c.getResources().getColor(R.color.white));
                }else{
                    Toast.makeText(_c, "Please connect to Internet and Retry", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    void createOnlyOkButtonDialog(final Context context, String title, String message, String okButtonName,
                                  final TextView textView, final String seat_code, final int current_status, final int new_status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButtonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                /*updateSeatCount(SEAT_AVAILABLE,-1);
                updateSeatCount(SEAT_AVAILABLE,-1);
                updateUserSeatConfirmationStatusInList(id, seat_code, current_status, new_status);
               */
                isAllowedToBookNewSeat = true;
                //userSeatStatus = 4;
                textView.setBackgroundColor(context.getResources().getColor(R.color.greylight));
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    void createOtherUserProfileDialog(final Context context, String title, String message, String okButtonName, final TextView textView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(okButtonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

	/*@Override
	public Object getItemId(int position) {
		return  _data.get(position);
	}*/

    @Override
    public int getItemCount() {
        return _data.size();
    }

}
