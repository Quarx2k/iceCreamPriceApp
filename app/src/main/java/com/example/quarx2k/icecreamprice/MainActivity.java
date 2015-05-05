package com.example.quarx2k.icecreamprice;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by quarx2k on 05.05.15.
 */
public class MainActivity extends ActionBarActivity {
    private static ArrayList<IceCream> iceCreamList;
    private static ArrayList<IceCream> iceCreamListSaved;
    private static IceCreamAdapter iceCreamAdapter;
    private static ListView listView;
    private static TextView finalVlaue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        listView = (ListView) findViewById(R.id.listView);
        finalVlaue = (TextView) footer.findViewById(R.id.finalValue);
        listView.addFooterView(footer);
        if (iceCreamList == null) {
            loadList();
        }
    }

    private void loadList() {
        iceCreamList = new ArrayList<>();
        iceCreamAdapter = new IceCreamAdapter(getApplicationContext(), iceCreamList);

        InputStream inputStream = getResources().openRawResource(R.raw.items);
        InputStreamReader inputreader = new InputStreamReader(inputStream);

        BufferedReader br = new BufferedReader(inputreader);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                IceCream iceCream = new IceCream();
                for(int i = 0; i < split.length; i++) {
                    if (i==0) {
                        iceCream.setName(split[i]);
                    } else if (i==1) {
                        iceCream.setPrice(split[i]);
                    }
                }
                iceCreamList.add(iceCream);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView.setAdapter(iceCreamAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        iceCreamListSaved = iceCreamList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iceCreamListSaved != null) {
            iceCreamList = iceCreamListSaved;
            IceCreamAdapter iceCreamAdapter = new IceCreamAdapter(getApplicationContext(), iceCreamList);
            listView.setAdapter(iceCreamAdapter);
            iceCreamAdapter.notifyDataSetChanged();
            hiddeKeyboard(this, getCurrentFocus());
        }
    }

    public static void updateFinalValue() {
        Double value = 0.00;
        for (IceCream cream : iceCreamList) {
            if (cream.getSumm() != null) {
                if (cream.getSumm().length() > 0)
                    value = value + Double.parseDouble(cream.getSumm());
                else
                    continue;
            }
        }
        finalVlaue.setText("Итого: " + String.valueOf(value) + " руб.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clean:
                loadList();
                hiddeKeyboard(this, getCurrentFocus());
                finalVlaue.setText("Итого: " + "0.00" + " руб.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hiddeKeyboard(Context context, View v) {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
