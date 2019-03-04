package balychev.oleg.blch.exchangerates.model.server;

import com.google.gson.annotations.SerializedName;

public class ExchangeRate {
    @SerializedName("r030")
    private int id;
    @SerializedName("txt")
    private String name;
    @SerializedName("rate")
    private double rate;
    @SerializedName("cc")
    private String abbreviation;
    @SerializedName("exchangedate")
    private String date;

    public ExchangeRate() {
    }

    public ExchangeRate(int id, String name, double rate, String abbreviation, String date) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.abbreviation = abbreviation;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                ", abbreviation='" + abbreviation + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getFormattedRate(){
        StringBuilder rateString = new StringBuilder(String.format("%1.8f", rate));
        while(rateString.substring(rateString.length()-1).equals("0"))
            rateString.deleteCharAt(rateString.length()-1);
        return rateString.toString();
    }

}
