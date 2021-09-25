package com.portfoliowatch.model.nasdaq;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LabelValue {

    @SerializedName("Label")
    private String label;

    @SerializedName("Value")
    private String value;
}
