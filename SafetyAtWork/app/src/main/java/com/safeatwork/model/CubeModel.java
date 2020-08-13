package com.safeatwork.model;

import java.util.ArrayList;
import java.util.List;

public class CubeModel {

    public CubeModel(String row_number, String cube_number, List<SeatModel> seatModelList) {
        this.row_number = row_number;
        this.seatModelList = seatModelList;
        this.cube_number = cube_number;
    }

    public CubeModel() {

    }

    public SeatModel getSeatModelByPosition(int position) {
        if (seatModelList.size()>position) {
            return seatModelList.get(position);
        }else return null;
    }

    public List<SeatModel> getSeatModelList() {
        return seatModelList;
    }

    public void setSeatModelList(List<SeatModel> seatModelList) {
        this.seatModelList = seatModelList;
    }
/*
    public int getRow_num() {
        return row_number;
    }

    public void setRow_num(int row_number) {
        this.row_number = row_number;
    }*/

    List<SeatModel> seatModelList = new ArrayList<SeatModel>();
    //int row_number;

    public String getCube_number() {
        return cube_number;
    }

    public void setCube_number(String cube_number) {
        this.cube_number = cube_number;
    }

    String cube_number;

    public String getRow_number() {
        return row_number;
    }

    public void setRow_number(String row_number) {
        this.row_number = row_number;
    }

    String row_number;
}