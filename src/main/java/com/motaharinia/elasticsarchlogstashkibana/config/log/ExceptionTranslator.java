package com.motaharinia.elasticsarchlogstashkibana.config.log;


import com.motaharinia.elasticsarchlogstashkibana.config.log.model.ExceptionDto;
import com.motaharinia.elasticsarchlogstashkibana.config.log.model.ExceptionMessageDto;
import com.motaharinia.elasticsarchlogstashkibana.config.log.model.ExceptionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس جمع کننده رویدادهای کنترلر<br>
 * این کلاس تمامی خطاهای صادر شده در سطح کنترلرها را میگیرد<br>
 * خطاها را لاگ میکند و یک مدل خروجی یونیک برای خطاها به سمت کلاینت ارسال میکند
 */
@ControllerAdvice
@Component
@Slf4j
public class ExceptionTranslator {

    /**
     * عنوان سامانه
     */
    @Value("${spring.application.name}")
    private String appName;

    /**
     * پورت سامانه
     */
    @Value("${server.port}")
    private int appPort;


    private MessageSource messageSource;

    public ExceptionTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * این متد تمام خطاهای سامانه رو که از سطح کنترلر پرت شده باشد را گرفته
     * و پس از بررسی نوع خطا موارد زیر را برای آن انجام میدهد
     * تبدیل انواع خطا به یک مدل واحد ExceptionDto
     * ثبت خطای دریافتی در محل ذخیره سازی لاگها ELK
     *
     * @param exception           خطای دریافتی
     * @param httpServletRequest  درخواست وب
     * @param locale              زبان محلی
     * @param httpServletResponse پاسخ وب
     * @return خروجی: مدل خطا
     */
    @ExceptionHandler(Exception.class)
    @RequestMapping(headers = "x-requested-with=XMLHttpRequest", produces = "application/json")
    public @ResponseBody
    ExceptionDto doException(Exception exception, HttpServletRequest httpServletRequest, Locale locale, HttpServletResponse httpServletResponse) {
        ExceptionDto exceptionDto = new ExceptionDto(appName, String.valueOf(appPort));
        System.out.println("exception.getClass():" + exception.getClass());
        if (exception != null && exception.getClass() != null) {
            if (exception instanceof BusinessException) {
                exceptionDto = this.getDtoFromBusinessException((BusinessException) exception);
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (exception instanceof MethodArgumentNotValidException) {
                exceptionDto = this.getDtoFromMethodArgumentNotValidException((MethodArgumentNotValidException) exception);
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                exceptionDto = this.getDtoFromGeneralException(exception);
                httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        exceptionDto.setUrl(this.getRequestUrl(httpServletRequest));
        exceptionDto.setIpAddress(this.getRequestIpAddress(httpServletRequest));
//        CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal != null) {
//            exceptionDto.setUsername(principal.getUsername());
//            exceptionDto.setUserId(principal.getId());
//        }
//        Marker exceptionTypeMarker = MarkerFactory.getMarker(exceptionDto.getType().getValue());
        log.error(exceptionDto.getMessageDtoList().get(0).getMessage(), kv("exceptionDto", exceptionDto));
        return exceptionDto;
    }


    /**
     * متد سازنده مدل خطا از خطای بیزینس
     *
     * @param businessException خطای بیزینس
     * @return خروجی: مدل خطا
     */
    private ExceptionDto getDtoFromBusinessException(BusinessException businessException) {
        List<ExceptionMessageDto> messageDtoList = new ArrayList<>();
        String translatedMessage = messageSource.getMessage("businessException." + businessException.getMessage(), new Object[]{}, LocaleContextHolder.getLocale());
        messageDtoList.add(new ExceptionMessageDto(translatedMessage, this.getStackTraceString(businessException), this.getStackTraceLineString(businessException), ""));
        ExceptionDto exceptionDto = new ExceptionDto(appName, String.valueOf(appPort));
        exceptionDto.setType(ExceptionTypeEnum.BUSINESS_EXCEPTION);
        exceptionDto.setExceptionClassName(businessException.getExceptionClassName());
        exceptionDto.setDataId(businessException.getDataId());
        if (messageDtoList.size() > 0) {
            exceptionDto.setMessage(messageDtoList.get(0).getMessage());
        }
        exceptionDto.setMessageDtoList(messageDtoList);
        exceptionDto.setDescription(businessException.getDescription());
        return exceptionDto;
    }


    /**
     * متد سازنده مدل خطا از خطای اعتبارسنجی
     *
     * @param methodArgumentNotValidException خطای اعتبارسنجی
     * @return خروجی: مدل خطا
     */
    private ExceptionDto getDtoFromMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<ExceptionMessageDto> messageDtoList = new ArrayList<>();
        ExceptionDto exceptionDto = new ExceptionDto(appName, String.valueOf(appPort));
        exceptionDto.setType(ExceptionTypeEnum.VALIDATION_EXCEPTION);
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        String translatedMessage;
        for (FieldError fieldError : fieldErrors) {
            String modelName = fieldError.getObjectName();
            translatedMessage = messageSource.getMessage(fieldError.getDefaultMessage(), new Object[]{}, LocaleContextHolder.getLocale());
            messageDtoList.add(new ExceptionMessageDto(translatedMessage, this.getStackTraceString(methodArgumentNotValidException), this.getStackTraceLineString(methodArgumentNotValidException), modelName + "." + fieldError.getField()));
        }
        if (messageDtoList.size() > 0) {
            exceptionDto.setMessage(messageDtoList.get(0).getMessage());
        }
        exceptionDto.setMessageDtoList(messageDtoList);
        exceptionDto.setDescription("");
        return exceptionDto;
    }

    /**
     * متد سازنده مدل خطا از خطای عمومی
     *
     * @param generalException خطای عمومی
     * @return خروجی: مدل خطا
     */
    private ExceptionDto getDtoFromGeneralException(Exception generalException) {
        List<ExceptionMessageDto> messageDtoList = new ArrayList<>();
        messageDtoList.add(new ExceptionMessageDto(generalException.getMessage(), this.getStackTraceString(generalException), this.getStackTraceLineString(generalException), ""));
        ExceptionDto exceptionDto = new ExceptionDto(appName, String.valueOf(appPort));
        exceptionDto.setType(ExceptionTypeEnum.GENERAL_EXCEPTION);
        exceptionDto.setExceptionClassName("");
        exceptionDto.setDataId("");
        if (messageDtoList.size() > 0) {
            exceptionDto.setMessage(messageDtoList.get(0).getMessage());
        }
        exceptionDto.setMessageDtoList(messageDtoList);
        exceptionDto.setDescription("");
        return exceptionDto;
    }


    /**
     * این متد رشته stacktrace خطا را خروجی میدهد
     *
     * @param exception خطا
     * @return خروجی: رشته stacktrace خطا
     */
    private String getStackTraceString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    /**
     * این متد خط اول فنی خطا را به صورت رشته خروجی میدهد
     *
     * @param exception خطا
     * @return خروجی: خط اول رشته stacktrace خطا
     */
    private String getStackTraceLineString(Exception exception) {
        if (exception.getStackTrace() != null) {
            StackTraceElement[] stackTraceElements = exception.getStackTrace();
            String relatedToClassName = stackTraceElements[0].getClassName();
            String relatedToMethodName = stackTraceElements[0].getMethodName();
            String relatedToLineNumber = Integer.toString(stackTraceElements[0].getLineNumber());
            return ("ClassName:" + relatedToClassName + " MethodName:" + relatedToMethodName + " LineNumber:" + relatedToLineNumber);
        } else {
            return "";
        }
    }


    /**
     * این متد نشانی وب را از درخواست وب خروجی میدهد
     *
     * @param httpServletRequest درخواست وب
     * @return خروجی: نشانی وب
     */
    private String getRequestUrl(HttpServletRequest httpServletRequest) {
        if (httpServletRequest != null) {
            return httpServletRequest.getServletPath();
        } else {
            return "";
        }
    }

    /**
     * این متد آی پی کاربر را از درخواست وب خروجی میدهد
     *
     * @param httpServletRequest درخواست وب
     * @return خروجی: آی پی کاربر
     */
    private String getRequestIpAddress(HttpServletRequest httpServletRequest) {
        if (httpServletRequest != null) {
            String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpServletRequest.getRemoteAddr();
            }
            return ipAddress;
        } else {
            return "";
        }
    }

}
