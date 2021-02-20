package com.portfoliowatch.model;

import com.portfoliowatch.util.PortfolioWatchEnums;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @Column(name = "position_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long positionId;

    @Column(name = "portfolio_id", nullable = false)
    @NotNull
    private Long portfolioId;

    @Column(nullable = false)
    private String ticker;

    @Column(name = "share_count")
    private Double shareCount;

    @Column(name = "cost_basis")
    private Double costBasis;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(hidden = true)
    private Date dateInserted;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(hidden = true)
    private Date dateUpdated;

}
