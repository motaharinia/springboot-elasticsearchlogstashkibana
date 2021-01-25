package com.motaharinia.elasticsarchlogstashkibana.config.log.model;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس شرح مدل اکسپشن
 */
@Data
@AllArgsConstructor
public class ExceptionMessageDto {

    /**
     * پیام خطا
     */
    private String message;
    /**
     * مسیر فنی خطا
     */
    private String stackTrace;
    /**
     * خط ابتدای مسیر فنی خطا
     */
    public String stackTraceLine;
    /**
     * مرجع خطا که میتواند طبق نظر توسعه دهنده برای موارد خاص ست شود
     */
    public String reference;

}
