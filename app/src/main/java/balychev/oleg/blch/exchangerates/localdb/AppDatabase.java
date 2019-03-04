package balychev.oleg.blch.exchangerates.localdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import balychev.oleg.blch.exchangerates.model.database.DatabaseCurrency;
import balychev.oleg.blch.exchangerates.model.database.DatabaseExchangeRate;

@Database(entities = {DatabaseCurrency.class, DatabaseExchangeRate.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
        public abstract ExchangeRateDao exchangeRateDao();
}
