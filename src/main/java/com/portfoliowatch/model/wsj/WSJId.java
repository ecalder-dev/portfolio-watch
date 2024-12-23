package com.portfoliowatch.model.wsj;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WSJId {
  private String application;
  private List<WSJInstrument> instruments;
  private boolean expanded;
  private int refreshInterval;
  private String serverSideType;

  public void addInstrument(String symbol, String name) {
    if (instruments == null) {
      instruments = new ArrayList<>();
    }
    instruments.add(new WSJInstrument(symbol, name));
  }
}
