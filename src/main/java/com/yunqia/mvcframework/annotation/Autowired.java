package com.yunqia.mvcframework.annotation;

import java.lang.annotation.*;


/**
 * 使用该注解标注的属性将自动装配
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Autowired {

    String value() default "";

}
