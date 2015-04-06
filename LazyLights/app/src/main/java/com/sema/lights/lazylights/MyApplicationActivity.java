package com.sema.lights.lazylights;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug;
import android.widget.Button;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.  
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MyApplicationActivity extends Activity implements OnClickListener {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "lazylights";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
        Button randomBtn, allOffBtn, morgenmadBtn, timerBtn, colaBtn;
        randomBtn = (Button) findViewById(R.id.buttonRand);
        allOffBtn = (Button) findViewById(R.id.buttonAllOff);
        morgenmadBtn  = (Button) findViewById(R.id.buttonMorgenmad);
        timerBtn = (Button) findViewById(R.id.button1min);
        colaBtn = (Button) findViewById(R.id.buttonCola);
        randomBtn.setOnClickListener(this);
        allOffBtn.setOnClickListener(this);
        morgenmadBtn.setOnClickListener(this);
        timerBtn.setOnClickListener(this);
        colaBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final PHBridge bridge = phHueSDK.getSelectedBridge();

        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        switch (v.getId()) {
            case R.id.buttonRand:
                int[] stue = new int[]{0, 2};

                for (int i : stue) {
                    PHLightState _ls0 = new PHLightState();
                    PHLight light = allLights.get(i);
                    int ic = new Random().nextInt(MAX_HUE);
                    _ls0.setHue(ic);
                    //Log.w("random", Integer.toString(ic));
                    _ls0.setOn(true);
                    _ls0.setBrightness(200);
                    _ls0.setSaturation(200);
                    //String validState = lightState.validateState();
                    //Log.w("Lights", light.toString());
                    bridge.updateLightState(light, _ls0, listener);
                }
                break;

            case R.id.buttonAllOff:
                PHLightState _ls1 = new PHLightState();
                _ls1.setOn(false);
                bridge.setLightStateForDefaultGroup(_ls1);
                break;

            case R.id.buttonMorgenmad:
                getMorgenMad(bridge, allLights);
                break;


            case R.id.button1min:
                PHLightState _lst = new PHLightState();
                _lst.setOn(true);
                _lst.setHue(5000);
                bridge.updateLightState(allLights.get(0), _lst, null);

                long ONE_MINUTE_IN_MILLIS = 6000;//millisecs;
                long t = new Date().getTime();
                Date date2 = new Date(t + ONE_MINUTE_IN_MILLIS);
                Log.w("timer starts", date2.toString());

                PHSchedule schedule = new PHSchedule("1-min timer");
                PHLightState _lst2 = new PHLightState();
                _lst2.setOn(false);
                schedule.setLightState(_lst2);
                schedule.setLightIdentifier("0");
                schedule.setDate(date2);  // Create your date object here with your desired start time
                bridge.createSchedule(schedule, new PHScheduleListener() {
                    @Override
                    public void onCreated(PHSchedule phSchedule) {
                        Log.w(TAG, "1-min created?");
                    }

                    @Override
                    public void onSuccess() {
                        Log.w(TAG, "1-min success?");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.w(TAG, "1-min error");
                    }

                    @Override
                    public void onStateUpdate(Map<String, String> stringStringMap, List<PHHueError> phHueErrors) {
                        Log.w(TAG, "1-min timer is over");
                        // turn on another light
                        //bridge.updateLightState(allLights.get(1), lightState, null);
                    }
                });
                break;

            case R.id.buttonCola:
                PHLightState _lsc = new PHLightState();
                _lsc.setOn(false);
                bridge.setLightStateForDefaultGroup(_lsc);
                int cola_allowed=(Math.random() < 0.6) ? 0 : 1;
                //Log.w(TAG, Integer.toString(cola_allowed));
                PHLight light = allLights.get(0);
                float[] xy;
                if (cola_allowed == 1)
                    xy = PHUtilities.calculateXYFromRGB(0, 255, 0, light.getModelNumber());
                else
                    xy = PHUtilities.calculateXYFromRGB(255, 0, 0, light.getModelNumber());
                PHLightState _ls = new PHLightState();
                _ls.setX(xy[0]);
                _ls.setY(xy[1]);
                _ls.setOn(true);
                bridge.updateLightState(light, _ls, null);
                break;
        }
    }

    void getMorgenMad(PHBridge bridge, List<PHLight> allLights){
        PHLightState _lsm = new PHLightState();
        _lsm.setOn(true);
        PHLight light = allLights.get(2);
        _lsm.setHue(36000);
        _lsm.setBrightness(200);
        _lsm.setSaturation(200);
        bridge.updateLightState(light, _lsm, null);

    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };


    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
