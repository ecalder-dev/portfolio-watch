package com.portfoliowatch.model.wsj;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WSJData {
  private List<WSJInstrument> instruments;
}
