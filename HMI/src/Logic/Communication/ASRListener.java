package Logic.Communication;

// An interface to be implemented by everyone interested in "Hello" events
public interface ASRListener {
    void onPositionResponseReceived(boolean succeeded);
    void onGetPositionReceived(byte x, byte y);
}
