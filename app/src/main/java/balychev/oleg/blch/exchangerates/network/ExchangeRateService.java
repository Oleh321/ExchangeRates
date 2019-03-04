package balychev.oleg.blch.exchangerates.network;

import java.util.ArrayList;


import balychev.oleg.blch.exchangerates.model.server.ExchangeRate;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeRateService {

    @GET("exchange?json")
    Call<ArrayList<ExchangeRate>> getListExchangeRateByDate(@Query("date") String date);

    @GET("exchange?json")
    Call<ArrayList<ExchangeRate>> getExchangeRate(@Query("valcode") String valcode, @Query("date") String date);

}
