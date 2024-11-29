package com.portfoliowatch.model.entity.base;

import java.util.Date;

public interface BaseEvent {

    Long getId();

    void setId(Long id);

    Date getDatetimeCreated();

    void setDatetimeCreated(Date datetimeCreated);

    Date getDatetimeUpdated();

    void setDatetimeUpdated(Date datetimeUpdated);

}
