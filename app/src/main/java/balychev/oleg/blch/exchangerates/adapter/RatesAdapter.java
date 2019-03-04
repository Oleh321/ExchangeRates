package balychev.oleg.blch.exchangerates.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import balychev.oleg.blch.exchangerates.databinding.SingleRateLayoutBinding;
import balychev.oleg.blch.exchangerates.model.server.ExchangeRate;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.RatesHolder> {

    private final OnItemClick onItemClick;
    private ArrayList<ExchangeRate> exchangeRates;

    public RatesAdapter(ArrayList<ExchangeRate> list, OnItemClick onItemClick){
        exchangeRates = list;
        this.onItemClick = onItemClick;
    }

    public void setExchangeRates(ArrayList<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public ArrayList<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    private final View.OnClickListener mInternalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ExchangeRate rate = (ExchangeRate) view.getTag();
            onItemClick.onItemClick(rate);
        }
    };

    @NonNull
    @Override
    public RatesAdapter.RatesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SingleRateLayoutBinding binding = SingleRateLayoutBinding.inflate(inflater, parent,  false);
        return new RatesHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RatesAdapter.RatesHolder holder, int position) {
        ExchangeRate rate = exchangeRates.get(position);
        holder.binding.setExchangeRate(rate);
        holder.itemView.setTag(rate);
        holder.itemView.setOnClickListener(mInternalListener);
    }

    @Override
    public int getItemCount() {
        return exchangeRates.size();
    }


    public class RatesHolder extends RecyclerView.ViewHolder{

        private SingleRateLayoutBinding binding;

        public RatesHolder(@NonNull View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }

    public interface OnItemClick {

        void onItemClick(@NonNull ExchangeRate exchangeRate);

    }
}
