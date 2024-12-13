package com.portfoliowatch.model.nasdaq;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyStats {
  @SerializedName("Volume")
  private LabelValue volume;

  @SerializedName("ExpenseRatio")
  private LabelValue expenseRatio;
}
