package com.example.quarx2k.icecreamprice;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by quarx2k on 05.05.15.
 */
public class MainActivity extends AppCompatActivity {
    private RealmResults<IceCream> mIceCreamList;
    private IceCreamAdapter iceCreamAdapter;
    private RecyclerView recyclerView;
    private Realm mRealm;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRealm = Realm.getDefaultInstance();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        loadList();
        setAdapter();
    }

    private void loadList() {
        mIceCreamList = mRealm.where(IceCream.class).findAll();
        if (mIceCreamList.isEmpty()) {
            mRealm.executeTransaction(inRealm -> {
                InputStream inputStream = getResources().openRawResource(R.raw.items);
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputReader);
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] split = line.split(",");
                        IceCream iceCream = new IceCream();
                        for(int i = 0; i < split.length; i++) {
                            if (i==0) {
                                iceCream.setName(split[i]);
                            } else if (i==1) {
                                iceCream.setPrice(Float.parseFloat(split[i]));
                            }
                        }
                        inRealm.copyToRealm(iceCream);
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                IceCream iceCream = new IceCream();
                iceCream.setPrice(1F);
                iceCream.setName("Брак");
                inRealm.copyToRealm(iceCream);
            });
        }

    }

    private void setAdapter() {
        iceCreamAdapter = new IceCreamAdapter(this, mIceCreamList, mRealm);
        recyclerView.setAdapter(iceCreamAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateFinalValue() {
        mToolbar.setTitle(String.format(Locale.ENGLISH,
                "ИТОГО: %s РУБ.", mRealm.where(IceCream.class)
                        .sum("sum")));
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
                mRealm.executeTransaction(inRealm -> inRealm.delete(IceCream.class));
                loadList();
                hiddeKeyboard(this, getCurrentFocus());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hiddeKeyboard(Context context, View v) {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
