package com.safeatwork.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.safeatwork.model.Constants;
import com.safeatwork.model.CubeModel;
import com.safeatwork.model.EmployeeModel;
import com.safeatwork.model.EmployeeSeatModel;
import com.safeatwork.model.RowModel;
import com.safeatwork.model.SeatModel;
import com.safeatwork.model.dbformat.CubeModelDbFormat;
import com.safeatwork.model.dbformat.RowModelDbFormat;

import java.util.ArrayList;
import java.util.List;

import static com.safeatwork.model.Constants.APP_NAME;
import static com.safeatwork.model.Constants.SEAT_AVAILABLE;
import static com.safeatwork.model.Constants.SEAT_BOOKED;

public class LocalDataBaseHelper {
    String TAG = APP_NAME+"_LocalDataBaseHelper";

    private Context context;
    private SQLiteDatabase db;
    @SuppressLint("SdCardPath")
    private static final String DB_NAME = Constants.DB_NAME;
    public static String DB_PATH = "/data/data/" + APP_NAME + "/databases/" + DB_NAME;
    private final int DB_VERSION = 1;

    public static final String EMPLOYEE_TABLE_NAME = "employee_table";
    private final String EMPLOYEE_TABLE_ROW_ID = "id";
    private final String EMPLOYEE_TABLE_ROW_EMP_NAME = "emp_name";
    private final String EMPLOYEE_TABLE_ROW_EMP_ID = "emp_id";
    private final String EMPLOYEE_TABLE_ROW_EMP_EMAIL = "emp_email";
    private final String EMPLOYEE_TABLE_ROW_EMP_PWD = "emp_password";
    private final String EMPLOYEE_TABLE_ROW_RM_EMAIL = "rm_email";
    private final String EMPLOYEE_TABLE_ROW_RM_NAME = "rm_name";
    private final String EMPLOYEE_TABLE_ROW_ADMIN_RIGHTS = "admin_rights";
    private final String EMPLOYEE_TABLE_ROW_SEAT_CODE = "seat_code";

    public static final String SEAT_TABLE_NAME = "seat_table";
    private final String SEAT_TABLE_ROW_ID = "id";
    private final String SEAT_TABLE_ROW_TOWER_NUMBER = "tower_num";
    private final String SEAT_TABLE_ROW_FLOOR_NUMBER = "floor_num";
    private final String SEAT_TABLE_ROW_ODC_NUMBER = "odc_num";
    private final String SEAT_TABLE_ROW_ROW_NUMBER = "row_num";
    private final String SEAT_TABLE_ROW_CUBE_NUMBER = "cube_num";
    private final String SEAT_TABLE_ROW_SEAT_NUMBER = "seat_num";
    private final String SEAT_TABLE_ROW_SEAT_STATUS = "seat_status";
    private final String SEAT_TABLE_ROW_SEAT_BOOKING_TIME = "seat_booking_time";
    private final String SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE = "seat_booked_for";
    private final String SEAT_TABLE_ROW_SEAT_DEFAULT_USER_NAME = "default_user_name";
    private final String SEAT_TABLE_ROW_SEAT_DEFAULT_USER_ID = "default_user_empid";
    private final String SEAT_TABLE_ROW_SEAT_DEFAULT_USER_RM_EMAIL = "default_user_rm_email";
    private final String SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME = "temp_user_name";
    private final String SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID = "temp_user_id";
    private final String SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL = "temp_user_rm_name";
    private final String SEAT_TABLE_ROW_SEAT_DATE = "seat_date";

    public static final String NOTIFICATION_TABLE_NAME = "notification_table";
    private final String NOTIFICATION_TABLE_ROW_ID = "id";
    private final String NOTIFICATION_TABLE_ROW_TITLE = "title";
    private final String NOTIFICATION_TABLE_ROW_MESSAGE = "message";
    private final String NOTIFICATION_TABLE_ROW_TIMESTAMP = "timestamp";

    public SQLiteDatabase getDB() {
        return this.db;
    }

