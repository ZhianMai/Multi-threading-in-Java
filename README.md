# Multi-threading in Java
(In Progress)

This repo is a collection of multi-threading concept demo in Java.

## Contents
- Thread basics
  - Thread Creation: <i>Thread</i> class, <i>Runnable</i> interface, <i>Callable</i> interface, and Thread pool.
  - Thread Properties: ID, name, priority, state, and daemon thread.
- Thread communications
  - stop(), join(), interrupt(), yield(), and InterruptedException.
- Critical section, data racing, atomicity, and locking.
- Producer-consumer, semaphore, and more.

## 1. Thread Basics

### 1.1 Single Thread Creation

The first step of multi-threading.

#### 1.1.1 Inheriting <i>Thread</i> Class :link:[link](src/johnston/thread/basic/creation/InheritedThread.java)

- Create a class that extends <i>Thread</i> class;
- Override run() method by putting the multi-threading logic in it;
- Create an object of the multi-threaded class;
- Call start() method.

#### 1.1.2 Implementing <i>Runnable</i> Class :link:[link](src/johnston/thread/basic/creation/RunnableImpl.java)
- Create a class that implements <i>Runnable</i> interface;
- This interface has run() method only, and no other fields or methods;
- So override run() method by putting the multi-threading logic in it;
- Create a <i>Runnable</i> object, and we cannot directly call start()!
- Create a <i>Thread</i> object, and pass the Runnable object into the constructor;
- Call the Thread object start() method.

#### 1.1.3 Anonymous Class  :link:[link](src/johnston/thread/basic/creation/AnonymousThread.java)
- Create a Thread object, then new a Runnable implementation inside the constructor;
- Call start() method!

#### 1.1.4 Anonymous Class with Lambda Expression :link:[link](src/johnston/thread/basic/creation/AnonymousLambdaThread.java)
- Similar to 1.1.3, but the code is less again.

#### 1.1.5 :warning: Comparing Thread and Runnable :link:[link](src/johnston/thread/basic/creation/ThreadRunnableComparison.java)
In OOD, it favors interface over inheritance, so implementing Runnable interface for thread creation is preferable.

Also, using Runnable interface can greatly decouple data and logic. Class which implements Runnable can start multiple threads
to work on the same data which is inside the class. However, the data should be free from data racing!

In this demo, I created two groups (Alice and Bob group) to eat 5 apples.
- Alice group extends Thread, while Bob group extends Runnable;
- Each Alice group thread cannot collaborate to eat a same set of apples, unless the apple is outside the class;
- A Bob group can start multiple threads to eat a same set of apples, because interface Runnable holds no data field!

#### 1.1.6 Async Thread with Return Value: Callable & FutureTask :link:[link](src/johnston/thread/basic/creation/CallableThread.java)
Async thread: thread that runs async task, and supports return result. Basic Thread class and Runnable interface do not support return value on run() method.
- <i>Callable</i> interface: has only one method named call() which is like run() method in Runnable but can return value and throw exception. Callable object cannot be the target
  of Thread instance.
- <i>FutureTask</i> class: the connection between Callable interface and Thread instance.

<br />

### 1.2 Thread Pool
Using Thread class start() method to run a thread is a one-time process. When a thread is terminated, it cannot run again.
For a large scale multi-threaded system, it's very resource-consuming.

There is an alternate way to run multi-threading: using thread pool. Thread pool is provided in Java concurrency package. Its thread is reusable, and also provide better thread management
than threads run by start() method.

Thread pool is created by <i>Executors</i>, a factory method provided by Java concurrency package.

#### 1.2.1 Single Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorSingleThread.java)
Executor can create a thread pool with single thread. The execution order is guaranteed FIFO.
 
When using Thread class to run a thread, it's one time usage, but a thread pool can reuse
its thread slot.
 
In this Demo, a single thread pool is loaded multiple threads, and these threads will be
executed in FIFO order.

Single thread pool is a blocking queue with unlimited size.

#### 1.2.2 Fixed-size Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorFixedPool.java)
Using <i>Executors</i> factory can create a thread pool. The thread pool can run Runnable thread or Callable thread.

The thread pool can better manage multi running threads, including limiting maximum concurrent threads, and utilize system resources.

#### 1.2.3 Thread Pool Shutdown :link:[link](src/johnston/thread/basic/creation/executors/ThreadPoolShutdown.java)
A thread pool can run forever if not calling shutdown() method, which will prevent the main
thread from terminating.
 
When shutdown() is called, the thread pool will no longer receive new thread task, and waits
for all threads in queue are executed, then exits.
 
Adding new threads on a shutdown thread pool would throw RejectedExecutionException.
 
In this demo, four tasks are added to the thread pool then shut it down. Four tasks will be 
finished after shutting down the thread pool.


#### 1.2.4 Thread Pool Shutdown Now :link:[link](src/johnston/thread/basic/creation/executors/ThreadPoolShutdownNow.java)
If shutdownNow() is called, the thread pool will terminates all threads that are currently
running, and return the tasks that are not yet started.

