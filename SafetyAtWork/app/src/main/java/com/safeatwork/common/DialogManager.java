package com.safeatwork.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import static com.safeatwork.model.Constants.SEAT_AVAILABLE;
import static com.safeatwork.model.Constants.SEAT_BLOCKED_BY_ADMIN;

public class DialogManager {
    void launchSelectorDialog(Activity activity, int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int[] statusArray = {SEAT_AVAILABLE, SEAT_BLOCKED_BY_ADMIN};
        int selection = (status == SEAT_AVAILABLE) ? 0 : 1;
        CharSequence[] items = new CharSequence[]{"Available", "Blocked"};
        builder.setTitle("Select Option");
        builder.setSingleChoiceItems(items, selection, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface d, int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    /*case 2:
                        break;
                    case 3:
                        break;*/
                }
            }

        });
        builder.setNegativeButton("Cancel", null);
        builder.setTitle("Change Seat Status");
        builder.show();
    }
}
