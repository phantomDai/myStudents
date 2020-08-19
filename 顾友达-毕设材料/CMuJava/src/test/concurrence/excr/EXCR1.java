/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 20:49
 * @des:
 */

package test.concurrence.excr;

public class EXCR1 {
    private final Object object = new Object();

    public void log1(String msg1) {
        synchronized (this) {
            System.out.println(msg1);
        }
    }

    public void log2(String msg1, String msg2) {
        System.out.println(msg1);
        synchronized (this) {
            System.out.println(msg2);
        }
    }

    public void log3(String msg1, String msg2, int i) {
        synchronized (this) {
            System.out.println(msg1);
            System.out.println(msg2);
        }
        System.out.println(i);
    }

    public void log4(String msg1, String msg2, int i) {
        System.out.println(msg1);
        synchronized (object) {
            System.out.println(msg2);
            System.out.println(i);
        }
        System.out.println("hhhhhhh");
    }

    public void log5() {
        System.out.println("QWER");
        System.out.println("ASDF");
        synchronized (object) {
            System.out.println("ZXCV");
            System.out.println("AWSD");
        }
        System.out.println("FJ");
        System.out.println("GH");
    }
}
