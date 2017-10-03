package me.michaeljiang.mqttsimple.component;

/**
 * Created by MichaelJiang on 2017/5/20.
 */

public class ComponentSetting {
    /**MqttSetting**/
    public final static int MQTT_STATE_CONNECTED=1;
    public final static int MQTT_STATE_LOST=2;
    public final static int MQTT_STATE_FAIL=3;
    public final static int MQTT_STATE_RECEIVE=4;

    /**Bubdle**/
    public final static String TOPIC="topic";
    public final static String MESSAGE="message";
}
