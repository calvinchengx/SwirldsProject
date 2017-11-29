import com.swirlds.platform.*;

import java.nio.charset.StandardCharsets;

public class SwirldsAppMain implements SwirldMain {

    public Platform platform;

    public int selfId;

    public Console console;

    public final int sleepPeriod = 100; // milliseconds

    /**
     * This is just for debugging: it allows the app to run in Eclipse. If the config.txt exists and lists a
     * particular SwirldMain class as the one to run, then it can run in Eclipse (with the green triangle
     * icon).
     *
     * @param args
     *            these are not used
     */
    public static void main(String[] args) {
        Browser.main(null);
    }

    @Override
    public void init(Platform platform, int i) {
        this.platform = platform;
        this.selfId = i;
        this.console = platform.createConsole(true);
        platform.setAbout("Hello Swirld v 1.0\n");
        platform.setSleepAfterSync(sleepPeriod);
    }

    @Override
    public void run() {
        String myName = platform.getState().getAddressBookCopy()
                .getAddress(selfId).getSelfName();

        console.out.println("Hello Swirld! from " + myName);

        // create a transaction. For this example app,
        // we will define each transactions to simply
        // be a string in UTF-8 encoding.
        byte[] transaction = myName.getBytes(StandardCharsets.UTF_8);

        // Send the transaction to the Platform, which will then
        // forward it to the State object.
        // The Platform will also send the transaction to
        // all the other members of the community during syncs with them.
        // The community as a whole will decide the order of the transactions
        platform.createTransaction(transaction, null);
        String lastReceived = "";

        while (true) {
            SwirldsAppState state = (SwirldsAppState) platform
                    .getState();
            String received = state.getReceived();

            if (!lastReceived.equals(received)) {
                lastReceived = received;
                console.out.println("Received: " + received); // print all received transactions
            }
            try {
                Thread.sleep(sleepPeriod);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void preEvent() {

    }

    @Override
    public SwirldState newState() {
        return new SwirldsAppState();
    }
}
