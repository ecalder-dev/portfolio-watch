package com.portfoliowatch.model.tdameritrade;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class TDAmeriToken {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Integer expiresIn;

    private String scope;

    @SerializedName("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;

    private String error;

    private Long accessTokenTimestamp;

    private Long refreshTokenTimestamp;
}
