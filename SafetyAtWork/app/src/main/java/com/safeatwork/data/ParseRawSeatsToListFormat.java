package com.safeatwork.data;

import android.util.Log;

import com.safeatwork.model.CubeModel;
import com.safeatwork.model.EmployeeSeatModel;
import com.safeatwork.model.RowModel;
import com.safeatwork.model.SeatModel;
import com.safeatwork.model.dbformat.CubeModelDbFormat;
import com.safeatwork.model.dbformat.RowModelDbFormat;
import com.safeatwork.model.dbformat.Seat;

import java.util.ArrayList;
import java.util.List;

import static com.safeatwork.model.Constants.APP_NAME;

public class ParseRawSeatsToListFormat {
    String TAG = APP_NAME+"_ParseRawSeatsToListFormat";
    /*public Seat convertSeatModelToRawSeat(SeatModel seatModel){
        Seat seat = new Seat(String.valueOf(seatModel.getId()), seatModel.get, seat.floor_num
                , seat.odc_num, seat.row_num, seat.cube_num
                , seat.seat_num, Integer.parseInt(seat.seat_status), seat.seat_booking_time
                , seat.seat_booked_for, new EmployeeSeatModel(seat.default_user_name,seat.default_user_empid,seat.default_user_rm_email),
                new EmployeeSeatModel(seat.temp_user_name,seat.temp_user_id,seat.temp_user_rm_name), seat.seat_date);


                *//*new Seat("1", "54203937", "Venkat Nemali", "sarath@hcl.com",
                "1", "1", "1", "3", "09-07-2020", "09-07-2020 12:10:00", "09-07-2020",
                "1", "0", "empty", "empty", "empty", "1");*//*
        return seat;
    }*/

    public List<RowModel> convertRawSeatsToList(List<Seat> seatList) {

        List<RowModel> rowModelList = new ArrayList<>();
        try {
            List<RowModelDbFormat> completeList = new ArrayList<>();
            completeList.addAll(setRowListBasedOnRowId(1, seatList));
            completeList.addAll(setRowListBasedOnRowId(2, seatList));
            completeList.addAll(setRowListBasedOnRowId(3, seatList));
            completeList.addAll(setRowListBasedOnRowId(4, seatList));

            Log.e(TAG, "after loop");
            for (RowModelDbFormat rowModel1 : completeList) {
                RowModelDbFormat rowModel = rowModel1;
                CubeModelDbFormat cubeModel = null;
                List<CubeModel> cubeModelListUser = new ArrayList<>();
                List<SeatModel> seatModelList1 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList1()) {
                    cubeModel = cubeModel1;
                    seatModelList1.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    Log.e(TAG, "seat " + cubeModel.getSeat_date() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList1));

                List<SeatModel> seatModelList2 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList2()) {
                    cubeModel = cubeModel1;
                    seatModelList2.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    Log.e(TAG, "seat " + cubeModel.getSeat_date() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList2));

                List<SeatModel> seatModelList3 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList3()) {
                    cubeModel = cubeModel1;
                    seatModelList3.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    Log.e(TAG, "seat " + cubeModel.getSeat_date() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }

                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList3));

                List<SeatModel> seatModelList4 = new ArrayList<>();
                for (CubeModelDbFormat cubeModel1 : rowModel.getCubeModelDbFormatList4()) {
                    cubeModel = cubeModel1;
                    seatModelList4.add(new SeatModel(cubeModel.getSeat_date(), cubeModel.getId(), cubeModel.getCube_id(), cubeModel.getSeat_code(), cubeModel.getSeat_status()
                            , cubeModel.getDefaultEmployeeSeatModel(), cubeModel.getTemporaryEmployeeSeatModel()));
                    Log.e(TAG, "seat " + cubeModel.getSeat_date() + " " + cubeModel.getId() + " " + cubeModel.getRow_number() + " " + cubeModel.getCube_id() + " " + cubeModel.getSeat_code() + " status" + cubeModel.getSeat_status());
                }
                cubeModelListUser.add(new CubeModel(cubeModel.getRow_number(), cubeModel.getCube_id(), seatModelList4));
                rowModelList.add(new RowModel(cubeModel.getTower_number(), cubeModel.getFloor_number(), cubeModel.getOdc_number(), cubeModel.getRow_number(), cubeModelListUser));
            }


            return rowModelList;
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
            return new ArrayList<RowModel>();
        }
    }


    List<RowModelDbFormat> setRowListBasedOnRowId(int row, List<Seat> rowModelDbFormatList) {
        List<Seat> rowModelDbFormatListForRow = new ArrayList<>();
        for (Seat rowModelDbFormat : rowModelDbFormatList) {
            int row_id_in_loop = Integer.parseInt(rowModelDbFormat.row_num);
            if (row == row_id_in_loop) {
                rowModelDbFormatListForRow.add(rowModelDbFormat);
            }
        }
        return splitCubesInRow(rowModelDbFormatListForRow);
    }

    List<RowModelDbFormat> splitCubesInRow(List<Seat> rowModelDbFormatList) {
        List<CubeModelDbFormat> cubeModelDbFormatList1 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList2 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList3 = new ArrayList<>();
        List<CubeModelDbFormat> cubeModelDbFormatList4 = new ArrayList<>();
        List<RowModelDbFormat> rowModelDbFormatListNew = new ArrayList<>();
        for (Seat rowModelDbFormat : rowModelDbFormatList) {
            int cube_id_in_loop = Integer.parseInt(rowModelDbFormat.cube_num);
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

    CubeModelDbFormat convertRowModelDataToCubeModel(Seat seat) {
        return new CubeModelDbFormat(Integer.parseInt(seat.id), seat.tower_num, seat.floor_num
                , seat.odc_num, seat.row_num, seat.cube_num
                , seat.seat_num, Integer.parseInt(seat.seat_status), seat.seat_booking_time
                , seat.seat_booked_for, new EmployeeSeatModel(seat.default_user_name, seat.default_user_empid, seat.default_user_rm_email),
                new EmployeeSeatModel(seat.temp_user_name, seat.temp_user_id, seat.temp_user_rm_name), seat.seat_date);
    }


}