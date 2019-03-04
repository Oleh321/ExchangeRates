package balychev.oleg.blch.exchangerates.model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "exchange_rate",
        foreignKeys =   @ForeignKey(entity = DatabaseCurrency.class,
                                    parentColumns = "id",
                                    childColumns = "currency_id",
                                    onDelete = CASCADE))

public class DatabaseExchangeRate {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Double rate;
    @ColumnInfo(name = "date_exchange")
    private String dateExchange;
    @ColumnInfo(name = "currency_id")
    private int currencyId;

    public DatabaseExchangeRate() {
    }

    public DatabaseExchangeRate(Double rate, String dateExchange, int currencyId) {
        this.rate = rate;
        this.dateExchange = dateExchange;
        this.currencyId = currencyId;
    }

    public DatabaseExchangeRate(long id, Double rate, String dateExchange, int currencyId) {
        this.id = id;
        this.rate = rate;
        this.dateExchange = dateExchange;
        this.currencyId = currencyId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDateExchange() {
        return dateExchange;
    }

    public void setDateExchange(String dateExchange) {
        this.dateExchange = dateExchange;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String toString() {
        return "DatabaseExchangeRate{" +
                "id=" + id +
                ", rate=" + rate +
                ", dateExchange='" + dateExchange + '\'' +
                ", currencyId=" + currencyId +
                '}';
    }
}
