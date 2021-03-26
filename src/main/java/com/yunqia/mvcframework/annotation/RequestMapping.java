package com.yunqia.mvcframework.annotation;


import java.lang.annotation.*;

/**
 *
 * 该注解为请求映射注解
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface RequestMapping {

    String value() default "";

}
