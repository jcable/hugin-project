package bbc.wsinteg.hugin;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

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
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item value
        Cursor c = (Cursor)l.getItemAtPosition(position);

        content.setText(c.getString(1));

    }

}
