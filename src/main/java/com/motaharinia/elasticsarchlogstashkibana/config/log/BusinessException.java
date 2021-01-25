package com.motaharinia.elasticsarchlogstashkibana.config.log;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطای بیزینس پایه که تمامی کلاسهای خطای بیزینس سامانه از آن توسعه می یابند
 */
@Getter
@Setter
public abstract class BusinessException extends RuntimeException {

    /**
     * کلاسی که در آن خطا اتفاق می افتد
     */
    private String exceptionClassName;
    /**
     * شناسه یونیک داده ای که خطا دز آن اتفاق افتاده
     */
    private String dataId;
    /**
     * پیام خطا
     */
    private String message;
    /**
     * توضیحاتی در مورد خطا
     */
    private String description;


    /**
     * متد سازنده اکسپشن
     *
     * @param exceptionClass       کلاسی که در آن خطا اتفاق می افتد
     * @param dataId     شناسه یونیک داده ای که خطا دز آن اتفاق افتاده
     * @param message     پیام خطا
     * @param description توضیحاتی در مورد خطا
     */
    public BusinessException(@NotNull Class exceptionClass, @NotNull String dataId, @NotNull String message, String description) {
        this.exceptionClassName = exceptionClass.getSimpleName();
        this.dataId = dataId;
        this.message = message;
        this.description = description;
    }
}
