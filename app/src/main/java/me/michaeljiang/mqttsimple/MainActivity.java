package me.michaeljiang.mqttsimple;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.michaeljiang.mqttsimple.component.ComponentSetting;
import me.michaeljiang.mqttsimple.component.mqtt.MyMqtt;

public class MainActivity extends AppCompatActivity {
    /**MQTT相关参数**/
    private MyMqtt myMqtt;
    private static String host = "192.168.1.1";//主机地址
    private static String port = "1883";//MQTT端口(一般为1883)
    private static String userID = "";//用户ID(无可以不填)
    private static String passWord = "";//用户密码(无可以不填)
    private static String clientID = UUID.randomUUID().toString();//随机生成字符串，防止clientID冲突
    private static String TAG = "MainActivity";

    /**UI相关参数**/
    private EditText edt_pubTopic,edt_pubMsg,edt_subTopic,edt_host; //文本输入控件
    private Button btn_pub,btn_sub,btn_clear,btn_connect,btn_disConnect;//按钮控件
    private ListView listView;//列表视图
    private ArrayAdapter adapter;//列表视图填充器
    private List<String> message = new ArrayList<String>();//消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initMqtt();
    }

    private void init(){
        //通过id发现对应控件
        edt_pubTopic=(EditText)findViewById(R.id.edt_pubTopic);
        edt_pubMsg=(EditText)findViewById(R.id.edt_pubMessage);
        edt_subTopic=(EditText)findViewById(R.id.edt_subTopic);
        edt_host = (EditText)findViewById(R.id.edt_host);
        edt_host.setText(host);
        btn_pub=(Button)findViewById(R.id.btn_pub);
        btn_sub=(Button)findViewById(R.id.btn_sub);
        btn_clear=(Button)findViewById(R.id.btn_clear);
        listView = (ListView)findViewById(R.id.list_message);

        //设置列表格式
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,message);
        listView.setAdapter(adapter);

        //设置按钮点击动作
        btn_disConnect = (Button)findViewById(R.id.btn_disConnect);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMqtt.setMqttSetting(edt_host.getText().toString(),port,userID,passWord,clientID);//开启Mqtt连接
                myMqtt.connectMqtt();
            }
        });
        btn_disConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMqtt.disConnectMqtt();//断开Mqtt连接
            }
        });
        btn_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMqtt.pubMsg(edt_pubTopic.getText().toString(),edt_pubMsg.getText().toString().getBytes(),1);//发送消息
            }
        });
        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMqtt.subTopic(edt_subTopic.getText().toString(),2);//收听指定Topic
            }
        });
    }

    private void initMqtt(){
        myMqtt = new MyMqtt(new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what== ComponentSetting.MQTT_STATE_CONNECTED){
                    //当Mqtt连接成功时
                    Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"连接成功");
                }else if(msg.what==ComponentSetting.MQTT_STATE_LOST){
                    //当丢失连接后
                    Toast.makeText(MainActivity.this,"连接丢失，进行重连",Toast.LENGTH_SHORT).show();
                }else if(msg.what==ComponentSetting.MQTT_STATE_FAIL){
                    //当连接失败时
                    Toast.makeText(MainActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
                }else if(msg.what==ComponentSetting.MQTT_STATE_RECEIVE){
                    //当主线程收到数据后做什么
                    Bundle bundle = msg.getData();
                    message.add(bundle.getString(ComponentSetting.TOPIC)+":"+bundle.getString(ComponentSetting.MESSAGE));//向List添加数据
                    adapter.notifyDataSetChanged();//调用Adapter的notifyDataSetChanged，更改列表的显示
                }
                super.handleMessage(msg);
            }
        });
        myMqtt.setMqttSetting(host,port,userID,passWord,clientID);//设置当前Mqtt连接信息
    }


}
