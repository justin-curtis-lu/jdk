/*
 * copyright
 */

/*
 * @test
 * @bug 6181786
 * @summary TODO
 * @run junit test
 */

import org.junit.jupiter.api.Test;

import java.util.TimeZone;

// A Class used to send a message
class Sender {
    public void send(String id)
    {
        System.out.println("Default is \t" + TimeZone.getDefault());
        try {
            Thread.sleep(1000);
            System.out.println("Changing default");
            TimeZone.setDefault(TimeZone.getTimeZone(id));
        }
        catch (Exception e) {
            System.out.println("Thread  interrupted.");
        }
        System.out.println("Default is \t" + TimeZone.getDefault());
    }
}

// Class for send a message using Threads
class ThreadedSend extends Thread {
    private String msg;
    Sender sender;

    // Receives a message object and a string
    // message to be sent
    ThreadedSend(String m, Sender obj)
    {
        msg = m;
        sender = obj;
    }

    public void run()
    {
        // Only one thread can send a message
        // at a time.
        synchronized (sender)
        {
            // synchronizing the send object
            sender.send(msg);
        }
    }
}

// Driver class
class test {

    @Test
    public void myTest() {
        Sender send = new Sender();
        ThreadedSend S1 = new ThreadedSend(TimeZone.getAvailableIDs()[0], send);
        ThreadedSend S2 = new ThreadedSend(TimeZone.getAvailableIDs()[10], send);

        // Start two threads of ThreadedSend type
        S1.start();
        S2.start();

        // wait for threads to end
        try {
            S1.join();
            S2.join();
        }
        catch (Exception e) {
            System.out.println("Interrupted");
        }
    }
}