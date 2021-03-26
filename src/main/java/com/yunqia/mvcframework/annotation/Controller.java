package com.yunqia.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * 该注解为将使用该注解标注的类加载大IOC容器中
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Controller {

    String value() default "";

}
