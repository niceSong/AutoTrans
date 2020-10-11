package com.tyytogether.trans;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class BeanTrans {

    Map<String, Operation> funMapping = new HashMap<>();
    Map<String, Object > valueMapping = new HashMap<>();

    public BeanTrans mapping(String targetName, Object targetValue) {
        valueMapping.put(targetName, targetValue);
        return this;
    }

    public BeanTrans mapping(String targetName, Operation operation) {
        funMapping.put(targetName, operation);
        return this;
    }

    public <S, T> Object autoTrans(S source, Class<T> target) {
        try {
            return autoTrans(source, target.newInstance());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Object autoTrans(Object source, Object target){
        Map<String, Field> targetFieldsMap = Arrays.stream(target.getClass().getDeclaredFields()).collect(Collectors.toMap(it->it.getName(), it->it));
        return merge(source, target, targetFieldsMap);
    }

    public Object merge(Object source, Object target, Map<String, Field> targetFieldsMap){
        targetFieldsMap.forEach((targetName, targetField) -> {
            // 手动转换
            if(valueMapping.containsKey(targetName)){
                setFieldValue(valueMapping.get(targetName), target, targetField);
            }
            else if(funMapping.containsKey(targetName)){
                setFieldValue(funMapping.get(targetName).operation(),
                        target, targetField);
            }
            // 自动转换
            else {
                Field sourceField = getField(source, targetName);
                if(lsDirectMerge(source, targetField, sourceField)){
                    setFieldValue(getFieldValue(source, sourceField), target, targetField);
                }else {
                    try {
                        tryMerge(source, target, sourceField, targetField);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return target;
    }

    // 类型相同 且 不是集合
    public boolean lsDirectMerge(Object source, Field targetField, Field sourceField){
        if( targetField.getType() == sourceField.getType()
                &&
            !(getFieldValue(source, sourceField) instanceof Collection)
        ){
            return true;
        }
        return false;
    }

    public void tryMerge(Object source, Object target, Field sourceField, Field targetField) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object sourceFieldValue = getFieldValue(source, sourceField);
        // 处理集合
        if(sourceFieldValue instanceof Collection){
            ParameterizedType tParameterizedType = (ParameterizedType)targetField.getGenericType();
            ParameterizedType sParameterizedType = (ParameterizedType)sourceField.getGenericType();
            Type targetParamType = tParameterizedType.getActualTypeArguments()[0];
            // 集合的范型参数类型相等，便可注解赋值
            if(targetParamType == sParameterizedType.getActualTypeArguments()[0]){
                setFieldValue(sourceFieldValue, target, targetField);
            }
            List<Object> list = (List<Object>) ((Collection) sourceFieldValue).stream().map(sourceItem->{
                if(sourceItem == null){
                    return null;
                } else {
                    try {
                        Object targetItem = Class.forName(targetParamType.getTypeName()).newInstance();
                        autoTrans(sourceItem, targetItem);
                        return targetItem;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }).collect(Collectors.toList());

            setFieldValue(list, target, targetField);
        }
        // 处理类中类
        else {
            if(sourceFieldValue != null){
                Object targetValue = (getFieldValue(target, targetField) == null)
                        ? Class.forName(targetField.getType().getName()).newInstance()
                        : getFieldValue(target, targetField);
                autoTrans(sourceFieldValue, targetValue);
                setFieldValue(targetValue, target, targetField);
            }
        }
    }

    public void setFieldValue(Object value, Object targetObj, Field targetField) {
        targetField.setAccessible(true);
        try {
            targetField.set(targetObj, value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object getFieldValue(Object source, Field sourceField) {
        sourceField.setAccessible(true);
        try{
            return sourceField.get(source);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public Field getField(Object obj, String name){
        try{
            Field field = obj.getClass().getDeclaredField(name);
            return field;
        }catch (Exception e){
            // 未找到
            return null;
        }
    }
}
