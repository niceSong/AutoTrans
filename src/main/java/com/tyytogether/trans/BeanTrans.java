package com.tyytogether.trans;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanTrans<R> {
    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
    Object source;
    Object target;
    Map<String, Supplier<?>> funMapping = new HashMap<>();
    Map<String, Object> valueMapping = new HashMap<>();

    public BeanTrans<?> mapping(String targetName, Object targetValue) {
        valueMapping.put(targetName, targetValue);
        return this;
    }

    public BeanTrans<?> mapping(String targetName, Supplier<?> operation) {
        funMapping.put(targetName, operation);
        return this;
    }

    public <S> BeanTrans(S source, Class<?> target) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.source = source;
        this.target = getInstance(target);
    }

    public R startTrans() {
        return (R) trans(this.source, this.target);
    }

    public Object trans(Object source, Object target) {
        Map<String, Field> targetFieldsMap = Arrays.stream(target.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, it -> it));
        return merge(source, target, targetFieldsMap);
    }

    public Object merge(Object source, Object target, Map<String, Field> targetFieldsMap) {
        targetFieldsMap.forEach((targetName, targetField) -> {
            // 手动转换
            if (valueMapping.containsKey(targetName)) {
                setFieldValue(valueMapping.get(targetName), target, targetField);
            } else if (funMapping.containsKey(targetName)) {
                setFieldValue(funMapping.get(targetName).get(),
                        target, targetField);
            }
            // 自动转换
            else {
                Field sourceField = getField(source, targetName);
                // 源没有该属性时赋值为 null
                if (sourceField == null) {
                    setFieldValue(null, target, targetField);
                } else if (lsDirectMerge(source, targetField, sourceField)) {
                    setFieldValue(getFieldValue(source, sourceField), target, targetField);
                } else {
                    tryMerge(source, target, sourceField, targetField);
                }
            }
        });
        return target;
    }

    // 类型相同 && 不是集合，才能直接赋值
    public boolean lsDirectMerge(Object source, Field targetField, Field sourceField) {
        return targetField.getType() == sourceField.getType() &&
                !(getFieldValue(source, sourceField) instanceof Collection);
    }

    public void tryMerge(Object source, Object target, Field sourceField, Field targetField) {
        Object sourceFieldValue = getFieldValue(source, sourceField);
        // 为集合
        if (sourceFieldValue instanceof Collection) {
            ParameterizedType tParameterizedType = (ParameterizedType) targetField.getGenericType();
            ParameterizedType sParameterizedType = (ParameterizedType) sourceField.getGenericType();
            Type targetParamType = tParameterizedType.getActualTypeArguments()[0];
            Type sourceParamType = sParameterizedType.getActualTypeArguments()[0];
            // 集合的范型参数类型相等，便可直接赋值
            if (targetParamType.equals(sourceParamType)) {
                setFieldValue(sourceFieldValue, target, targetField);
            }
            List<Object> list = Stream.of(sourceFieldValue).map(sourceItem -> {
                try {
                    Object targetItem = getInstance(Class.forName(targetParamType.getTypeName()));
                    trans(sourceItem, targetItem);
                    return targetItem;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            setFieldValue(list, target, targetField);
        }
        // 为类
        else {
            if (sourceFieldValue != null) {
                try {
                    Object targetValue = (getFieldValue(target, targetField) == null)
                            ? getInstance(Class.forName(targetField.getType().getName()))
                            : getFieldValue(target, targetField);
                    trans(sourceFieldValue, targetValue);
                    setFieldValue(targetValue, target, targetField);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFieldValue(Object value, Object targetObj, Field targetField) {
        targetField.setAccessible(true);
        try {
            targetField.set(targetObj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getFieldValue(Object source, Field sourceField) {
        sourceField.setAccessible(true);
        try {
            return sourceField.get(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Field getField(Object obj, String name) {
        try {
            return obj.getClass().getDeclaredField(name);
        } catch (Exception e) {
            // 未找到
            return null;
        }
    }

    private static Object getInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor<?> constructor = reflectionFactory.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
