# Multithreading in Java

This repo is a collection of multithreading concept demo in Java.

## Contents
- ### [Thread Basics](#thread_basis)
  
  - <b>Thread Creation</b>: <i>Thread</i> class, <i>Runnable</i> interface, and <i>Callable</i> interface.
  - <b>Thread Pool</b> by java.util.concurrent.<i>Executors</i>;
  - <b>Thread Properties</b>: ID, name, priority, state, and daemon thread;
  - <b>Thread Pool Properties</b>: blocking queue, hook methods, and thread pool shutdown properly.

- ### [Thread Communications](#thread_communications)
  
  - <b>Basics</b>: stop(), join(), interrupt(), yield(), wait(), notify(), and InterruptedException;
  - <i>ThreadLocal</i>
  - <i>CountDownLatch</i>
    
- ### [Thread Safety and Locking](#thread_safety_locking)
  - Data racing
  - Keyword <i>synchronized</i>
  - Lock Strategies
  - Producer-Consumer Problem
  - JUC Atomic
  - ABA Problem 
  - Visibility, Sequencing Problem, and Keyword <i>volatile</i>
  - Explicit Lock: Lock, Condition, ReadWriteLock, Semaphore, CyclicBarrier, StampledLock, etc.
    
- ### [Java Thread-safe Container](#thread_safe_container)
  
    - Collections.synchronized*(...)
    - CopyOnWriteArrayList
    - BlockingQueue 
    - ConcurrentHashMap

- ### [Thread-safe Design Patterns](#thread_safe_design_pattern)
  - Singleton
  - Master-worker
  - Fork-join
  - Async Callback Future Task  

- ### [Multithreading Demo](#multithreading_demo)
  - Matrix Multiplication
  - Calculating Angle between Two Vectors
  - Fork-join Merge Sort
  - Thread-safe Hash Map  


<br />
<a name="thread_basis"></a>

## 1. Thread Basics

### 1.1 Single Thread Creation

The first step of multithreading.

#### 1.1.1 Inheriting <i>Thread</i> Class :link:[link](src/johnston/thread/basic/creation/single_thread/InheritedThread.java)

- Create a class that extends <i>Thread</i> class;
- Override run() method by putting the multithreading logic in it;
- Create an object of the multi-threaded class;
- Call start() method.

#### 1.1.2 Implementing <i>Runnable</i> Class :link:[link](src/johnston/thread/basic/creation/single_thread/RunnableImpl.java)
- Create a class that implements <i>Runnable</i> interface;
- This interface has run() method only, and no other fields or methods;
- So override run() method by putting the multithreading logic in it;
- Create a <i>Runnable</i> object, and we cannot directly call start()!
- Create a <i>Thread</i> object, and pass the Runnable object into the constructor;
- Call the Thread object start() method.

#### 1.1.3 Anonymous Class  :link:[link](src/johnston/thread/basic/creation/single_thread/AnonymousThread.java)
- Create a Thread object, then new a Runnable implementation inside the constructor;
- Call start() method!

#### 1.1.4 Anonymous Class with Lambda Expression :link:[link](src/johnston/thread/basic/creation/single_thread/AnonymousLambdaThread.java)
- Similar to 1.1.3, but the code is less again.

#### 1.1.5 :warning: Comparing Thread and Runnable :link:[link](src/johnston/thread/basic/creation/single_thread/ThreadRunnableComparison.java)
In OOD, it favors interface over inheritance, so implementing Runnable interface for thread creation is preferable.

Also, using Runnable interface can greatly decouple data and logic. Class which implements Runnable can start multiple threads
to work on the same data which is inside the class. However, the data should be free from data racing!

In this demo, I created two groups (Alice and Bob group) to eat 5 apples.
- Alice group extends Thread, while Bob group extends Runnable;
- Each Alice group thread cannot collaborate to eat a same set of apples, unless the apple is outside the class;
- A Bob group can start multiple threads to eat a same set of apples, because interface Runnable holds no data field!

#### 1.1.6 Async Thread with Return Value: Callable & FutureTask :link:[link](src/johnston/thread/basic/creation/single_thread/CallableThread.java)
Async thread: thread that runs async task, and supports return result. Basic Thread class and Runnable interface do not support return value on run() method.
- <i>Callable</i> interface: has only one method named call() which is like run() method in Runnable but can return value and throw exception. Callable object cannot be the target
  of Thread instance.
- <i>FutureTask</i> class: the connection between Callable interface and Thread instance.

<br />

### 1.2 Thread Pool Creating Threads
Using Thread class start() method to run a thread is a one-time process. When a thread is terminated, it cannot run again.
For a large scale multi-threaded system, it's very resource-consuming.

There is an alternate way to run multithreading: using thread pool. Thread pool is in Java concurrency package. Its thread is reusable, and also provide better thread management
than threads run by start() method.

Thread pool is created by <i>Executors</i>, a factory method provided by Java concurrency package.

#### 1.2.1 Single Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorSingleThread.java)
Executor can create a thread pool with single thread. The execution order is FIFO.
 
When using Thread class to run a thread, it's one time usage, but a thread pool can reuse
its thread slot.
 
In this Demo, a single thread pool loads multiple threads, and these threads will be
executed in FIFO order.

Single thread pool is a blocking queue with unlimited size.

#### 1.2.2 Fixed-size Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorFixedThreadPool.java)
Thread pool with fixed size n can allow at most n threads run at the same time. The order is
still FIFO.

