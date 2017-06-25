package com.example.trantrungduong95.myapplication.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.trantrungduong95.myapplication.Adapter.ContactListAdapter;
import com.example.trantrungduong95.myapplication.MainActivity;
import com.example.trantrungduong95.myapplication.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ngomi_000 on 5/30/2017.
 */

public class Fragment_Compose extends android.support.v4.app.Fragment{
    ImageView icon;
    EditText edtreply;
    AnimationDrawable frameAnimation;
    LinearLayout attachment_panel;
    ImageView imageView;
    MultiAutoCompleteTextView multiAutoCompleteTextView;
    boolean flag = false;
    public static boolean flag2 = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose, container, false);

        addControls(view);
        addEvents();
        return view;
        }

    private void addEvents() {
        //todo compose_recipients and grorp
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        inputContact();
        if (edtreply.getText().toString().equals("")){
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        //click newsend
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtreply.getText().toString().equals("") && !multiAutoCompleteTextView.getText().toString().equals("")) {
                    String phonenumber = multiAutoCompleteTextView.getText().toString();
                    String message = edtreply.getText().toString();

                    Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(phonenumber);
                    String phone = "";
                    while (m.find())
                    {
                        phone = m.group(1).replaceAll("-", "");
                        phone.replaceAll(" ", "");
                        phone.replace("+84", "");
                        //((MainActivity) getActivity()).SendSMS(phonenumber, message);
                    }
                    ((MainActivity) getActivity()).SendSMS(phonenumber, message);
                    flag2 = true;

                    //set item Main, check senew
                    MainActivity.item.setAddress(phonenumber);
                    MainActivity.item.setBody(message);
                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    MainActivity.item.setDate(mydate);

                    ((MainActivity) getActivity()).showFragmentConversation();
               }
                else Toast.makeText(getContext(), getString(R.string.notempty)+"", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addControls(View view) {
        icon = (ImageView) view.findViewById(R.id.compose_icon);
        edtreply = (EditText) view.findViewById(R.id.compose_reply_text);
        attachment_panel = (LinearLayout) view.findViewById(R.id.attachment_panel);
        imageView = (ImageView) view.findViewById(R.id.send);
        multiAutoCompleteTextView = (MultiAutoCompleteTextView) view.findViewById(R.id.txtPhoneNo);
        ((MainActivity) getActivity()).setTitle(R.string.title_compose);
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
    private void inputContact(){
        String[] proj = { BaseColumns._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE, };
        Cursor peopleCursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, proj, null, null, null);
        ContactListAdapter contactadapter = new ContactListAdapter(getActivity(), peopleCursor);
        multiAutoCompleteTextView.setThreshold(1);
        multiAutoCompleteTextView.setAdapter(contactadapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
}
