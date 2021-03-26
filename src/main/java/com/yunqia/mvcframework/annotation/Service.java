package com.yunqia.mvcframework.annotation;


import java.lang.annotation.*;

/**
 *
 * 该注解为service层类注解，使被该注解标注的类加入到IOC容器中
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Service {

    String value() default "";

}
