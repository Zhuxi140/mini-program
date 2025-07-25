package com.zhuxi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BloomFilterCheck {
    String BloomFilterName();
    String key1();
    String key2() default "";
    boolean rejectIfMiss() default  true;
}
