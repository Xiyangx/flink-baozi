package com.sunac.test;

import com.sunac.utils.TypeUtils;
import scala.annotation.meta.param;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.test
 * @date:2022/9/15
 */
public class TestJava {

    public static void main(String[] args) throws Exception {

//test set
        Person person2 = new Person();
//        String field2 = "name";
//        setValue(person2, person2.getClass(), field2, Person.class.getDeclaredField(field2).getType(), 12);
//        System.out.println(person2);
        //获取某个属性的类型
//        Field name = Person.class.getDeclaredField("name");
//        System.out.println(Person.class.getDeclaredField("name").getType().getName());
//        Object getMethod = getGetMethod(person2, field2);
//        System.out.println(getMethod);
        setValue(person2, Person.class, "name", Person.class.getDeclaredField("name").getType(), TypeUtils.stringToTarget("12", Person.class.getDeclaredField("name").getType()));
        setValue(person2, Person.class, "sex", Person.class.getDeclaredField("sex").getType(), TypeUtils.stringToTarget("男", Person.class.getDeclaredField("sex").getType()));

        Field[] fie = Person.class.getDeclaredFields();
        for (Field field : fie) {
            //    public static void setValue(Object obj, Class<?> clazz, String filedName, Class<?> typeClass, Object value) {
            String fieldName = field.getName();
            System.out.println(getGetMethod(person2, fieldName));
        }
//        //通过反射操作属性-----set/get方法
//        Object obj=Person.class.newInstance();
//        Field getFid=Person.class.getDeclaredField("name");
//        getFid.set(obj,"华润希望小学");
//        System.out.println(getFid.get(obj));


    }

    public static Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }

    /**
     * 根据属性，拿到set方法，并把值set到对象中
     *
     * @param obj       对象
     * @param clazz     对象的class
     * @param typeClass
     * @param value
     */
    public static void setValue(Object obj, Class<?> clazz, String filedName, Class<?> typeClass, Object value) {
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        try {
            Method method = clazz.getDeclaredMethod(methodName, new Class[]{typeClass});
            method.invoke(obj, new Object[]{getClassTypeValue(typeClass, value)});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 通过class类型获取获取对应类型的值
     *
     * @param typeClass class类型
     * @param value     值
     * @return Object
     */
    private static Object getClassTypeValue(Class<?> typeClass, Object value) {
        if (typeClass == int.class || value instanceof Integer) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == short.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == byte.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == double.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == long.class) {
            if (null == value) {
                return 0;
            }
            return value;
        } else if (typeClass == String.class) {
            if (null == value) {
                return "";
            }
            return value;
        } else if (typeClass == boolean.class) {
            if (null == value) {
                return true;
            }
            return value;
        } else if (typeClass == BigDecimal.class) {
            if (null == value) {
                return new BigDecimal(0);
            }
            return new BigDecimal(value + "");
        } else {
            return typeClass.cast(value);
        }
    }
}
