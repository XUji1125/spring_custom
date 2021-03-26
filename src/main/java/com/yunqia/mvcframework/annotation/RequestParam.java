package com.yunqia.mvcframework.annotation;


import java.lang.annotation.*;

/**
 * 该注解用于请求参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestParam {

    String value() default "";

}
