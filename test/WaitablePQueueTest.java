package test;

import WaitablePQueue.WaitablePQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaitablePQueueTest {

    WaitablePQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new WaitablePQueue<Integer>();
    }

    @Test
    void enqueue() {
    }

    @Test
    void dequeue() {
    }

    @Test
    void remove() {
        queue.enqueue(15);
        queue.enqueue(20);
        queue.enqueue(1);
        queue.enqueue(5);
        queue.enqueue(10);
        assertEquals(5, queue.size());
        assertTrue(queue.remove(15));
        assertEquals(4, queue.size());
        assertEquals(1, queue.peek());
        assertTrue(queue.remove(1));
        assertEquals(3, queue.size());
        assertEquals(5, queue.peek());
    }

    @Test
    void peek() {
        queue.enqueue(15);
        queue.enqueue(20);
        queue.enqueue(1);
        queue.enqueue(5);
        queue.enqueue(10);
        assertEquals(1, queue.peek());
        queue.dequeue();
        assertEquals(5, queue.peek());
    }

    @Test
    void size() {
        assertEquals(0, queue.size());
        queue.enqueue(15);
        queue.enqueue(20);
        queue.enqueue(1);
        queue.enqueue(5);
        queue.enqueue(10);
       // System.out.println(queue.priorityQueue.toString());
        assertEquals(5, queue.size());
        queue.dequeue();
        assertEquals(4, queue.size());
        queue.dequeue();
        assertEquals(3 , queue.size());
        queue.dequeue();
        assertEquals(2, queue.size());
        queue.dequeue();
        assertEquals(1, queue.size());
        queue.dequeue();
        assertEquals(0, queue.size());

    }

    @Test
    void isEmpty() {
        assertTrue(queue.isEmpty());
        queue.enqueue(15);
        assertFalse(queue.isEmpty());
        queue.dequeue();
        assertTrue(queue.isEmpty());
    }
}