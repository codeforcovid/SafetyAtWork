package com.safeatwork.model;


import java.util.ArrayList;
import java.util.List;

public class RowModel {

    public RowModel(String row_number, List<CubeModel> cubeModelList) {
        this.cubeModelList = cubeModelList;
        this.row_number = row_number;
    }


    String tower_number;
    String floor_number;
    String odc_number;
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



    public RowModel(String tower_number, String floor_number, String odc_number, String row_number, List<CubeModel> cubeModelList) {
        this.tower_number = tower_number;
        this.floor_number = floor_number;
        this.odc_number = odc_number;
        this.row_number = row_number;
        this.cubeModelList = cubeModelList;
    }

    String row_number;
    List<CubeModel> cubeModelList = new ArrayList<CubeModel>();


    public RowModel() {

    }

    public List<CubeModel> getCubeModelList() {
        return cubeModelList;
    }

    public void setCubeModelList(List<CubeModel> cubeModelList) {
        this.cubeModelList = cubeModelList;
    }

    public String getRow_number() {
        return row_number;
    }

    public void setRow_number(String row_number) {
        this.row_number = row_number;
    }



}