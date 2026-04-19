package com.example.hello;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * HelloClient — A command-line client that talks to HelloService.
 *
 * Analogy: This is the CUSTOMER in our restaurant.
 * 1. Go to reception desk (ServiceManager) and ask for the kitchen
 * 2. Get the menu interface (IHelloService)
 * 3. Place orders (call methods)
 * 4. Print the food that comes back (results)
 *
 * Usage: adb shell hello_client [name]
 */
public class HelloClient {

    private static final String SERVICE_NAME = "hello_service";

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  HelloService Client");
        System.out.println("======================================");

        // Step 1: Ask ServiceManager for the service
        // Analogy: "Excuse me, where is the hello_service kitchen?"
        System.out.println("\n[1] Looking up service '" + SERVICE_NAME + "'...");
        IBinder binder = ServiceManager.getService(SERVICE_NAME);

        if (binder == null) {
            System.err.println("ERROR: Service '" + SERVICE_NAME + "' not found!");
            System.err.println("Is the service running? Try:");
            System.err.println("  adb shell /system/bin/hello_service_server &");
            System.exit(1);
            return;
        }

        System.out.println("    Service found!");

        // Step 2: Convert the raw Binder handle to our typed interface
        // Analogy: "Give me the menu so I know what I can order"
        IHelloService service = IHelloService.Stub.asInterface(binder);

        try {
            // Step 3: Call methods (place orders)
            String name = (args.length > 0) ? args[0] : "World";

            // --- Test 1: sayHello ---
            System.out.println("\n[2] Calling sayHello(\"" + name + "\")...");
            String greeting = service.sayHello(name);
            System.out.println("    Result: " + greeting);

            // --- Test 2: add ---
            int a = 42, b = 58;
            System.out.println("\n[3] Calling add(" + a + ", " + b + ")...");
            int sum = service.add(a, b);
            System.out.println("    Result: " + a + " + " + b + " = " + sum);

            // --- Test 3: getSystemTime ---
            System.out.println("\n[4] Calling getSystemTime()...");
            String time = service.getSystemTime();
            System.out.println("    Result: " + time);

            // --- Test 4: mul ---
            a = 5; b = 5;
            System.out.println("\n[3] Calling mul(" + a + ", " + b + ")...");
            int mul = service.mul(a, b);
            System.out.println("    Result: " + a + " + " + b + " = " + mul);

            // Summary
            System.out.println("\n======================================");
            System.out.println("  All 4 tests passed!");
            System.out.println("======================================");

        } catch (RemoteException e) {
            System.err.println("ERROR: Remote call failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
