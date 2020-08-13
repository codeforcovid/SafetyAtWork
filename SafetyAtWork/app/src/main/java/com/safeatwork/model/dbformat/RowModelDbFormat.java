package com.safeatwork.model.dbformat;

import com.safeatwork.model.EmployeeSeatModel;

import java.util.ArrayList;
import java.util.List;

public class RowModelDbFormat {

    int id;
    String tower_number;
    String floor_number;
    String odc_number;
    String row_number;
    String cube_id;
    String seat_code;
    String seat_booking_time;
    String seat_booked_for_date;
    int seat_status = 0;
    EmployeeSeatModel defaultEmployeeSeatModel;
    EmployeeSeatModel temporaryEmployeeSeatModel;
    String seat_date;

    public String getSeat_date() {
        return seat_date;
    }

    public void setSeat_date(String seat_date) {
        this.seat_date = seat_date;
    }

    List<CubeModelDbFormat> cubeModelDbFormatList1 = new ArrayList<>();

    public List<CubeModelDbFormat> getCubeModelDbFormatList1() {
        return cubeModelDbFormatList1;
    }

    public void setCubeModelDbFormatList1(List<CubeModelDbFormat> cubeModelDbFormatList1) {
        this.cubeModelDbFormatList1 = cubeModelDbFormatList1;
    }

    public List<CubeModelDbFormat> getCubeModelDbFormatList2() {
        return cubeModelDbFormatList2;
    }

    public void setCubeModelDbFormatList2(List<CubeModelDbFormat> cubeModelDbFormatList2) {
        this.cubeModelDbFormatList2 = cubeModelDbFormatList2;
    }

    public List<CubeModelDbFormat> getCubeModelDbFormatList3() {
        return cubeModelDbFormatList3;
    }

    public void setCubeModelDbFormatList3(List<CubeModelDbFormat> cubeModelDbFormatList3) {
        this.cubeModelDbFormatList3 = cubeModelDbFormatList3;
    }

    public List<CubeModelDbFormat> getCubeModelDbFormatList4() {
        return cubeModelDbFormatList4;
    }

    public void setCubeModelDbFormatList4(List<CubeModelDbFormat> cubeModelDbFormatList4) {
        this.cubeModelDbFormatList4 = cubeModelDbFormatList4;
    }

    List<CubeModelDbFormat> cubeModelDbFormatList2 = new ArrayList<>();
    List<CubeModelDbFormat> cubeModelDbFormatList3 = new ArrayList<>();
    List<CubeModelDbFormat> cubeModelDbFormatList4 = new ArrayList<>();

    public RowModelDbFormat(List<CubeModelDbFormat> cubeModelDbFormatList1, List<CubeModelDbFormat> cubeModelDbFormatList2, List<CubeModelDbFormat> cubeModelDbFormatList3, List<CubeModelDbFormat> cubeModelDbFormatList4) {
        this.cubeModelDbFormatList1 = cubeModelDbFormatList1;
        this.cubeModelDbFormatList2 = cubeModelDbFormatList2;
        this.cubeModelDbFormatList3 = cubeModelDbFormatList3;
        this.cubeModelDbFormatList4 = cubeModelDbFormatList4;
    }



    public RowModelDbFormat(int id, String tower_number, String floor_number, String odc_number, String row_number, String cube_id,
                            String seat_code, int seat_status, String seat_booking_time, String seat_booked_for_date,
                            EmployeeSeatModel defaultEmployeeSeatModel, EmployeeSeatModel temporaryEmployeeSeatModel,String seat_date) {
        this.id = id;
        this.tower_number = tower_number;
        this.floor_number = floor_number;
        this.odc_number = odc_number;
        this.row_number = row_number;
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_booking_time = seat_booking_time;
        this.seat_booked_for_date = seat_booked_for_date;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
        this.seat_date = seat_date;
    }

    public String getRow_number() {
        return row_number;
    }

    public void setRow_number(String row_number) {
        this.row_number = row_number;
    }


    public String getTower_number() {
        return tower_number;
    }

    public void setTower_number(String tower_number) {
        this.tower_number = tower_number;
    }

    public String getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(String floor_number) {
        this.floor_number = floor_number;
    }

    public String getOdc_number() {
        return odc_number;
    }

    public void setOdc_number(String odc_number) {
        this.odc_number = odc_number;
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



    public RowModelDbFormat(String cube_id, String seat_code, int seat_status) {
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
    }

    public RowModelDbFormat(String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel) {
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = new EmployeeSeatModel(null,null,null);
    }

    public RowModelDbFormat(String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel, EmployeeSeatModel temporaryEmployeeSeatModel) {
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
    }


    public RowModelDbFormat(int id, String cube_id, String seat_code, int seat_status, EmployeeSeatModel defaultEmployeeSeatModel, EmployeeSeatModel temporaryEmployeeSeatModel) {
        this.id = id;
        this.cube_id = cube_id;
        this.seat_code = seat_code;
        this.seat_status = seat_status;
        this.defaultEmployeeSeatModel = defaultEmployeeSeatModel;
        this.temporaryEmployeeSeatModel = temporaryEmployeeSeatModel;
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


}