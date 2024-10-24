package com.example.demo.common.httpclient.holiday.model;

import com.example.demo.common.httpclient.BooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;

public class HolidayResponse {
    private String resultCode;
    @JsonProperty("resultMsg")
    private String resultMessage;
    @JsonProperty("numOfRows")
    private Integer pageSize;
    private Integer pageNo;
    private Integer totalCount;
    @JsonProperty("locdate")
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate date;

    @JsonProperty("seq")
    private Integer sequence;

    private String dateKind;
    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean isHoliday;
    private String dateName;
}
