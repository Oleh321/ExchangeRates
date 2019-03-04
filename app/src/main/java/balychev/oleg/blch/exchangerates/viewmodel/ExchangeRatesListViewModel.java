package balychev.oleg.blch.exchangerates.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import balychev.oleg.blch.exchangerates.MyApp;
import balychev.oleg.blch.exchangerates.activity.CurrencyBarChartActivity;
import balychev.oleg.blch.exchangerates.adapter.RatesAdapter;
import balychev.oleg.blch.exchangerates.model.server.ExchangeRate;
import balychev.oleg.blch.exchangerates.utils.DataWorker;
import balychev.oleg.blch.exchangerates.utils.DateFormatter;
import balychev.oleg.blch.exchangerates.utils.MyUtils;

public class ExchangeRatesListViewModel implements RatesAdapter.OnItemClick, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<ExchangeRate> allExchangeRates;

    private DataWorker worker;
    private RatesAdapter adapter;

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private Toolbar toolbar;
    private TextView emptyTextView;
    private SwipeRefreshLayout refreshLayout;

    private ProgressDialog progressDialog;

    private Calendar calendar;

    public final static String SAVED_SEARCH = "saved search";
    public final static String SAVED_CURRENT_DATE = "saved current date";

    public ExchangeRatesListViewModel(Bundle savedBundle,
                                      RecyclerView recyclerView,
                                      EditText searchEditText,
                                      Toolbar toolbar,
                                      TextView emptyTextView,
                                      SwipeRefreshLayout refreshLayout) {
        this.searchEditText = searchEditText;
        this.toolbar = toolbar;
        this.recyclerView = recyclerView;
        this.emptyTextView = emptyTextView;
        this.refreshLayout = refreshLayout;

        worker = MyApp.getInstance().getWorker();

        progressDialog = new ProgressDialog(recyclerView.getContext());
        progressDialog.setTitle("Loading...");

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new RatesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               search(s);
            }

            @Override
            public void afterTextChanged(Editable s) {    }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.RED);

        calendar = Calendar.getInstance();

        if(savedBundle != null){
            calendar.setTimeInMillis(savedBundle.getLong(SAVED_CURRENT_DATE));
            String search = savedBundle.getString(SAVED_SEARCH);
            searchEditText.setText(search);
            manageSearchField();
        }
        loadRecyclerView();
    }

    public void saveStates(Bundle outState) {
        outState.putString(SAVED_SEARCH, searchEditText.getText().toString().trim());
        outState.putLong(SAVED_CURRENT_DATE, calendar.getTimeInMillis());
    }

    private void search(CharSequence s) {
        if (allExchangeRates ==  null){
            allExchangeRates = new ArrayList<>();
        }
        sortByExchangeRateName(allExchangeRates);
        if(s.toString().trim().equals("")){
            adapter.setExchangeRates(allExchangeRates);
        } else {
            ArrayList<ExchangeRate> subList = new ArrayList<>();
            for (ExchangeRate rate : allExchangeRates) {
                if (rate.getName().toLowerCase().contains(s.toString().trim().toLowerCase())
                        || rate.getAbbreviation().contains(s.toString().trim().toUpperCase())) {
                    subList.add(rate);
                }
            }
            adapter.setExchangeRates(subList);
        }
        adapter.notifyDataSetChanged();
        showEmptyTextView(adapter.getExchangeRates().isEmpty());
    }

    private void showEmptyTextView(boolean empty){
        if (empty){
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void forceLoad(){
        new AsyncTask<Void, Void, ArrayList<ExchangeRate>>() {
            @Override
            protected ArrayList<ExchangeRate> doInBackground(Void... voids) {
                return worker.forceLoadListFromServerFirst(calendar);
            }

            @Override
            protected void onPostExecute(ArrayList<ExchangeRate> rates) {
                super.onPostExecute(rates);
                allExchangeRates = rates;
                refreshLayout.setRefreshing(false);
                search(searchEditText.getText().toString());
            }

        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    private void loadRecyclerView() {
        new AsyncTask<Void, Void, ArrayList<ExchangeRate>>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected ArrayList<ExchangeRate> doInBackground(Void... voids) {
               return worker.loadListExchangeRates(calendar);
            }

            @Override
            protected void onPostExecute(ArrayList<ExchangeRate> rates) {
                super.onPostExecute(rates);
                toolbar.setTitle(DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar));
                allExchangeRates = rates;
                search(searchEditText.getText().toString());
                progressDialog.dismiss();
            }
        }.execute();
    }

    public void changeVisibilitySearchField() {
        if(searchEditText.getVisibility() == View.VISIBLE){
            searchEditText.setText("");
            searchEditText.setVisibility(View.GONE);
            MyUtils.hideKeybord((Activity) recyclerView.getContext());
            showEmptyTextView(allExchangeRates ==  null || allExchangeRates.isEmpty());
        } else {
            searchEditText.setVisibility(View.VISIBLE);
        }
    }

    public void manageSearchField(){
        searchEditText.setVisibility("".equals(searchEditText.getText().toString().trim())? View.GONE : View.VISIBLE);
    }

    public void sortByExchangeRateName(ArrayList<ExchangeRate> rates){
        Collections.sort(rates, (t0, t1) -> t0.getRate() > t1.getRate()? -1 : 1);
    }

    public void unbind() {
        recyclerView = null;
        searchEditText = null;
        toolbar = null;
        emptyTextView = null;
        progressDialog = null;
        refreshLayout = null;
    }

    public void createDialog() {
        DatePickerDialog tpd = new DatePickerDialog(recyclerView.getContext(), myCallBack,  calendar.get(Calendar.YEAR),
                 calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.add(Calendar.DAY_OF_MONTH, 1);
        Calendar calendarMin = new GregorianCalendar(2000,0, 1);
        tpd.getDatePicker().setMinDate(calendarMin.getTimeInMillis());
        tpd.getDatePicker().setMaxDate(calendarMax.getTimeInMillis());
        tpd.show();
    }

    private DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
           calendar.set(year, monthOfYear, dayOfMonth);
           loadRecyclerView();
        }
    };

    @Override
    public void onItemClick(@NonNull ExchangeRate exchangeRate) {
        Intent intent = CurrencyBarChartViewModel.getIntent(exchangeRate.getAbbreviation(), recyclerView.getContext());
        recyclerView.getContext().startActivity(intent);
    }

    @Override
    public void onRefresh() {
        forceLoad();
    }
}