package com.safeatwork.model.dbformat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Seat {

    public String cube_num;
    public String default_user_empid;
    public String default_user_name;
    public String default_user_rm_email;
    public String floor_num;
    public String id;
    public String odc_num;
    public String row_num;
    public String seat_booked_for;
    public String seat_booking_time;
    public String seat_date;
    public String seat_num;
    public String seat_status ;
    public String temp_user_name;
    public String temp_user_id;
    public String temp_user_rm_name;
    public String tower_num;


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cube_num", cube_num);
        result.put("default_user_empid", default_user_empid);
        result.put("default_user_name", default_user_name);
        result.put("default_user_rm_email", default_user_rm_email);
        result.put("floor_num", floor_num);
        result.put("id", id);
        result.put("odc_num", odc_num);
        result.put("row_num", row_num);
        result.put("seat_booked_for", seat_booked_for);
        result.put("seat_booking_time", seat_booking_time);
        result.put("seat_date", seat_date);
        result.put("seat_num", seat_num);
        result.put("seat_status", seat_status);
        result.put("temp_user_name", temp_user_name);
        result.put("temp_user_id", temp_user_id);
        result.put("temp_user_rm_name", temp_user_rm_name);
        result.put("tower_num", tower_num);

        return result;
    }

    public Seat(String cube_num, String default_user_empid, String default_user_name, String default_user_rm_email, String floor_num, String id, String odc_num, String row_num, String seat_booked_for, String seat_booking_time, String seat_date, String seat_num, String seat_status, String temp_user_name, String temp_user_id, String temp_user_rm_name, String tower_num) {
        this.cube_num = cube_num;
        this.default_user_empid = default_user_empid;
        this.default_user_name = default_user_name;
        this.default_user_rm_email = default_user_rm_email;
        this.floor_num = floor_num;
        this.id = id;
        this.odc_num = odc_num;
        this.row_num = row_num;
        this.seat_booked_for = seat_booked_for;
        this.seat_booking_time = seat_booking_time;
        this.seat_date = seat_date;
        this.seat_num = seat_num;
        this.seat_status = seat_status;
        this.temp_user_name = temp_user_name;
        this.temp_user_id = temp_user_id;
        this.temp_user_rm_name = temp_user_rm_name;
        this.tower_num = tower_num;
    }


    public Seat(){

    }
}