#### 1.2.3 Cached Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorCachedThreadPool.java)
Cached thread pool has mutable size. Each new coming task will be allocated a new thread. The
size of cached thread pool is unbounded until reaches the limit that JVM allows.

A thread whose idle time is over 60s will be terminated.

#### 1.2.4 Thread Pool Returning Result :link:[link](src/johnston/thread/basic/creation/executors/ExecutorReturnablePool.java)
Using <i>Executors</i> factory can create a thread pool. The thread pool can run Runnable thread or Callable thread.

The thread pool can better manage multi running threads, including limiting maximum concurrent threads, and utilize system resources.

#### 1.2.5 Scheduled Thread Pool :link:[link](src/johnston/thread/basic/creation/executors/ExecutorScheduledThreadPool.java)
Scheduled thread pool supports repeatedly running a thread task with fixed time of period.
Thread pool will run the scheduled task repeatedly until calling shutdown() method.

<i>scheduledThreadPool</i> has two types of schedule methods: fixed period or fixed delay.

For fixed period, the period is minimal runtime of one task. If task A1 runs longer than
the period, than it won't start task A2 until A1 finishes. If task A1 runs faster than the
period, A2 won't start but wait until the minimal interval time is satisfied.

For fixed delay, the delay is the fixed interval between two tasks. If task A1 finishes, it
will wait the delay time than start task A2.

Beside scheduled thread pool, Executors also provides single thread scheduled executor.

In this demo, a set of threads with different scheduled periods are loaded into to thread pool,
and let the thread pool run for a while. The result shows that threads with smaller scheduled
period run more times than others. Fixed delay demo skips because it's very similar to this one.

#### 1.2.6 :warning: Standard ThreadPoolExecutor :link:[link](src/johnston/thread/basic/creation/executors/StandardThreadPoolExecutor.java)
In general, using Executors factory to create thread pool is forbidden in large-scale
development. The standard method is to use standard ThreadPoolExecutor, although Executors
factory invokes ThreadPoolExecutor.

Important ThreadPoolExecutor constructor parameter
corePoolSize: the minimum amount of core threads;
maximumPoolSize: the maximum amount of threads in the pool.
keepAliveTime: the maximum duration of a non-core threads being idle.
threadFactory: creations of new thread.
BlockingQueue<Runnable>: queue to hold tasks when no idle core threads available.
RejectedExecutionHandler: ways to handle new tasks when the pool is full, like throwing
  exception, discard, or replacing the oldest blocking task.

Thread pool task scheduling policy when getting a new task:
 - If the number current core threads is < corePoolSize, new a new thread for this task, even
   though some core threads are idle;
 - If the number current core threads is >= corePoolSize...
   - Find if there are any idle core threads, then replace it;
   - else if no idle core threads, and the blocking queue is not full, enqueue;
   - else if the blocking queue is full, new a new thread until the total number of threads > maximumPoolSize
   - else execute rejected execution policy.

This demo shows how threadPoolExecutor scheduling tasks with different thread pool parameters
and different reject task policies.

#### 1.2.7 ThreadFactory Interface :link:[link](src/johnston/thread/basic/creation/executors/ThreadFactoryDemo.java)
<i>ThreadFactory</i> is an interface from concurrent package. It has only one
method: newThread(Runnable r). User can implement this interface to customize the method of
creating new threads, and pass this interface instance to a thread pool. The thread pools then
will use this thread factory to create reusable new threads.

In this demo, an implementation of ThreadFactory is created and pass it to the thread pool.
When the thread pool receives new Runnable tasks, it will use the threads created by
ThreadFactory to run it.

### 1.3 Thread Properties

#### 1.3.1 Thread ID :link:[link](src/johnston/thread/basic/properties/single_thread/ThreadID.java)
Each thread has a unique ID assigned by the JVM.

To get the id of a thread, call:
- threadA.getId();
- Thread.currentThread().getId();

#### 1.3.2 Thread Naming  :link:[link](src/johnston/thread/basic/properties/single_thread/ThreadNaming.java)
Each thread has its name, either assigned by the JVM or by user.
To set the name of a thread:
- Pass in the name when calling constructor;
- Call threadA.setName("NewName");

To get the name of a thread, call threadA.getName().

#### 1.3.3 Thread Priority :link:[link](src/johnston/thread/basic/properties/single_thread/ThreadPriority.java)
Each thread can set its priority, from 1 as the lowest, to 10 as the highest. Thread
has higher priority can schedule earlier than other threads with lower priority.

To set the priority of threadA, call threadA.setPriority(1).
To get the priority of threadA, call threadA.getPriority().
<i>Thread</i> class provides priority constants:
- Thread.MAX_PRIORITY: 10;
- Thread.MIN_PRIORITY: 1.

The default priority is 5.

This demo class creates 10 threads, and each of them runs a heavy task. The last thread has
the highest priority, while others have the lowest priority. Then the highest one always 
finishes first.

#### 1.3.4 Thread State :link:[link](src/johnston/thread/basic/properties/single_thread/ThreadState.java)
A thread can have 6 different states, which represent 6 certain parts of its life cycle.

- NEW: a multi-threaded object is created, and before it runs;
- RUNNABLE: when a thread is running or scheduled to run (JVM called start(), and wait for CPU time slice);
- BLOCKED: when a thread is waiting to enter critical section;
- WAITING/TIMED_WAITING: a thread is called wait(), join(), or sleep();
- TERMINATED: a thread is finished running.

This class simulates all these states of a thread.

