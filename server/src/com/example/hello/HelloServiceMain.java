package com.example.hello;

import android.app.ActivityThread;
import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ServiceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelloServiceMain extends IHelloService.Stub {

    private static final String TAG = "HelloService";
    private static final String SERVICE_NAME = "hello_service";

    @Override
    public String sayHello(String name) {
        Log.i(TAG, "sayHello() called with name: " + name);
        return "Hello, " + name + "! Greetings from HelloService.";
    }

    @Override
    public int add(int a, int b) {
        Log.i(TAG, "add() called with a=" + a + ", b=" + b);
        return a + b;
    }

    @Override
    public String getSystemTime() {
        Log.i(TAG, "getSystemTime() called");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
        return sdf.format(new Date());
    }

    @Override
    public int mul(int a, int b) {
        Log.i(TAG, "Mul() called with a=" + a + ", b=" + b);
        return a * b;
    }

    // ========== VEHICLE PROPERTY SUBSCRIPTION ==========

    private static void startVehiclePropertySubscription(Context context) {
        HandlerThread handlerThread = new HandlerThread("VehiclePropertyThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        Car.createCar(context, handler, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER,
            (car, ready) -> {
                if (!ready) {
                    Log.e(TAG, "CarService is not ready!");
                    return;
                }

                Log.i(TAG, "CarService connected. Subscribing to vehicle properties...");

                CarPropertyManager carPropertyManager =
                    (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);

                if (carPropertyManager == null) {
                    Log.e(TAG, "CarPropertyManager is null!");
                    return;
                }

                // Subscribe to GEAR
                carPropertyManager.registerCallback(
                    new CarPropertyManager.CarPropertyEventCallback() {
                        @Override
                        public void onChangeEvent(CarPropertyValue value) {
                            int gear = (int) value.getValue();
                            Log.i(TAG, "[GEAR CHANGE] Gear = " + gearToString(gear));
                            // removed System.out.println
                        }

                        @Override
                        public void onErrorEvent(int propId, int zone) {
                            Log.e(TAG, "[GEAR ERROR] propId=" + propId + " zone=" + zone);
                        }
                    },
                    VehiclePropertyIds.GEAR_SELECTION,
                    CarPropertyManager.SENSOR_RATE_ONCHANGE
                );

                Log.i(TAG, "Subscribed to PERF_VEHICLE_SPEED and GEAR_SELECTION");
            });
    }

    private static String gearToString(int gear) {
        switch (gear) {
            case 1:  return "NEUTRAL";
            case 2:  return "REVERSE";
            case 4:  return "PARK";
            case 8:  return "DRIVE";
            case 16: return "FIRST";
            case 32: return "SECOND";
            case 64: return "THIRD";
            default: return "UNKNOWN(" + gear + ")";
        }
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        Log.i(TAG, "===== HelloService starting =====");

        Looper.prepareMainLooper();

        // Get system context — required for Car API
        Context context = ActivityThread.systemMain().getSystemContext();

        // Register our service
        HelloServiceMain service = new HelloServiceMain();
        ServiceManager.addService(SERVICE_NAME, service);
        Log.i(TAG, "Registered '" + SERVICE_NAME + "' with ServiceManager");

        // Start vehicle property subscription
        startVehiclePropertySubscription(context);

        Log.i(TAG, "Entering main loop. Service is ready.");
        Looper.loop();

        Log.wtf(TAG, "Main loop exited unexpectedly!");
    }
}