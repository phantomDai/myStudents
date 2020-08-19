/*
 * @author: GuYouda
 * @date: 2018/4/12
 * @time: 10:15
 * @des: 对象类型相关工具类
 */

package mujava.op.concurrence.util;

import openjava.mop.OJClass;

public class ObjectTypeUtil {
    public static final String[] BASE_TYPE = {"byte", "short", "int", "char",
            "float", "double", "long", "boolean"};

    /**
     * 根据一个变量的类名判断是否为Java的基本数据类型
     *
     * @param name 变量名字
     * @return true or false
     */
    public static boolean isBaseType(String name) {
        return ArrayUtil.inArray(name, BASE_TYPE);
    }

    /**
     * 判断对象的类型是否为指定类型
     * @param ojClass 对象
     * @param targetType 目标类型 包含报名，例如：java.lang.Thread
     * @return true/false
     */
    public static final boolean isTargetType(OJClass ojClass, String targetType) {
        if (ojClass == null || targetType == null || "".equals(targetType.trim())) {
            return false;
        }
        return targetType.equals(ojClass.getName());
    }
}
