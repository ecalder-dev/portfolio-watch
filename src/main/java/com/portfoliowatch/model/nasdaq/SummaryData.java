package com.portfoliowatch.model.nasdaq;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SummaryData {
    @SerializedName("Exchange")
    private LabelValue exchange;
    @SerializedName("Sector")
    private LabelValue sector;
    @SerializedName("Industry")
    private LabelValue industry;
    @SerializedName("OneYrTarget")
    private LabelValue oneYrTarget;
    @SerializedName("AverageVolume")
    private LabelValue averageVolume;
    @SerializedName("PreviousClose")
    private LabelValue previousClose;
    @SerializedName("FiftTwoWeekHighLow")
    private LabelValue fiftyTwoWeekHighLow;
    @SerializedName("MarketCap")
    private LabelValue marketCap;
    @SerializedName("PERatio")
    private LabelValue peRatio;
    @SerializedName("ForwardPE1Yr")
    private LabelValue forwardPE1Yr;
    @SerializedName("EarningsPerShare")
    private LabelValue earningsPerShare;
    @SerializedName("TodayHighLow")
    private LabelValue todayHighLow;
    @SerializedName("ShareVolume")
    private LabelValue shareVolume;
    @SerializedName("FiftyDayAvgDailyVol")
    private LabelValue fiftyDayAvgDailyVol;
    @SerializedName("AnnualizedDividend")
    private LabelValue annualizedDividend;
    @SerializedName("ExDividendDate")
    private LabelValue exDividendDate;
    @SerializedName("DividendPaymentDate")
    private LabelValue dividendPaymentDate;
    @SerializedName("Yield")
    private LabelValue yield;
    @SerializedName("Alpha")
    private LabelValue alpha;
    @SerializedName("WeightedAlpha")
    private LabelValue weightedAlpha;
    @SerializedName("StandardDeviation")
    private LabelValue standardDeviation;
    @SerializedName("AvgDailyVol20Days")
    private LabelValue avgDailyVol20Days;
    @SerializedName("AvgDailyVol65Days")
    private LabelValue avgDailyVol65Days;
    @SerializedName("AUM")
    private LabelValue aum;
    @SerializedName("ExpenseRatio")
    private LabelValue expenseRatio;

}