#### 1.3.5 :warning: Difference between run() and start() :link:[link](src/johnston/thread/basic/properties/single_thread/RunStartDiff.java)
run() method contains the main logic/task for a thread to run.
start() method make a thread start running run().
So simply calling run() directly would not start multithreading, it just calls run() method in 
the current thread!

#### 1.3.6 Daemon Thread  :link:[link](src/johnston/thread/basic/properties/single_thread/DaemonThread.java)
In general, a process would not exit execution as long as it has at least one running thread, but some threads are doing 
background task, like file auto saving thread, or producer thread using external resources. These threads should not block 
the process from exiting.

We can set these threads as daemon thread, so they won't bother the main thread when main is about to exit.

Call threadA.setDaemon(true) to mark it as a daemon thread. The default is false.

Only can set a thread daemon before it starts. Trying to set its daemon status when running will
throw InterruptedException.

The threads created by Daemon thread are Daemon threads by default. It's allowed to manually set them as user thread.

<br />

### 1.4 Thread Pool Properties


#### 1.4.1 Thread Pool Shutdown :link:[link](src/johnston/thread/basic/properties/executors/ThreadPoolShutdown.java)
A thread pool can run forever if not calling shutdown() method, which will prevent the main
thread from terminating.

When shutdown() is called, the thread pool will no longer receive new thread task, and waits
for all threads in queue are executed, then exits.

Adding new threads on a shutdown thread pool would throw RejectedExecutionException.

In this demo, four tasks are added to the thread pool then shut it down. Four tasks will be
finished after shutting down the thread pool.

#### 1.4.2 Thread Pool Shutdown Now :link:[link](src/johnston/thread/basic/properties/executors/ThreadPoolShutdownNow.java)
If shutdownNow() is called, the thread pool will terminate all threads that are currently
running, and return the tasks that are not yet started.

#### 1.4.3 :warning: BlockingQueue :link:[link](src/johnston/thread/basic/properties/executors/ThreadBlockingQueue.java)
Blocking queue is a queue that if the size is empty and one thread requires dequeue, then it
blocks that thread (wait) until one element is available to dequeue. Each thread pool needs
a blocking queue.

Blocking queue has several implementations:
- <i>ArrayBlockingQueue</i>: queue implemented by array. The order is FIFO. Size must be defined.

- <i>LinkedBlockingQueue</i>: queue implemented by linked-list. The order is FIFO. Size can be
  defined. If not, then the size is unlimited.

- <i>PriorityBlockingQueue</i>: a priority queue that can take in comparator. If no comparator
  provided, then use the natural order of the elements. Size is unlimited.

- <i>DelayQueue</i>: like a blocking queue. Elements can dequeue only if they are expired. It
  can be used in producer-consumer scenario.
  The elements stored in DelayQueue must implement Delayed interface. The element that will
  expire first will be dequeued first. It cannot dequeue unexpired elements.

- <i>SynchronousQueue</i>: a queue with size 1. This queue allows two threads exchange data
  thread-safely.
    - take(): if the queue has no data, then the caller will be waiting until data available.
    - poll(): like take(), but if no data available, then it returns null instead of waiting.
    - put(E e): the caller has to wait until another thread takes away the data.
    - offer(E e): enqueue data only if another thread is waiting for data.
      -isEmpty(): ALWAYS return true!

This demo contains usage of four types of BlockingQueue implementations.

#### 1.4.4 Thread Pool Hook Methods :link:[link](src/johnston/thread/basic/properties/executors/ThreadPoolHookMethods.java)
ThreadPoolExecutor has three hood methods:
 - void beforeExecute(Thread t, Runnable target): this method runs before each task begins.
 - afterExecute(Runnable target, Throwable t): this method runs after each task begins.
 - terminated(): this method runs when the pool is shutdown.

 These three methods can be overridden to do some tasks, like customize environment, clean up
 data, etc.

 This demo overrides these three methods to record the total runtime of all Runnable tasks. The
 task will sleep random milli sec.

#### 1.4.5 Thread Pool Rejection Policy
 - <i>DiscardOldestPolicy()</i>: dequeue & discard one task to make room
 - <i>AbortPolicy()</i>: throw RejectedExecutionException
 - <i>CallerRunsPolicy()</i>: thread who submit task run task itself.
 - <i>DiscardPolicy()</i>: silent AbortPolicy().
 - self-defined policy: implements the method <i>rejectedExecution</i> in the interface RejectedExecutionHandler.

Demo see 1.2.6.

#### 1.4.6 Thread Pool Properly Shutdown :link:[link](src/johnston/thread/basic/properties/executors/ThreadPoolProperShutDown.java)
General way to shut down a thread pool:
 - Call shutdown() to stop accepting new task;
 - awaitTermination(long timeout, TimeUnit unit) to wait for the existing task to finish.
 - If timeout, call shutdownNow() to force all task finished.
 - Iteratively call shutdownNow() -- awaitTermination()

<br />
<a name="thread_communications"></a>

## 2. Thread Communications

#### 2.1 (Danger) Stop a thread :link:[link](src/johnston/thread/communications/ThreadStop.java)
Calling stop() method of a thread can immediately terminate that thread.

This operation is not safety. If a stopped thread holds a lock, then it
would not release it. If the thread which is about to stop is using database, then it would
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
When threadA calls threadB.interrupt(), threadB can receive an interruption signal, and
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

#### 2.6 Thread Waiting :link:[link](src/johnston/thread/communications/YieldThread.java)
Calling wait() method of a lock can let it the thread who holds this lock release it, so another
thread can enter the critical section.

