# Gimbal Android Adobe Analytics Integration #

This sample project shows how Gimbal can be integrated with Adobe Analytics.

In this integration information regarding Place events are sent to Adobe Analytics. You can than use this information from within Adobe's analytic tools.

## Setup ##

Please refer to the [hello-gimbal-android](https://github.com/gimbalinc/hello-gimbal-android) for generic Gimbal setup and dependencies.

You will need to enter your Gimbal API Key into the AppService.java file.

You will need to add your Adobe libraries and configuration into the project. This includes the following:

* ADBMobileConfig.json, which needs to be placed into your assets folder.
* adobeMobileLibrary-{version}.jar which needs to be added as a project dependency.

For more detailed information see the [Adobe Documentation](https://marketing.adobe.com/resources/help/en_US/mobile/android/dev_qs.html)

## GimbalAdobeAnalyticsAdapter ##

To make integration easier we have created a helper class GimbalAdobeAnalyticsAdapter that exposes a simple method

```java
GimbalAdobeAnalyticsAdapter.startGimbalAndLogEvents(Application application, String gimbalApiKey, boolean debugLoggingEnabled)
```

By invoking this method with the appropriate Gimbal API key both frameworks are initialized and place events are monitored and sent to Adobe.

The code for GimbalAdobeAnalyticsAdapter shows how Place events are sent to Adobe.

```java

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
```