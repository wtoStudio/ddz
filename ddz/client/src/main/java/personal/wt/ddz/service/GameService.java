package personal.wt.ddz.service;

import com.alibaba.fastjson.JSONObject;
import personal.wt.ddz.entity.Message;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ttb
 */
public class GameService {

    private SocketChannel socketChannel = SocketClient.getInstance().getSocketChannel();

    public void sendMsg(Message message){
        String json = JSONObject.toJSONString(message);
        ByteBuffer buffer = ByteBuffer.wrap(json.getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientIp(){
        try {
            InetSocketAddress localAddress = (InetSocketAddress) socketChannel.getLocalAddress();
            return localAddress.getAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("获取客户端IP地址出错");
    }

    public int getClientPort(){
        try {
            InetSocketAddress localAddress = (InetSocketAddress) socketChannel.getLocalAddress();
            return localAddress.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("获取客户端端口出错");
    }
}
