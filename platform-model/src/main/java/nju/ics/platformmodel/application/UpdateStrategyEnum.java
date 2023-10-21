package nju.ics.platformmodel.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UpdateStrategyEnum {
    @JsonProperty("0")
    DEFAULT,

    @JsonProperty("1")
    ROLLING,
}
