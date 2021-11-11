package johnston.thread.thread_safety_and_locking.consumer_producer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.UnsafeDataBufferImpl;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.ConsumeRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.Consumer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.ProduceRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.Producer;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Demo of producer and consumer using not thread-safe buffer. If there are more than one producer
 * and more than one consumer, then race condition exists at:
 * - Between buffer.get() size check and get -- size != 0 but get null, or index out of bound;
 * - Between buffer.put() size check and put -- size > capacity.
 *
 * This is because both check size and get element operation are atomic, but putting them together
 * is not! Same as check size and put element operation.
 */
public class BadConsumingProducing {
  public static void main(String[] args) {
    DataBuffer<Integer> unsafeDataBuffer = new UnsafeDataBufferImpl<>();
    ProduceRandomIntAction produceAction = new ProduceRandomIntAction(unsafeDataBuffer);
    ConsumeRandomIntAction consumeAction = new ConsumeRandomIntAction(unsafeDataBuffer);
    Producer producer = new Producer("Producer A", produceAction, 10);
    Consumer consumer = new Consumer("Consumer A", consumeAction, 10);
    int cpuCoreAmount = 4;

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        cpuCoreAmount,
        cpuCoreAmount * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(cpuCoreAmount),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    for (int i = 0; i < 2; i++) {
      threadPool.execute(producer);
      threadPool.execute(consumer);
    }
  }
}
