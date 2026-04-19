// IHelloService.aidl
// This is the "menu" — it declares the contract between client and service.
// The AIDL compiler auto-generates Java stub code from this.
package com.example.hello;

interface IHelloService {
    // Returns a greeting message for the given name
    String sayHello(String name);

    // Adds two integers and returns the result
    int add(int a, int b);

    // Returns the current system time as a formatted string
    String getSystemTime();

    int mul(int a, int b);
}