    public LocalDataBaseHelper(Context context) {
        this.context = context;
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String newTableQueryString = "create table if not exists " +
                    EMPLOYEE_TABLE_NAME +
                    " (" +
                    EMPLOYEE_TABLE_ROW_ID + " integer primary key autoincrement not null," +
                    EMPLOYEE_TABLE_ROW_EMP_NAME + "," +
                    EMPLOYEE_TABLE_ROW_EMP_ID + "," +
                    EMPLOYEE_TABLE_ROW_EMP_EMAIL + "," +
                    EMPLOYEE_TABLE_ROW_EMP_PWD + "," +
                    EMPLOYEE_TABLE_ROW_RM_EMAIL + "," +
                    EMPLOYEE_TABLE_ROW_RM_NAME + "," +
                    EMPLOYEE_TABLE_ROW_ADMIN_RIGHTS + "," +
                    EMPLOYEE_TABLE_ROW_SEAT_CODE +
                    ");";
            sqLiteDatabase.execSQL(newTableQueryString);

            String newTableQueryString1 = "create table if not exists " +
                    SEAT_TABLE_NAME +
                    " (" +
                    //SEAT_TABLE_ROW_ID + " integer primary key autoincrement not null," +
                    SEAT_TABLE_ROW_ID + "," +
                    SEAT_TABLE_ROW_TOWER_NUMBER + "," +
                    SEAT_TABLE_ROW_FLOOR_NUMBER + "," +
                    SEAT_TABLE_ROW_ODC_NUMBER + "," +
                    SEAT_TABLE_ROW_ROW_NUMBER + "," +
                    SEAT_TABLE_ROW_CUBE_NUMBER + "," +
                    SEAT_TABLE_ROW_SEAT_NUMBER + "," +
                    SEAT_TABLE_ROW_SEAT_STATUS + " integer," +
                    SEAT_TABLE_ROW_SEAT_BOOKING_TIME + "," +
                    SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE + "," +
                    SEAT_TABLE_ROW_SEAT_DEFAULT_USER_NAME + "," +
                    SEAT_TABLE_ROW_SEAT_DEFAULT_USER_ID + "," +
                    SEAT_TABLE_ROW_SEAT_DEFAULT_USER_RM_EMAIL + "," +
                    SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME + "," +
                    SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID + "," +
                    SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL + "," +
                    SEAT_TABLE_ROW_SEAT_DATE +
                    ");";
            sqLiteDatabase.execSQL(newTableQueryString1);

            String newTableQueryString2 = "create table if not exists " +
                    NOTIFICATION_TABLE_NAME +
                    " (" +
                    NOTIFICATION_TABLE_ROW_ID + " integer primary key autoincrement not null," +
                    NOTIFICATION_TABLE_ROW_TITLE + "," +
                    NOTIFICATION_TABLE_ROW_MESSAGE + "," +
                    NOTIFICATION_TABLE_ROW_TIMESTAMP +
                    ");";
            sqLiteDatabase.execSQL(newTableQueryString2);
        }

        @Override
        public void close() {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db != null && db.isOpen())
                db.close();
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public void dbInit(LocalDataBaseHelper localDataBaseHelper) {
        SQLiteDatabase db = localDataBaseHelper.getDB();
        if (db == null)
            db = SQLiteDatabase.openDatabase(LocalDataBaseHelper.DB_PATH, null, 0);
    }

    public void dbDeInit(LocalDataBaseHelper localDataBaseHelper) {
        SQLiteDatabase db = localDataBaseHelper.getDB();
        if (db != null && db.isOpen())
            db.close();
    }

    public void updateSeatStatusWithBookingTime(int id, String status, String bookingTime, String bookedFor) {
        Log.e(TAG, "updateSeatStatus id " + id + " status " + status);
        ContentValues values = new ContentValues();
        values.put(SEAT_TABLE_ROW_SEAT_STATUS, status);
        values.put(SEAT_TABLE_ROW_SEAT_BOOKING_TIME, bookingTime);
        values.put(SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, bookedFor);
        db.update(SEAT_TABLE_NAME, values, SEAT_TABLE_ROW_ID + "=" + id, null);
    }

