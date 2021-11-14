package johnston.thread.thread_safety_and_locking.consumer_producer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.WaitingSafeDataBufferImpl;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.ConsumeRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer.Consumer;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.ProduceRandomIntAction;
import johnston.thread.thread_safety_and_locking.consumer_producer.components.producer.Producer;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A better version of consumer-producer. It blocks the consumer if the buffer is empty, and blocks
 * the producer if the buffer is full. It allows producer and consumer run concurrently and avoid
 * useless inquiry that rejecting producer and returning null to consumer.
 */
public class BlockingConsumingProducing {
  public static void main(String[] args) {
    DataBuffer<Integer> waitingDataBuffer = new WaitingSafeDataBufferImpl<>();
    ProduceRandomIntAction produceAction = new ProduceRandomIntAction(waitingDataBuffer);
    ConsumeRandomIntAction consumeAction = new ConsumeRandomIntAction(waitingDataBuffer);
    Producer slowProducer = new Producer("Slow Producer", produceAction, 500);
    Producer fastProducer = new Producer("Fast Producer", produceAction, 100);
    Consumer slowConsumer = new Consumer("Slow Consumer", consumeAction, 500);
    Consumer fastConsumer = new Consumer("Fast Consumer", consumeAction, 100);
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
      threadPool.execute(slowProducer);
      threadPool.execute(fastProducer);
      threadPool.execute(slowConsumer);
      threadPool.execute(fastConsumer);
    }

    threadPool.shutdown();
  }
}
