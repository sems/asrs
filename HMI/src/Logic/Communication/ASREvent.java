package Logic.Communication;

import java.util.ArrayList;
import java.util.List;

public class ASREvent {
    private List<ASREventListener> asrListeners = new ArrayList<>();

    public void addASRListener(ASREventListener toAdd) {
        asrListeners.add(toAdd);
    }

    public void onPositionResponseReceived(ErrorCode ec) {
        // Notify everybody that may be interested.
        for (ASREventListener hl : asrListeners)
            hl.onPositionResponseReceived(ec);
    }

    public void onGetPositionReceived(byte x, byte y) {
        // Notify everybody that may be interested.
        for (ASREventListener hl : asrListeners)
            hl.onGetPositionReceived(x, y);
    }

    void onLog(String log) {
        for (ASREventListener hl : asrListeners)
            hl.onLog(log);
    }

    void onUnloadResponseReceived() {
        for (ASREventListener hl : asrListeners)
            hl.onUnloadResponseReceived();
    }
}
