# Multi-threading in Java

This repo is a collection of multi-threading concept demo in Java.

## Contents
- Thread basics
  - Thread Creation: Thread class, Runnable interface, and async thread.
- Thread communications
- Critical section, data racing, atomicity, and locking
- producer-consumer, and semaphore

## 1. Thread Basics

### 1.1 Thread Creation 

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

#### 1.1.6 Async Thread with return value: Callable & FutureTask :link:[link](src/johnston/thread/basic/creation/CallableThread.java)
Async thread: thread that runs async task, and supports return result. Basic Thread class and Runnable interface do not support return value on run() method.
- <i>Callable</i> interface: has only one method named call() which is like run() method in Runnable but can return value and throw exception. Callable object cannot be the target
  of Thread instance.
- <i>FutureTask</i> class: the connection between Callable interface and Thread instance.

#### 1.1.7 Thread pool :link:[link](src/johnston/thread/basic/creation/ExecutorPool.java)
Using <i>Executors</i> factory can create a thread pool. The thread pool can run Runnable thread or Callable thread.

The thread pool can better manage multi running threads, including limiting maximum concurrent threads, and utilize system resources.

<br />

### 1.2 Thread Properties

#### 1.2.1 Thread ID :link:[link](src/johnston/thread/basic/properties/ThreadID.java)
Each thread has a unique ID assigned by the JVM.

To get the id of a thread, call:
- threadA.getId();
- Thread.currentThread().getId();

#### 1.2.2 Thread Naming  :link:[link](src/johnston/thread/basic/properties/ThreadNaming.java)
Each thread has its name, either assigned by the JVM or by user.
To set the name of a thread:
- Pass in the name when calling constructor;
- Call threadA.setName("NewName");

To get the name of a thread, call threadA.getName().

#### 1.2.3 Thread Priority :link:[link](src/johnston/thread/basic/properties/ThreadPriority.java)
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

#### 1.2.4 Thread State :link:[link](src/johnston/thread/basic/properties/ThreadState.java)
A thread can have 6 different states, which represent 6 certain parts of its life cycle.

- NEW: a multi-threaded object is created, and before it runs;
- RUNNABLE: when a thread is running or scheduled to run (JVM called start(), and wait for CPU time slice);
- BLOCKED: when a thread is waiting to enter critical section;
- WAITING/TIMED_WAITING: a thread is called wait() ro sleep();
- TERMINATED: a thread is finished running.

This class simulates all these states of a thread.

#### 1.2.5 :warning: Difference of run() and start() :link:[link](src/johnston/thread/basic/properties/RunStartDiff.java)
run() method contains the main logic/task for a thread to run.
start() method make a thread start running run().
So simply calling run() directly would not start multi-threading, it just calls run() method in 
the current thread!

#### 1.2.6 Daemon Thread  :link:[link](src/johnston/thread/basic/properties/DaemonThread.java)
In general, a process would not exit execution as long as it has at least one running thread, but some threads are doing 
background task, like file auto saving thread, or producer thread using external resources. These threads should not block 
the process from exiting.

We can set these threads as daemon thread, so they won't bother the main thread when main is about to exit.

Call threadA.setDaemon(true) to mark it as a daemon thread. The default is false.