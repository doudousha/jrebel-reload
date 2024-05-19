package com.example.plugin;

import okhttp3.*;
import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
注意: 使用maven assembly 打包，而不是package打包

-Drebel.plugins=D:\\plugin-template-1.0.0.jar -Drebel.log.stdout=true -Drebel.myq.timeout=3600  -Drebel.myq.wsUrl=ws://127.0.0.1:8780

-Drebel.plugins=D:\\plugin-template-1.0.0.jar -Drebel.jrebel-hooks-plugin=true -Drebel.log.stdout=true  -Drebel.myq.message.url=http://192.168.10.116/pub?id=123
-Drebel.plugins=D:/code/jrebel-reload/code/jrebel-plugin-templateV2/target/plugin-template-1.0.0-jar-with-dependencies.jar -Drebel.log.stdout=true  -Drebel.myq.message.url=http://192.168.10.116/pub?id=123

-Drebel.plugins=D:/code/jrebel-reload/code/jrebel-plugin-templateV2/target/plugin-template-1.0.0-jar-with-dependencies.jar -Drebel.log.stdout=true  -Drebel.myq.message.url=http://192.168.10.116/pub?id=123 -Drebel.xml=D:/code/myq-desgin-ktor/code/myq/design-start/src/main/resources/rebel.xml
参考：
调用项目的classLoad: https://github.com/atsu85/jrebel-hooks-plugin
jrebel参数配置：https://manuals.jrebel.com/jrebel/misc/agentsettings.html


 */
public class PluginTemplate implements Plugin {
  private  OkHttpClient okHttpClient;
  private String url;
  private final static AtomicBoolean locked = new AtomicBoolean(false);

  private final  AtomicReference<List<TimerTask>> tasks = new AtomicReference<>();

  private final  AtomicReference<TimerTask> taskAtomic = new AtomicReference<>();
  private static Timer timer = new Timer();

  public PluginTemplate() {
    tasks.set(new ArrayList<TimerTask>());
  }

  private void sendMsg(){

  }

  private void registryEvent() {
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
        log("onClassEvent begin");
        TimerTask task = new TimerTask() {
          @Override
          public void run() {
            try {
              try {
                // okhtt3 发送消息
                String json = "{\"action\":\"classes.reload.success\"}";
                RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);
                Request request = (new Request.Builder())
                        .url(url)
                        .post(body)
                        .build();
                Response resp = okHttpClient.newCall(request).execute();
                log("通知更新代码:"+resp.code());
                //log("通知-"+url+": " + resp.code() +",");
              } catch (final Exception e) {
                log("异常："+e.getMessage());
              }finally {
                tasks.get().clear();
              }

            } catch (final Exception e) {
              log("异常：" + e.getMessage());
            }
          }
        };

        for (int i = 0; i < tasks.get().size(); i++) {
          boolean m = tasks.get().get(i).cancel();
        }

        // 600毫秒后执行任务
        timer.schedule(task, 600);
        tasks.get().add(task);

        log("重新加载了文件:" + klass.getName() + ",eventType:" + eventType+"");
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
    this.url = System.getProperty("rebel.myq.message.url");
    log("同步页面服务: rebel.myq.message.url=" + this.url);

    try {
      this.okHttpClient = (new OkHttpClient()).newBuilder()
              .readTimeout(3, TimeUnit.SECONDS)
              .build();

      registryEvent();
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
