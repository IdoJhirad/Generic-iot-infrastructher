//package test;
//
//import org.junit.jupiter.api.Test;
//import priorityqueueberier.WaitablePQueueCond;
//import WaitablePQueue.WaitablePQueue;
//
//import static java.lang.Thread.sleep;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Comparator;
//
//public class WaitableTest {
//    WaitablePQueueCond<Integer> pqBound = new WaitablePQueueCond<>(10);
//    WaitablePQueue<Integer> pqNoBound = new WaitablePQueue<Integer>();
//
//    Comparator<Pavel> comperator = new Comparator<Pavel>() {
//
//        public int compare(Pavel o1, Pavel o2) {
//            return o1.priority - o2.priority;
//        }
//
//
//    };
//
//    WaitablePQueue<WaitableTest.Pavel> pqNoBoundComperator = new WaitablePQueue<>(comperator);
//    WaitablePQueueCond<WaitableTest.Pavel> pqBoundComparator = new WaitablePQueueCond<>(10,comperator);
//
//
//    private class Pavel {
//        public int priority;
//        public Pavel(int priority) {
//            this.priority = priority;
//        }
//    }
//
//    Pavel pavel0 = new Pavel(0);
//
//    Pavel pavel1 = new Pavel(1);
//    Pavel pavel2 = new Pavel(2);
//    Pavel pavel3 = new Pavel(3);
//    Pavel pavel4 = new Pavel(4);
//    Pavel pavel5 = new Pavel(5);
//    Pavel pavel6 = new Pavel(6);
//    Pavel pavel7 = new Pavel(7);
//
//    @Test
//    public void waitableEnqueue1() {
//        pqNoBound.enqueue(6);
//        pqNoBound.enqueue(1);
//        pqNoBound.enqueue(7);
//        pqNoBound.enqueue(2);
//        pqNoBound.enqueue(3);
//        pqNoBound.enqueue(5);
//        pqNoBound.enqueue(4);
//    }
//
//    @Test
//    public void waitableDequeue1() {
//        pqNoBound.enqueue(6);
//        pqNoBound.enqueue(1);
//        pqNoBound.enqueue(7);
//        pqNoBound.enqueue(2);
//        pqNoBound.enqueue(3);
//        pqNoBound.enqueue(5);
//        pqNoBound.enqueue(4);
//
//        assertEquals(1, (int) pqNoBound.dequeue());
//        assertEquals(2, (int) pqNoBound.dequeue());
//        assertEquals(3, (int) pqNoBound.dequeue());
//        assertEquals(4, (int) pqNoBound.dequeue());
//        assertEquals(5, (int) pqNoBound.dequeue());
//        assertEquals(6, (int) pqNoBound.dequeue());
//        assertEquals(7, (int) pqNoBound.dequeue());
//
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    System.out.println("hey1");
//
//                    sleep(1000);
//                    System.out.println("hey2");
//                    pqNoBound.enqueue(100);
//                    System.out.println("hey3");
//
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        thread.start();
//        assertEquals(100, (int) pqNoBound.dequeue());
//
////        assertNull(pqNoBound.dequeue(3, TimeUnit.SECONDS));
//    }
//
//    @Test
//    public void waitableRemove1() {
//        pqNoBound.enqueue(6);
//        pqNoBound.enqueue(1);
//        pqNoBound.enqueue(7);
//        pqNoBound.enqueue(2);
//        pqNoBound.enqueue(3);
//        pqNoBound.enqueue(5);
//        pqNoBound.enqueue(4);
//
//        assertFalse(pqNoBound.remove(0));
//
//        assertTrue(pqNoBound.remove(1));
//        assertTrue(pqNoBound.remove(2));
//        assertTrue(pqNoBound.remove(3));
//        assertTrue(pqNoBound.remove(4));
//        assertTrue(pqNoBound.remove(5));
//        assertTrue(pqNoBound.remove(6));
//        assertTrue(pqNoBound.remove(7));
//
//        assertFalse(pqNoBound.remove(1));
//        assertFalse(pqNoBound.remove(2));
//        assertFalse(pqNoBound.remove(3));
//        assertFalse(pqNoBound.remove(4));
//    }
//
//    @Test
//    public void waitablePQPeek1() {
//        pqNoBound.enqueue(6);
//        assertEquals(6, (int) pqNoBound.peek());
//        pqNoBound.enqueue(1);
//        assertEquals(1, (int) pqNoBound.peek());
//        pqNoBound.enqueue(7);
//        assertEquals(1, (int) pqNoBound.peek());
//        pqNoBound.enqueue(2);
//        assertEquals(1, (int) pqNoBound.peek());
//        pqNoBound.dequeue();
//        assertEquals(2, (int) pqNoBound.peek());
//        pqNoBound.dequeue();
//        assertEquals(6, (int) pqNoBound.peek());
//        pqNoBound.dequeue();
//        assertEquals(7, (int) pqNoBound.peek());
//    }
//
//    @Test
//    public void waitableSize1() {
//        assertEquals(0, pqNoBound.size());
//        pqNoBound.enqueue(6);
//        assertEquals(1, pqNoBound.size());
//        pqNoBound.enqueue(2);
//        assertEquals(2, pqNoBound.size());
//        pqNoBound.enqueue(7);
//        assertEquals(3, pqNoBound.size());
//        pqNoBound.enqueue(2);
//        assertEquals(4, pqNoBound.size());
//        pqNoBound.enqueue(3);
//        assertEquals(5, pqNoBound.size());
//        pqNoBound.enqueue(5);
//        assertEquals(6, pqNoBound.size());
//        pqNoBound.enqueue(4);
//        assertEquals(7, pqNoBound.size());
//
//        pqNoBound.dequeue();
//        assertEquals(6, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(5, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(4, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(3, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(2, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(1, pqNoBound.size());
//        pqNoBound.dequeue();
//        assertEquals(0, pqNoBound.size());
//    }
//
//    @Test
//    public void waitableIsEmpty1() {
//        assertTrue(pqNoBound.isEmpty());
//        pqNoBound.enqueue(6);
//        assertFalse(pqNoBound.isEmpty());
//        pqNoBound.enqueue(2);
//        assertFalse(pqNoBound.isEmpty());
//        pqNoBound.dequeue();
//        assertFalse(pqNoBound.isEmpty());
//        pqNoBound.dequeue();
//        assertTrue(pqNoBound.isEmpty());
//    }
//
//    @Test
//    public void waitableEnqueue1Comperator1() {
//        pqNoBoundComperator.enqueue(pavel6);
//        pqNoBoundComperator.enqueue(pavel1);
//        pqNoBoundComperator.enqueue(pavel7);
//        pqNoBoundComperator.enqueue(pavel2);
//        pqNoBoundComperator.enqueue(pavel3);
//        pqNoBoundComperator.enqueue(pavel5);
//        pqNoBoundComperator.enqueue(pavel4);
//    }
//
//    @Test
//    public void waitableDequeue1Comperator1() {
//        pqNoBoundComperator.enqueue(pavel6);
//        pqNoBoundComperator.enqueue(pavel1);
//        pqNoBoundComperator.enqueue(pavel7);
//        pqNoBoundComperator.enqueue(pavel2);
//        pqNoBoundComperator.enqueue(pavel3);
//        pqNoBoundComperator.enqueue(pavel5);
//        pqNoBoundComperator.enqueue(pavel4);
//
//        assertEquals(pavel1, pqNoBoundComperator.dequeue());
//        assertEquals(pavel2, pqNoBoundComperator.dequeue());
//        assertEquals(pavel3, pqNoBoundComperator.dequeue());
//        assertEquals(pavel4, pqNoBoundComperator.dequeue());
//        assertEquals(pavel5, pqNoBoundComperator.dequeue());
//        assertEquals(pavel6, pqNoBoundComperator.dequeue());
//        assertEquals(pavel7, pqNoBoundComperator.dequeue());
//
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    System.out.println("hey1");
//
//                    sleep(1000);
//                    System.out.println("hey2");
//                    pqNoBoundComperator.enqueue(pavel1);
//                    System.out.println("hey3");
//
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        thread.start();
//        assertEquals(pavel1, pqNoBoundComperator.dequeue());
//
//      //  assertNull(pqNoBoundComperator.dequeue(3, TimeUnit.SECONDS));
//    }
//
//    @Test
//    public void waitableRemoveComperator1() {
//        pqNoBoundComperator.enqueue(pavel6);
//        pqNoBoundComperator.enqueue(pavel1);
//        pqNoBoundComperator.enqueue(pavel7);
//        pqNoBoundComperator.enqueue(pavel2);
//        pqNoBoundComperator.enqueue(pavel3);
//        pqNoBoundComperator.enqueue(pavel5);
//        pqNoBoundComperator.enqueue(pavel4);
//
//        assertFalse(pqNoBoundComperator.remove(pavel0));
//
//        assertTrue(pqNoBoundComperator.remove(pavel1));
//        assertTrue(pqNoBoundComperator.remove(pavel2));
//        assertTrue(pqNoBoundComperator.remove(pavel3));
//        assertTrue(pqNoBoundComperator.remove(pavel4));
//        assertTrue(pqNoBoundComperator.remove(pavel5));
//        assertTrue(pqNoBoundComperator.remove(pavel6));
//        assertTrue(pqNoBoundComperator.remove(pavel7));
//
//        assertFalse(pqNoBoundComperator.remove(pavel1));
//        assertFalse(pqNoBoundComperator.remove(pavel2));
//        assertFalse(pqNoBoundComperator.remove(pavel3));
//        assertFalse(pqNoBoundComperator.remove(pavel4));
//    }
//
//    @Test
//    public void waitablePQPeekComperator1() {
//        pqNoBoundComperator.enqueue(pavel6);
//        assertEquals(pavel6, pqNoBoundComperator.peek());
//        pqNoBoundComperator.enqueue(pavel1);
//        assertEquals(pavel1, pqNoBoundComperator.peek());
//        pqNoBoundComperator.enqueue(pavel7);
//        assertEquals(pavel1, pqNoBoundComperator.peek());
//        pqNoBoundComperator.enqueue(pavel2);
//        assertEquals(pavel1, pqNoBoundComperator.peek());
//        pqNoBoundComperator.dequeue();
//        assertEquals(pavel2, pqNoBoundComperator.peek());
//        pqNoBoundComperator.dequeue();
//        assertEquals(pavel6, pqNoBoundComperator.peek());
//        pqNoBoundComperator.dequeue();
//        assertEquals(pavel7, pqNoBoundComperator.peek());
//    }
//
//    @Test
//    public void waitableSizeComperator1() {
//        assertEquals(0, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel6);
//        assertEquals(1, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel2);
//        assertEquals(2, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel7);
//        assertEquals(3, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel2);
//        assertEquals(4, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel3);
//        assertEquals(5, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel5);
//        assertEquals(6, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.enqueue(pavel4);
//        assertEquals(7, (int) pqNoBoundComperator.size());
//
//        pqNoBoundComperator.dequeue();
//        assertEquals(6, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(5, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(4, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(3, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(2, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(1, (int) pqNoBoundComperator.size());
//        pqNoBoundComperator.dequeue();
//        assertEquals(0, (int) pqNoBoundComperator.size());
//    }
//
//    @Test
//    public void waitableIsEmptyComperator1() {
//        assertTrue(pqNoBoundComperator.isEmpty());
//        pqNoBoundComperator.enqueue(pavel6);
//        assertFalse(pqNoBoundComperator.isEmpty());
//        pqNoBoundComperator.enqueue(pavel2);
//        assertFalse(pqNoBoundComperator.isEmpty());
//        pqNoBoundComperator.dequeue();
//        assertFalse(pqNoBoundComperator.isEmpty());
//        pqNoBoundComperator.dequeue();
//        assertTrue(pqNoBoundComperator.isEmpty());
//    }
//
//    @Test
//    public void waitableEnqueue2() {
//        pqBound.enqueue(6);
//        pqBound.enqueue(1);
//        pqBound.enqueue(7);
//        pqBound.enqueue(2);
//        pqBound.enqueue(3);
//        pqBound.enqueue(5);
//        pqBound.enqueue(4);
//    }
//
////    @Test
////    public void waitableDequeue2() {
////        pqBound.enqueue(6);
////        pqBound.enqueue(1);
////        pqBound.enqueue(7);
////        pqBound.enqueue(2);
////        pqBound.enqueue(3);
////        pqBound.enqueue(5);
////        pqBound.enqueue(4);
////
////        assertEquals(1, (int) pqBound.dequeue());
////        assertEquals(2, (int) pqBound.dequeue());
////        assertEquals(3, (int) pqBound.dequeue());
////        assertEquals(4, (int) pqBound.dequeue());
////        assertEquals(5, (int) pqBound.dequeue());
////        assertEquals(6, (int) pqBound.dequeue());
////        assertEquals(7, (int) pqBound.dequeue());
////
////        Thread thread = new Thread(new Runnable() {
////            public void run() {
////                try {
////                    System.out.println("hey1");
////
////                    sleep(1000);
////                    System.out.println("hey2");
////                    pqBound.enqueue(100);
////                    System.out.println("hey3");
////
////                } catch (InterruptedException e) {
////                    throw new RuntimeException(e);
////                }
////            }
////        });
////
////        thread.start();
////        assertEquals(100, (int) pqBound.dequeue());
////
////       // assertNull(pqBound.dequeue(3, TimeUnit.SECONDS));
////    }
//
//    @Test
//    public void waitableRemove2() {
//        pqBound.enqueue(6);
//        pqBound.enqueue(1);
//        pqBound.enqueue(7);
//        pqBound.enqueue(2);
//        pqBound.enqueue(3);
//        pqBound.enqueue(5);
//        pqBound.enqueue(4);
//
//        assertFalse(pqBound.remove(0));
//
//        assertTrue(pqBound.remove(1));
//        assertTrue(pqBound.remove(2));
//        assertTrue(pqBound.remove(3));
//        assertTrue(pqBound.remove(4));
//        assertTrue(pqBound.remove(5));
//        assertTrue(pqBound.remove(6));
//        assertTrue(pqBound.remove(7));
//
//        assertFalse(pqBound.remove(1));
//        assertFalse(pqBound.remove(2));
//        assertFalse(pqBound.remove(3));
//        assertFalse(pqBound.remove(4));
//    }
//
//    @Test
//    public void waitablePQPeek2() {
//        pqBound.enqueue(6);
//        assertEquals(6, (int) pqBound.peek());
//        pqBound.enqueue(1);
//        assertEquals(1, (int) pqBound.peek());
//        pqBound.enqueue(7);
//        assertEquals(1, (int) pqBound.peek());
//        pqBound.enqueue(2);
//        assertEquals(1, (int) pqBound.peek());
//        pqBound.dequeue();
//        assertEquals(2, (int) pqBound.peek());
//        pqBound.dequeue();
//        assertEquals(6, (int) pqBound.peek());
//        pqBound.dequeue();
//        assertEquals(7, (int) pqBound.peek());
//    }
//
//    @Test
//    public void waitableSize2() {
//        assertEquals(0, (int) pqBound.size());
//        pqBound.enqueue(6);
//        assertEquals(1, (int) pqBound.size());
//        pqBound.enqueue(2);
//        assertEquals(2, (int) pqBound.size());
//        pqBound.enqueue(7);
//        assertEquals(3, (int) pqBound.size());
//        pqBound.enqueue(2);
//        assertEquals(4, (int) pqBound.size());
//        pqBound.enqueue(3);
//        assertEquals(5, (int) pqBound.size());
//        pqBound.enqueue(5);
//        assertEquals(6, (int) pqBound.size());
//        pqBound.enqueue(4);
//        assertEquals(7, (int) pqBound.size());
//
//        pqBound.dequeue();
//        assertEquals(6, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(5, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(4, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(3, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(2, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(1, (int) pqBound.size());
//        pqBound.dequeue();
//        assertEquals(0, (int) pqBound.size());
//    }
//
//    @Test
//    public void waitableIsEmpty2() {
//        assertTrue(pqBound.isEmpty());
//        pqBound.enqueue(6);
//        assertFalse(pqBound.isEmpty());
//        pqBound.enqueue(2);
//        assertFalse(pqBound.isEmpty());
//        pqBound.dequeue();
//        assertFalse(pqBound.isEmpty());
//        pqBound.dequeue();
//        assertTrue(pqBound.isEmpty());
//    }
//
//    @Test
//    public void waitableEnqueue1Comperator2() {
//        pqBoundComparator.enqueue(pavel6);
//        pqBoundComparator.enqueue(pavel1);
//        pqBoundComparator.enqueue(pavel7);
//        pqBoundComparator.enqueue(pavel2);
//        pqBoundComparator.enqueue(pavel3);
//        pqBoundComparator.enqueue(pavel5);
//        pqBoundComparator.enqueue(pavel4);
//    }
//
////    @Test
////    public void waitableDequeue1Comperator2() {
////        pqBoundComparator.enqueue(pavel6);
////        pqBoundComparator.enqueue(pavel1);
////        pqBoundComparator.enqueue(pavel7);
////        pqBoundComparator.enqueue(pavel2);
////        pqBoundComparator.enqueue(pavel3);
////        pqBoundComparator.enqueue(pavel5);
////        pqBoundComparator.enqueue(pavel4);
////
////        assertEquals(pavel1, pqBoundComparator.dequeue());
////        assertEquals(pavel2, pqBoundComparator.dequeue());
////        assertEquals(pavel3, pqBoundComparator.dequeue());
////        assertEquals(pavel4, pqBoundComparator.dequeue());
////        assertEquals(pavel5, pqBoundComparator.dequeue());
////        assertEquals(pavel6, pqBoundComparator.dequeue());
////        assertEquals(pavel7, pqBoundComparator.dequeue());
////
////        Thread thread = new Thread(new Runnable() {
////            public void run() {
////                try {
////                    System.out.println("hey1");
////
////                    sleep(1000);
////                    System.out.println("hey2");
////                    pqBoundComparator.enqueue(pavel1);
////                    System.out.println("hey3");
////
////                } catch (InterruptedException e) {
////                    throw new RuntimeException(e);
////                }
////            }
////        });
////
////        thread.start();
////        assertEquals(pavel1, pqBoundComparator.dequeue());
////
////    //    assertNull(pqBoundComparator.dequeue(3, TimeUnit.SECONDS));
////    }
//
//    @Test
//    public void waitableRemoveComperator2() {
//        pqBoundComparator.enqueue(pavel6);
//        pqBoundComparator.enqueue(pavel1);
//        pqBoundComparator.enqueue(pavel7);
//        pqBoundComparator.enqueue(pavel2);
//        pqBoundComparator.enqueue(pavel3);
//        pqBoundComparator.enqueue(pavel5);
//        pqBoundComparator.enqueue(pavel4);
//
//        assertFalse(pqBoundComparator.remove(pavel0));
//
//        assertTrue(pqBoundComparator.remove(pavel1));
//        assertTrue(pqBoundComparator.remove(pavel2));
//        assertTrue(pqBoundComparator.remove(pavel3));
//        assertTrue(pqBoundComparator.remove(pavel4));
//        assertTrue(pqBoundComparator.remove(pavel5));
//        assertTrue(pqBoundComparator.remove(pavel6));
//        assertTrue(pqBoundComparator.remove(pavel7));
//
//        assertFalse(pqBoundComparator.remove(pavel1));
//        assertFalse(pqBoundComparator.remove(pavel2));
//        assertFalse(pqBoundComparator.remove(pavel3));
//        assertFalse(pqBoundComparator.remove(pavel4));
//    }
//
//    @Test
//    public void waitablePQPeekComperator2() {
//        pqBoundComparator.enqueue(pavel6);
//        assertEquals(pavel6, pqBoundComparator.peek());
//        pqBoundComparator.enqueue(pavel1);
//        assertEquals(pavel1, pqBoundComparator.peek());
//        pqBoundComparator.enqueue(pavel7);
//        assertEquals(pavel1, pqBoundComparator.peek());
//        pqBoundComparator.enqueue(pavel2);
//        assertEquals(pavel1, pqBoundComparator.peek());
//        pqBoundComparator.dequeue();
//        assertEquals(pavel2, pqBoundComparator.peek());
//        pqBoundComparator.dequeue();
//        assertEquals(pavel6, pqBoundComparator.peek());
//        pqBoundComparator.dequeue();
//        assertEquals(pavel7, pqBoundComparator.peek());
//    }
//
//    @Test
//    public void waitableSizeComperator2() {
//        assertEquals(0, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel6);
//        assertEquals(1, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel2);
//        assertEquals(2, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel7);
//        assertEquals(3, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel2);
//        assertEquals(4, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel3);
//        assertEquals(5, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel5);
//        assertEquals(6, (int) pqBoundComparator.size());
//        pqBoundComparator.enqueue(pavel4);
//        assertEquals(7, (int) pqBoundComparator.size());
//
//        pqBoundComparator.dequeue();
//        assertEquals(6, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(5, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(4, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(3, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(2, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(1, (int) pqBoundComparator.size());
//        pqBoundComparator.dequeue();
//        assertEquals(0, (int) pqBoundComparator.size());
//    }
//
//    @Test
//    public void waitableIsEmptyComperator2() {
//        assertTrue(pqBoundComparator.isEmpty());
//        pqBoundComparator.enqueue(pavel6);
//        assertFalse(pqBoundComparator.isEmpty());
//        pqBoundComparator.enqueue(pavel2);
//        assertFalse(pqBoundComparator.isEmpty());
//        pqBoundComparator.dequeue();
//        assertFalse(pqBoundComparator.isEmpty());
//        pqBoundComparator.dequeue();
//        assertTrue(pqBoundComparator.isEmpty());
//    }
//
//    @Test
//    public void waitableUpperBoundComperator2() {
//        pqBound.enqueue(6);
//        pqBound.enqueue(1);
//        pqBound.enqueue(7);
//        pqBound.enqueue(2);
//        pqBound.enqueue(3);
//        pqBound.enqueue(5);
//        pqBound.enqueue(4);
//        pqBound.enqueue(8);
//        pqBound.enqueue(9);
//        pqBound.enqueue(10);
//
//        long timeChecker = System.currentTimeMillis();
//
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    sleep(1000);
//                    pqBound.dequeue();
//
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        thread.start();
//
//        pqBound.enqueue(11);
//    }
//}
