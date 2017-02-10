package com.gimbal.adobe_analytics_integration;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import java.util.LinkedList;


public class AppService extends Service {
    public static final String APPSERVICE_STARTED_ACTION = "appservice_started";
    private static final int MAX_NUM_EVENTS = 100;
    private static final String GIMBAL_API_KEY = "PUT_YOUR_GIMBAL_API_KEY_HERE";
    private PlaceEventListener placeEventListener;
    private LinkedList<String> events;
    private GimbalAdobeAnalyticsAdapter gimbalAdobeAnalyticsAdapter;

    @Override
    public void onCreate(){
        setupAdobeIntegration();
        setupApplication();
    }

    private void setupAdobeIntegration() {
        gimbalAdobeAnalyticsAdapter = new GimbalAdobeAnalyticsAdapter();
        gimbalAdobeAnalyticsAdapter.startGimbalAndLogEvents(this.getApplication(),GIMBAL_API_KEY, true);
    }

    private void setupApplication() {
        events = new LinkedList<>(GimbalDAO.getEvents(getApplicationContext()));
        Gimbal.setApiKey(this.getApplication(), GIMBAL_API_KEY);
        placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                addEvent(String.format("Start Visit for %s", visit.getPlace().getName()));
            }
            @Override
            public void onVisitEnd(Visit visit) {
                addEvent(String.format("End Visit for %s", visit.getPlace().getName()));
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);
        Gimbal.start();
    }

    private void addEvent(String event) {
        while (events.size() >= MAX_NUM_EVENTS) {
            events.removeLast();
        }
        events.add(0, event);
        GimbalDAO.setEvents(getApplicationContext(), events);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notifyServiceStarted();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        gimbalAdobeAnalyticsAdapter.stopGimbalAndLogEvents();
        PlaceManager.getInstance().removeListener(placeEventListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyServiceStarted() {
        Intent intent = new Intent(APPSERVICE_STARTED_ACTION);
        sendBroadcast(intent);
    }
}
