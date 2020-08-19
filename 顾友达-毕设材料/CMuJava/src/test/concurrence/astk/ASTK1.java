/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 0:40
 * @des:
 */

package test.concurrence.astk;

public class ASTK1 {
    private static String str = "ASTK1";
    private  String str1 = "ASTK1";

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

    private synchronized void test3() {
        System.out.println(str);
        System.out.println("");
    }
}
