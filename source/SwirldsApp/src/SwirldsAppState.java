import com.swirlds.platform.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This holds the current state of the swirld. For this simple "hello swirld" code, each transaction is just
 * a string, and the state is just a list of the strings in all the transactions handled so far, in the
 * order that they were handled.
 */
public class SwirldsAppState implements SwirldState {
    /**
     * The shared state is just a list of the strings in all transactions, listed in the order received
     * here, which will eventually be the consensus order of the community.
     */
    private List<String> strings = Collections
            .synchronizedList(new ArrayList<String>());
    /** names and addresses of all members */
    private AddressBook addressBook;

    /** @return all the strings received so far from the network */
    public synchronized List<String> getStrings() {
        return strings;
    }

    /** @return all the strings received so far from the network, concatenated into one */
    public synchronized String getReceived() {
        return strings.toString();
    }

    /** @return the same as getReceived, so it returns the entire shared state as a single string */
    public String toString() {
        return strings.toString();
    }

    // ///////////////////////////////////////////////////////////////////

    @Override
    public synchronized AddressBook getAddressBookCopy() {
        return addressBook.copy();
    }

    @Override
    public synchronized FastCopyable copy() {
        SwirldsAppState copy = new SwirldsAppState();
        copy.copyFrom(this);
        return copy;
    }

    @Override
    public void copyTo(FCDataOutputStream outStream) {
        try {
            Utilities.writeStringArray(outStream,
                    strings.toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void copyFrom(FCDataInputStream inStream) {
        try {
            strings = new ArrayList<String>(
                    Arrays.asList(Utilities.readStringArray(inStream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void copyFrom(SwirldState old) {
        strings = Collections.synchronizedList(
                new ArrayList<String>(((SwirldsAppState) old).strings));
        addressBook = ((SwirldsAppState) old).addressBook.copy();
    }

    @Override
    public synchronized void handleTransaction(long id, boolean consensus,
                                               Instant timeCreated, byte[] transaction, Address address) {
        strings.add(new String(transaction, StandardCharsets.UTF_8));
    }

    @Override
    public void noMoreTransactions() {
    }

    @Override
    public synchronized void init(Platform platform, AddressBook addressBook) {
        this.addressBook = addressBook;
    }
}

