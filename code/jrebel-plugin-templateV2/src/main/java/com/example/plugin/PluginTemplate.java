package com.example.plugin;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.*;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
注意: 使用maven assembly 打包，而不是package打包

-Drebel.plugins=D:\\plugin-template-1.0.0.jar -Drebel.log.stdout=true -Drebel.myq.timeout=3600  -Drebel.myq.wsUrl=ws://127.0.0.1:8780

参考：
调用项目的classLoad: https://github.com/atsu85/jrebel-hooks-plugin
jrebel参数配置：https://manuals.jrebel.com/jrebel/misc/agentsettings.html


 */
public class PluginTemplate implements Plugin {
  private  WsHandler ws;
  private int timeout = 3000;
  private String wsUrl = "";

  public PluginTemplate() {

  }

  private void registryEvent(){
    // 创建ClassEventListener对象
    Reloader reloader = ReloaderFactory.getInstance();
    reloader.addClassLoadListener(new ClassEventListener() {
      public void onClassEvent(int eventType, Class klass) {
        try {
          log("addClassLoadListener");
          log("加载了文件:" + klass.getName() + ",eventType:" + eventType);
        } catch (final Exception e) {

        }
      }
      @Override
      public int priority() {
        return 0;
      }
    });

    reloader.addClassReloadListener(new ClassEventListener() {
      public void onClassEvent(int eventType, @SuppressWarnings("rawtypes") Class klass) {
        try {
          log("addClassReloadListener");
          log("重新加载了文件:" + klass.getName() + ",eventType:" + eventType);

          ws.sendMsg("重新加载了文件");
        } catch (final Exception e) {

        }
      }

      @Override
      public int priority() {
        return 0;
      }

    });
  }

  @Override
  public void preinit() {
    log("PluginTemplate preinit 初始化=======================");

    registryEvent();

    String timeoutStr = System.getProperty("rebel.myq.timeout");
    String wsUrlStr = System.getProperty("rebel.myq.wsUrl");
    ws://127.0.0.1:8780


    log("缓冲执行时间: Drebel.myq.timeout=" + timeoutStr);
    if (timeoutStr != null && timeoutStr.trim().length() > 0) {
      this.timeout = Integer.parseInt(timeoutStr);
    }
    log("同步页面服务: Drebel.myq.wsUrl=" + wsUrlStr);
    if (wsUrlStr != null && wsUrlStr.trim().length() > 0) {
      this.wsUrl = wsUrlStr;
    }

    try {
      this.ws = WsHandler.init( this.wsUrl,  this.timeout);
      log("ws初始化成功");
    } catch (Exception e) {
      log("连接异常" + e);
    }

  }

  public void log(String str){
    try {

      LoggerFactory.getInstance().echo(str);
    } catch (Exception e) {
      LoggerFactory.getInstance().echo("error===========:" +str);
    }
  }



  @Override
  public boolean checkDependencies(ClassLoader cl, ClassResourceSource crs) {
    // check if plugin should be enabled in classloader cl
    return true;
  }

  @Override
  public String getId() {
    return "PluginTemplate";
  }

  @Override
  public String getName() {
    return "PluginTemplate";
  }

  @Override
  public String getDescription() {
    return "PluginTemplate";
  }

  @Override
  public String getAuthor() {
    return "PluginTemplate";
  }

  @Override
  public String getWebsite() {
    return "PluginTemplate";
  }

  @Override
  public String getSupportedVersions() {
    return null;
  }

  @Override
  public String getTestedVersions() {
    return null;
  }
}
