package balychev.oleg.blch.exchangerates.localdb;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import balychev.oleg.blch.exchangerates.model.database.DatabaseCurrency;
import balychev.oleg.blch.exchangerates.model.database.DatabaseExchangeRate;

@Dao
public interface ExchangeRateDao {

    @Insert
    long insertExchangeRate(DatabaseExchangeRate exchangeRate);

    @Insert
    void insertCurrency(DatabaseCurrency currency);

    @Query("SELECT * FROM currency")
    List<DatabaseCurrency> getCurrencies();

    @Query("SELECT * FROM exchange_rate WHERE date_exchange = :date")
    List<DatabaseExchangeRate> getExchangeRates(String date);

    @Query("SELECT * FROM currency WHERE id = :id")
    DatabaseCurrency getCurrencyById(int id);

    @Query("SELECT * FROM exchange_rate WHERE date_exchange = :date AND currency_id = :currencyId")
    DatabaseExchangeRate getExchangeRateByCurrencyAndDate(String date, int currencyId);


    @Query("SELECT * " +
            "FROM exchange_rate er INNER JOIN currency c " +
                "ON c.id = er.currency_id " +
            "WHERE date_exchange = :date AND c.textcode = :valcode")
    DatabaseExchangeRate getExchangeRateByValcodeAndDate(String date, String valcode);
}
