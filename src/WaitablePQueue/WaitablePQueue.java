package WaitablePQueue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class WaitablePQueue<E> {

    PriorityQueue<E> priorityQueue;
    private Semaphore emptySem = new Semaphore(0);; //represent the size of the PQ


    public WaitablePQueue() {
        this(null);
    }
    public WaitablePQueue(Comparator<E> comperator) {
        priorityQueue = new PriorityQueue<>(comperator);
    }


    public void enqueue(E e) {
        synchronized (priorityQueue){
            priorityQueue.add(e);
        }
        emptySem.release();
    }

    public E dequeue() {
        try {
            emptySem.acquire();
            synchronized (priorityQueue){
                return priorityQueue.poll();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Sem", e);
        }

    }

    public E dequeue(long timeout, TimeUnit unit) {
        try {
            if(!emptySem.tryAcquire(timeout, unit)){
                return null;
            }
            synchronized (priorityQueue){
                return priorityQueue.poll();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Sem", e);
        }
    }

    public boolean remove(E element) {
        boolean removed;
        synchronized (priorityQueue) {
            removed = priorityQueue.remove(element);
        }
        if(removed){
            emptySem.tryAcquire();
        }
        return removed;
    }

    public boolean remove(E element, long timeout, TimeUnit unit) {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        while(System.nanoTime() < deadline){
            if(remove(element)){
                return true;
            }
            try {
                long remaining = deadline - System.nanoTime();
                if(remaining > 0){
                    if(emptySem.tryAcquire(remaining, TimeUnit.NANOSECONDS)){
                        emptySem.release();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Sem", e);
            }
        }
        return false;
    }

    public E peek(){
        synchronized (priorityQueue){
            return priorityQueue.peek();
        }
    }

    public int size(){
        synchronized (priorityQueue){
        return priorityQueue.size();
        }
    }

    public boolean isEmpty(){
        synchronized (priorityQueue){
            return priorityQueue.isEmpty();
        }
    }
}
