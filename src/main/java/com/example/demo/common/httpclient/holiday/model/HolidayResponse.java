package com.example.demo.common.httpclient.holiday.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class HolidayResponse {
    @XmlElement(name = "header")
    private Header header;

    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    @Setter
    public static class Header {
        @XmlElement(name = "resultCode")
        private String resultCode;
        @XmlElement(name = "resultMsg")
        private String resultMsg;
    }

    @XmlElement(name = "body")
    private Body body;

    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    @Setter
    public static class Body {
        @XmlElement(name = "items")
        private Items items;

        @XmlAccessorType(XmlAccessType.FIELD)
        @Getter
        @Setter
        public static class Items {
            @XmlElement(name = "item")
            private List<Item> item;

            @XmlAccessorType(XmlAccessType.FIELD)
            @Getter
            @Setter
            public static class Item {
                @XmlElement(name = "dateKind")
                private String dateKind;

                @XmlElement(name = "dateName")
                private String dateName;

                @XmlElement(name = "isHoliday")
                private String isHoliday;

                @XmlElement(name = "locdate")
                private String locdate;

                @XmlElement(name = "seq")
                private int seq;
            }
        }

        @XmlElement(name = "numOfRows")
        private int numOfRows;

        @XmlElement(name = "pageNo")
        private int pageNo;

        @XmlElement(name = "totalCount")
        private int totalCount;

    }
}




