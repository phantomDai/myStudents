/*
 * @author: GuYouda
 * @date: 2018/4/12
 * @time: 19:48
 * @des:
 */

package test.concurrence.mxc;

import java.util.concurrent.CountDownLatch;

public abstract class MXC_Latche1 implements Runnable {

    private CountDownLatch latch;
    private CountDownLatch latch1 = new CountDownLatch(99);
    private String serviceName;
    private boolean serviceUp;

    //Get latch object in constructor so that after completing the task, thread can countDown() the latch
    public MXC_Latche1(String serviceName, int count) {
        super();
        latch = new CountDownLatch(count);
        this.serviceName = serviceName;
        this.serviceUp = false;
    }

    @Override
    public void run() {
        try {
            verifyService();
            serviceUp = true;
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            serviceUp = false;
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isServiceUp() {
        return serviceUp;
    }

    //This methos needs to be implemented by all specific service checker
    public abstract void verifyService();
}