<br />

### 1.3 Thread Properties

#### 1.3.1 Thread ID :link:[link](src/johnston/thread/basic/properties/ThreadID.java)
Each thread has a unique ID assigned by the JVM.

To get the id of a thread, call:
- threadA.getId();
- Thread.currentThread().getId();

#### 1.3.2 Thread Naming  :link:[link](src/johnston/thread/basic/properties/ThreadNaming.java)
Each thread has its name, either assigned by the JVM or by user.
To set the name of a thread:
- Pass in the name when calling constructor;
- Call threadA.setName("NewName");

To get the name of a thread, call threadA.getName().

#### 1.3.3 Thread Priority :link:[link](src/johnston/thread/basic/properties/ThreadPriority.java)
Each thread can set its priority, from 1 as the lowest, to 10 as the highest. Thread
has higher priority can schedule earlier than other threads with lower priority.

To set the priority of threadA, call threadA.setPriority(1).
To get the priority of threadA, call threadA.getPriority().
<i>Thread</i> class provides priority constants:
- Thread.MAX_PRIORITY: 10;
- Thread.MIN_PRIORITY: 1.

The default priority is 5.

This demo class creates 10 threads, and each of them runs a heavy task. The last thread is set
to the highest priority, while others have the lowest priority. Then the highest one always 
finishes first.

#### 1.3.4 Thread State :link:[link](src/johnston/thread/basic/properties/ThreadState.java)
A thread can have 6 different states, which represent 6 certain parts of its life cycle.

- NEW: a multi-threaded object is created, and before it runs;
- RUNNABLE: when a thread is running or scheduled to run (JVM called start(), and wait for CPU time slice);
- BLOCKED: when a thread is waiting to enter critical section;
- WAITING/TIMED_WAITING: a thread is called wait(), join(), or sleep();
- TERMINATED: a thread is finished running.

This class simulates all these states of a thread.

#### 1.3.5 :warning: Difference between run() and start() :link:[link](src/johnston/thread/basic/properties/RunStartDiff.java)
run() method contains the main logic/task for a thread to run.
start() method make a thread start running run().
So simply calling run() directly would not start multi-threading, it just calls run() method in 
the current thread!

#### 1.3.6 Daemon Thread  :link:[link](src/johnston/thread/basic/properties/DaemonThread.java)
In general, a process would not exit execution as long as it has at least one running thread, but some threads are doing 
background task, like file auto saving thread, or producer thread using external resources. These threads should not block 
the process from exiting.

We can set these threads as daemon thread, so they won't bother the main thread when main is about to exit.

Call threadA.setDaemon(true) to mark it as a daemon thread. The default is false.

Only can set a thread daemon before it starts. Trying to set its daemon status when running will
throw InterruptedException.

The threads created by Daemon thread are Daemon threads by default. It's allowed to manually set them as user thread.

<br />

## 2. Thread Communications

#### 2.1 (Danger) Stop a thread :link:[link](src/johnston/thread/communications/ThreadStop.java)
Calling stop() method of a thread can immediately terminate that thread.

This operation is not safety. If the thread being stopped holds a lock, then it
would not release it. If the thread being stopped is using database, then it would
cause data inconsistency. So this method is @Deprecated.

#### 2.2 Thread Join :link:[link](src/johnston/thread/communications/ThreadJoin.java)
 When calling threadB.join() in threadA, threadA would wait until threadB finishes the
 work.

 Suppose threadA needs the result from threadB, then threadA can call threadB.join() to
 wait for the threadB instead of using a while loop to run a spinlock.

 ThreadA that called join() may throw InterruptedException if other threads interrupt ThreadA.

 This demo creates two threads (A & B) to run a random int generator until get the target number.
 Main thread will wait until A exits, but will not wait B. So main thread always get the target
 number from A and needs some luck to get the same from B.
 
#### 2.3 Thread Interruption :link:[link](src/johnston/thread/communications/ThreadInterruption.java)
When threadA calls threadB.interrupt(), threadB can receive a interruption signal, and
decide what to do next. So calling interrupt() to threadB does not stop or pause threadB if
it's running (not waiting or sleeping).

This demo is to interrupt a thread.

#### 2.4 InterruptedException :link:[link](src/johnston/thread/communications/ThreadInterruptedException.java)
When interrupting a sleeping or joined thread, it will throw InterruptedException.

#### 2.5 Thread Yielding :link:[link](src/johnston/thread/communications/YieldThread.java)
Method yield() is provided by Thread class. When a thread calls Thread.yield(), it gives up
the usage of CPU and put itself into the thread scheduling, which depends on the thread
priority. The state is still Runnable, but it's "ready to run" instead of "running".

In this demo, an array of low priority thread, and an array of high priority thread yield to
each other, and the final result shows that high priority threads have more execution times than the lower
priority ones.