In this Demo, thread A will enter the critical section first, but needs to wait 1 sec. Thread A
will release the lock and let thread B enter. Thread A will continue right after the wait()
method if other threads notify it, or the wait() is countdown.

When calling LOCK.wait(), the synchronized block holds LOCK, so it can release. If using other
objects to call wait(), then it will throw IllegalMonitorStateException.

#### 2.7 Difference between wait() and sleep()
It seems that these two methods have the same behavior: letting the current thread give up the CPU, but there
is a big difference behind it.

- A thread can sleep everywhere during execution, while a thread can wait only in the critical section.
- Suppose a thread is holding a lock, if it sleeps, it will hold the lock; if it waits, it will release the lock.
- Sleep is a method in Thread class, like an util method. Wait is a method called by the lock object (monitor) which is 
occupied, and let the thread which holds the lock wait. Of course the object lock, and the current thread inside the 
  critical section can be the same.

#### 2.8 Thread Notifying :link:[link](src/johnston/thread/communications/ThreadNotifying.java)
Calling notify() from a lock object can pick a thread from the waiting pool to start. The
waiting pool holds threads waiting for that lock. The thread that calls notify() is in RUNNING
state, and the awake thread is in BLOCKED state instead of WAITING. It will be the next one
to enter critical section.
<p>
In this demo, many producer threads and many consumers thread run at the same time. If a
consumer cannot get resource, it will wait. If a producer notice consumers are waiting, it
will notify them to wake up.

#### 2.9 DeadLock
Suppose variable varA associated with lock lockA, and variable varB associated with lock lockB; now thread tA holds
lockA and need varB, and thread tB holds lockB and need varA. Now none of them would release their lock, and they are in
<i>deadlock</i> state. They lock each other and won't make any progress.
``` java
// Thread A
public void run() {
  synchronized(lockA) {
    // Deadlock trapped here 
    synchronized(lockB) {
      varA.set(...);
      varB.set(...);
    }
  }
}

// Thread B
public void run() {
  synchronized(lockB) {
    // Deadlock trapped here 
    synchronized(lockA) {
      varA.set(...);
      varB.set(...);
    }
  }
}
```

Four conditions of deadlock:
 - Mutual exclusion exists: shared variable allows only one thread entered;
 - Hold and wait: threads waiting to access shared variable are blocked;
 - Non-preemptive allocation: only the lock holder can release the lock (no timeout);
 - Circular waiting (most important) : two threads holding one lock and waiting for each other's lock.

Missing any one of the four conditions can break the deadlock. However, condition 1, 2, and 3 are essential in thread
safety, so eliminating condition 4 is the best option.

``` java
public void run() {
  synchronized(lockB) {
    varB.set(...);
  }
  synchronized(lockA) {
    varA.set(...);
  }
}
```

#### 2.10 Resolving DeadLock  :link:[link](src/johnston/thread/communications/DeadLockResolution.java)
Util class ThreadMXBean can detect existing threads trapped in deadlock and return their thread
id. In this demo I first created two threads trapped in deadlock, then use ThreadMXBean to
locate and resolve their deadlock, letting them finish execution correctly.

However, the best way is to locate the deadlock before it happens!

#### 2.11 ThreadLocal :link:[link](src/johnston/thread/communications/ThreadLocalInnerCommunication.java)
<i>ThreadLocal</i> is a convenient way to ensure data safety in multithreading. It's like a hash map
where the key is the thread task id, and the value is the variable belonging to that thread task
only. It's a more efficient way to ensure data-racing free than using "synchronized" keyword.
ThreadLocal also helps decouple among multithreading methods and class when variable sharing is
required.

ThreadLocal is usually static and final. Static ensures it can be used across all threads, and
final ensures it won't be replaced causing threads losing their data.

Always use remove() method to remove instance when the current thread is about to terminate, because
ThreadLocal is static and final, so it will hold reference of a thread instance forever even though
it's terminated.

In this demo, each Runnable task has its own unique random number n, and it creates a variable
in the ThreadLocal object, then increment that variable n times. The result shows that
ThreadLocal would not mix the variables that each of them belongs to one Runnable task only.

Beware that ThreadLocal may implicitly couple classes.

#### 2.12 CountDownLatch Waiting :link:[link](src/johnston/thread/communications/CountDownLatchWaitBlocking.java)
<i>CountDownLatch</i> is a decremental counter for multithreading. It inits as an integer, and any
threads can call countDown() to make is decrement one time. Threads calling CountDownLatch::await
will be blocked until the counter is 0. It's like a join() method that can specify the location
of exit-joining point instead of waiting the joined thread terminated.

#### 2.13 CountDownLatch All Threads Starting Together :link:[link](src/johnston/thread/communications/CountDownLatchWaitingToBegin.java)
This is a different usage of CountDownLatch. Instead of letting the calling wait() thread to
wait until decrement to 0, let all threads starts at the same time by calling await()!

#### 2.14 LockSupport Util Class  :link:[link](src/johnston/thread/communications/LockSupportDemo.java)
LockSupport is a util class provided by JUC. It allows a thread to sleep and let other threads
to wake it up explicitly. It's like using Thread.sleep() but allows other thread to wake it up,
and wound not throw interruption when interrupted. So it's more flexible than sleep().

<br />
<a name="thread_safety_locking"></a>

## 3. Locking and Thread Safety

### 3.1 Basics

#### 3.1.1 Data Racing :link:[link](src/johnston/thread/thread_safety_and_locking/DataRacing.java)
If a set of operation is not <i>atomic</i>, then <i>race condition</i> may have. Race condition can cause <i>data racing</i>.

