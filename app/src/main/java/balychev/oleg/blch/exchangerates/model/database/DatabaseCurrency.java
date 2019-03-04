package balychev.oleg.blch.exchangerates.model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import androidx.room.PrimaryKey;

@Entity(tableName = "currency")
public class DatabaseCurrency {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "name_currency")
    private String nameCurrency;
    private String textcode;

    public DatabaseCurrency(int id, String nameCurrency, String textcode) {
        this.id = id;
        this.nameCurrency = nameCurrency;
        this.textcode = textcode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCurrency() {
        return nameCurrency;
    }

    public void setNameCurrency(String nameCurrency) {
        this.nameCurrency = nameCurrency;
    }

    public String getTextcode() {
        return textcode;
    }

    public void setTextcode(String textcode) {
        this.textcode = textcode;
    }

    @Override
    public String toString() {
        return "DatabaseCurrency{" +
                "id=" + id +
                ", nameCurrency='" + nameCurrency + '\'' +
                ", textcode='" + textcode + '\'' +
                '}';
    }
}
