package com.example.trantrungduong95.myapplication;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trantrungduong95.myapplication.Fragment.Fragment_Compose;
import com.example.trantrungduong95.myapplication.Fragment.Fragment_Conversation;

import com.example.trantrungduong95.myapplication.Receiver.Constants;
import com.example.trantrungduong95.myapplication.Receiver.SmsDeliveredReciever;
import com.example.trantrungduong95.myapplication.Receiver.SmsSentReciever;
import com.example.trantrungduong95.myapplication.model.ContactItem;
import com.example.trantrungduong95.myapplication.Adapter.MyPagerAdapter;
import com.example.trantrungduong95.myapplication.ui.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSearch;
    android.support.v7.app.ActionBar action;

    public static ArrayList<ContactItem> contactItems;
    public static String adress ="";
    public static ContactItem item;
    public static boolean flagClearList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferencesActivity.getTheme(this));
        Utils.setLocale(this);
        setContentView(R.layout.activity_main);
        //Todo default app

        initToolBar();
        initInstancesDrawer();
        addControls();
        addEvents();
    }

    private void addEvents() {
        contactItems =new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void addControls() {
        action = getSupportActionBar(); //get the actionbar
        if (action != null) {
            action.setIcon(R.mipmap.ic_launcher);
        }
        item = new ContactItem();
    }

    public void showFragmentCompose(){
        Fragment_Compose fragment_compose = new Fragment_Compose();
        android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager1.beginTransaction();
        fragmentTransaction.replace(R.id.frm_content,fragment_compose);
        fragmentTransaction.commit();
    }

    public void showFragmentConversation(){
        Fragment_Conversation fragment_conversation = new Fragment_Conversation();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frm_content,fragment_conversation);
        fragmentTransaction.commit();
    }

    public void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstancesDrawer() {
        TabLayout tabs = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.vpPager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),getApplicationContext());

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.GREEN,
                Color.RED,
                Color.BLACK,
                Color.BLUE
        };

        ColorStateList myList = new ColorStateList(states, colors);
        tabs.setTabTextColors(myList);
    }

    public void SendSMS(String phoneNumber, String message){
        //Todo send sms
        this.registerReceiver(new SmsSentReciever(), new IntentFilter(Constants.SENT));
        this.registerReceiver(new SmsDeliveredReciever(), new IntentFilter(Constants.DELIVERED));

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(Constants.SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(Constants.DELIVERED), 0);

        /*Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);*/

        SmsManager sms=SmsManager.getDefault();
        if (message.length()>160) {
            ArrayList<String> messageParts = sms.divideMessage(message);

            sms.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
        }
        else {
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            MainActivity.flagClearList = true;
        }
        //Toast.makeText(getApplicationContext(), "Message Sent successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_settings:
                return true;*/
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch(){

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSearch = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_cancel));

            isSearchOpened = true;
        }
    }
    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    private void doSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Todo Filter()
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public ArrayList<ContactItem> ReadInbox() {

        Uri uriSms = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(uriSms, null, null, null, null);

        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                ContactItem contactItem = new ContactItem();
                String id = c.getString(c.getColumnIndexOrThrow("_id"));
                contactItem.setThreadId(id);

                String phone = c.getString(c.getColumnIndexOrThrow("address"));
                contactItem.setAddress(phone);

                String body = c.getString(c.getColumnIndexOrThrow("body"));
                contactItem.setBody(body);

                String name = getContactName(phone);
                if (name !=null){
                    contactItem.setDisplayName(name);
                }

                Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
                contactItem.setPhotoUri(my_contact_Uri);

                String date = c.getString(c.getColumnIndexOrThrow("date"));
                long milliSeconds = Long.parseLong(date);
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                String finalDateString = formatter.format(calendar.getTime());
                contactItem.setDate(finalDateString);

                int read = c.getInt(c.getColumnIndexOrThrow("read"));
                contactItem.setRead(read);

                contactItems.add(contactItem);
                c.moveToNext();
            }
            c.close();
        }
        return contactItems;
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor=cr.query(uri, new String[]{ContactsContract.Contacts.DISPLAY_NAME},null,null,null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
    //todo delete SMS


}
