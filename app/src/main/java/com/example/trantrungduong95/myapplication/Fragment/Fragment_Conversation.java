package com.example.trantrungduong95.myapplication.Fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.trantrungduong95.myapplication.Adapter.ChatAdapter;
import com.example.trantrungduong95.myapplication.MainActivity;
import com.example.trantrungduong95.myapplication.PreferencesActivity;
import com.example.trantrungduong95.myapplication.R;
import com.example.trantrungduong95.myapplication.model.ContactItem;
import com.example.trantrungduong95.myapplication.ui.BounceListView;
import com.example.trantrungduong95.myapplication.ui.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ngomi_000 on 5/30/2017.
 */

public class Fragment_Conversation extends Fragment {
    ImageView icon;
    EditText edtreply;
    AnimationDrawable frameAnimation;
    LinearLayout attachment_panel;
    ImageView imageView;
    boolean flag = false;
    BounceListView bounceListView;
    String adress ="";
    ChatAdapter adp;
    ArrayList<ContactItem> contactItems_adress;
    /**
     * Minimum date.
     */
    public static final long MIN_DATE = 10000000000L;

    /**
     * Miliseconds per seconds.
     */
    private static final Calendar CAL_DAYAGO = Calendar.getInstance();

    static {
        // Get time for now - 24 hours
        CAL_DAYAGO.add(Calendar.DAY_OF_MONTH, -1);
    }
    public static final long MILLIS = 1000L;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        addControls(view);
        addEvents();
        return view;
    }

    private void addEvents() {
        showList();
        updateSendNew();
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        animationAttachment();
        onClickSend();
    }
    private void onClickSend(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber ="";
                if (!edtreply.getText().toString().equals("")) {
                    if (!adress.equals("")) {
                        phonenumber = adress;
                    }
                    else /*if (MainActivity.item == null)*/
                    {
                        phonenumber = contactItems_adress.get(0).getAddress();
                    }

                    String message = edtreply.getText().toString();
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()).substring(0,16);

                    ((MainActivity) getActivity()).SendSMS(phonenumber, message);
                    updateList(phonenumber,message,mydate);
                    edtreply.setText("");
                }
                else  Toast.makeText(getContext(), getString(R.string.notempty)+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animationAttachment() {
        if (edtreply.getText().toString().equals("")){
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("d131", "1111");
                    if (!flag) {
                        icon.setImageResource(R.drawable.arrow_to_plus);
                        frameAnimation = (AnimationDrawable) icon.getDrawable(); // nạp ảnh
                        frameAnimation.start();
                        attachment_panel.setVisibility(View.VISIBLE);
                        flag = true;
                    }
                    else
                    {
                        icon.setImageResource(R.drawable.plus_to_arrow);
                        frameAnimation = (AnimationDrawable) icon.getDrawable(); // nạp ảnh
                        frameAnimation.start();
                        attachment_panel.setVisibility(View.GONE);
                        flag = false;
                    }

                }
            });
        }
    }

    private void addControls(View view) {
        icon = (ImageView) view.findViewById(R.id.compose_icon);
        edtreply = (EditText) view.findViewById(R.id.compose_reply_text);
        attachment_panel = (LinearLayout) view.findViewById(R.id.attachment_panel);
        imageView = (ImageView) view.findViewById(R.id.send);
        bounceListView = (BounceListView) view.findViewById(R.id.conversation);
        contactItems_adress = new ArrayList<>();
        if (!MainActivity.adress.equals("")){
            adress =MainActivity.adress;
        }
        ((MainActivity) getActivity()).setTitle(R.string.title_conversation);
    }
    private void showList(){
        if (contactItems_adress.size()==0)
        {
            for (int i=MainActivity.contactItems.size()-1;i>=0;i--){
                if (MainActivity.contactItems.get(i).getAddress().equals(String.valueOf(adress))){
                    contactItems_adress.add((MainActivity.contactItems.get(i)));
                }
            }
            adp = new ChatAdapter(getActivity(), contactItems_adress);
            bounceListView.setAdapter(adp);
        }
    }
    private void updateSendNew(){
        if (MainActivity.item != null)
        {
            if (Fragment_Compose.flag2) {
                contactItems_adress.clear();
                contactItems_adress.add(MainActivity.item);

                adp = new ChatAdapter(getActivity(), contactItems_adress);
                bounceListView.setAdapter(adp);
                Fragment_Compose.flag2 = false;
                MainActivity.item = null;
            }
        }

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_search);
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getContext(),MainActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateList(String a, String b, String d){
        ContactItem contactItem = new ContactItem();
        contactItem.setAddress(a);
        contactItem.setBody(b);
        contactItem.setDate(d);
        contactItems_adress.add(contactItem);
        adp = new ChatAdapter(getActivity(), contactItems_adress);
        bounceListView.setAdapter(adp);
    }
    public static String getDate(final Context context, final long time) {
        long t = time;
        if (t < MIN_DATE) {
            t *= MILLIS;
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PreferencesActivity.PREFS_FULL_DATE, false)) {
            return DateFormat.getTimeFormat(context).format(t) + " "
                    + DateFormat.getDateFormat(context).format(t);
        } else if (t < CAL_DAYAGO.getTimeInMillis()) {
            return DateFormat.getDateFormat(context).format(t);
        } else {
            return DateFormat.getTimeFormat(context).format(t);
        }
    }
}
