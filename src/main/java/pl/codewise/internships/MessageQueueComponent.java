package pl.codewise.internships;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class MessageQueueComponent implements MessageQueue {

    private List<Message> messages;

    private static ScheduledExecutorService scheduledExecutorService = newScheduledThreadPool(1);


    public MessageQueueComponent(int initialMaintenanceDelay, int maintenancePeriod) {
        this.messages = Collections.synchronizedList(new LinkedList<>());
        Runnable task = () -> {
            removeOlderThanFiveMin();
            removeUnnecessaryMessages();
        };

        scheduledExecutorService.scheduleAtFixedRate(task, initialMaintenanceDelay, maintenancePeriod, TimeUnit.SECONDS);
    }

    @Override
    public void add(Message message) {
        messages.add(0, message);
    }

    @Override
    public Snapshot snapshot() {
        return new Snapshot(
                messages.subList(0, Math.min(messages.size(), 100))
        );
    }

    @Override
    public long numberOfErrorMessages() {
        return messages.stream()
                .filter(x -> x.getErrorCode() >= 400)
                .count();
    }

    private void removeOlderThanFiveMin() {

        long currentTime = System.currentTimeMillis();

        messages = messages.parallelStream()
                .filter(x -> currentTime - x.getTime() <= TimeUnit.MINUTES.toMillis(5))
                .collect(Collectors.toList());

    }

    private void removeUnnecessaryMessages() {
        if (messages.size() > 100) {
            messages = messages.subList(0, 100);
        }
    }

}
