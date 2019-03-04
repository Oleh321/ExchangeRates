package balychev.oleg.blch.exchangerates.viewmodel;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import balychev.oleg.blch.exchangerates.MyApp;
import balychev.oleg.blch.exchangerates.activity.CurrencyBarChartActivity;
import balychev.oleg.blch.exchangerates.model.server.ExchangeRate;
import balychev.oleg.blch.exchangerates.utils.DataWorker;
import balychev.oleg.blch.exchangerates.utils.DateFormatter;

public class CurrencyBarChartViewModel {

    private BarChart barChart;
    private Button fromCalendarButton;
    private Button toCalendarButton;

    private ProgressDialog dialog;

    private Calendar fromCalendar;
    private Calendar toCalendar;

    private DataWorker worker;

    private String currentValcode;

    private static final String INTENT_VALCODE_CURRENCY = "intent valcode currency";
    private static final String SAVED_DATE_FROM = "saved date from";
    private static final String SAVED_DATE_TO = "saved date to";

    public CurrencyBarChartViewModel(Bundle savedData,
                                     Intent intent,
                                     BarChart barChart,
                                     Button fromCalendarButton,
                                     Button toCalendarButton) {
        this.barChart = barChart;
        this.fromCalendarButton = fromCalendarButton;
        this.toCalendarButton = toCalendarButton;

        dialog = new ProgressDialog(barChart.getContext());
        dialog.setCancelable(false);
        dialog.setTitle("Loading..");

        toCalendar = Calendar.getInstance();
        fromCalendar = Calendar.getInstance();

        if(savedData == null){
            fromCalendar.add(Calendar.DAY_OF_YEAR, -7);
        } else {
            toCalendar.setTimeInMillis(savedData.getLong(SAVED_DATE_TO));
            fromCalendar.setTimeInMillis(savedData.getLong(SAVED_DATE_FROM));
        }

        fromCalendarButton.setText(DateFormatter.getCalendarIn_ddMMyyyy_Format(fromCalendar));
        toCalendarButton.setText(DateFormatter.getCalendarIn_ddMMyyyy_Format(toCalendar));

        fromCalendarButton.setOnClickListener(v->showFromDialog());
        toCalendarButton.setOnClickListener(v->showToDialog());

        worker = MyApp.getInstance().getWorker();
        currentValcode = intent.getStringExtra(INTENT_VALCODE_CURRENCY);

        initBarChart();
        loadData();
    }

    public void savedState(Bundle bundle){
        bundle.putLong(SAVED_DATE_FROM, fromCalendar.getTimeInMillis());
        bundle.putLong(SAVED_DATE_TO, toCalendar.getTimeInMillis());
    }

    @SuppressLint("StaticFieldLeak")
    public void loadData() {
        new AsyncTask<Void, Void, ArrayList<ExchangeRate>>() {
            int count;
            long offsetInMiles;

            @Override
            protected void onPreExecute() {
                dialog.show();
                super.onPreExecute();
                long difference = toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis();
                int differenceDates = (int) (difference / (24 * 60 * 60 * 1000));

                if (differenceDates < 9) {
                    count = differenceDates;
                } else {
                    count = 9;
                }
                offsetInMiles = difference/count;
            }

            @Override
            protected ArrayList<ExchangeRate> doInBackground(Void... voids) {
                ArrayList<ExchangeRate> list = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(fromCalendar.getTimeInMillis());
                for (int i = 0; i < count+1; i++) {
                    ExchangeRate er = worker.loadSingleExchangeRate(calendar, currentValcode);
                    if(er!=null) list.add(er);
                    if (offsetInMiles > Integer.MAX_VALUE){
                        calendar.add(Calendar.DAY_OF_YEAR, (int)(offsetInMiles/(24 * 60 * 60 * 1000)));
                    } else {
                        calendar.add(Calendar.MILLISECOND, (int)(offsetInMiles));
                    }

                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<ExchangeRate> rates) {
                super.onPostExecute(rates);
                dialog.dismiss();
                if(rates == null || rates.size() < 7){
                    Toast.makeText(barChart.getContext(), "Lack of data", Toast.LENGTH_LONG).show();
                    return;
                }
                loadBarChart(rates);
            }
        }.execute();

    }

    public void initBarChart(){
        barChart.setDrawBarShadow(false);
     //   barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
       // barChart.setFitBars(false);
        barChart.setSelected(false);
    }

    public void loadBarChart(ArrayList<ExchangeRate> rates){
        List<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < rates.size(); i++){
            entries.add(new BarEntry(i, (float) rates.get(i  ).getRate()));
        }
        BarDataSet set = new BarDataSet(entries, rates.get(0).getName());
        BarData data = new BarData(set);

        barChart.setData(data);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter((value, axis) -> {

            if (rates.size() <= (int)value) return "";
            if(rates.size()<=8){
                return ((int)value)%2 == 0? rates.get((int)value).getDate(): "" ;
            }
            return rates.get((int)value).getDate();
        });
    }

    public void showFromDialog(){
        createDialog(true, fromCalendar, toCalendar, fromCalendarButton);
    }

    public void showToDialog() {
        createDialog(false, toCalendar, fromCalendar, toCalendarButton);
    }

    // true - from, false - to
    private void createDialog(boolean from, Calendar calendar, Calendar anotherCalendar, Button button) {
        DatePickerDialog tpd = new DatePickerDialog(barChart.getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    button.setText(DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar));
                    loadData();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar minCalendar = new GregorianCalendar(2000, 0, 1);
        Calendar maxCalendar = new GregorianCalendar();
        maxCalendar.add(Calendar.DAY_OF_YEAR, 1);

        if (from) {
            maxCalendar.setTimeInMillis(anotherCalendar.getTimeInMillis());
            maxCalendar.add(Calendar.DAY_OF_YEAR, -6);
            tpd.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
            tpd.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        } else {
            minCalendar.setTimeInMillis(anotherCalendar.getTimeInMillis());
            minCalendar.add(Calendar.DAY_OF_YEAR, 7);
            tpd.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
            tpd.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
        }
        tpd.show();
    }

    public static Intent getIntent(String valcode, Context context){
        Intent intent = new Intent(context, CurrencyBarChartActivity.class);
        intent.putExtra(INTENT_VALCODE_CURRENCY, valcode);
        return intent;
    }

    public void unbind() {
        barChart = null;
        fromCalendarButton = null;
        toCalendarButton = null;
    }
}