This demo shows that incremental operation <i>var++</i> is not thread-safety.

#### 3.1.2 Lock Strategy Comparison :link:[link](src/johnston/thread/thread_safety_and_locking/LockStrategyComparison.java)
Exclusively locking critical section can avoid data racing, and comes with performance overhead.
So minimizing the critical section execution code and using more flexible locking strategy can
help to improve performance.

This demo compares performance on two different strategies of locking: using one lock for all
synced variable, and each variable uses one specific lock.

#### 3.1.3 Synchronized Static Method  :link:[link](src/johnston/thread/thread_safety_and_locking/StaticMethodLock.java)
Using synchronized keyword to modify static method is different to modify non-static method.
Synchronized non-static method is object-level lock, while synchronized static method is
class-level lock.

This demo shows that threads from two different objects can access a synchronized non-static
method at the same time, while synchronized static method allows only one thread entered at
the same time.

### 3.2 Producer-Consumer Problem
Producer-Consumer Problem is same as Bounded-Buffer Problem. Producers and consumers share a same buffer. Producers
put data to the buffer while consumers take data from it. It requires:
- Producers and consumers should work concurrently.
- The buffer capacity is limited.
- Producers cannot put data to the buffer when it's full.
- Consumers cannot take any data when the buffer is empty, and won't take duplicated data.
- When producers and consumers are waiting, their threads should not be blocked.

#### 3.2.1 Producer-Consumer Prep. :link:[link](src/johnston/thread/thread_safety_and_locking/consumer_producer/components)
This package contains:
- Producer and produce action (Callable);
- Consumer and consume action (Callable);
- Buffer interface and its implementations.

The architecture is:

<p>
<b>Producer</b> --> <b>Produce Action</b> --> <b>Buffer</b> --> <b>Consume Action</b> --> <b>Consumer</b>
</p>

#### 3.2.2 Bad Producer-Consumer  :link:[link](src/johnston/thread/thread_safety_and_locking/consumer_producer/BadConsumingProducing.java)
Demo of producer and consumer using not thread-safe buffer. If there are more than one producer
and more than one consumer, then race condition exists at:
- Between buffer.get() size check and get -- size != 0 but get null, or index out of bound;
- Between buffer.put() size check and put -- size > capacity.

This is because both check size and get element operation are atomic, but putting them together
is not! Same as check size and put element operation.

#### 3.2.3 Synced Read & Write Producer-Consumer  :link:[link](src/johnston/thread/thread_safety_and_locking/consumer_producer/SyncedConsumingProducing.java)
Implementation of thread-safe data buffer. FIFO. Simply sync the produce and consume method.
It serializes the read and write method, which has a great performance penalty. It also exists
useless query: if the buffer is full, then reject to insert; if the buffer is empty, then return
null.

#### 3.2.4 Blocking Producer-Consumer  :link:[link](src/johnston/thread/thread_safety_and_locking/consumer_producer/BlockingConsumingProducing.java)
A better version of consumer-producer. It blocks the consumer if the buffer is empty, and blocks
the producer if the buffer is full. It allows producer and consumer run concurrently and avoid
useless inquiry that rejecting producer and returning null to consumer.

#### 3.2.5 Using Explicit Lock :link:[link](src/johnston/thread/thread_safety_and_locking/consumer_producer/ExplicitLockConsumingProducing.java)
Using ReentrantLock which is a explicit lock to ensure thread-safety. It uses Java lightweight 
lock (spinlock) to block threads instead of heavyweight lock.

### 3.3 JUC Atomic Variable
Package java.util.concurrent.atomic provides several atomic variables to guard thread-safety. They use volatile variable and java
lightweight lock to ensure no data racing. Lightweight lock is optimistic lock which use spin lock to block the waiting threading.
Since operations on single variable are not time-consuming, so it's much more efficient than using heavyweight lock. Heavyweight 
lock needs to switch to OS kernel mode to perform thread scheduling, so using heavyweight lock to guard single variable
thread-safety has very high performance penalty.

#### 3.3.1 Atomic Primitives  :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/AtomicIntegerDemo.java)
There are three primitive types in JUC atomic package: int, long, and boolean. They ensure
operations like increment, decrement, set are all atomic, so it's thread safe.

This demo shows that AtomicInteger is thread-safety.

#### 3.3.2 Atomic Array  :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/AtomicArrayDemo.java)
Atomic array has three components: integer, long, and reference array. They guarantee each
element in the array is thread-safety.

#### 3.3.3 Atomic Reference  :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/AtomicReferenceDemo.java)
There are three reference types in JUC atomic package: reference, stamped reference, and marked
reference. AtomicReference can ensure that referencing the object can always be atomic. The
AtomicStampedReference is like adding an integer as version or mark on the object like
<Object, Integer>. The AtomicMakrkableReference is like <Object, Boolean>.

In Java, assigning variable a value is atomic. The source code shows that the set method of
AtomicReference is simply an equal mark, but its lazySet, getAndSet, compareAndSet, and
weakCompareAndSet methods guarantee to be atomic since they involve two operations. They use CAS
to ensure thread-safety which uses low-level OS calls from the package <i>unsafe</i>.

This demo shows that getAndSet() method is atomic. There are ten threads which concurrently
record the previous version, and update a new version on the atomic reference using
getAndSet(). The atomic reference can guarantee that all records return from getAndSet() are
consistent.

Be careful! Modifying the object referenced by AtomicReference is not atomic!

