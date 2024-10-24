package com.example.demo.common.httpclient.holiday.exchanger;

import com.example.demo.common.httpclient.holiday.model.HolidayResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

@HttpExchange("https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
public interface HolidayExchanger {

    @GetExchange(accept = MediaType.APPLICATION_XML_VALUE)
    HolidayResponse get(@RequestParam Map<String, Object> params);
}
