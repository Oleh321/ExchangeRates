package balychev.oleg.blch.exchangerates.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import balychev.oleg.blch.exchangerates.R;
import balychev.oleg.blch.exchangerates.viewmodel.ExchangeRatesListViewModel;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class ExchangeRatesListActivity extends AppCompatActivity {

    private ExchangeRatesListViewModel viewModel;

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private Toolbar toolbar;
    private TextView emptyTextView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates_list);

        searchEditText = findViewById(R.id.act_erl_et_search);
        toolbar = findViewById(R.id.act_erl_toolbar);
        recyclerView = findViewById(R.id.act_erl_recycler_view);
        emptyTextView = findViewById(R.id.act_erl_tv_not_found);
        refreshLayout = findViewById(R.id.act_erl_swipe_refresh_layout);

        setSupportActionBar(toolbar);

        viewModel = new ExchangeRatesListViewModel(
                savedInstanceState, recyclerView, searchEditText, toolbar, emptyTextView, refreshLayout);


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.saveStates(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exchange_rate_list_menu, menu);
        menu.findItem(R.id.menu_erl_action_calendar);
        menu.findItem(R.id.menu_erl_action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_erl_action_calendar:
                viewModel.createDialog();
                return true;
            case R.id.menu_erl_action_search:
                viewModel.changeVisibilitySearchField();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.manageSearchField();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.unbind();
    }

}
