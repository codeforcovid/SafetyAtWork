package com.safeatwork.model;

public class SeatModel {


    EmployeeSeatModel defaultEmployeeSeatModel;
    EmployeeSeatModel temporaryEmployeeSeatModel;
    int id;
    String cube_id;
    String seat_code;
    String seat_booking_time;
    String seat_date;

    public String getSeat_date() {
        return seat_date;
    }

    public void setSeat_date(String seat_date) {
        this.seat_date = seat_date;
    }


    public String getSeat_booking_time() {
        return seat_booking_time;
    }

    public void setSeat_booking_time(String seat_booking_time) {
        this.seat_booking_time = seat_booking_time;
    }

    public String getSeat_booked_for_date() {
        return seat_booked_for_date;
    }

    public void setSeat_booked_for_date(String seat_booked_for_date) {
        this.seat_booked_for_date = seat_booked_for_date;
    }

    String seat_booked_for_date;

    public SeatModel(String cube_id, String seat_code, int seat_status) {
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
    }

    public SeatModel(String seat_date,String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel) {
        this.seat_date = seat_date;
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = new EmployeeSeatModel(null,null,null);
    }

    public SeatModel(String seat_date, String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel, EmployeeSeatModel temporaryEmployeeSeatModel) {
        this.seat_date = seat_date;
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
    }


    public SeatModel(String seat_date,int id,String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel, EmployeeSeatModel temporaryEmployeeSeatModel) {
        this.seat_date = seat_date;
        this.id = id;
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
    }



    public SeatModel() {

    }

    public EmployeeSeatModel getDefaultEmployeeSeatModel() {
        return defaultEmployeeSeatModel;
    }

    public void setDefaultEmployeeSeatModel(EmployeeSeatModel defaultEmployeeSeatModel) {
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
    }

    public EmployeeSeatModel getTemporaryEmployeeSeatModel() {
        return temporaryEmployeeSeatModel;
    }

    public void setTemporaryEmployeeSeatModel(EmployeeSeatModel temporaryEmployeeSeatModel) {
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCube_id() {
        return cube_id;
    }

    public void setCube_id(String cube_id) {
        this.cube_id = cube_id;
    }

    public String getSeat_code() {
        return seat_code;
    }

    public void setSeat_code(String seat_code) {
        this.seat_code = seat_code;
    }

    public int getSeat_status() {
        return seat_status;
    }

    public void setSeat_status(int seat_status) {
        this.seat_status = seat_status;
    }

    int seat_status = 0;

}