/*
 * @author: GuYouda
 * @date: 2018/4/3
 * @time: 10:05
 * @des: 数组工具类
 */

package mujava.op.concurrence.util;

public class ArrayUtil {
    public static final String[] BASE_TYPE = {"byte", "short", "int", "long", "float", "double", "char", "boolean"};

    /**
     * 判断数组里面是否存在某个值
     *
     * @param str   查询的内容
     * @param array 数组
     * @return true or false
     */
    public static boolean inArray(String str, String[] array) {

        if (str.trim().isEmpty() || array.length == 0) {
            return false;
        }

        for (String item : array) {
            if (item.equals(str)) {
                return true;
            }
        }
        return false;
    }
}
