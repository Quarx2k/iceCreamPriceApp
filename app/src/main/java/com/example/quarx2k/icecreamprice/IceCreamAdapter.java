package com.example.quarx2k.icecreamprice;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by quarx2k on 05.05.15.
 */
public class IceCreamAdapter extends ArrayAdapter<IceCream> {
    private Context context;
    public IceCreamAdapter(Context context, ArrayList<IceCream> ice) {
        super(context, 0, ice);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final IceCream iceCream = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list, parent, false);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.summ = (TextView) convertView.findViewById(R.id.summ);
            viewHolder.stock = (EditTextBackEvent) convertView.findViewById(R.id.stock);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.price.setText(iceCream.getPrice());
        viewHolder.name.setText(iceCream.getName());
        viewHolder.stock.setText(iceCream.getStock());

        if (iceCream.getSumm() == null)
            viewHolder.summ.setText("0.00");
        else
            viewHolder.summ.setText(iceCream.getSumm());

        viewHolder.stock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String stockText = viewHolder.stock.getText().toString();
                String newPrice = viewHolder.price.getText().toString();
                iceCream.setStock(stockText);
                if (newPrice.length() > 0 && stockText.length() > 0) {
                    String summ = String.valueOf(Integer.parseInt(stockText) * Double.parseDouble(newPrice));
                    iceCream.setSumm(summ);
                    viewHolder.summ.setText(summ + " руб.");
                    MainActivity.updateFinalValue();
                } else {
                    viewHolder.summ.setText("0.00");
                    MainActivity.updateFinalValue();
                }
            }
        });


        viewHolder.stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewHolder.stock.clearFocus();
                    InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        return convertView;
    }
    private class ViewHolder {
        TextView name;
        TextView price;
        TextView summ;
        EditTextBackEvent stock;
    }
}