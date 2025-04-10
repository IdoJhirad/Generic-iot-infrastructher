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
        return dequeue();
    }

    public boolean remove(E element) {

        synchronized (priorityQueue) {
            return priorityQueue.remove(element);
        }
    }

    public boolean remove(E element, long timeout, TimeUnit unit) {
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
