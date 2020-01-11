package personal.wt.ddz.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author lenovo
 */
@Getter
public class SocketClient {

    private static SocketClient socketClient = new SocketClient();
    private SocketChannel socketChannel;
    private Selector selector;

    private SocketClient() {
        boolean finishConnect = false;
        //连接服务器
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("192.168.40.118", 9305));
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            finishConnect = socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(finishConnect && socketChannel.isConnected()){
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
            ExecutorService pool = new ThreadPoolExecutor(5, 200,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
            pool.submit(() -> {
                while(true){
                    int select = 0;
                    try {
                        select = selector.select(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(select <= 0){
                        continue;
                    }
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    for(Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext();){
                        SelectionKey selectionKey = it.next();
                        if(selectionKey.isReadable()){
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            try {
                                length = channel.read(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("服务端返回数据：" + new String(buffer.array(), 0, length));
                        }
                    }
                }
            });
        }
    }

    public static SocketClient getInstance(){
        return socketClient;
    }
}
