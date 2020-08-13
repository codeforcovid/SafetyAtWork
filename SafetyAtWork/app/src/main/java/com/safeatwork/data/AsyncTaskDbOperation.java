package com.safeatwork.data;

import android.content.Context;
import android.os.AsyncTask;

import static com.safeatwork.model.Constants.SEAT_BOOKED;

public class AsyncTaskDbOperation extends AsyncTask<Void, Void, Boolean> {
    LocalDataBaseHelper localDataBaseHelper;
    private int id;
    private String status;
    private String bookingTime;
    private String bookedFor;
    private int operation;
    private String tempUserName;
    private String tempUserId;
    private String tempUserRmEmail;
    private boolean isUserSeat;

    public static final int DB_OPER_UPDATE_SEAT_STATUS = 0;
    public static final int DB_OPER_UPDATE_STATUS_BOOKING_TIME = 1;
    public static final int DB_OPER_UPDATE_SEAT_TEMP_EMP = 2;
    public static final int DB_OPER_UPDATE_SEAT_SWIPEOUT = 3;
    public static final int DB_OPER_INSERT_SEATS = 4;

    public AsyncTaskDbOperation(int operation, int id, String status, Context context) {
        this.operation = operation;
        this.id = id;
        this.status = status;
        localDataBaseHelper = new LocalDataBaseHelper(context);
        localDataBaseHelper.dbInit(localDataBaseHelper);
    }

    public AsyncTaskDbOperation(int operation, int id, String status, String bookingTime, String bookedFor, Context context){
        this.operation = operation;
        this.id = id;
        this.status = status;
        this.bookingTime = bookingTime;
        this.bookedFor = bookedFor;
        localDataBaseHelper = new LocalDataBaseHelper(context);
        localDataBaseHelper.dbInit(localDataBaseHelper);
    }

    public AsyncTaskDbOperation(int operation,int id, String tempUserName, String tempUserId, String tempUserRmEmail,
                                String bookingTime, String bookedFor, String status, Context context){
        this.operation = operation;
        this.id = id;
        this.status = status;
        this.bookingTime = bookingTime;
        this.bookedFor = bookedFor;
        this.tempUserName=tempUserName;
        this.tempUserId=tempUserId;
        this.tempUserRmEmail=tempUserRmEmail;
        localDataBaseHelper = new LocalDataBaseHelper(context);
        localDataBaseHelper.dbInit(localDataBaseHelper);
    }

    public AsyncTaskDbOperation(int operation, int id, boolean isUserSeat, Context context) {
        this.operation = operation;
        this.id = id;
        this.isUserSeat = isUserSeat;
        localDataBaseHelper = new LocalDataBaseHelper(context);
        localDataBaseHelper.dbInit(localDataBaseHelper);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        switch (operation) {
            case DB_OPER_UPDATE_SEAT_STATUS:
                localDataBaseHelper.updateSeatStatus(id, String.valueOf(status));
                break;
            case DB_OPER_UPDATE_STATUS_BOOKING_TIME:
                localDataBaseHelper.updateSeatStatusWithBookingTime(id, String.valueOf(SEAT_BOOKED), bookingTime, bookedFor);
                break;
            case DB_OPER_UPDATE_SEAT_TEMP_EMP:
                localDataBaseHelper.updateTemperoryEmpSeatDetails(id, tempUserName, tempUserId,
                        tempUserRmEmail, bookingTime, bookedFor, status);
                break;
            case DB_OPER_UPDATE_SEAT_SWIPEOUT:
                localDataBaseHelper.updateSeatStatusAfterSwipeOut(id, isUserSeat);
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        localDataBaseHelper.dbDeInit(localDataBaseHelper);
    }
}

