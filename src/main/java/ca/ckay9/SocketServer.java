package ca.ckay9;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SocketServer extends WebSocketServer {
    private CxWar cx_war;

    public SocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    public SocketServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {

    }

    @Override
    public void onError(WebSocket connection, Exception exception) {
        exception.printStackTrace();
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        
    }

    @Override
    public void onStart() {
        Utils.getPlugin().getLogger().info("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}
