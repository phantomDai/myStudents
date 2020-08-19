/*
 * @author: GuYouda
 * @date: 2018/4/12
 * @time: 16:36
 * @des:
 */

package test.concurrence.msf;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class MSF1 {
    private Semaphore semaphore ;
    private Semaphore semaphore1 = new Semaphore(10) ;
    private Semaphore semaphore2 ;
    private boolean resourceArray[];
    private  ReentrantLock lock;
    public MSF1() {
        this.resourceArray = new boolean[10];
        this.semaphore = new Semaphore(10,true);
        this.lock = new ReentrantLock(true);//公平模式的锁，先来的先选
        for(int i=0 ;i<10; i++){
            resourceArray[i] = true;//初始化为资源可用的情况
        }
    }
    public void useResource(int userId) throws InterruptedException {
        semaphore2 = new Semaphore(15,false);

        semaphore.acquire();
        try{
            //semaphore.acquire();
            int id = getResourceId();//占到一个坑
            System.out.print("userId:"+userId+"正在使用资源，资源id:"+id+"\n");
            Thread.sleep(100);//do something，相当于于使用资源
            resourceArray[id] = true;//退出这个坑
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            semaphore.release();//释放信号量，计数器加1
        }
    }
    private int getResourceId(){
        int id = -1;
        lock.lock();
        try {
            //lock.lock();//虽然使用了锁控制同步，但由于只是简单的一个数组遍历，
            // 效率还是很高的，所以基本不影响性能。
            for(int i=0; i<10; i++){
                if(resourceArray[i]){
                    resourceArray[i] = false;
                    id = i;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return id;
    }
}