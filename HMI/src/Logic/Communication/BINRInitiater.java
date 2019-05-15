package Logic.Communication;

import java.util.ArrayList;
import java.util.List;

public class BINRInitiater {
    private List<BINRListener> binrListeners = new ArrayList<BINRListener>();

    public void addBINRListener(BINRListener toAdd) {
        binrListeners.add(toAdd);
    }

    public void onResponseReceived() {
        // Notify everybody that may be interested.
        for (BINRListener hl : binrListeners)
            hl.responseReceived();
    }
}
