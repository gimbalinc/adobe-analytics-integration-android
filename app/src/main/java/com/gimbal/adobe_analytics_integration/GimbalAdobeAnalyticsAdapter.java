package com.gimbal.adobe_analytics_integration;

import android.app.Application;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GimbalAdobeAnalyticsAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GimbalAdobeAnalyticsAdapter.class);
    private GimbalPlaceEventListener placeEventListener;

    public void startGimbalAndLogEvents(Application application, String gimbalApiKey, boolean debugLoggingEnabled) {
        logger.info("starting gimbal and logging adobe place event analytics");
        Gimbal.setApiKey(application,gimbalApiKey);
        placeEventListener = new GimbalPlaceEventListener();
        PlaceManager.getInstance().addListener(placeEventListener);
        Gimbal.start();
        Config.setContext(application.getApplicationContext());
        Config.collectLifecycleData();
        Config.setDebugLogging(debugLoggingEnabled);
    }

    public void stopGimbalAndLogEvents() {
        logger.info("stoping gimbal");
        PlaceManager.getInstance().removeListener(placeEventListener);
    }

    private Map<String,Object> visitData(Visit visit) {
        Map<String,Object> visitData = new HashMap<>();
        visitData.put("gimbal.place.visit.visitID",visit.getVisitID());
        visitData.put("gimbal.place.visit.placeName",visit.getPlace().getName());
        visitData.put("gimbal.place.visit.placeID",visit.getPlace().getIdentifier());
        return visitData;
    }

    class GimbalPlaceEventListener extends PlaceEventListener {
        @Override
        public void onVisitStart(Visit visit) {
            logger.info("Begin Gimbal Visit for {}", visit.getPlace().getName());
            Analytics.trackAction("gimbal.place.visit.begin",visitData(visit));
        }

        @Override
        public void onVisitEnd(Visit visit) {
            logger.info("End Gimbal Visit for %@", visit.getPlace().getName());
            Analytics.trackAction("gimbal.place.visit.end",visitData(visit));
        }
    }

}