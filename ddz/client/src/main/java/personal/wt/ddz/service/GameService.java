package personal.wt.ddz.service;

import personal.wt.ddz.entity.Message;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ttb
 */
public class GameService {

    private SocketChannel socketChannel = SocketClient.getInstance().getSocketChannel();

    public void sendMsg(Message message){
        ByteBuffer buffer = ByteBuffer.wrap(message.toString().getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
