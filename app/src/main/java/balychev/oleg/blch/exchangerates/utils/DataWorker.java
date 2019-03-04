package balychev.oleg.blch.exchangerates.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.room.Room;
import balychev.oleg.blch.exchangerates.MyApp;
import balychev.oleg.blch.exchangerates.localdb.AppDatabase;
import balychev.oleg.blch.exchangerates.localdb.ExchangeRateDao;
import balychev.oleg.blch.exchangerates.model.database.DatabaseCurrency;
import balychev.oleg.blch.exchangerates.model.database.DatabaseExchangeRate;
import balychev.oleg.blch.exchangerates.model.server.ExchangeRate;
import balychev.oleg.blch.exchangerates.network.ExchangeRateClient;
import balychev.oleg.blch.exchangerates.network.ExchangeRateService;

public class DataWorker {

    private ExchangeRateService service;
    private ExchangeRateDao dao;

    public DataWorker(Application appContext) {
        service = ExchangeRateClient.getClient().create(ExchangeRateService.class);
        dao = Room.databaseBuilder(appContext, AppDatabase.class, "exchange_rate_db")
                .build().exchangeRateDao();
    }

    public ExchangeRate loadSingleExchangeRate(Calendar calendar, String valcode){
        if (checkInDatabase(calendar, valcode)) {
            return loadSingleFromLocalDatabase(calendar, valcode);
        } else {
            return loadSingleFromServer(calendar, valcode);
        }
    }

    public ArrayList<ExchangeRate> forceLoadListFromServerFirst(Calendar calendar){
        ArrayList<ExchangeRate> rates = loadListFromServer(calendar);
        if (rates != null && rates.size() != 0){
            return rates;
        } else if(checkInDatabase(calendar)){
            return loadListFromLocalDatabase(calendar);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<ExchangeRate> loadListExchangeRates(Calendar calendar){
        if (checkInDatabase(calendar)) {
            return loadListFromLocalDatabase(calendar);
        } else {
            return loadListFromServer(calendar);
        }
    }

    private boolean checkInDatabase(Calendar calendar) {
        return (dao.getCurrencies().size() != 0
                && dao.getExchangeRates(DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar)).size() != 0);
    }

    private boolean checkInDatabase(Calendar calendar, String valcode) {
        return (dao.getCurrencies().size() != 0
                && dao.getExchangeRateByValcodeAndDate(DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar), valcode) != null);
    }

    private ArrayList<ExchangeRate> loadListFromServer(final Calendar calendar) {
        try {
            ArrayList<ExchangeRate> rates = service.getListExchangeRateByDate(
                    DateFormatter.getCalendarIn_yyyyMMdd_Format(calendar)).execute().body();
            saveDataToLocalDatabase(rates, calendar);
            return rates;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<ExchangeRate> loadListFromLocalDatabase(Calendar calendar) {
        ArrayList<ExchangeRate> rates = new ArrayList<>();
        for (DatabaseExchangeRate der : dao.getExchangeRates(
                DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar))) {
            DatabaseCurrency dc = dao.getCurrencyById(der.getCurrencyId());
            rates.add(new ExchangeRate(dc.getId(),
                    dc.getNameCurrency(),
                    der.getRate(),
                    dc.getTextcode(),
                    der.getDateExchange()));
        }
        return rates;
    }

    private void saveDataToLocalDatabase(ArrayList<ExchangeRate> rates, Calendar calendar) {
        for (ExchangeRate rate : rates) {
            if (null == dao.getCurrencyById(rate.getId())) {
                dao.insertCurrency(new DatabaseCurrency(rate.getId(),
                        rate.getName(),
                        rate.getAbbreviation()));
            }
            if (null == dao.getExchangeRateByCurrencyAndDate(
                    DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar), rate.getId())){
                dao.insertExchangeRate(new DatabaseExchangeRate(0L,
                        rate.getRate(),
                        rate.getDate(),
                        rate.getId()));
            }
        }
    }

    private ExchangeRate loadSingleFromServer(Calendar calendar, String valcode) {
        try {
            ArrayList<ExchangeRate> rates = service.getExchangeRate(valcode,
                    DateFormatter.getCalendarIn_yyyyMMdd_Format(calendar)).execute().body();
            if (rates != null && rates.size() != 0){
               saveDataToLocalDatabase(rates, calendar);
                return rates.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExchangeRate loadSingleFromLocalDatabase(Calendar calendar, String valcode) {  ;
        DatabaseExchangeRate der = dao.getExchangeRateByValcodeAndDate(
                DateFormatter.getCalendarIn_ddMMyyyy_Format(calendar), valcode);
        DatabaseCurrency dc;
        if (der == null ||
                (dc = dao.getCurrencyById(der.getCurrencyId())) == null)
            return null;
        return new ExchangeRate(
                dc.getId(),
                dc.getNameCurrency(),
                der.getRate(),
                dc.getTextcode(),
                der.getDateExchange());
    }

}
