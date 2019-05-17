package Logic.Communication;

import java.util.ArrayList;
import java.util.List;

public class BINREvent {
    private List<BINREventListener> binrEventListeners = new ArrayList<BINREventListener>();

    public void addBINRListener(BINREventListener toAdd) {
        binrEventListeners.add(toAdd);
    }

    public void onResponseReceived() {
        // Notify everybody that may be interested.
        for (BINREventListener hl : binrEventListeners)
            hl.responseReceived();
    }
}
