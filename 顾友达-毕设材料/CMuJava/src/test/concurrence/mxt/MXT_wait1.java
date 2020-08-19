package test.concurrence.mxt;

class ThreadB extends Thread{  
    public ThreadB(String name) {  
        super(name);  
    }  
    public void run() {       
        System.out.println(Thread.currentThread().getName() + " run ");  
        // 死循环，不断运行。  
        while(true){;}  //  这个线程与主线程无关，无 synchronized   
    }  
}  
public class MXT_wait1 {
    public static void main(String[] args) {  
    	test();
    }  
    
    public static void test() {
    	ThreadB t1 = new ThreadB("t1");  
        synchronized(t1) {  
            try {  
                // 启动“线程t1”  
                System.out.println(Thread.currentThread().getName() + " start t1");  
                t1.start();  
                // 主线程等待t1通过notify()唤醒 或 notifyAll()唤醒，或超过3000ms延时；然后才被唤醒。  
                System.out.println(Thread.currentThread().getName() + " call wait ");  
                t1.wait(3000);  
                System.out.println(Thread.currentThread().getName() + " continue"); 
                t1.wait(1500,200);
              
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
	}
}  