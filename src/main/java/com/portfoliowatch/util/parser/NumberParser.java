package com.portfoliowatch.util.parser;

public final class NumberParser {

  private static final String charactersToRemove = "[$,%]";

  public static Double parseDouble(String str) {
    if (str == null) {
      return 0.0;
    } else {
      str = str.replaceAll(charactersToRemove, "");
      try {
        return Double.parseDouble(str);
      } catch (NumberFormatException e) {
        return 0.0;
      }
    }
  }

  public static Long parseLong(String str) {
    if (str == null) {
      return null;
    } else {
      str = str.replaceAll(charactersToRemove, "");
      try {
        return Long.parseLong(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
}
