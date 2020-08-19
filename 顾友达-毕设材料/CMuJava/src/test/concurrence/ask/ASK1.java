/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:04
 * @des:
 */

package test.concurrence.ask;

public class ASK1 {

    void test(){
        System.out.println("test");
    }

    private void test1(){
        System.out.println("test1");
    }

    public synchronized void test2(){
        System.out.println("synchronized test2");
    }
}
