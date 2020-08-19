/*
 * @author: GuYouda
 * @date: 2018/5/6
 * @time: 12:42
 * @des: 时间工具类
 */

package mujava.op.concurrence.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
    }
}

