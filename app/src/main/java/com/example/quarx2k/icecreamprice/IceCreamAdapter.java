package com.example.quarx2k.icecreamprice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java9.util.Objects;
import java9.util.Optional;

/**
 * Created by quarx2k on 05.05.15.
 */
public class IceCreamAdapter extends RealmRecyclerViewAdapter<IceCream, IceCreamAdapter.ViewHolder> {
    private MainActivity context;
    private RealmResults<IceCream> iceCreamList;
    private Realm mRealm;
    IceCreamAdapter(Context context, RealmResults<IceCream> data, Realm realm) {
        super(data, true, true);
        this.context = (MainActivity) context;
        this.iceCreamList = data;
        this.mRealm = realm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View parentView = inflater.inflate(R.layout.custom_list, parent, false);
        return new ViewHolder(parentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IceCream iceCream = iceCreamList.get(position);
        holder.price.setText(iceCream.getPrice().toString());
        holder.name.setText(iceCream.getName());
        if (iceCream.getSum() == null) {
            holder.sum.setText("0.00");
        } else {
            holder.sum.setText(iceCream.getSum().toString());
        }
        holder.stock.addTextChangedListener(holder.textWatcher);
        holder.textWatcher.updatePosition(position);
        holder.stock.setText(Optional.ofNullable(iceCream.getStock()).map(Object::toString).orElse(""));
        holder.stock.setSelection(holder.stock.getText().length());
    }

    @Override
    public int getItemCount() {
        return iceCreamList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView sum;
        EditTextBackEvent stock;
        TextWatcherAdapter textWatcher;
        ViewHolder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            sum = itemView.findViewById(R.id.summ);
            stock = itemView.findViewById(R.id.stock);
            textWatcher = new TextWatcherAdapter();
        }
    }

    private class TextWatcherAdapter implements TextWatcher {
        private int itemIndex;

        public void updatePosition(int itemIndex) {
            this.itemIndex = itemIndex;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final IceCream iceCream = iceCreamList.get(itemIndex);
            final String newStock = editable.toString();
            final Integer pStock = iceCream.getStock();
            final Integer stock = newStock.isEmpty() ? null : Integer.valueOf(newStock);
            if (!Objects.equals(pStock, stock)) {
                mRealm.executeTransaction(inRealm -> {
                    iceCream.setStock(stock);
                    iceCream.setSum(stock == null ? 0F : stock * iceCream.getPrice());
                    context.updateFinalValue();
                });
                notifyItemChanged(itemIndex);
            } else {
                context.updateFinalValue();
            }
        }
    }

}