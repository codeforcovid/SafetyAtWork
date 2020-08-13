package com.safeatwork.data;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.safeatwork.model.dbformat.Seat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.safeatwork.model.Constants.ACTIVITY_RESULT_READ_FILE;
import static com.safeatwork.model.Constants.APP_NAME;

public class ExcelSeatDataWork {
    String TAG = APP_NAME+"_ExcelSeatDataWork";

    public void launchFileSelector(Activity activiy) {
        Log.e(TAG, "launchFileSelector" );
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Log.e(TAG, "KITKAT" );
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.ms-excel");
            //intent.setDataAndType(uri, "application/vnd.ms-excel");

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

            activiy.startActivityForResult(intent, ACTIVITY_RESULT_READ_FILE);
        }
    }

    public List<Seat> initExcelWork(InputStream myInput) {

    //public List<Seat> initExcelWork(Context context) {
        Log.e(TAG, "initExcelWork" );
        List<Seat> firebaseSeatList = new ArrayList<>();

        try {
            Log.e(TAG, " initExcelWork try");
            //InputStream myInput;
            // initialize asset manager
            //AssetManager assetManager = context.getAssets();
            //  open excel sheet
            //File file = new File();
            //myInput = new FileInputStream(file);
            //myInput = assetManager.open("seatdata.xls");
            Log.e(TAG, " myInput" + myInput);
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Log.e(TAG, " mySheet" + mySheet);
            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            Log.e(TAG, " rowIter" + rowIter);
            int rowno = 0;
            while (rowIter.hasNext()) {
                Log.e(TAG, " while1");
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Log.e(TAG, " rowno !=0");
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    Seat seat = new Seat();
                    seat.id = String.valueOf(rowno-1);
                    //String sno = "", date = "", det = "";
                    while (cellIter.hasNext()) {
                        Log.e(TAG, " while2");
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            Log.e(TAG, " col 0");
                            seat.tower_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 1) {
                            seat.floor_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 2) {
                            seat.odc_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 3) {
                            seat.row_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 4) {
                            seat.cube_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 5) {
                            seat.seat_num = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 6) {
                            seat.default_user_name = myCell.toString();
                        } else if (colno == 7) {
                            seat.default_user_empid = String.valueOf((int)myCell.getNumericCellValue());
                        } else if (colno == 8) {
                            seat.default_user_rm_email = myCell.toString();
                        }
                        colno++;
                        /*seat.temp_user_id = "empty";
                        seat.temp_user_name = "empty";
                        seat.temp_user_rm_name = "empty";
                        seat.seat_booking_time = "empty";
                        seat.seat_booked_for = "empty";
                        seat.seat_status = String.valueOf(SEAT_BLOCKED_BY_ADMIN);*/
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    Log.e(TAG, " row data :" + seat.seat_num + " -- " +seat.default_user_name + " -- " + seat.default_user_empid + "  -- " + seat.default_user_rm_email);
                    //stringBuilder.append(seat.default_user_name + " -- " + seat.default_user_empid + "  -- " + seat.default_user_rm_email + "\n");
                    firebaseSeatList.add(seat);
                }
                rowno++;
            }
            //Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            return firebaseSeatList;
        } catch (Exception e) {
            Log.e(TAG, " initExcelWork catch");
            Log.e(TAG, "error " + e.toString());
            return null;
        }
    }


}
