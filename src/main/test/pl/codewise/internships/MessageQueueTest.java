package pl.codewise.internships;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class MessageQueueTest {

    private MessageQueueComponent messageQueueComponent;

    @BeforeEach
    public void initialize() {
        messageQueueComponent = new MessageQueueComponent(5, 5);
    }

    @Test
    public void tooManyAutoRemoveTest() {
        addMessages(140);

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(6));
        } catch (InterruptedException ex) {
            fail("Thread interrupted");
        }

        assertEquals(100, messageQueueComponent.snapshot().getMessages().size());
    }

    @Test
    public void tooOldMessagesAutoRemoveTest() {
        Message theOldestMessage = new Message("useragent", 200, System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));
        Message firstUnremovedMessage = new Message("test message useragent", 200, System.currentTimeMillis() - 100);

        messageQueueComponent.add(theOldestMessage);
        messageQueueComponent.add(firstUnremovedMessage);

        addMessages(40);

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(6));
        } catch (InterruptedException ex) {
            fail("Thread interrupted");
        }

        assertEquals(41, messageQueueComponent.snapshot().getMessages().size());
        assertEquals(firstUnremovedMessage, messageQueueComponent.snapshot().getMessages().get(0));

    }

    @Test
    public void messagesWithErrorCodeCountTest() {

        addMessages(10, 404);
        addMessages(10, 200);

        assertEquals(10, messageQueueComponent.numberOfErrorMessages());

    }

    @Test
    public void snapshotLessThan100Test() {

        for (int i = 0; i < 10; i++) {
            messageQueueComponent.add(new Message("", 200, System.currentTimeMillis()));
        }

        Snapshot snapshot = messageQueueComponent.snapshot();

        List<Message> messageList = snapshot.getMessages();

        assertEquals(10, messageList.size());

    }

    @Test
    public void snapshotMoreThan100Test() {

        addMessages(300);

        Message lastMessage = new Message("", 202, System.currentTimeMillis());
        messageQueueComponent.add(lastMessage);

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(6));
        } catch (InterruptedException ex) {
            fail("Thread interrupted");
        }

        Snapshot snapshot = messageQueueComponent.snapshot();

        List<Message> messageList = snapshot.getMessages();

        assertEquals(100, messageList.size());
        assertEquals(lastMessage, messageList.get(0));

    }

    private void addMessages(int count, int code) {
        for (int i = 0; i < count; i++) {
            messageQueueComponent.add(new Message("", code, System.currentTimeMillis()));
        }
    }

    private void addMessages(int count) {
        addMessages(count, 200);
    }

}