package com.example.trantrungduong95.myapplication.Adapter;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * Created by ngomi_000 on 6/8/2017.
 */

public class ContactListAdapter extends CursorAdapter implements
        Filterable {
    public Context con;

    public ContactListAdapter(Context context, Cursor c) {
        super(context, c);
        con = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(
                android.R.layout.simple_dropdown_item_1line, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int typeInt = cursor.getInt(3); // Phone.TYPE
        CharSequence type = ContactsContract.CommonDataKinds.Phone.getTypeLabel(con.getResources(), typeInt,
                null);
        ((TextView) view).setSingleLine(false);
        ((TextView) view).setText(cursor.getString(1) + "\n"
                + cursor.getString(2) + " " + type);
    }

    @Override
    public String convertToString(Cursor cursor) {
        return (cursor.getString(1) + "(" + cursor.getString(2) + ")");
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        ContentResolver cr = con.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                constraint.toString());
        String[] proj = { BaseColumns._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, };
        return cr.query(uri, proj, null, null, null);
    }
}
