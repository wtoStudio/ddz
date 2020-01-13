package personal.wt.ddz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import personal.wt.ddz.config.ServerConfig;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.entity.Message;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.MessageType;
import personal.wt.ddz.enums.UserStatus;
import personal.wt.ddz.ui.GamePanel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import static personal.wt.ddz.core.GameManager.MAX_MESSAGE_LENGTH;

/**
 * @author ttb
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
            socketChannel.connect(new InetSocketAddress(ServerConfig.IP, ServerConfig.PORT));
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
                        it.remove();
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
            Map<Integer, JSONObject> map = JSONObject.parseObject(content, Map.class);
            getMyIndex(map, localUser);
            int localUserIndex = localUser.getIndex();

            //------only for testing
            map.forEach((k, v) -> System.out.println(v.toJavaObject(User.class)));

            map.forEach((k, v) -> {
                User user = v.toJavaObject(User.class);
                //更新 gamePanel对象中 localUser, prevUser, nextUser属性的值
                if(localUserIndex == 1){
                    if(user.getIndex() == 3){
                        gamePanel.setPrevUser(user);
                    }else if(user.getIndex() == 2){
                        gamePanel.setNextUser(user);
                    }
                }else if(localUserIndex == 2){
                    if(user.getIndex() == 1){
                        gamePanel.setPrevUser(user);
                    }else if(user.getIndex() == 3){
                        gamePanel.setNextUser(user);
                    }
                }else if(localUserIndex == 3){
                    if(user.getIndex() == 2){
                        gamePanel.setPrevUser(user);
                    }else if(user.getIndex() == 1){
                        gamePanel.setNextUser(user);
                    }
                }
            });
            gamePanel.repaint();
        }else if(message.getType() == MessageType.READY_OK){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            if(user.getId().equals(localUser.getId())){
                gamePanel.getReadyBtn().setText("取消准备");
                localUser.setStatus(UserStatus.READY);
            }else if(user.getId().equals(prevUser.getId())){
                prevUser.setStatus(UserStatus.READY);
            }else if(user.getId().equals(nextUser.getId())){
                nextUser.setStatus(UserStatus.READY);
            }
            gamePanel.repaint();
        }else if(message.getType() == MessageType.UNREADY_OK){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            if(user.getId().equals(localUser.getId())){
                gamePanel.getReadyBtn().setText("准备");
                localUser.setStatus(UserStatus.IDLE);
            }else if(user.getId().equals(prevUser.getId())){
                prevUser.setStatus(UserStatus.IDLE);
            }else if(user.getId().equals(nextUser.getId())){
                nextUser.setStatus(UserStatus.IDLE);
            }
            gamePanel.repaint();
        }else if(message.getType() == MessageType.EXIT){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            if(user.getId().equals(prevUser.getId())){
                gamePanel.setPrevUser(null);
            }else if(user.getId().equals(nextUser.getId())){
                gamePanel.setNextUser(null);
            }
            gamePanel.repaint();
        }else if(message.getType() == MessageType.DEAL_CARD){
            localUser.setStatus(UserStatus.PLAYING);
            prevUser.setStatus(UserStatus.PLAYING);
            nextUser.setStatus(UserStatus.PLAYING);
            String content = message.getContent();
            JSONObject jsonObject = JSONObject.parseObject(content);
            List<Card> hiddenCardList = ((JSONArray)jsonObject.get(4)).toJavaList(Card.class);
            gamePanel.setHiddenCardList(hiddenCardList);

            List<Card> localUserCardList = ((JSONArray)jsonObject.get(localUser.getIndex())).toJavaList(Card.class);
            localUser.setCardList(localUserCardList);

            List<Card> prevUserCardList = ((JSONArray)jsonObject.get(prevUser.getIndex())).toJavaList(Card.class);
            prevUser.setCardList(prevUserCardList);

            List<Card> nextUserCardList = ((JSONArray)jsonObject.get(nextUser.getIndex())).toJavaList(Card.class);
            nextUser.setCardList(nextUserCardList);

            gamePanel.repaint();
        }
    }

    /**
     * 获取当前玩家在服务端被分配到的位置
     * @param map
     * @return
     */
    private void getMyIndex(Map<Integer, JSONObject> map, User localUser){
        map.forEach((index, userJson) -> {
            User user = JSONObject.toJavaObject(userJson, User.class);
            String ip = user.getIp();
            int port = user.getPort();
            if(localUser.getIp().equals(ip) && localUser.getPort() == port){
                localUser.setIndex(index);
            }
        });
    }
}
