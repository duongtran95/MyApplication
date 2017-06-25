package com.example.trantrungduong95.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.trantrungduong95.myapplication.Adapter.CustomListViewAdapter;
import com.example.trantrungduong95.myapplication.Fab.FloatingActionButton;
import com.example.trantrungduong95.myapplication.MainActivity;

import com.example.trantrungduong95.myapplication.PreferencesActivity;
import com.example.trantrungduong95.myapplication.R;
import com.example.trantrungduong95.myapplication.model.ContactItem;
import com.example.trantrungduong95.myapplication.ui.BounceListView;

import java.util.ArrayList;

/**
 * Created by ngomi_000 on 5/30/2017.
 */

public class Fragment_Conversations extends Fragment {
    FloatingActionButton floatingActionButton;
    View view;
    BounceListView bounceListView;
    public static ArrayList<ContactItem> contactItems_conversations;
    public CustomListViewAdapter adapter;

    private String[] longItemClickDialog = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_conversations, container, false);
        setHasOptionsMenu(true);
        addControls(view);

        addEvents();

/*        getActivity().setTheme(PreferencesActivity.getTheme(getContext()));
        Utils.setLocale(getContext());*/

        return view;
    }

    private void addEvents() {

        if (!MainActivity.flagClearList) {
            contactItems_conversations.addAll(((MainActivity) getActivity()).ReadInbox());
            removeDuplicates(contactItems_conversations);
            adapter = new CustomListViewAdapter(getActivity(), contactItems_conversations);
            bounceListView.setAdapter(adapter);
        }
        else {
            MainActivity.contactItems.clear();
            contactItems_conversations.clear();
            contactItems_conversations.addAll(((MainActivity) getActivity()).ReadInbox());
            removeDuplicates(contactItems_conversations);
            adapter = new CustomListViewAdapter(getActivity(), contactItems_conversations);
            bounceListView.setAdapter(adapter);
        }


        bounceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.adress = contactItems_conversations.get(position).getAddress();
                ((MainActivity) getActivity()).showFragmentConversation();
            }
        });
        bounceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage(" Are you sure you want to delete this message?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String body = contactItems_conversations.get(position).getBody();
                        String number = contactItems_conversations.get(position).getAddress();
                        int messageId = Integer.parseInt(contactItems_conversations.get(position).getThreadId());
                        //deleteSMS1(getActivity(),messageId);
                        Uri uriSms = Uri.parse("content://sms/");
                        deleteSMS(getActivity(), body, number);
                        contactItems_conversations.remove( contactItems_conversations.get(position));
                        adapter.notifyDataSetChanged();
                    }


                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });

        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ((MainActivity) getActivity()).showFragmentCompose();
                                                        floatingActionButton.setVisibility(View.VISIBLE);
                                                    }
                                                }
        );

    }
    public void addControls(View view) {
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button_floating_action);
        bounceListView = (BounceListView) view.findViewById(R.id.conversations_list);
        contactItems_conversations = new ArrayList<>();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: // start settings activity
                    startActivity(new Intent(getActivity(), PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void removeDuplicates(ArrayList<ContactItem> list) {
        for (int i=0;i<list.size();i++){
            for (int j=i+1;j<list.size()-1;j++)
            {
                if (list.get(i).getAddress().equals(list.get(j).getAddress()))
                {
                    list.remove(j);
                    j--;
                }
            }
        }
        for (int a = 0;a<list.size()-1;a++) {
            if (list.get(a).getAddress().equals(list.get(list.size() - 1).getAddress())) {
                list.remove(list.get(list.size() - 1));
            }
        }
    }

    //Todo deleteSMS
    public void deleteSMS(Context context, String message, String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[]{"_id", "thread_id", "address", "person",
                            "date", "body"}, "read=0", null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    if (message.equals(body) && address.equals(number)) {
                        int ret=context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), "date=?",
                                new String[]{c.getString(4)});

                        Log.e("log>>>", "Delete success.........");
                    }
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            Log.e("log>>>", e.toString());
        }
    }
}
