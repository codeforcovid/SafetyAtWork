package com.safeatwork.model;

public class Constants {

    public static final int SEAT_AVAILABLE = 0; //White
    public static final int SEAT_BOOKED = 1; //Green
    public static final int SEAT_BLOCKED_BY_ADMIN = 2; //Grey
    public static final int SEAT_USER_NOT_BOOKED = 3; //Gold
    public static final int SEAT_USER_BOOKED = 4; //Blue
    public static final int SEAT_USER_NOT_BOOKED_BUT_RISK = 5;

    public static String DATE_FORMAT = "dd-MM-yyyy";
    public static String ADMIN_ACCESS = "1";

    public static String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss a";

    public static final int DIALOG_LOG_OUT = 0;
    public static final int DIALOG_SWIPE_OUT = 1;
    public static final int DIALOG_DATA_RESET = 2;
    public static final int DIALOG_EXCEL_UPLOAD = 3;

    public static final String EXCEL_FILE_NAME = "seatdata";
    public static final String APP_PREFERENCE = "AppPreference";

    public static final String PREF_USER_LOGIN = "UserLogin";
    public static final String PREF_USER_NAME = "UserName";
    public static final String PREF_UNIQ_NOTIFY_ID = "uniqueNotifyId";
    public static final String PREF_ADMIN_ACCESS = "AdminAccess";

    public static final String APP_NAME = "safeatwork";
    public static final String DB_NAME = APP_NAME+".db";

    public static final int ACTIVITY_RESULT_READ_PERMISSION = 2;
    public static final int ACTIVITY_RESULT_FINE_LOCATION_PERMISSION = 4;
    public static final int ACTIVITY_RESULT_READ_FILE = 50;

}