#### 3.3.4 Atomic Object Field Update   :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/AtomicObjectFieldUpdater.java)
Atomic filed updater can ensure modifying fields in an object is atomic. The filed to update must be public volatile.

### 3.4 ABA Problem

#### 3.4.1 ABA Problem   :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/ABAProblem.java)
The ABA problem can happen in multithreading synchronization. When thread A reads the shared
value twice and got the same value, and thread A considers this value has not changed. However,
during these two reads, another thread B had changed the value and then change it back. Thread
A cannot notice this changes and still perform executions.

This demo shows the effect of ABA problem in linked list operation. Thread A performs remove head
operation, and thread B perform modify the head.next operation. The result shows that all
remaining elements of the linked list are lost even though thread A performs "atomic check".

#### 3.4.2 Solving ABA Problem   :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/StampedRefNoABA.java)
Using AtomicStampedReference to flag the inconsistency of the synced object can avoid ABA
problem. It requires that every operation on the stamped reference should update the version
variable as well.

AtomicMarkableReference is a simplified AtomicStampedReference. It can only tell if the variable is
modified once or not.

### 3.5 LongAdder  :link:[link](src/johnston/thread/thread_safety_and_locking/juc_atomic/LongAdderDemo.java)
AtomicInteger can have hotspot problem when too many threads competing each other to get it.
Since AtomicInteger uses lightweight spinning lock to block waiting threads, so when too many
waiting threads it will consume a lot of CPU resource to run the spinning lock.
 
One of the solution is to use <i>LongAdder</i>. LongAdder is like ThreadLocal which assign a
separate variable to each thread. It returns the sum of all separated variable when calling
sum() method. It greatly avoids hotspot problem on AtomicInteger and maintains Atomicity. It's
a tradeoff on time-space.
 
This demo shows that LongAdder has much better performance than AtomicInteger.

### 3.6 :warning: Keyword volatile  :link:[link](src/johnston/thread/thread_safety_and_locking/VolatileDemo.java)
The three main problems in multithreading are: <b>atomicity</b>, <b>visibility</b>, and <b>sequencing</b>. Atomicity
already introduced above.

In modern memory architecture, memory has several levels from low read/write speed to high one. In general, shared variables
are in the main memory since they reside in the heap of JVM memory. When several threads run concurrently in several 
CPU cores, and they need to read the shared variables, then it will read from the main memory and store a copy in the local 
cache.

The visibility problem: if one thread has modified the value in its local cache, can other threads notice this change.

Modern compiler and CPU exercise out-of-order execution on instructions. This can greatly improve performance. However,
in multithreading, the out-of-order execution may cause side effects if the shared variable is dependent to the
instructions.

The sequencing problem: if the correctness of program relies on the order of instruction execution, how to ensure the order
of execution.

Variable modified by keyword <i>volatile</i> has memory barrier which prevent compiler and CPU from
out-of-order executing, and forcing write-through policy when the value is modified. volatile cannot be with keyword final.

In this demo, if we remove the volatile keyword, then the spinlock will never unlock.

Beware that keyword volatile does not guarantee atomicity!

### 3.7  :warning: Explicit Lock
Java object lock uses OS system call to perform thread locking. The JUC package provides several alternate explicit locks to
ensure thread-safety. Explicit lock does not make system call but to use Java execution like spinlock to lock thread. So all
locking operations like lock, unlock, wait, and notify need to code explicitly.

Using explicit lock is complicated than object lock, but it has better performance and more flexible locking policy like 
non-blocking lock.

#### 3.7.1 Lock Interface in JUC.locks
Lock interface is the entrance of explicit lock, like the monitor of object.

 - lock.lock(): acquires the lock if possible, otherwise block the thread;
 - lock.lockInterruptibly(): like lock(), and can throw exception when interrupt waiting;
 - lock.tryLock(): non-blocking lock, return boolean immediately. False if no lock available;
 - lock.unlock(): release the lock;
 - lock.newCondition(): return the Condition instance bounded to the current Lock instance.

#### 3.7.2 Condition Interface in JUC.locks
Each Condition interface bounds to exactly one Lock interface. It has three methods only:

 - condition.await(): let the thread which holds the lock wait;
 - condition.await(timeout, TimeUnit): timed await();
 - condition.signal(): wake up a waiting thread;
 - condition. signalAll(): wake up all waiting threads.

#### 3.7.3 Semantics of Explicit Lock Methods
Explicit lock is very similar to Java object locks:

 - lock.lock() --> enter synchronized(obj);
 - lock.unlock() --> exit synchronized(obj);
 - lock.tryLock() --> if synchronized() block is not available, then skip it;
 - condition.await() --> obj.wait();
 - condition.await(timeOut) --> obj.wait(timeOut); 
 - condition.signal() --> obj.notify();
 - condition.signalAll() --> obj.notifyAll().

#### 3.7.4 Implementation Details
 - lock() - unlock() should follow this pattern:
    ``` java
    Lock lock = new SomeLock();
    lock.lock();
    // No code between lock.lock() and try block to avoid throwing exception which
    // will skip the lock.unlock() method.
    try {
      // Enter the critical section
      // or 
      // condition.await();
    } finally {
     lock.unlock(); // The lock will always unlock.
    }
    ```
 - tryLock() - unlock()
    ``` java
    Lock lock = new SomeLock();
   
    if (lock.tryLock()) {
       try {
          // Enter the critical section
        } finally {
         lock.unlock(); // The lock will always unlock.
        }
    } else {
     // Do other things if the lock is not available
   }
    ```
