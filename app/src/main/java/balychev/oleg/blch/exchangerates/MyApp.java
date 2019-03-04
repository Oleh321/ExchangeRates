package balychev.oleg.blch.exchangerates;

import android.app.Activity;
import android.app.Application;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.room.Room;
import balychev.oleg.blch.exchangerates.activity.ExchangeRatesListActivity;
import balychev.oleg.blch.exchangerates.localdb.AppDatabase;
import balychev.oleg.blch.exchangerates.utils.DataWorker;

public class MyApp extends Application {

    private static MyApp instance;

    private DataWorker worker;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        worker = new DataWorker(this);
    }

    public static MyApp getInstance() {
        return instance;
    }

    public DataWorker getWorker() {
        return worker;
    }

}
