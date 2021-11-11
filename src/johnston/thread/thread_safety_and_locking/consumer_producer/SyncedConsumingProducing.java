package johnston.thread.thread_safety_and_locking.consumer_producer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.SyncSafeDataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.UnsafeDataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.ConsumeRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.Consumer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.ProduceRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.Producer;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SyncedConsumingProducing {
  public static void main(String[] args) {
    DataBuffer<Integer> unsafeDataBuffer = new SyncSafeDataBuffer<>();
    ProduceRandomIntAction produceAction = new ProduceRandomIntAction(unsafeDataBuffer, 1000);
    ConsumeRandomIntAction consumeAction = new ConsumeRandomIntAction(unsafeDataBuffer);
    Producer producer = new Producer("Producer A", produceAction);
    Consumer consumer = new Consumer("Consumer A", consumeAction, 1000);
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
