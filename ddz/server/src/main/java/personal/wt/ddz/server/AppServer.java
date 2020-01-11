package personal.wt.ddz.server;

import com.alibaba.fastjson.JSONObject;
import personal.wt.ddz.entity.Message;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ttb
 */
public class AppServer {
    public static final String IP = "192.168.40.118";
    public static final int PORT = 9305;
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(IP, PORT));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端已启动。。。");

        while(true){
            int select = selector.select(1000);
            if(select == 0){
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for(Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext();){
                SelectionKey selectionKey = it.next();
                if(selectionKey.isAcceptable()){
                    //接受客户端的连接
                    ServerSocketChannel ssChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = ssChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    //读取客户端发送过来的数据
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int length = socketChannel.read(buffer);
                    String msgStr = new String(buffer.array(), 0, length);
                    JSONObject.parseObject(msgStr, Message.class);
                    System.out.println("服务端接收到数据：" + msgStr);
                }
                it.remove();
            }
        }
    }
}
