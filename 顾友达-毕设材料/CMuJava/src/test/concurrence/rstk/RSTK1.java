/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 0:46
 * @des:
 */

package test.concurrence.rstk;

public class RSTK1 {

    private static String str = "RSTK1";
    private  String str1 = "RSTK1";

    private void test() {
        System.out.println("");
    }

    private static void test1() {
        System.out.println(str);
    }

    private synchronized void test2() {
        System.out.println(str);
        System.out.println(str1);
    }

    private synchronized static void test3() {
        System.out.println(str);
        System.out.println("");
    }
}
