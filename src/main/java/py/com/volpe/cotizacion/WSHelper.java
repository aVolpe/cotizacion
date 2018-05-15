package py.com.volpe.cotizacion;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Arturo Volpe
 * @since 5/15/18
 */
@Component
public class WSHelper {

    /**
     * This method is overly complicated
     * <p>
     * First we create a {@link StandardWebSocketClient}, and attach it to a manager along with a
     * handler, in this case an {@link AbstractWebSocketHandler} and we wait for the handleTextMessage for the
     * real message.
     * <p>
     * Obviously this API is created to be used in more complex scenarios, in this case we only need one message,
     * the message is automatically send (we don't need to ask for it).
     * <p>
     * The API is prepared to be used in an async environment, we make it 'sync' here using the wait/notify java
     * API.
     *
     * @return the json that the web service return
     */
    public String getDataWithoutSending(String url) {

        try {
            Object monitor = new Object();
            AtomicReference<String> val = new AtomicReference<>();

            StandardWebSocketClient swsc = new StandardWebSocketClient();
            WebSocketConnectionManager manager = new WebSocketConnectionManager(swsc, new AbstractWebSocketHandler() {
                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    val.set(message.getPayload());
                    session.close();
                    synchronized (monitor) {
                        monitor.notifyAll();
                    }
                }
            }, url);
            manager.setAutoStartup(true);
            manager.start();
            int retries = 4;
            String result = null;
            while (val.get() == null && retries-- > 0) {
                synchronized (monitor) {
                    monitor.wait(10000);
                    result = val.get();
                }
            }
            manager.stop();
            return result;
        } catch (Exception e) {
            throw new AppException(500, "can't connect to socket alberdi", e);
        }
    }
}
