package com.portfoliowatch.model.nasdaq;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CompanyProfile {

    @SerializedName("ModuleTitle")
    private LabelValue moduleTitle;

    @SerializedName("CompanyName")
    private LabelValue companyName;

    @SerializedName("Symbol")
    private LabelValue symbol;

    @SerializedName("Address")
    private LabelValue address;

    @SerializedName("Phone")
    private LabelValue phone;

    @SerializedName("Industry")
    private LabelValue industry;

    @SerializedName("Sector")
    private LabelValue sector;

    @SerializedName("Region")
    private LabelValue region;

    @SerializedName("CompanyDescription")
    private LabelValue companyDescription;

    @SerializedName("CompanyUrl")
    private LabelValue companyUrl;
}
