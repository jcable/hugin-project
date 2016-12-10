package bbc.wsinteg.hugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by cablej01 on 06/12/2016.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "NotificationBroadcastReceiver";

    public NotificationBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra("gcm.notification.title")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                NewsDatabase ndb = new NewsDatabase(context);
                ndb.addItem(
                        extras.getString("google.message_id"),
                        extras.getString("gcm.notification.title"),
                        extras.getString("gcm.notification.body")
                );
            }
        }
    }
}
