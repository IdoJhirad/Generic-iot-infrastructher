package threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import WaitablePQueue.WaitablePQueue;

public class ThreadPool implements Executor {

    //private filed
    private final WaitablePQueue<Task<?>> taskQueue = new WaitablePQueue<Task<?>>();
    private int numOfThreads;
    private final int  GODMODE_PRIORIY = 100;
    private final int  SHUTDOWN_PRIORITY = -1;
    //the last task to enter before termination
    private final Task<Void> awaitTerminationTask = new Task<>(new Poison(), SHUTDOWN_PRIORITY - 1 );
    private final AtomicBoolean isPause = new AtomicBoolean(false);
    private final AtomicBoolean isShuDown= new AtomicBoolean(false);
    private final Object poolLock = new Object();

    //Ctor
    // Default numberOfThreads depend on number of cores
    public ThreadPool() {
        this(Runtime.getRuntime().availableProcessors()+1);
    }

    //receives the original number of threads
    public ThreadPool(int numberOfThreads) {
        if(numberOfThreads <= 0) {
            throw new IllegalArgumentException();
        }
        this.numOfThreads = numberOfThreads;
        for(int i = 0 ; i < numberOfThreads ; ++i){
            Thread curr = new Thread(this::threadOperation);
            curr.start();
        }
    }

    //add task methods
    @Override
    public void execute(Runnable runnable) {
       submit(runnable);
    }

    public Future<?> submit(Runnable command){
        return submit(Executors.callable(command), Priority.MEDIUM);
    }

    public Future<?> submit(Runnable command, Priority p){
        return submit(Executors.callable(command), p);
    }

    public <T> Future<T> submit(Callable<T> command){
        return submit(command, Priority.MEDIUM);
    }

    public <T> Future<T> submit(Runnable command, Priority p, T value){
        return submit(Executors.callable(command,value),p );
    }

    public <T> Future<T> submit(Callable<T> command, Priority p){
        if(isShuDown.get()) {
            throw new RejectedExecutionException();
        }
        if(command == null ) {
            throw new NullPointerException();
        }
        Task<T> task = new Task<>(command, p.getValue());
        taskQueue.enqueue(task);
        return task.future;
    }

    // setter
    // if threads are removed, they should be the first threads that not running
    public void setNumOfThreads(int numOfThreads){
        if(numOfThreads <= 0) {
            throw new IllegalArgumentException();
        }
        for(int i = this.numOfThreads; i < numOfThreads; ++i){
            Thread curr = new Thread(this::threadOperation);
            curr.start();
        }

        for (int i = numOfThreads; i < this.numOfThreads; ++i) {
            Task<Void> task = new Task<>(new Poison(), GODMODE_PRIORIY);
            task.stop = true;
           taskQueue.enqueue(task);
        }
        this.numOfThreads = numOfThreads;
    }

    //operations
    public void pause(){
        isPause.set(true);
        for(int i = 0 ; i < numOfThreads; ++i) {
            Task<Void> task = new Task<>(new SleepingPill(), GODMODE_PRIORIY);
            this.taskQueue.enqueue(task);
        }
    }
    public void resume(){
        isPause.set(false);
        synchronized (poolLock){
            poolLock.notifyAll();
        }
    }
    public void shutdown(){
        isShuDown.set(true);
        for(int i = 0 ; i < numOfThreads - 1 ; ++i) {
            Task<Void> task = new Task<>(new Poison(), SHUTDOWN_PRIORITY);
            task.stop = true;
            this.taskQueue.enqueue(task);
        }
        this.awaitTerminationTask.stop = true;
        this.taskQueue.enqueue(awaitTerminationTask);
        numOfThreads = 0;
    }
    public void awaitTermination(){
        try {
            awaitTerminationTask.future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean awaitTermination(long timeout,TimeUnit unit) throws InterruptedException {

        try {
            awaitTerminationTask.future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException  e) {
            throw new InterruptedException();
        }
        catch ( TimeoutException e) {
            return false;
        }
        return true;
    }

    //private handler methods
    private  void threadOperation() {
        boolean stopFlag = true;

        while (stopFlag) {
            Task<?> task = taskQueue.dequeue();
            if(task != null && !task.future.isCancelled()) {
                try {
                    task.taskExec();
                    stopFlag = !task.stop;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class Task<E> implements Comparable<Task<E>> {
        private final Callable<E> command;
        private final int priority;
        private final Future<E> future;
        private E res;
        private final Object lock = new Object();
        private volatile boolean stop;
        private volatile boolean cancel;
        private volatile boolean isTaskDone;
        private Thread currThread;
        private ExecutionException exception;

        //Ctor
        public Task(Callable<E> command, int priority) {
            this.future = new TaskFuture();
            this.priority = priority;
            this.command = command;
        }

        private void taskExec()  {
           currThread = Thread.currentThread();
            try {
                res = command.call();
            }
            catch (Exception e) {
               exception = new ExecutionException(e);
            }
           isTaskDone = true;
            synchronized (lock) {
              lock.notifyAll();
            }
        }


        @Override
        public int compareTo(Task<E> task){
            return task.priority - this.priority;
    }

        private class TaskFuture implements Future<E> {

            @Override
            public boolean cancel(boolean b) {
                if(isTaskDone || isCancelled()) {
                    return false;
                }
                if(b && currThread != null) {
                    currThread.interrupt();
                }

                boolean removed = taskQueue.remove(Task.this);
                synchronized (lock) {
                    isTaskDone = true;
                    lock.notifyAll();
                }
                if(removed){
                    cancel = true;
                }
                return removed;
            }

            @Override
            public boolean isCancelled() {return cancel;}

            @Override
            public boolean isDone() {
                return isTaskDone;
            }

            @Override
            public E get() throws InterruptedException, ExecutionException {
                synchronized (lock){
                    while (!isTaskDone) {
                        lock.wait();
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                    }
                }

                if(this.isCancelled()) {
                    throw new CancellationException();
                }

                if(Task.this.exception != null) {
                    throw  exception;
                }
                return Task.this.res;
            }

            @Override
            public E get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                long remainingTime = timeUnit.toMillis(l);
                long deadLine = System.currentTimeMillis() + remainingTime;


                synchronized (lock) {
                    while (!isTaskDone && remainingTime > 0) {
                        lock.wait(remainingTime);
                        remainingTime = deadLine - System.currentTimeMillis();
                    }
                }

                if(this.isCancelled()) {
                    throw new CancellationException();
                }

                if(!isTaskDone) {
                    throw new TimeoutException("get timeout pass");
                }

                if(Task.this.exception != null) {//task need to be done to check the exception
                    throw  exception;
                }

                return Task.this.res;
            }
        }
    }
    private static class Poison implements Callable<Void> {

        @Override
        public Void call()  {
            System.out.println("Poison is taken thread:" +Thread.currentThread().getName()+" stopped!" );
            return null;
        }
    }
    private class SleepingPill implements Callable<Void> {

        @Override
        public Void call()  {
            synchronized (ThreadPool.this.poolLock){
                while (isPause.get()){
                    try {
                        poolLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }
    }

}

