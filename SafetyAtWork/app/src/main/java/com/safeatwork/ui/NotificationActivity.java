package com.safeatwork.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.safeatwork.R;
import com.safeatwork.adapters.NotificationAdapter;
import com.safeatwork.common.TimeAndDate;
import com.safeatwork.data.LocalDataBaseHelper;
import com.safeatwork.model.EmployeeSeatModel;
import com.safeatwork.model.NotificationModel;
import com.safeatwork.model.dbformat.RowModelDbFormat;

import java.util.ArrayList;
import java.util.List;

import static com.safeatwork.model.Constants.DATE_FORMAT;
import static com.safeatwork.model.Constants.TIME_FORMAT;

public class NotificationActivity extends AppCompatActivity {

    List<NotificationModel> notificationModelList = new ArrayList();
    TimeAndDate timeAndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        timeAndDate = new TimeAndDate();
        new AsyncTaskDbSeatOperation(timeAndDate.getTodaysDate(DATE_FORMAT)).execute();
    }


    public class AsyncTaskDbSeatOperation extends AsyncTask<Void, Void, Boolean> {

        private String date;

        //List<RowModel> rowModelList;
        public AsyncTaskDbSeatOperation(String date) {
            this.date = date;

        }

        @Override
        protected void onPreExecute() {
           /* if(progressBar.getVisibility()== View.GONE) {
                progressBar.setVisibility(View.VISIBLE);
            }*/
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //List<RowModel> completeList = new ArrayList<>();

            LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(getApplicationContext());
            localDataBaseHelper.dbInit(localDataBaseHelper);
            List<RowModelDbFormat> rowModelList = new ArrayList<>();
            if (localDataBaseHelper.isTableExists(LocalDataBaseHelper.SEAT_TABLE_NAME)) {
                rowModelList.addAll(localDataBaseHelper.getBookedSeatNotification(date));
            }
            for (RowModelDbFormat rowModelDbFormat : rowModelList) {
                NotificationModel notificationModel = new NotificationModel();
                if (!TextUtils.isEmpty(rowModelDbFormat.getTemporaryEmployeeSeatModel().getEmp_name())) {
                    EmployeeSeatModel tempModel = rowModelDbFormat.getTemporaryEmployeeSeatModel();
                    notificationModel.setTitle("Seat Booked by " + tempModel.getEmp_name()+"("+tempModel.getEmp_id()+")");
                } else {
                    EmployeeSeatModel defModel = rowModelDbFormat.getDefaultEmployeeSeatModel();
                    notificationModel.setTitle("Seat Booked by " + defModel.getEmp_name()+"("+defModel.getEmp_id()+")");
                }
                notificationModel.setMessage("Seat Location: T"+rowModelDbFormat.getTower_number()
                        +"-F"+rowModelDbFormat.getFloor_number()+"-ODC"+rowModelDbFormat.getOdc_number()+
                        "-R"+rowModelDbFormat.getRow_number()+"-C"+rowModelDbFormat.getCube_id()+
                        "-S"+rowModelDbFormat.getSeat_code());

                if(!TextUtils.isEmpty(rowModelDbFormat.getSeat_booking_time())){
                    notificationModel.setTimestamp(rowModelDbFormat.getSeat_booking_time());

                }else {
                    notificationModel.setTimestamp(timeAndDate.getTodaysDate(TIME_FORMAT));
                }
                notificationModelList.add(notificationModel);
            }
            Log.e("testt", "notificationModelList lize before" + notificationModelList.size());
            localDataBaseHelper.dbDeInit(localDataBaseHelper);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            RecyclerView dynamicRecyclerView = findViewById(R.id.notificationRecyclerView);
            TextView row_number = findViewById(R.id.date_notification);
            row_number.setText(timeAndDate.getTodaysDate(DATE_FORMAT));

            StaggeredGridLayoutManager mStaggeredLayoutManager_Category = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            if (dynamicRecyclerView != null)
                dynamicRecyclerView.setLayoutManager(mStaggeredLayoutManager_Category);

            NotificationAdapter notificationAdapter = new NotificationAdapter(getApplicationContext(), notificationModelList);
            //Log.e("cubeModelListD size", String.valueOf(cubeModelListDynamic.size()));
            dynamicRecyclerView.setAdapter(notificationAdapter);
           /* if(progressBar.getVisibility()==View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }*/
        }
    }
}

