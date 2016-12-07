package bbc.wsinteg.hugin;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * Created by cablej01 on 06/12/2016.
 */

public class ClientCursorAdapter  extends ResourceCursorAdapter {

    public ClientCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.row_title);
        title.setText(cursor.getString(cursor.getColumnIndex("title")));

        TextView body = (TextView) view.findViewById(R.id.row_body);
        body.setText(cursor.getString(cursor.getColumnIndex("body")));

    }
}
