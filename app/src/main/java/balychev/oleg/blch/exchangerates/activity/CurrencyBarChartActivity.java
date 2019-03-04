package balychev.oleg.blch.exchangerates.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import balychev.oleg.blch.exchangerates.R;
import balychev.oleg.blch.exchangerates.viewmodel.CurrencyBarChartViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

public class CurrencyBarChartActivity extends AppCompatActivity {

    private Button fromDateButton;
    private Button toDateButton;
    private BarChart barChart;

    private CurrencyBarChartViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_bar_chart);

        fromDateButton = findViewById(R.id.act_cur_barchart_btn_from);
        toDateButton = findViewById(R.id.act_cur_barchart_btn_to);
        barChart = findViewById(R.id.act_cur_barchart);

        viewModel = new CurrencyBarChartViewModel(
                savedInstanceState,
                getIntent(),
                barChart,
                fromDateButton,
                toDateButton);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.savedState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.unbind();
    }
}