#### 3.7.5 ReentrantLock on Producer-Consumer (See 3.2.5)
ReentrantLock allows a thread to lock multiple times in the critical section:
   ``` java
    Lock lock = new SomeLock();
    lock.lock();
    lcok.lock(); // Second time locking

    try {
    // Enter the critical section
    // or 
    // condition.await();
    } finally {
    lock.unlock();
    lock.unlock(); // Second time unlocking needed
    }
   ```

As an explicit lock, ReentrantLock does not use heavyweight lock. 

#### 3.7.6 Optimistic Lock vs. Pessimistic Lock
<i>Pessimistic Lock</i> considers that threads entering critical section are not "reliable", so it allows only
one thread to enter critical section exclusively and blocks other threads. It has a high performance
overhead in multithreading task. Java built-in object lock and ReeentrantLock are pessimistic lock.

<i>Optimistic Lock</i> uses loosely policy that using version record to track the shared variable status when reading
or writing it to ensure its atomicity. It allows multiple threads to read at the same time. ReentrantReadWriteLock is
an optimistic lock.

For implementation of ReentrantReadWriteLock, check out my Thread-safe linked list & hash map repo :link:[link](https://github.com/ZhianMai/Thread-safe-LinkedList-Hashmap)

#### 3.7.7 Fair Lock vs. Nonfair Lock  :link:[link](src/johnston/thread/thread_safety_and_locking/FairLockDemo.java)
Fair lock distributes locks in the order of waiting queue, while nonfair lock allows thread to jump ahead of the waiting queue
and acquire the lock, which may lead to some threads' starvation. However, fair lock has performance overhead to maintain
fairness.

Nonfair lock has better performance. Suppose thread A just released the lock, and it needs to pick the next candidate thread.
If the next candidate thread stays in the same CPU core as thread A, then it can skip context switching step.

Run this demo multiple times, and the fair lock can produce waiting timeout threads. If the
CPU has great performance, increase the THREAD_AMOUNT variable.

So far I could not prove the fairness of the fair look because it seems that unfair lock holds fairness as well!

#### 3.7.8 Semaphore  :link:[link](src/johnston/thread/thread_safety_and_locking/SemaphoreDemo.java)
Semaphore is a shared lock that holds a set of permit. Threads which can acquire one or more
permits from semaphore can enter the critical section guarded by semaphore. Threads also can
release one or more permits to the semaphore.

Semaphore is not a mutual exclusive lock. If one malicious thread wants to enter the critical
section guarded by semaphore, it can just release a permit for itself!

In this demo the number of permit in semaphore is 2, so each period (five seconds) only two
tasks are running.

#### 3.7.9 CyclicBarrier  :link:[link](src/johnston/thread/thread_safety_and_locking/CyclicBarrierDemo.java)
Util class CyclicBarrier is like reusable CountdownLatch. It's a barrier that can keep all
caller threads to wait at the barrier point until all ready, then notifies all threads.

#### 3.7.10 ReadWriteLock  :link:[link](https://github.com/ZhianMai/Thread-safe-LinkedList-Hashmap/blob/main/src/main/java/johnston/hashmap/MyHashMapReentrantImpl.java)
ReadWriteLock provides two separate locks: read lock and write lock
``` java
    private final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
    private final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();
```
ReadWriteLock can ensure:
 - Threads holding READ_LOCK are not mutually exclude each other;
 - A thread holding READ_LOCK mutually exclude a thread holding WRITE_LOCK;
 - Only one thread can hold READ_LOCK.

It allows acquiring read lock after acquiring the write lock, but cannot do vice versa.

For demo see my Thread-safe hash map repo.

#### 3.7.11 StampledLock
An alternate implementation of ReentrantReadWriteLock. It supports optimistic read lock (no lock on reading)
before the first time acquiring the write lock. It provides a time stamp to check the validity of
optimistic read lock. 

Its usage is complicated...

<br />
<a name="thread_safe_container"></a>

## 4. Java Thread-safe Container

### 4.1 Thread-safe Data Structure in java.util
The java.util package provides several thread-safe data structures which use keyword synchronized.
- <i>Vector</i>: like an ArrayList;
- <i>Stack</i>: LIFO list;
- <i>HashTable</i>: like a HashMap
``` java
Vector<Integer> vector = new Vector<>();
Stack<Integer> stack = new Stack<>();
Map<Integer, Integer> table = new Hashtable<>();
```
These data structures have high performance overhead because they use heavyweight lock.

### 4.2 Wrapper Method in Collections
<i>Collections</i> class provides several static methods which can covert the not thread-safe List and Map 
data structure to thread-safe one, including HashMap, LinkedList, etc. It simply uses synchronized keyword
to wrap the original methods.
``` java
 List<String> list = new ArrayList<String>();
 List<String> syncedList = Collections.synchronizedList(list);

 Map<String, Integer> map = new HashMap<>();
 Map<String, Integer> syncedMap = Collections.synchronizedMap(map);
 
 // more...
```
### 4.3 JUC High Concurrency Container
JUC provides a set of container which supports high concurrency based on lock-free design using CAS and
keyword volatile. CAS ensures atomicity, and volatile ensures visibility.

#### 4.3.1 CopyOnWriteArrayList
"Copy on Write" means:
 - let the read thread (accessor) read from the original data;
 - let the write thread (mutator) modify on a copied data, then its volatile reference points to the copied data.
   It allows only one mutator at a time.

``` java
List<Integer> syncedList = new CopyOnWriteArrayList();
syncedList.add(1);
//...
```
CopyOnWriteArrayList is not suitable for heavy-write environment, since it needs to make a copy on each
mutation. It does not hold any read lock at all!

#### 4.3.2 BlockingQueue
See 1.4.3.

Recommend using ArrayBlockingQueue rather than LinkedBlockingQueue, because it would not generate node instance.

#### :warning: 4.3.3 ConcurrentHashMap
The design idea behind ConcurrentHashMap is a masterpiece in multithreading. By default, it separates the hashMap into 16
sub-hashMap (entries), and each entry has its own lock. It's like a distributed cluster with a load balancer. It can
significantly balance the loading to avoid hotspot problem.

<br />
<a name="thread_safe_design_pattern"></a>

## 4. Thread-safe Design Patterns
In high concurrency situation, many threads may read a shared variable at the same time, and context-switching may happen
in anywhere. Some design patterns like Singleton, which is safe in single-threaded environment, may not be safe here.

### 4.1 Singleton

#### 4.1.1 Unsafe Singleton
A general lazy-init Singleton class is like: 

``` java
 public class UnsafeSingleton {
   private UnsafeSIngleton() {}
   private static final UnsafeSingleton INSTANCE;
   
   public static UnsafeSingleton getInstance() {
     if (INSTANCE == null) {
       // Problem 1#
       INSTANCE = new UnsafeSIngleton();
     }
     return INSTANCE;
   }
 }
```

This class is not thread-safe. Before INSTANCE init, it may have multiple threads calling getInstance() at
the same time, while initializing variable is not atomic, so it may create multiple instances.

#### 4.1.2 Singleton with Synced Double-checked Init
``` java
 public class BetterSingleton {
   private BetterSingleton() {}
   private static final BetterSingleton INSTANCE;
   
   public static BetterSingleton getInstance() {
     if (INSTANCE == null) {
         synchronized(BetterSingleton.class) {
           if (INSTANCE == null) {
             INSTANCE = new BetterSingleton();
           } 
         }
     }
     return INSTANCE;
   }
 }
```

The init block of this singleton class uses synchronized block and double check designs.
- If multiple threads call getInstance() at the beginning, it ensures they get into the init block
one-by-one.
- After the first thread init the INSTANCE, the other threads can still enter the init block but can
not init INSTANCE anymore.
- Later on the sync block won't bother the running.

However, there is still a problem here: the constructor init is not atomic, how if during the initialization
context-switch happens?

#### 4.1.3 Thread-safe Static Inner Class Singleton  :link:[link](src/johnston/thread/concurrency/design_pattern/StaticInnerClassSingleton.java)
This singleton class ensures singleton instance init is lazy, atomic, and only once. The code is short and clear.

### 4.2 Master-Worker Pattern
The master class is responsible for:
- accepting task from clients;
- distributing work to worker class;
- gathering result from worker class;
- returning result to clients.

It's like the leader znode and worker znodes in zookeeper. The core idea of this pattern is divide-and-conquer. 

### 4.3 Fork-join Pattern   :link:[link](src/johnston/thread/concurrency/design_pattern/forkjoin/public class SumCalculationTask extends RecursiveTask<Long> {
.java)
Fork-join pattern is like master-worker pattern without master node. It breaks the master task into parallel subtasks
recursively until reach the base case, like merge sort. Subtasks are push to the FIFO blocking queue, and available 
threads (CPU core) would get the subtask to execute. Fork-join is designed for CPU-bounded tasks.

