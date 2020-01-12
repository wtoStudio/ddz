package personal.wt.ddz.server;

import com.alibaba.fastjson.JSONObject;
import personal.wt.ddz.entity.Message;
import personal.wt.ddz.entity.User;
import personal.wt.ddz.enums.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static personal.wt.ddz.core.GameManager.MAX_MESSAGE_LENGTH;

/**
 * @author ttb
 */
public class AppServer {
    public static final String IP = "192.168.40.118";
    public static final int PORT = 9305;
    public static ServerSocketChannel serverSocketChannel;
    public static Selector selector;
    /**
     * 仅考虑最简单的模型：最多三个玩家
     */
    public static Map<String, User> users = new ConcurrentHashMap(3);
    public static volatile int index = 0;
    public static void main(String[] args) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(IP, PORT));
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
                        e.printStackTrace();
                        SocketAddress remoteAddress = null;
                        try {
                            remoteAddress = socketChannel.getRemoteAddress();
                            System.err.println("与【" + remoteAddress + "】的连接断开了");
                            socketChannel.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        selectionKey.cancel();
                        removeByIpAndPort(remoteAddress);
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


    public static void handleMessage(Message message, SocketChannel excludeSocketChannel){
        if(message.getType() == MessageType.JOIN){
            String content = message.getContent();
            User user = JSONObject.parseObject(content, User.class);
            user.setIndex(index++);
            if(users.size() >= 3){
                return;
            }
            //把新加入的用户存放到users集合中
            users.put(user.getId(), user);

            //给其他客户端发送消息，通知有新用户加入了，使客户端能够更新自己的UI
            String json = JSONObject.toJSONString(users);
            Message m = Message.builder()
                    .type(MessageType.ALL_JOINED)
                    .content(json)
                    .build();
            dispatchMessage(m, null);
        }else if(message.getType() == MessageType.READY){

        }
    }

    public static void dispatchMessage(Message message, SocketChannel excludeSocketChannel){
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

    public static void removeByIpAndPort(SocketAddress socketAddress){
        if(socketAddress == null){
            return;
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
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
                dispatchMessage(message, null);
            }
        });
    }
}
