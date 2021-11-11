package johnston.thread.thread_safety_and_locking.consumer_producer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.SyncSafeDataBufferImpl;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.ConsumeRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.Consumer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.ProduceRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.Producer;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An simple way to make producer-consumer thread safe: sync put() and get() method. It's similar
 * to have only one producer and one consumer since put() and get() allows at most one thread at a
 * time. This solution serializes producer and consumer action, forcing them to be single-threaded.
 * The performance penalty is very high.
 */
public class SyncedConsumingProducing {
  public static void main(String[] args) {
    DataBuffer<Integer> unsafeDataBuffer = new SyncSafeDataBufferImpl<>();
    ProduceRandomIntAction produceAction = new ProduceRandomIntAction(unsafeDataBuffer);
    ConsumeRandomIntAction consumeAction = new ConsumeRandomIntAction(unsafeDataBuffer);
    Producer slowProducer = new Producer("Slow Producer", produceAction, 5000);
    Producer fastProducer = new Producer("Fast Producer", produceAction, 1000);
    Consumer slowConsumer = new Consumer("Slow Consumer", consumeAction, 5000);
    Consumer fastConsumer = new Consumer("Fast Consumer", consumeAction, 5000);
    int cpuCoreAmount = 4;

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        cpuCoreAmount,
        cpuCoreAmount * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(cpuCoreAmount),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    for (int i = 0; i < 1; i++) {
      threadPool.execute(slowProducer);
      threadPool.execute(fastProducer);
      threadPool.execute(slowConsumer);
      threadPool.execute(fastConsumer);
    }
  }
}
