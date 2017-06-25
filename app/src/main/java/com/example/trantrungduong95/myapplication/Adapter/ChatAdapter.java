package com.example.trantrungduong95.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.trantrungduong95.myapplication.R;
import com.example.trantrungduong95.myapplication.model.ContactItem;

import java.util.ArrayList;
/**
 * Created by ngomi_000 on 5/4/2017.
 */

public class ChatAdapter extends ArrayAdapter<ContactItem>{
    // List context
    private final Context context;
    // List values
    private final ArrayList<ContactItem> smsList;

    public ChatAdapter(Context context, ArrayList<ContactItem> smsList) {
        super(context, R.layout.custom_chat, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.custom_chat, parent, false);

        TextView bodyphone = (TextView) rowView.findViewById(R.id.txtcustomChat);
        String bp = smsList.get(position).getBody();
        bodyphone.setText(bp);
        TextView datephone = (TextView) rowView.findViewById(R.id.txtcustomTime);
        String dp = smsList.get(position).getDate();
        //datephone.setText(dp.substring(11,16));
        datephone.setText(dp);

        TextView chatseen = (TextView) rowView.findViewById(R.id.txtChatSeen);
/*        int cs = smsList.get(position).getRead();
        if(cs.equals("inbox")) {
            chatseen.setText("Da xem");
        } else chatseen.setText("Dang gởi đi");*/

        return rowView;
    }
}
