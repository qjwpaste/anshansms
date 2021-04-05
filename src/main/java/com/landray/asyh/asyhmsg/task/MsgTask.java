package com.landray.asyh.asyhmsg.task;

import com.landray.asyh.asyhmsg.beans.KKSMSSEND;
import com.landray.asyh.asyhmsg.dao.MsgDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@EnableScheduling
@Service
public class MsgTask {
    @Autowired
    MsgDao msgDao;

    Logger logger= LoggerFactory.getLogger(MsgTask.class);

    @Value("${sendSmsTask.socketIP}")
    String socketIP;

    @Value("${sendSmsTask.socketPort}")
    Integer socketPort;

    // 每隔30秒执行一次
    //@Scheduled(cron ="0/30 * * * * ?")
    @Scheduled(cron="${sendSmsTask.time}")
    public void selectUnusedMsg(){
        //查询状态为未发起的kk信息
        List<KKSMSSEND> kkSmsSendList = msgDao.selectUnusedMsg();
        System.out.println("查询结果："+kkSmsSendList);
        logger.debug("查询结果："+kkSmsSendList);
        for (Iterator<KKSMSSEND> msgit = kkSmsSendList.iterator(); msgit.hasNext(); ) {
            KKSMSSEND msg =  msgit.next();
            sendMsg(msg);
        }
    }

    /**
     * Socket通讯发送报文
     * @param message
     * @return
     */
    public String socketSend(String message) {
        String backMsg = "";
        try {
            //Socket socket = new Socket("11.14.1.6", 5001);
            Socket socket = new Socket(socketIP, socketPort);
            // 向服务端程序发送数据
            OutputStream ops = socket.getOutputStream();
            OutputStreamWriter opsw = new OutputStreamWriter(ops);
            BufferedWriter bw = new BufferedWriter(opsw);

            bw.write(message);
            bw.flush();

            // 从服务端程序接收数据
            InputStream ips = socket.getInputStream();
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String s = "";
            while ((s = br.readLine()) != null) {
                backMsg += s;
            }

            br.close();
            bw.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("SocketSend_error:"+e.getMessage());
        }
        return backMsg;
    }

    public void sendMsg(KKSMSSEND msg) {
        try {
            // 获取验证码手机号和姓名
            //String code = String.valueOf((int)(Math.random()*900000 + 100000));
            String code = msg.getContent().substring(0,6);
            String phoneNO = msg.getRecivePhone();
            String name = msg.getReceiverName();
            System.out.println("生成的验证码："+code+"；名字："+name+"；手机号："+phoneNO);
            logger.debug("生成的验证码："+code+"；名字："+name+"；手机号："+phoneNO);
            // 生成报文需要信息
            String msdid = "ASBANKOA";
            String data = "{\"name\":\""+name+"\",\"code\":\""+code+"\",\"phoneNO\":\""+phoneNO+"\"}";
            String guid = msdid + new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()) + System.currentTimeMillis();
            System.out.println("msdid："+msdid+"；data："+data+"；guid："+guid);
            logger.debug("msdid："+msdid+"；data："+data+"；guid："+guid);
            // 拼接报文，用^B隔开；报文前加6位报文长度，不足补0
            // String message = msdid+"^B"+data+"^B"+guid+"^B";
            String message = msdid+"\002"+data+"\002"+guid+"\002"; // \002是^B字符？
            message = "SMS001" + String.format("%06d", message.length()) + message;
            System.out.println("报文："+message);
            logger.debug("报文："+message);
            // 调用短信接口
            String ret = socketSend(message);
            System.out.println("调用短信接口结果："+ret);
            logger.debug("调用短信接口结果："+ret);
            int i=0;
            //根据发送短信状态更新KK_SMS_SEND
            if("0000".equals(ret)){
                System.out.println("msgDao:"+msgDao);
                i = msgDao.updateSmsState(msg);
            }
            if(i==1){
                System.out.println("更新信息状态："+msg.getId()+"成功");
                logger.debug("更新信息状态："+msg.getId()+"成功");
            }else{
                System.out.println("更新信息状态："+msg.getId()+"失败");
                logger.debug("更新信息状态："+msg.getId()+"失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("sendMsg_error:"+e.getMessage());
        }

    }

}