    public void updateSeatStatus(int id, String status) {
        Log.e(TAG, "updateSeatStatus id " + id + " status " + status);
        ContentValues values = new ContentValues();
        values.put(SEAT_TABLE_ROW_SEAT_STATUS, status);
        db.update(SEAT_TABLE_NAME, values, SEAT_TABLE_ROW_ID + "=" + id, null);
    }

    public void updateSeatStatusAfterSwipeOut(int id, boolean isUserSeat) {
        Log.e(TAG, "db updateSeatStatusAfterSwipeOut id " + id + " isUserSeat " + isUserSeat);
        ContentValues values = new ContentValues();
        if (isUserSeat) {
            values.put(SEAT_TABLE_ROW_SEAT_BOOKING_TIME, "empty");
            values.put(SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, "empty");
        } else {
            values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME, "empty");
            values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID, "empty");
            values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL, "empty");
            values.put(SEAT_TABLE_ROW_SEAT_BOOKING_TIME, "empty");
            values.put(SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, "empty");
        }
        values.put(SEAT_TABLE_ROW_SEAT_STATUS, SEAT_AVAILABLE);

        db.update(SEAT_TABLE_NAME, values, SEAT_TABLE_ROW_ID + "=" + id, null);
    }

    public void updateTemperoryEmpSeatDetails(int id, String tempUserName, String tempUserId, String tempUserRmEmail,
                                              String bookingTime, String bookedFor, String status) {
        Log.e(TAG, "updateSeatStatus id " + id + " status " + status);
        ContentValues values = new ContentValues();
        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME, tempUserName);
        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID, tempUserId);
        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL, tempUserRmEmail);
        values.put(SEAT_TABLE_ROW_SEAT_BOOKING_TIME, bookingTime);
        values.put(SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, bookedFor);
        values.put(SEAT_TABLE_ROW_SEAT_STATUS, status);
        db.update(SEAT_TABLE_NAME, values, SEAT_TABLE_ROW_ID + "=" + id, null);
    }

    public List<RowModelDbFormat> getBookedSeatNotification(String date) {
        Log.e(TAG, "getBookedSeatNotification");

        List<RowModelDbFormat> rowModelDbFormatList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + SEAT_TABLE_NAME + " WHERE " + SEAT_TABLE_ROW_SEAT_DATE + " = '" + date + "' AND "
                    + SEAT_TABLE_ROW_SEAT_STATUS + " = '" + SEAT_BOOKED + "'", null);
            cursor.moveToFirst();


            if (!cursor.isAfterLast()) {
                do {
                    RowModelDbFormat rowModelDbFormat =
                            new RowModelDbFormat(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
                                    cursor.getString(6), cursor.getInt(7), cursor.getString(8),
                                    cursor.getString(9), new EmployeeSeatModel(cursor.getString(10), cursor.getString(11), cursor.getString(12)),
                                    new EmployeeSeatModel(cursor.getString(13), cursor.getString(14), cursor.getString(15))
                                    , cursor.getString(16));
                    rowModelDbFormatList.add(rowModelDbFormat);
                }
                while (cursor.moveToNext());
                //Log.e("kf", "data in local db" + table_name + listData.size());
            }

            Log.e(TAG, "rowModelDbFormatList " + rowModelDbFormatList.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
        return rowModelDbFormatList;
    }

    public List<RowModel> getAllSeatsData(/*String date*/) {
        Log.e("getAllSeatsData", "called");

        List<RowModel> rowModelList = new ArrayList<>();
        Cursor cursor = null;
        try {

            //cursor = db.rawQuery("SELECT * FROM " + SEAT_TABLE_NAME /*+ " WHERE " + SEAT_TABLE_ROW_SEAT_DATE + " = '" + date + "' COLLATE NOCASE"*/, null);

            cursor = db.query(
                    SEAT_TABLE_NAME,
                    new String[]{SEAT_TABLE_ROW_ID, SEAT_TABLE_ROW_TOWER_NUMBER, SEAT_TABLE_ROW_FLOOR_NUMBER
                            , SEAT_TABLE_ROW_ODC_NUMBER, SEAT_TABLE_ROW_ROW_NUMBER, SEAT_TABLE_ROW_CUBE_NUMBER
                            , SEAT_TABLE_ROW_SEAT_NUMBER, SEAT_TABLE_ROW_SEAT_STATUS, SEAT_TABLE_ROW_SEAT_BOOKING_TIME
                            , SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, SEAT_TABLE_ROW_SEAT_DEFAULT_USER_NAME,
                            SEAT_TABLE_ROW_SEAT_DEFAULT_USER_ID, SEAT_TABLE_ROW_SEAT_DEFAULT_USER_RM_EMAIL,
                            SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME , SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID,
                            SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL,SEAT_TABLE_ROW_SEAT_DATE},
                    null, null, null, null, null);
            cursor.moveToFirst();

            List<RowModelDbFormat> rowModelDbFormatList = new ArrayList<>();
            if (!cursor.isAfterLast()) {
                do {
                    RowModelDbFormat rowModelDbFormat =
                            new RowModelDbFormat(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
                                    cursor.getString(6), cursor.getInt(7), cursor.getString(8),
                                    cursor.getString(9), new EmployeeSeatModel(cursor.getString(10), cursor.getString(11), cursor.getString(12)),
                                    new EmployeeSeatModel(cursor.getString(13), cursor.getString(14), cursor.getString(15))
                                    , cursor.getString(16));
                    rowModelDbFormatList.add(rowModelDbFormat);
                }
                while (cursor.moveToNext());
                Log.e("kf", "data in local db" +  rowModelDbFormatList.size());
            }
            List<RowModelDbFormat> completeList = new ArrayList<>();
            completeList.addAll(setRowListBasedOnRowId(1, rowModelDbFormatList));
            completeList.addAll(setRowListBasedOnRowId(2, rowModelDbFormatList));
            completeList.addAll(setRowListBasedOnRowId(3, rowModelDbFormatList));
            completeList.addAll(setRowListBasedOnRowId(4, rowModelDbFormatList));

            //Log.e(TAG, "after loop");
            for (RowModelDbFormat rowModel1 : completeList) {
                RowModelDbFormat rowModel = rowModel1;
                CubeModelDbFormat cubeModel = null;
                List<CubeModel> cubeModelListUser = new ArrayList<>();
                List<SeatModel> seatModelList1 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList1()) {
                    cubeModel = cubeModel1;
                    seatModelList1.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    //Log.e(TAG, "seat " + cubeModel.getId() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList1));

                List<SeatModel> seatModelList2 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList2()) {
                    cubeModel = cubeModel1;
                    seatModelList2.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    //Log.e(TAG, "seat " + cubeModel.getId() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList2));

                List<SeatModel> seatModelList3 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList3()) {
                    cubeModel = cubeModel1;
                    seatModelList3.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    //Log.e(TAG, "seat " + cubeModel.getId() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }

                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList3));

                List<SeatModel> seatModelList4 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList4()) {
                    cubeModel = cubeModel1;
                    seatModelList4.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    //Log.e(TAG, "seat " + cubeModel.getId() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList4));
                rowModelList.add(new RowModel(cubeModel.getTower_number(), cubeModel.getFloor_number(), cubeModel.getOdc_number(), cubeModel.getRow_number(), cubeModelListUser));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
        return rowModelList;
    }

    List<RowModelDbFormat> setRowListBasedOnRowId(int row, List<RowModelDbFormat> rowModelDbFormatList) {
        List<RowModelDbFormat> rowModelDbFormatListForRow = new ArrayList<>();
        for (RowModelDbFormat rowModelDbFormat : rowModelDbFormatList) {
            int row_id_in_loop = Integer.parseInt(rowModelDbFormat.getRow_number());
            if (row == row_id_in_loop) {
                rowModelDbFormatListForRow.add(rowModelDbFormat);
            }
        }
        return splitCubesInRow(rowModelDbFormatListForRow);
    }

    List<RowModelDbFormat> splitCubesInRow(List<RowModelDbFormat> rowModelDbFormatList) {
        List<CubeModelDbFormat> cubeModelDbFormatList1 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList2 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList3 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList4 = new ArrayList<>();
        List<RowModelDbFormat> rowModelDbFormatListNew = new ArrayList<>();
        for (RowModelDbFormat rowModelDbFormat : rowModelDbFormatList) {
            int cube_id_in_loop = Integer.parseInt(rowModelDbFormat.getCube_id());
            if (cube_id_in_loop == 1) {
                cubeModelDbFormatList1.add(convertRowModelDataToCubeModel(rowModelDbFormat));
            } else if (cube_id_in_loop == 2) {
                cubeModelDbFormatList2.add(convertRowModelDataToCubeModel(rowModelDbFormat));
            } else if (cube_id_in_loop == 3) {
                cubeModelDbFormatList3.add(convertRowModelDataToCubeModel(rowModelDbFormat));
            } else if (cube_id_in_loop == 4) {
                cubeModelDbFormatList4.add(convertRowModelDataToCubeModel(rowModelDbFormat));
            }
        }

        rowModelDbFormatListNew.add(new RowModelDbFormat(cubeModelDbFormatList1, cubeModelDbFormatList2, cubeModelDbFormatList3, cubeModelDbFormatList4));
        return rowModelDbFormatListNew;
    }

    CubeModelDbFormat convertRowModelDataToCubeModel(RowModelDbFormat rowModelDbFormat) {
        return new CubeModelDbFormat(rowModelDbFormat.getId(), rowModelDbFormat.getTower_number(), rowModelDbFormat.getFloor_number()
                , rowModelDbFormat.getOdc_number(), rowModelDbFormat.getRow_number(), rowModelDbFormat.getCube_id()
                , rowModelDbFormat.getSeat_code(), rowModelDbFormat.getSeat_status(), rowModelDbFormat.getSeat_booking_time()
                , rowModelDbFormat.getSeat_booked_for_date(), rowModelDbFormat.getDefaultEmployeeSeatModel()
                , rowModelDbFormat.getTemporaryEmployeeSeatModel(), rowModelDbFormat.getSeat_date());
    }


    public void insertSeatData(List<RowModel> rowModelList, boolean isInsert) {

        /*String newTableQueryString1 = "create table if not exists " +
                SEAT_TABLE_NAME +
                " (" +
                SEAT_TABLE_ROW_ID + " integer primary key autoincrement not null," +
                SEAT_TABLE_ROW_TOWER_NUMBER + "," +
                SEAT_TABLE_ROW_FLOOR_NUMBER + "," +
                SEAT_TABLE_ROW_ODC_NUMBER + "," +
                SEAT_TABLE_ROW_ROW_NUMBER + "," +
                SEAT_TABLE_ROW_CUBE_NUMBER + "," +
                SEAT_TABLE_ROW_SEAT_NUMBER + "," +
                SEAT_TABLE_ROW_SEAT_STATUS + " integer," +
                SEAT_TABLE_ROW_SEAT_BOOKING_TIME + "," +
                SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE + "," +
                SEAT_TABLE_ROW_SEAT_DEFAULT_USER_NAME + "," +
                SEAT_TABLE_ROW_SEAT_DEFAULT_USER_ID + "," +
                SEAT_TABLE_ROW_SEAT_DEFAULT_USER_RM_EMAIL + "," +
                SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME + "," +
                SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID + "," +
                SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL + "," +
                SEAT_TABLE_ROW_SEAT_DATE +
                ");";
        db.execSQL(newTableQueryString1);*/
        Log.e(TAG, "insertSeatData");
        //ConcurrentModificationException
        try {
            for (RowModel rowModel : rowModelList) {
                for (CubeModel cubeModel : rowModel.getCubeModelList()) {
                    for (SeatModel seatModel : cubeModel.getSeatModelList()) {
                        //SeatModel s = seatModel;
                        // TODO Auto-generated method stub
                        ContentValues values = new ContentValues();

                        values.put(SEAT_TABLE_ROW_TOWER_NUMBER, rowModel.getTower_number());
                        values.put(SEAT_TABLE_ROW_FLOOR_NUMBER, rowModel.getFloor_number());
                        values.put(SEAT_TABLE_ROW_ODC_NUMBER, rowModel.getOdc_number());
                        values.put(SEAT_TABLE_ROW_ROW_NUMBER, rowModel.getRow_number());
                        values.put(SEAT_TABLE_ROW_CUBE_NUMBER, cubeModel.getCube_number());
                        values.put(SEAT_TABLE_ROW_SEAT_NUMBER, seatModel.getSeat_code());
                        values.put(SEAT_TABLE_ROW_SEAT_STATUS, seatModel.getSeat_status());
                        values.put(SEAT_TABLE_ROW_SEAT_BOOKING_TIME, seatModel.getSeat_booking_time());
                        values.put(SEAT_TABLE_ROW_SEAT_BOOKED_FOR_DATE, seatModel.getSeat_booked_for_date());
                        EmployeeSeatModel defaultEmployeeSeatModel = seatModel.getDefaultEmployeeSeatModel();
                        values.put(SEAT_TABLE_ROW_SEAT_DEFAULT_USER_NAME, defaultEmployeeSeatModel.getEmp_name());
                        values.put(SEAT_TABLE_ROW_SEAT_DEFAULT_USER_ID, defaultEmployeeSeatModel.getEmp_id());
                        values.put(SEAT_TABLE_ROW_SEAT_DEFAULT_USER_RM_EMAIL, defaultEmployeeSeatModel.getRm_email());
                        EmployeeSeatModel temporaryEmployeeSeatModel = seatModel.getTemporaryEmployeeSeatModel();
                        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_NAME, temporaryEmployeeSeatModel.getEmp_name());
                        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_ID, temporaryEmployeeSeatModel.getEmp_id());
                        values.put(SEAT_TABLE_ROW_SEAT_TEMPORARY_USER_RM_EMAIL, temporaryEmployeeSeatModel.getRm_email());
                        values.put(SEAT_TABLE_ROW_SEAT_DATE, seatModel.getSeat_date());
                        //Log.e(TAG, "Seat " + cubeModel.getRow_num() + " " + seatModel.getCube_num() + " " + seatModel.getSeat_num());
                        if (isInsert) {
                            //Log.e(TAG, "Insert "+seatModel.getId());
                            values.put(SEAT_TABLE_ROW_ID, seatModel.getId());
                            db.insert(SEAT_TABLE_NAME, null, values);
                        } else {
                            //Log.e(TAG, "Update "+seatModel.getId());
                            db.update(SEAT_TABLE_NAME, values, SEAT_TABLE_ROW_ID + "=" + String.valueOf(seatModel.getId()), null);
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertEmployeeData(EmployeeModel employeeModel) {
        Log.e("insertUserData", "called");

        EmployeeModel e = employeeModel;
        // TODO Auto-generated method stub
        ContentValues values = new ContentValues();

        values.put(EMPLOYEE_TABLE_ROW_EMP_NAME, e.getEmp_name());
        values.put(EMPLOYEE_TABLE_ROW_EMP_ID, e.getEmp_id());
        values.put(EMPLOYEE_TABLE_ROW_EMP_EMAIL, e.getEmp_email());
        values.put(EMPLOYEE_TABLE_ROW_EMP_PWD, e.getEmp_password());
        values.put(EMPLOYEE_TABLE_ROW_RM_EMAIL, e.getRm_email());
        values.put(EMPLOYEE_TABLE_ROW_RM_NAME, e.getRm_name());
        values.put(EMPLOYEE_TABLE_ROW_ADMIN_RIGHTS, e.getAdmin_rights());
        values.put(EMPLOYEE_TABLE_ROW_SEAT_CODE, e.getSeat_code());

        db.insert(EMPLOYEE_TABLE_NAME, null, values);
    }

    public EmployeeModel getUserDetails(String email, String password) {
        Log.e("getUserDetails", "called");
        EmployeeModel employeeModel = null;
        try {
            Cursor cursor;
            if (password == null) {
                cursor = db.rawQuery("SELECT * FROM " + EMPLOYEE_TABLE_NAME + " WHERE " + EMPLOYEE_TABLE_ROW_EMP_EMAIL + " = '" + email + "' COLLATE NOCASE", null);
            } else {
                cursor = db.rawQuery("SELECT * FROM " + EMPLOYEE_TABLE_NAME + " WHERE " + EMPLOYEE_TABLE_ROW_EMP_EMAIL + " = '" + email + "' COLLATE NOCASE AND "
                        + EMPLOYEE_TABLE_ROW_EMP_PWD + " = '" + password + "'", null);
            }
            //Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                do {
                    employeeModel = new EmployeeModel();
                    employeeModel.setEmp_name(cursor.getString(1));
                    employeeModel.setEmp_id(cursor.getString(2));
                    employeeModel.setEmp_email(cursor.getString(3));
                    employeeModel.setEmp_password(cursor.getString(4));
                    employeeModel.setRm_email(cursor.getString(5));
                    employeeModel.setRm_name(cursor.getString(6));
                    employeeModel.setAdmin_rights(cursor.getString(7));
                    employeeModel.setSeat_code(cursor.getString(8));
                    Log.e("employee name found", employeeModel.getEmp_name());
                }
                while (cursor.moveToNext());
            }
            Log.e("employeeModel", String.valueOf(employeeModel));
            cursor.close();
            //db.close();
        } catch (Exception e) {
            e.printStackTrace();
            return employeeModel;
        }
        return employeeModel;
    }

    public boolean isSeatDataExistForSpecificDate(String date) {
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + SEAT_TABLE_NAME + " WHERE " + SEAT_TABLE_ROW_SEAT_DATE + " = '" + date + "' COLLATE NOCASE", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
            }
            assert cursor != null;
            cursor.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public boolean isTableExists(String tableName) {
        try {
            //SQLiteDatabase db = _c.openOrCreateDatabase( DB_NAME , Context.MODE_PRIVATE,null);
            String sql_querry = "select * from " + tableName;
            Cursor cursor = db.rawQuery(sql_querry, null);

            //Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    //Log.e("ccc", String.valueOf(cursor.getCount()));
                    cursor.close();
                    //db.close();
                    return true;
                }
                //cursor.close();
            }

            assert cursor != null;
            cursor.close();
            //db.close();
        } catch (Exception e) {
            return false;
            //e.printStackTrace();

        }
        return false;
    }

    public void deleteEmployeeTable() {
        try {
            //db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
            db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEE_TABLE_NAME);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            //Toast.makeText(this.context, ""+e.toString(), 1000).show();
        }
    }

    public void deleteSeatTable() {
        try {
            //db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
            db.execSQL("DROP TABLE IF EXISTS " + SEAT_TABLE_NAME);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            //Toast.makeText(this.context, ""+e.toString(), 1000).show();
        }
    }

    public void deleteRowFromSeatTable(int rowID) {
        try {
            db.delete(SEAT_TABLE_NAME, SEAT_TABLE_ROW_ID + "=" + rowID, null);
            //delete//https://stackoverflow.com/questions/39069338/sqlite-delete-row-by-timestamp-x-days
            //tx.executeSql(DELETE FROM mytable WHERE (msg_when <= datetime('now', '-4 days'))",
            //db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEE_TABLE_NAME);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            //Toast.makeText(this.context, ""+e.toString(), 1000).show();
        }
    }

}
