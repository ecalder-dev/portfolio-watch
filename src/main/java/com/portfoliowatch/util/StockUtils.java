package com.portfoliowatch.util;

import org.apache.commons.lang3.math.NumberUtils;

public final class StockUtils {
  public static boolean isRatioValid(String ratio) {
    if (ratio == null) return false;
    String[] strSeg = ratio.split(":");
    if (strSeg.length != 2) return false;
    return NumberUtils.isCreatable(strSeg[0]) && NumberUtils.isCreatable(strSeg[1]);
  }
}
