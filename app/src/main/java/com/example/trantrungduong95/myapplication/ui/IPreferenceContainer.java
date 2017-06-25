package com.example.trantrungduong95.myapplication.ui;
import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
/**
 * Created by ngomi_000 on 6/15/2017.
 */

public interface IPreferenceContainer {

    /**
     * Get a {@link Preference}.
     *
     * @param key key
     * @return {@link Preference}
     */
    Preference findPreference(CharSequence key);

    /**
     * Get {@link Context}.
     *
     * @return {@link Context}
     */
    Context getContext();

    /**
     * Get {@link Activity}.
     *
     * @return {@link Activity}
     */
    Activity getActivity();
}
