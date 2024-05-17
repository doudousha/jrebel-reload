package com.example.plugin;

import com.sun.jndi.toolkit.url.Uri;
import org.java_websocket.WebSocketFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.zeroturnaround.javarebel.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WsHandler extends WebSocketClient implements ActionListener {
    public WsHandler(URI serverUri) {
        super(serverUri);
    }

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        WsHandler ws = new WsHandler(new URI(""));
//        ws.send("重新加载了文件");
//        Thread.sleep(6 * 1000);
//
//        ws.send("重新加载了文件");
//        Thread.sleep(6 * 1000);
//
//        ws.send("重新加载了文件");
//        Thread.sleep(6 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(3 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(3 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(4 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(2 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(4 * 1000);

        ws.send("重新加载了文件");
        Thread.sleep(3 * 1000);

        ws.send("重新加载了文件");

        Thread.sleep(10 * 1000);
    }

    private Timer timer;
    /**
     * 间歇时间
     */
    private int invokeTimeout = 3000;

    private long lastClickTime = 0;
    private long now = System.currentTimeMillis();

    private String msg = "";

    public void sendMsg(String str) {
        this.msg = str;
        lastClickTime = System.currentTimeMillis();

        if (timer == null) {
            timer = new Timer(this.invokeTimeout, this);
        }
        timer.setRepeats(false);
        timer.start();
    }


    public static WsHandler init(String wsUri, int invokeTimeout) throws URISyntaxException {
        URI uri = new URI(wsUri);
        WsHandler handler = new WsHandler(uri);
        handler.invokeTimeout = invokeTimeout;
        // 连接
        handler.connect();

        return handler;
    }

    private String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return df.format(date);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        now = System.currentTimeMillis();
        LoggerFactory.getInstance().echo(now + "-" + lastClickTime);
        if (now - lastClickTime > invokeTimeout) {
            this.send( getDate()+" " + this.msg);
            LoggerFactory.getInstance().echo("发送ws消息==============" + this.msg);
            //System.out.println("发送");
        } else {
            // 重置计时器
            timer.restart();
        }
    }


    //websockt===================================

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