Java JUC package provides a set of Fork-join framework to support this pattern. See the link above for detailed
implementation demo.

### 4.4 Producer-consumer Pattern
See 3.2.

### 4.5 Future Pattern 
The core of future pattern is async call, which would not block the caller thread and return the result in the future to
the caller thread. The fork-join merge sort also includes this pattern as well.

#### 4.5.1 Async Callback Using Guava :link:[link](src/johnston/thread/concurrency/design_pattern/callback_guava/GuavaFutureDemo.java)
Cons of thread.join(): no return, and it's blocking.
Cons of futureTask.get(): it's blocking, although it returns value.

To achieve non-blocking  returning result from other threads, it needs some powerful libs (middleware). The Guava from Google comes 
to help!

The guava concurrent package provides a callback functionality to support non-blocking returning result. The thread crater A
needs to register a callback object first, then bind it with subtask thread B. When the subtask thread B finishes the task, it will
notify its callee A using the callback object. Thread A is never blocked.

FutureTask:
 - needs to explicitly get the result from other threads;
 - the get() process is blocking;

Guava callback:
 - other threads notify the caller thread if the result is ready;
 - the caller thread is never blocked.

<br />
<a name="multithreading_demo"></a>

## 5. Demos of Using Multithreading

### 5.1 Matrix Multiplication :link:[link](src/johnston/thread/demo/multi_threading/MatrixMultiplication.java)
Matrix multiplication is a computationally heavy task. Using multithreading to calculate matrix multiplication
is 4X faster than single-threaded. This demo uses thread pool to create worker threads.

User can define the CPU core amount and thread amount. The thread amount is how many working threads will be
created, and the CPU core amount is how many threads run concurrently.

### 5.2 Fork-join Merge Sort  :link:[link](src/johnston/thread/demo/multi_threading/merge_sort/ForkJoinMergeSort.java)
The recursion tree of fork-join is identical to the recursion tree of merge sort, so why not combine them together?
When the length of input array is 100000000, the fork-join merge sort is 3x faster than the single-threaded one.

### 5.3 Thread-safe HashMap :link:[link](https://github.com/ZhianMai/Thread-safe-LinkedList-Hashmap)
