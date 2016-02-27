package com.lozasolutions.spaces4all.application;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Application Base
 */


public class BaseApplication extends Application {

    private RequestQueue requestQueue;
    private static BaseApplication instance;
    public static final String TAG = BaseApplication.class.getSimpleName();
    boolean internetConnection;

    //Network connection
    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;


    @Override
    public void onCreate() {
        super.onCreate();

        //Singleton
        instance = this;

        //Network Bus
        final EventBus bus = new EventBus();
        busWrapper = getGreenRobotBusWrapper(bus);
        networkEvents = new NetworkEvents(this, busWrapper).enableInternetCheck();
        busWrapper.register(this);
        networkEvents.register();

    }


    @NonNull
    public static BusWrapper getGreenRobotBusWrapper(final EventBus bus) {
        return new BusWrapper() {
            @Override
            public void register(Object object) {
                bus.register(object);
            }

            @Override
            public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override
            public void post(Object event) {
                bus.post(event);
            }
        };
    }


    @Subscribe
    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus status = event.getConnectivityStatus();

        if(status.equals(ConnectivityStatus.MOBILE_CONNECTED) || status.equals(ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET)){

            BaseApplication.getInstance().setInternetConnection(true);

        }else{

            //TODO No internet connection
            BaseApplication.getInstance().setInternetConnection(false);
        }

    }

    public  boolean getInternetConnection() {
        return internetConnection;
    }

    public  void setInternetConnection(boolean internetConnection) {
        this.internetConnection = internetConnection;
    }


    @Override
    public void onTerminate() {

        super.onTerminate();

    }

    public NetworkEvents getNetworkEvents() {
        return networkEvents;
    }

    public void setNetworkEvents(NetworkEvents networkEvents) {
        this.networkEvents = networkEvents;
    }

    public BusWrapper getBusWrapper() {
        return busWrapper;
    }

    public void setBusWrapper(BusWrapper busWrapper) {
        this.busWrapper = busWrapper;
    }

    /**
     * Returns the application instance.
     *
     * @return The application instance.
     */
    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    /**
     * Returns the Volley requests queue, initializing it if it doesn't exist
     *
     * @return The requests queue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Allows to add a Volley request to the requests global queue, setting a tag in order to
     * identify the added request.
     *
     * @param req The request to be added
     * @param tag The tag which identifies the request.
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * Allows to add a Volley request to the requests global queue
     *
     * @param req The request to be added
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancels an specified volley request
     *
     * @param tag tag of the request to be canceled.
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
