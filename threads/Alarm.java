package nachos.threads;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		//KThread.currentThread().yield();
	    KThread temp;
        if(KThread.waitQ.queue.size() == 0){
            return;
        }	
        while(KThread.waitQ.top().waitTime < Machine.timer().getTime()){
            temp = KThread.waitQ.pop();
            temp.ready();
        }
        
	}
 
	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */ 
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
        if(x <= 0){
            return;
        }
		long wakeTime = Machine.timer().getTime() + x;
        KThread.currentThread().waitTime = wakeTime;
        KThread.waitQ.push(KThread.currentThread());
        KThread.currentThread().sleep();
	}

    public static void alarmTest1() {
        int durations[] = {1000, 10*1000, 100*1000};
        long t0, t1;

        for (int d : durations) {
            t0 = Machine.timer().getTime();
            ThreadedKernel.alarm.waitUntil (d);
            t1 = Machine.timer().getTime();
            System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
        }
    }
    public static void selfTest() {
        
        alarmTest1();
    }
}

/*public class waitQueue {
    
    public List queue;

    public waitQueue(){
        queue = new ArrayList();
    }

    public void push(KThread thread){
        
        boolean flag = false;  
        
        for(int i = 0; i < queue.size(); i++){
            if(thread.waitTime < queue.get(i).waitTime){
                queue.add(i, thread);
                flag = true; 
            }
        }
        if(!flag){
            queue.add(queue.size() - 1, thread);
        }

    }
    public KThread top(){
        return queue.get(0);
    }
    public KThread pop(){
        
        KThread temp = queue.get(0);
        queue.remove(0);
        return temp;
    }
    
}*/