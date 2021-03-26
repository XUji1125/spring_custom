package com.yunqia.mvcframework.annotation;


import java.lang.annotation.*;

/**
 * 使用该注解标注的类也会被加载到IOC容器中
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Component {

    String value() default "";

}
