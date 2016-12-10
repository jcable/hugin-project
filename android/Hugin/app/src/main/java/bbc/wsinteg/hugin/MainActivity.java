package bbc.wsinteg.hugin;

import android.app.ListActivity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.InputStream;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static com.google.android.gms.internal.zzs.TAG;

public class MainActivity extends ListActivity {

    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (TextView)findViewById(R.id.output);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras == null) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
/*
        File f = new File(new File(getFilesDir(), "docs"), "thailand-38173269");
            File file = new File(new File(getFilesDir(), "docs"), "thailand-38173269.html");
            f.renameTo(file);
        boolean b = file.canRead();
        Uri contentUri = FileProvider.getUriForFile(this, "bbc.wsinteg.hugin.fileprovider", file);

        Intent internetIntent = new Intent(Intent.ACTION_VIEW);
        internetIntent.setDataAndType(contentUri, "text/html");
        internetIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(internetIntent);
        */
        }

        NewsDatabase ndb = new NewsDatabase(getApplicationContext());



        ClientCursorAdapter adapter = new ClientCursorAdapter(
                this, R.layout.list_item, ndb.getCursor(), 0 );

        // assign the list adapter
        setListAdapter(adapter);

        if(intent.hasExtra("google.message_id")) { // TODO select the item
            Cursor c = ndb.retrieve(intent.getStringExtra("google.message_id"));
            content.setText(c.getString(1));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item value
        Cursor c = (Cursor)l.getItemAtPosition(position);

        content.setText(c.getString(1));

    }

}
