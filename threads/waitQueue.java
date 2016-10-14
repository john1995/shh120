package nachos.threads;
import nachos.machine.*; 
import java.util.ArrayList;
import java.util.List;

public class waitQueue {
    
    public List queue;

    public waitQueue(){
        queue = new ArrayList<KThread>();
    }

    public void push(KThread thread){
        
        boolean flag = false;  
        
        for(int i = 0; i < queue.size(); i++){
            if(thread.waitTime < (queue.get(i)).waitTime){
                queue.add(i, thread);
                flag = true; 
            }
        }
        if(!flag){
            if(queue.size() == 0){
                queue.add(0,thread);
            }else{
                queue.add(queue.size() - 1, thread);
            }
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
    

}
