package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	
    int store = 0;
    Lock lock;
    Lock lock2;
    Lock lock3;
    Condition speakers;
    Condition listeners;
    Condition syncro;
	   
    /**
	 * Allocate a new communicator.
	 */
	public Communicator() {
       
        lock = new Lock();
        lock2 = new Lock();
        lock3 = new Lock();
        speakers = new Condition(lock);
        listeners = new Condition(lock);
        syncro = new Condition(lock);
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word the integer to transfer.
	 */
	public void speak(int word) {
      lock.acquire();
      while(store != 0){  //cant return until we know something has listened
        speakers.sleep();    //put all speakers to sleep until someone has listened 
      }

      store =  word;
      listeners.wake();       //someone spoke so now someone needs to listen 
      syncro.sleep();                  
      lock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
	    int toRet = store;
        lock.acquire();	
     
        while(store == 0){   //cant return until we know someone said something
            listeners.sleep();    //put all listeners to sleep until someone has spoken
        } 
        store = 0;
        //listeners.sleep();
        speakers.wake();  //someone has listened now someone must speak?
        syncro.wake();   
        lock.release();
        return toRet;
	}

 public static void commTest6() {
    final Communicator com = new Communicator();
    final long times[] = new long[4];
    final int words[] = new int[2];
    KThread speaker1 = new KThread( new Runnable () {
        public void run() {
            com.speak(4);
            times[0] = Machine.timer().getTime();
        //    System.out.println("should speak"); 
        }
        });
    speaker1.setName("S1");
    KThread speaker2 = new KThread( new Runnable () {
        public void run() {
            com.speak(7);
            times[1] = Machine.timer().getTime();
        //    System.out.println("dont speak");
        }
        });
    speaker2.setName("S2");
    KThread listener1 = new KThread( new Runnable () {
        public void run() {
            times[2] = Machine.timer().getTime();
            words[0] = com.listen();
        //    System.out.println("should listen");
        }
        });
    listener1.setName("L1");
    KThread listener2 = new KThread( new Runnable () {
        public void run() {
            times[3] = Machine.timer().getTime();
            words[1] = com.listen();
        //    System.out.println("dont listen");
        }
        });
    listener2.setName("L2");
    
    speaker1.fork(); speaker2.fork(); listener1.fork(); listener2.fork();
    speaker1.join(); speaker2.join(); listener1.join(); listener2.join();
    Lib.assertTrue(words[0] == 4, "Didn't listen back spoken word."); 
    //Lib.assertTrue(words[1] == 7, "Didn't listen back spoken word.");
    Lib.assertTrue(times[0] > times[2], "speak() returned before listen() called.");
    Lib.assertTrue(times[1] > times[3], "speak() returned before listen() called.");
    System.out.println("commTest6 successful!");
    }

    public static void selfTest() {
        commTest6();
    }

}
