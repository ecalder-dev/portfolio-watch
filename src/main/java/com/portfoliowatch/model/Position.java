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

    @Column(nullable = false)
    private String symbol;

    @Column(name = "shares")
    private Double shares;

    @Column(name = "cost_basis")
    private Double costBasis;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(hidden = true)
    private Date dateUpdated;

}