package Logic.Communication;

// An interface to be implemented by everyone interested in "Hello" events
public interface BINREventListener {
    void dropResponseReceived();
    void onLog(String log);
}
