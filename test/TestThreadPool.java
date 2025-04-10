package test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import threadpool.Priority;
import threadpool.ThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
//
//class ThreadPoolTest {
//
//    @org.junit.jupiter.api.Test
//    void test() throws ExecutionException, InterruptedException {
//
//        ThreadPool tp = new ThreadPool(6);
//
//        Future<Integer> f1 = tp.submit(new doSomething());
//        Future<Integer> f2 = tp.submit(new doSomethingElse());
//
//        assertFalse(f1.isDone());
//        assertFalse(f2.isDone());
//
//        assertEquals(8, f1.get());
//        assertEquals(9, f2.get());
//
//        assertTrue(f1.isDone());
//        assertTrue(f2.isDone());
//
//        // Test setNumOfThreads to reduce the thread count
//        System.out.println("\nSet number of threads to 3");
//        tp.setNumOfThreads(3);
//
//        Future<Integer> f3 = tp.submit(new doSomething());
//        Future<Integer> f4 = tp.submit(new doSomethingElse());
//        Future<Integer> f5 = tp.submit(new doSomething());
//        Future<Integer> f6 = tp.submit(new doSomethingElse());
//        Future<Integer> f7 = tp.submit(new doSomething());
//        Future<Integer> f8 = tp.submit(new doSomethingElse());
//
//        sleep(1500);
//        System.out.println("\nSet number of threads to 4");
//        tp.setNumOfThreads(4);
//
//        f3.get(); f4.get(); f5.get(); f6.get(); f7.get(); f8.get();
//
//        // Test reducing the thread count further
//        System.out.println("\nSet number of threads to 1");
//        tp.setNumOfThreads(1);
//
//        System.out.println("submit before cancel");
//        f3 = tp.submit(new doSomething(), Priority.HIGH);
//        f4 = tp.submit(new doSomethingElse(), Priority.LOW);
////UNTIL HERE IS GOOD
//        // Test cancellation of tasks
//        System.out.println("\ncancel");
//        assertTrue(f4.cancel(false));
//        assertTrue(f4.isCancelled());
//        assertTrue(f4.isDone());
//
////TODO
//        System.out.println("before get");
//        f3.get(); // The task that was not canceled should complete
//
//        // Test pause
//        System.out.println("\npause");
//        tp.pause();
//        sleep(4000);
//
//        // Submitting tasks during pause, they should be queued but not executed
//        f5 = tp.submit(new doSomething());
//        f6 = tp.submit(new doSomethingElse());
//        f7 = tp.submit(new doSomething());
//        f8 = tp.submit(new doSomethingElse());
//
//        // Check that tasks are not done yet (since the pool is paused)
//        assertFalse(f5.isDone());
//        assertFalse(f6.isDone());
//
//        // Resume the thread pool and verify tasks continue
//        tp.setNumOfThreads(3);
//        sleep(1000);
//
//        System.out.println("\nresume");
//        tp.resume();
//        sleep(2000);
////
//        assertEquals(8, f5.get());
//        assertEquals(9, f6.get());
//        f7.get();
//        f8.get();
//
//        // Test shutdown behavior
//        f6 = tp.submit(new doSomethingElse());
//        System.out.println("\nshutdown");
//        tp.shutdown();
//
////        // Await termination and check that tasks complete
////        tp.awaitTermination();
////        assertTrue(f6.isDone());
////
////        // Test submitting tasks after shutdown (should throw exception)
////        assertThrows(RejectedExecutionException.class, () -> tp.submit(new doSomething()));
//    }
//}
//
//class doSomething implements Callable<Integer> {
//
//    @Override
//    public Integer call() throws Exception {
//        System.out.println(currentThread() + " started");
//        for (int i = 0; i < 3; i++) {
//            sleep(1000);
//            System.out.println(currentThread().toString() + " prints something: " + i);
//        }
//        System.out.println(currentThread() + " done");
//        return 8;
//    }
//}
//
//class doSomethingElse implements Callable<Integer> {
//
//    @Override
//    public Integer call() throws Exception {
//        System.out.println(currentThread() + " started");
//        for (int i = 0; i < 3; i++) {
//            sleep(1000);
//            System.out.println(currentThread().toString() + " prints something else: " + i);
//        }
//        System.out.println(currentThread() + " done");
//        return 9;
//    }
//}

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestThreadPool {
    private static Monitor monitor = new Monitor();
    private static String message = "message";
    private static int messageNumber;

    @Test
    public void a_testCreatePool(){
        ThreadPool threadPool = new ThreadPool(3);
        threadPool.shutdown();
    }

    @Test
    public void b_testSubmitRunnable() throws ExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(1);
        Future<?> future = threadPool.submit(()-> monitor.writeMessage("runnable task 1"));
        assertNull(future.get());
        assertEquals("runnable task 1", monitor.getLastMessage());
        Future<String> future1 = threadPool.submit(()->doNothing(), Priority.HIGH, message + ++messageNumber);
        assertEquals("message1", future1.get());
        threadPool.shutdown();
    }

    @Test
    public void c_testSubmitCallable() throws ExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(1);
        Future<String > future = threadPool.submit(()-> message + ++messageNumber);
        assertEquals("message2", future.get());
        assertEquals(threadPool.submit(()->20).get(), Integer.valueOf(20));
        threadPool.shutdown();
    }

    @Test
    public void d_testPriority() throws ExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(1);
        for (int i = 0; i < 3; i++) {
            threadPool.submit(()->{
                System.out.println("low");
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ++messageNumber;
                monitor.writeMessage(message + " low");
            }, Priority.LOW);
        }
        threadPool.submit(()->{
            System.out.println("high");

            ++messageNumber;
            monitor.writeMessage(message + " high");
        }, Priority.HIGH);

        threadPool.shutdown();
       threadPool.awaitTermination();
        assertEquals(6, messageNumber);
        assertEquals("message low", monitor.getLastMessage());
    }

    @Test
    public void e_setNumOfThreads() throws InterruptedException {
        Callable<Void> callable = () -> {
            sleep(1000);
            System.out.println("task with thread num" +Thread.currentThread() + " done" );
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        long startTime = System.nanoTime();
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.setNumOfThreads(3);
        threadPool.shutdown();
        threadPool.awaitTermination();
        long endTime = System.nanoTime();
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        assertTrue(3000 >= elapsedTime);

        threadPool = new ThreadPool(3);
        threadPool.setNumOfThreads(1);
        startTime = System.nanoTime();
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);

        threadPool.shutdown();
        threadPool.awaitTermination();
        endTime = System.nanoTime();
        elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        assertTrue(6000 <= elapsedTime);
    }

    @Test
    public void f_testPauseResume() throws InterruptedException {
        Callable<Void> callable = () -> {
            ++messageNumber;
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        sleep(1000);
        threadPool.pause();
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        sleep(1000);
        assertEquals(12, messageNumber);

        threadPool.resume();
        threadPool.shutdown();
        threadPool.awaitTermination();
        assertEquals(16, messageNumber);
    }

    @Test
    public void g_testAwait() throws InterruptedException {
        Callable<Void> callable = () -> {
            sleep(1000);
            ++messageNumber;
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);

        threadPool.shutdown();
        threadPool.awaitTermination();
        assertEquals(20, messageNumber);
    }

    @Test
    public void h_testAwaitWithTime() throws InterruptedException {
        Callable<Void> callable = () -> {
            sleep(1000);
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);
        threadPool.submit(callable);

        threadPool.shutdown();
        assertFalse(threadPool.awaitTermination(3, TimeUnit.SECONDS));
        assertTrue(threadPool.awaitTermination(2, TimeUnit.SECONDS));
    }

    @Test
    public void i_testFutureCancel() throws InterruptedException {
        Callable<Void> callable = () -> {
            sleep(1000);
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        Future<Void> future = threadPool.submit(callable);
        sleep(500);
        assertFalse(future.cancel(false));
        Future<Void> future2 = threadPool.submit(callable);
        Future<Void> future3 = threadPool.submit(callable);
        Future<Void> future4 = threadPool.submit(callable);
        assertTrue(future2.cancel(false));
        assertTrue(future3.cancel(false));
        assertTrue(future4.cancel(false));
        assertFalse(future4.cancel(false));
        assertFalse(future.cancel(false));

        assertFalse(future.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(future3.isCancelled());
        assertTrue(future4.isCancelled());

        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    @Test
    public void j_testFutureIsDone() throws InterruptedException {
        Callable<Void> callable = () -> {
            sleep(1000);
            return null;
        };
        ThreadPool threadPool = new ThreadPool(1);
        Future<Void> future = threadPool.submit(callable);
        assertFalse(future.isDone());
        sleep(1200);
        assertTrue(future.isDone());
        Future<Void> future2 = threadPool.submit(callable);
        Future<Void> future3 = threadPool.submit(callable);
        future2.cancel(true);
        assertTrue(future2.isDone());

        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    @Test
    public void k_testFutureGet() throws InterruptedException, ExecutionException {
        ThreadPool threadPool = new ThreadPool(1);
        Future<String> future1 = threadPool.submit(()->"String");
        Future<Integer> future2 = threadPool.submit(()-> 13 + 5);
        Future<Double> future3 = threadPool.submit(()-> 4.12);
        String s = future1.get();
        Integer i = future2.get();
        Double d = future3.get();
        assertEquals(s, "String");
        assertEquals(Integer.valueOf(18), i);
        assertEquals(Double.valueOf(4.12), d);

        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    @Test
    public void l_testFutureGetWithTime() throws InterruptedException, ExecutionException, TimeoutException {
        ThreadPool threadPool = new ThreadPool(1);
        Future<String> future1 = threadPool.submit(()->{
            sleep(2000);
            return "String";
        });
        String s = future1.get(3, TimeUnit.SECONDS);
        assertEquals(s, "String");

        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    @Test
    public void m_testException() throws InterruptedException {
        ThreadPool threadPool = new ThreadPool(1);
        assertThrows(IllegalArgumentException.class, ()->new ThreadPool(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(-1));
        assertThrows(NullPointerException.class, () -> threadPool.submit((Runnable) null));
        assertThrows(NullPointerException.class, () -> threadPool.submit((Callable<?>) null));
        assertThrows(NullPointerException.class, () -> threadPool.submit((Runnable) null, Priority.HIGH));
        assertThrows(NullPointerException.class, () -> threadPool.submit((Callable<?>) null, Priority.HIGH));
       // assertThrows(RejectedExecutionException.class, threadPool::awaitTermination); // calling awaitTermination before shutdown
        assertThrows(IllegalArgumentException.class, ()->threadPool.setNumOfThreads(0));

        threadPool.submit(()-> {
            try {
                sleep(1000);
            } catch (InterruptedException ignored) {}
        });
        Future<Void> future2 = threadPool.submit(()->null);
        future2.cancel(true);
        assertThrows(CancellationException.class, future2::get);

        Future<?> futureWithException = threadPool.submit(() -> {
            throw new RuntimeException("Test Exception");
        });
        assertThrows(ExecutionException.class, futureWithException::get);

        Future<Void> longRunningTask = threadPool.submit(() -> {
            try {
                sleep(2000); // Simulate long task
            } catch (InterruptedException ignored) {}
            return null;
        });
        assertThrows(TimeoutException.class, () -> longRunningTask.get(1, TimeUnit.SECONDS));  // Timeout of 1 second

        Future<?> future = threadPool.submit(() -> {
            try {
                sleep(3000);  // Simulate long task
            } catch (InterruptedException ignored) {}
        });

        Thread.currentThread().interrupt(); // Simulate interruption
        assertThrows(InterruptedException.class, future::get);

        threadPool.shutdown();
        threadPool.awaitTermination();
        assertThrows(RejectedExecutionException.class, () -> threadPool.submit(() -> System.out.println("Task after shutdown")));
    }


    private void doNothing(){}

    private static class Monitor{
        private List<String> history = new ArrayList<>();
        private String lastMessage;

        public void writeMessage(String s){
            lastMessage = s;
            history.add(s);
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}
