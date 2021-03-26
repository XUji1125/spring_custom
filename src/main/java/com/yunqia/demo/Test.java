package com.yunqia.demo;

import com.yunqia.demo.service.impl.TestDemo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {


    public static void main(String[] args) {


       /* TestDemo testDemo = new TestDemo();
        Class<? extends TestDemo> aClass = testDemo.getClass();*/
        Class<?> aClass = null;
        TestDemo testDemo = null;
        try {
            aClass = Class.forName("com.yunqia.demo.service.impl.TestDemo");
            testDemo = (TestDemo)aClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            Field field = aClass.getDeclaredField("name");
            field.setAccessible(true);
            Object testD = aClass.newInstance();
            field.set(testD, "周星星");

            Object o = field.get(testD);

            Method method = aClass.getDeclaredMethod("test", String.class);
            Object message = method.invoke(testDemo, new Object[]{"测试数据"});

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

}
