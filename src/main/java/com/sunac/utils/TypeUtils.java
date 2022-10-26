package com.sunac.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.utils
 * @date:2022/9/16
 */
public class TypeUtils {
    public static Object stringToNullableTarget(String string, Class<?> t) throws Exception {
        return string == null ? null : t.getConstructor(String.class).newInstance(string);
    }

    public static Field getFieldType(Class classT, String fieldName) throws NoSuchFieldException {
        Field declaredField;
        try {
            // 获取不到父类的field，抛异常，到classT.getSuperclass().getDeclaredField获取
            declaredField = classT.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            declaredField = classT.getSuperclass().getDeclaredField(fieldName);
        }
        return declaredField;
    }

    public static Object stringToTarget(String string, Class<?> t) throws Exception {
        boolean nullOrEmpty = StringUtils.isEmpty(string);

        if (Double.class.equals(t) || double.class.equals(t)) {
            return nullOrEmpty ? null : Double.parseDouble(string);
        } else if (Long.class.equals(t) || long.class.equals(t)) {
            return nullOrEmpty ? null : Long.parseLong(string);
        } else if (Integer.class.equals(t) || int.class.equals(t)) {
            return nullOrEmpty ? null : Integer.parseInt(string);
        } else if (Float.class.equals(t) || float.class.equals(t)) {
            return nullOrEmpty ? null : Float.parseFloat(string);
        } else if (Short.class.equals(t) || short.class.equals(t)) {
            return nullOrEmpty ? null : Short.parseShort(string);
        } else if (Boolean.class.equals(t) || boolean.class.equals(t)) {
            return nullOrEmpty ? null : Boolean.parseBoolean(string);
        } else if (Number.class.isAssignableFrom(t)) {
            return t.getConstructor(String.class).newInstance(nullOrEmpty ? "0" : string);
        } else {
            return nullOrEmpty ? "" : t.getConstructor(String.class).newInstance(string);
        }

    }

    public static Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return "";
    }

    /**
     * 根据属性，拿到set方法，并把值set到对象中
     *
     * @param obj       对象
     * @param clazz     对象的class
     * @param typeClass
     * @param value
     */
    public static void setValue(Object obj, Class<?> clazz, String filedName, Class<?> typeClass, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = "set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, new Class[]{typeClass});
        } catch (Exception ex) {
            method = clazz.getMethod(methodName, new Class[]{typeClass});
        }
        method.invoke(obj, new Object[]{getClassTypeValue(typeClass, value)});
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
