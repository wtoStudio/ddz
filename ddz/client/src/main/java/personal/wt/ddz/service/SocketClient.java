package personal.wt.ddz.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import personal.wt.ddz.entity.Message;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.MessageType;
import personal.wt.ddz.ui.GamePanel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static personal.wt.ddz.core.GameManager.MAX_MESSAGE_LENGTH;

/**
 * @author lenovo
 */
@Getter
public class SocketClient {

    private static SocketClient socketClient = new SocketClient();
    private SocketChannel socketChannel;
    private Selector selector;
    private static final String SERVER_IP = "192.168.40.118";
    private static final int SERVER_PORT = 9305;

    private SocketClient() {
        boolean finishConnect = false;
        //连接服务器
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            finishConnect = socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(finishConnect && socketChannel.isConnected()){
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("pool-%d").build();
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
                            ByteBuffer buffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH);
                            int length = 0;
                            try {
                                length = channel.read(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String msgStr = new String(buffer.array(), 0, length);
                            Message message = JSONObject.parseObject(msgStr, Message.class);
                            handleMsg(message);
                        }
                    }
                }
            });
        }
    }

    public static SocketClient getInstance(){
        return socketClient;
    }

    private void handleMsg(Message message){
        GamePanel gamePanel = GamePanel.getInstance();
        User localUser = gamePanel.getLocalUser();
        User prevUser = gamePanel.getPrevUser();
        User nextUser = gamePanel.getNextUser();
        if(message.getType() == MessageType.ALL_JOINED){
            String content = message.getContent();
            Map<String, JSONObject> map = JSONObject.parseObject(content, Map.class);
            System.out.println(map);
            map.forEach((k, v) -> {
                User user = v.toJavaObject(User.class);
                //更新 gamePanel对象中 localUser, prevUser, nextUser属性的值
                if(user.getIndex() < localUser.getIndex()){
                    gamePanel.setPrevUser(user);
                }else if(user.getIndex() > localUser.getIndex()){
                    gamePanel.setNextUser(user);
                }else if(user.getIndex() == localUser.getIndex()){
                    gamePanel.setLocalUser(user);
                }
            });
            gamePanel.repaint();
        }else if(message.getType() == MessageType.READY){

        }else if(message.getType() == MessageType.EXIT){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            if(user.getId().equals(prevUser.getId())){
                gamePanel.setPrevUser(null);
            }else if(user.getId().equals(nextUser.getId())){
                gamePanel.setNextUser(null);
            }
            gamePanel.repaint();
        }
    }
}
