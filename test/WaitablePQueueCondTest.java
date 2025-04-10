//package test;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import priorityqueueberier.WaitablePQueueCond;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class WaitablePQueueCondTest {
//
//    WaitablePQueueCond<Integer> queue;
//
//        @BeforeEach
//        void setUp() {
//            queue = new WaitablePQueueCond<Integer>(10);
//        }
//
//        @Test
//        void enqueue() {
//            queue.enqueue(15);
//            queue.enqueue(20);
//            queue.enqueue(1);
//            queue.enqueue(5);
//            queue.enqueue(10);
//            queue.enqueue(7);
//            queue.enqueue(0);
//            queue.enqueue(60);
//            queue.enqueue(58);
//            queue.enqueue(100);
//            assertEquals(10, queue.size());
//            //queue.enqueue(70);
//
//        }
//
//        @Test
//        void dequeue() {
//        }
//
//        @Test
//        void remove() {
//            queue.enqueue(15);
//            queue.enqueue(20);
//            queue.enqueue(1);
//            queue.enqueue(5);
//            queue.enqueue(10);
//            assertEquals(5, queue.size());
//            assertTrue(queue.remove(15));
//            assertEquals(4, queue.size());
//            assertEquals(1, queue.peek());
//            assertTrue(queue.remove(1));
//            assertEquals(3, queue.size());
//            assertEquals(5, queue.peek());
//        }
//
//        @Test
//        void peek() {
//            queue.enqueue(15);
//            queue.enqueue(20);
//            queue.enqueue(1);
//            queue.enqueue(5);
//            queue.enqueue(10);
//            assertEquals(1, queue.peek());
//            queue.dequeue();
//            assertEquals(5, queue.peek());
//        }
//
//        @Test
//        void size() {
//            assertEquals(0, queue.size());
//            queue.enqueue(15);
//            queue.enqueue(20);
//            queue.enqueue(1);
//            queue.enqueue(5);
//            queue.enqueue(10);
//            // System.out.println(queue.priorityQueue.toString());
//            assertEquals(5, queue.size());
//            queue.dequeue();
//            assertEquals(4, queue.size());
//            queue.dequeue();
//            assertEquals(3 , queue.size());
//            queue.dequeue();
//            assertEquals(2, queue.size());
//            queue.dequeue();
//            assertEquals(1, queue.size());
//            queue.dequeue();
//            assertEquals(0, queue.size());
//
//        }
//
//        @Test
//        void isEmpty() {
//            assertTrue(queue.isEmpty());
//            queue.enqueue(15);
//            assertFalse(queue.isEmpty());
//            queue.dequeue();
//            assertTrue(queue.isEmpty());
//        }
//    }