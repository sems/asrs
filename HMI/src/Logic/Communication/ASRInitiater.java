package Logic.Communication;

import java.util.ArrayList;
import java.util.List;

public class ASRInitiater {
    private List<ASRListener> asrListeners = new ArrayList<ASRListener>();

    public void addASRListener(ASRListener toAdd) {
        asrListeners.add(toAdd);
    }

    public void onPositionResponseReceived(boolean succeeded) {
        // Notify everybody that may be interested.
        for (ASRListener hl : asrListeners)
            hl.onPositionResponseReceived(succeeded);
    }

    public void onGetPositionReceived(byte x, byte y) {
        // Notify everybody that may be interested.
        for (ASRListener hl : asrListeners)
            hl.onGetPositionReceived(x,y);
    }
}
