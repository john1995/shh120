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
		KThread.currentThread().yield();
	    	KThread temp;
        	while(KThread.waitQ.queue.size() != 0 && KThread.waitQ.top().waitTime < Machine.timer().getTime()){
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
		//disable interrupts
		Machine.interrupt().disable();
        	if(x <= 0){
            		return;
        	}
		long wakeTime = Machine.timer().getTime() + x;

        	KThread.currentThread().waitTime = wakeTime;
        	KThread.waitQ.push(KThread.currentThread());
        	KThread.sleep();
		//enable interrupts
		Machine.interrupt().enable();
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

	//tests if wait until is called on multiple threads, it will still syncronize
	private static void alarmTest2(){
		System.out.println("Starting alarm test 2");
		KThread child1 = new KThread( new Runnable () {
			public void run() {
				for(int i = 0; i < 10; i++)
					System.out.println("child 1 is executing");
            			long t0 = Machine.timer().getTime();
				ThreadedKernel.alarm.waitUntil(1000 * 1000);
				long t1 = Machine.timer().getTime();
				System.out.println("child1 waited for " + (t1 - t0) + "ticks");
			}
		});
		child1.setName("child1").fork();


		KThread child2 = new KThread( new Runnable () {
			public void run() {
				for(int i = 0; i < 10; i++)
					System.out.println("child 2 is executing");
				long t0 = Machine.timer().getTime();
				ThreadedKernel.alarm.waitUntil(10 * 1000);
				long t1 = Machine.timer().getTime();
				System.out.println("child2 waited for " + (t1 - t0) + "ticks");
			}
		});
		child2.setName("child2").fork();

		KThread child3 = new KThread( new Runnable () {
			public void run() {
				for(int i = 0; i < 10; i++)
					System.out.println("child 3 is executing");
				long t0 = Machine.timer().getTime();
				ThreadedKernel.alarm.waitUntil(100 * 1000);
				long t1 = Machine.timer().getTime();
				System.out.println("child3 waited for " + (t1 - t0) + "ticks");
			}
		});
		child3.setName("child3").fork();

		child1.join();

		System.out.println("Done");


	}	

    public static void selfTest() {
        
        alarmTest1();
	alarmTest2();
    }
}


