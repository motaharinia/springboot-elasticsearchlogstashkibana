package com.motaharinia.elasticsarchlogstashkibana.config.log.model;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل اکسپشن
 */
@Data
public class ExceptionDto {

    /**
     *نام سامانه
     */
    private String appName;
    /**
     *پورت سامانه
     */
    private String appPort;
    /**
     * نوع خطا
     */
    private ExceptionTypeEnum type;
    /**
     * کلاسی که در آن خطا اتفاق می افتد
     */
    private String exceptionClassName;
    /**
     * شناسه یونیک داده ای که خطا دز آن اتفاق افتاده
     */
    private String dataId;
    /**
     * اولین پیام خطا
     */
    private String message;
    /**
     * پیامهای خطا
     */
    private List<ExceptionMessageDto> messageDtoList = new ArrayList<>();
    /**
     * توضیحاتی در مورد خطا
     */
    private String description;
    /**
     * زمانی که در آن خطا اتفاق افتاده است
     */
    private String dateOfException = String.valueOf(Instant.now().toEpochMilli());
    /**
     * نشانی وب در زمان بروز خطا
     */
    private String url;
    /**
     * نشانی آی پی کاربر در زمان بروز خطا
     */
    private String ipAddress;
    /**
     * کلمه کاربری کاربر (در صورتی که لاگین باشد) در زمان بروز خطا
     */
    private String username;
    /**
     * شناسه کاربر (در صورتی که لاگین باشد) در زمان بروز خطا
     */
    private Long userId;

    public ExceptionDto(String appName, String appPort) {
        this.appName = appName;
        this.appPort = appPort;
    }
}
