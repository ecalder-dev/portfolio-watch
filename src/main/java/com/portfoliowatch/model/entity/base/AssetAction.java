package com.portfoliowatch.model.entity.base;

import java.util.Date;

public interface AssetAction extends Base {

    String getSymbol();

    void setSymbol(String symbol);

    Double getShares();

    void setShares(Double shares);

    Date getDateTransacted();

    void setDateTransacted(Date dateTransacted);

}
