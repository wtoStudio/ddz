package personal.wt.ddz.server;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import personal.wt.ddz.config.ServerConfig;
import personal.wt.ddz.core.GameManager;
import personal.wt.ddz.entity.Card;
import personal.wt.ddz.entity.Message;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.MessageType;
import personal.wt.ddz.enums.UserStatus;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static personal.wt.ddz.core.GameManager.*;

/**
 * @author ttb
 * 服务端类
 */
@Setter
@Getter
public class AppServer extends Observable {
    public static AppServer appServer = new AppServer();
    public ServerSocketChannel serverSocketChannel;
    public Selector selector;
    /**
     * 仅考虑最简单的模型：最多三个玩家
     */
    public Map<Integer, User> users = new ConcurrentHashMap(3);

    /**
     * 存放3张底牌
     */
    public List<Card> hiddenCardList = new ArrayList<>(3);

    private AppServer(){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(ServerConfig.IP, ServerConfig.PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("服务端已启动。。。");

        while(true){
            int select = 0;
            try {
                select = selector.select(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(select == 0){
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for(Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext();){
                SelectionKey selectionKey = it.next();
                if(selectionKey.isAcceptable()){
                    //接受客户端的连接
                    ServerSocketChannel ssChannel = (ServerSocketChannel) selectionKey.channel();
                    try {
                        SocketChannel socketChannel = ssChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(selectionKey.isReadable()){
                    //读取客户端发送过来的数据
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(MAX_MESSAGE_LENGTH);
                    int length = 0;
                    try {
                        length = socketChannel.read(buffer);
                    } catch (IOException e) {
                        SocketAddress remoteAddress;
                        try {
                            remoteAddress = socketChannel.getRemoteAddress();
                            System.err.println("与【" + remoteAddress + "】的连接断开了");
                            removeByIpAndPort(socketChannel);
                            socketChannel.close();
                            selectionKey.cancel();
                        } catch (IOException ex) {
                            //
                        }
                    }
                    String msgStr = new String(buffer.array(), 0, length);
                    if(!"".equals(msgStr)){
                        Message message = JSONObject.parseObject(msgStr, Message.class);
                        handleMessage(message, socketChannel);
                    }
                }
                it.remove();
            }
        }
    }

    public void handleMessage(Message message, SocketChannel excludeSocketChannel){
        if(message.getType() == MessageType.JOIN){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            if(users.size() >= 3){
                return;
            }
            //把新加入的用户存放到users集合中
            if(users.get(1) == null){
                user.setIndex(1);
                users.put(1, user);
            }else if(users.get(2) == null){
                user.setIndex(2);
                users.put(2, user);
            }else if(users.get(3) == null){
                user.setIndex(3);
                users.put(3, user);
            }else{
                System.out.println("房间已满，用户【" + user.getName() + "】未加入成功");
            }

            System.out.println("用户【" + user.getName() + "】加入房间");
            //给其他客户端发送消息，通知有新用户加入了，使客户端能够更新自己的UI
            String json = JSONObject.toJSONString(users);
            Message m = Message.builder()
                    .type(MessageType.ALL_JOINED)
                    .content(json)
                    .build();
            dispatchMessage(m, null);
        }else if(message.getType() == MessageType.READY || message.getType() == MessageType.UNREADY){
            String userId = message.getContent();
            User user = findUserById(userId);
            Message msg = null;
            if(message.getType() == MessageType.READY){
                user.setStatus(UserStatus.READY);
                if(this.users.size() == 3
                        && this.users.get(1).getStatus() == UserStatus.READY
                        && this.users.get(2).getStatus() == UserStatus.READY
                        && this.users.get(3).getStatus() == UserStatus.READY){
                    GameManager.gameManager.dealCard(users.get(1), users.get(2), users.get(3), hiddenCardList);
                    Map<Integer, List<Card>> map = new HashMap<>(4);
                    map.put(1, this.users.get(1).getCardList());
                    map.put(2, this.users.get(2).getCardList());
                    map.put(3, this.users.get(3).getCardList());
                    map.put(4, this.hiddenCardList);
                    msg = Message.builder().type(MessageType.DEAL_CARD).content(JSONObject.toJSONString(map)).build();
                    //dispatchMessage(m, null);
                    this.users.get(1).setStatus(UserStatus.PLAYING);
                    this.users.get(2).setStatus(UserStatus.PLAYING);
                    this.users.get(3).setStatus(UserStatus.PLAYING);
                }else{
                    msg = Message.builder().type(MessageType.READY_OK).content(JSONObject.toJSONString(user)).build();
                }
            }else if(message.getType() == MessageType.UNREADY){
                user.setStatus(UserStatus.IDLE);
                msg = Message.builder().type(MessageType.UNREADY_OK).content(JSONObject.toJSONString(user)).build();
            }
            dispatchMessage(msg, null);
        }
    }

    public void dispatchMessage(Message message, SocketChannel excludeSocketChannel){
        Set<SelectionKey> keys = selector.keys();
        for(SelectionKey selectionKey : keys){
            SelectableChannel channel = selectionKey.channel();
            if(channel instanceof SocketChannel && excludeSocketChannel != channel){
                SocketChannel socketChannel = (SocketChannel) channel;
                try {
                    socketChannel.write(ByteBuffer.wrap(JSONObject.toJSONString(message).getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeByIpAndPort(SocketChannel socketChannel) throws IOException {
        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
        if(remoteAddress == null){
            return;
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
        String ip = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        users.forEach((userId, user) -> {
            if(user.getIp().equals(ip) && user.getPort() == port){
                users.remove(userId);
                System.out.println("玩家【" + user.getName() + "】退出了房间");
                //退出房间时， 给剩下的所有客户端发消息，告知有人退出了
                String json = JSONObject.toJSONString(user);
                Message message = Message.builder()
                        .type(MessageType.EXIT)
                        .content(json)
                        .build();
                dispatchMessage(message, socketChannel);
            }
        });
    }

    private User findUserById(String userId){
        Set<Map.Entry<Integer, User>> entries = users.entrySet();
        for(Map.Entry<Integer, User> entry : entries){
            User user = entry.getValue();
            if(user.getId().equals(userId)){
                return user;
            }
        }
        throw new RuntimeException("服务端找不不到id=[" + userId + "]的玩家");
    }

    public static AppServer getInstance(){
        return appServer;
    }

    public static void main(String[] args) {
        AppServer.getInstance();
    }
}