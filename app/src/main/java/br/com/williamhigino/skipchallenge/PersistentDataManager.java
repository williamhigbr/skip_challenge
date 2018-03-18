package br.com.williamhigino.skipchallenge;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 * Created by williamhigino on 18/03/2018.
 */

public class PersistentDataManager {

    public static final String CURRENT_CHART = "CURRENT_CHART";
    public static final String CURRENT_CUSTOMER = "CURRENT_CUSTOMER";

    private SharedPreferences preferences;
    private static PersistentDataManager instance;

    private PersistentDataManager(Context context)
    {
        preferences = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
    }

    public static PersistentDataManager getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new PersistentDataManager(context);
        }
        return instance;
    }

    public void SaveModel(Serializable model, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        if (model == null) {
            editor.remove(key);
        }
        else {
            String modelString = new Gson().toJson(model);
            editor.putString(key, modelString);
        }
        editor.apply();
    }

    public <T>T ReadModel(String key, Class<T> type)
    {
        String modelRawString = null;
        T model = null;
        if (preferences.contains(key))
        {
            modelRawString = preferences.getString(key, null);
            model = new Gson().fromJson(modelRawString, type);
        }
        return model;
    }

}
