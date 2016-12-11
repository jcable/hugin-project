package bbc.wsinteg.hugin;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by cablej01 on 06/12/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    private static final String SENDER_ID = "854409559981";
    private int msgId = 0;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String,String> m = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + m);
            String id = remoteMessage.getMessageId();
            if(id == null)
                id = remoteMessage.getData().get("title");
            handleData(id, m);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String id = remoteMessage.getMessageId();
            if(id == null)
                id = remoteMessage.getNotification().getTitle();
            handleNotification(id, remoteMessage.getNotification());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private void handleNotification(String id, RemoteMessage.Notification message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", message.getTitle());
        intent.putExtra("body", message.getBody());
        NewsDatabase ndb = new NewsDatabase(getApplicationContext());
        ndb.addItem(id, message.getTitle(), message.getBody());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleData(String id, Map<String,String> m) {
        Context context = getApplicationContext();
        FileDecoder fd = FileDecoder.getInstance(context);
        String name = m.get("filename");
        int position = Integer.parseInt(m.get("part"));
        fd.addPart(name, position, m.get("body"));
        // TODO see if this is a resend or otherwise out of order part and now completes the file
        Uri contentUri = null;
        if(m.get("last").equals("1")) {
            fd.joinFiles(name, position + 1);
            Base64InputStream is;
            try {
                FileInputStream fis = context.openFileInput(name);
                is = new Base64InputStream(fis, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                return;
            }
            File folder = context.getExternalFilesDir("docs");
            boolean ok = folder.mkdir();
            File f = fd.unzip(is, folder);
            if (f != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(context, context.getPackageName()+ ".fileprovider", f);
                }
                else {
                    contentUri = Uri.fromFile(f);
                }
            }
        }
        if(contentUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(contentUri, "text/html");
            startActivity(intent);
        }
    }

    private void send(Map<String,String> data) {
        RemoteMessage.Builder msg = new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(++msgId));
        for(Map.Entry<String,String> e : data.entrySet()) {
            msg.addData(e.getKey(), e.getValue());
        }
        FirebaseMessaging.getInstance().send(msg.build());
    }
}
