This project contains two subprojects 

## bench

THis is a java testbench to test if the jni worked! 

it's using gradle so you can sue the gradle cli, or any modern java ide to test the project (without fiddling around with ide settings)

in the gradle settings, we're already setting the java.library.path to the `native/target/release/` folder, so make sure to first run `cargo build --release` in the native folder to generate the native library for your platform (in the test, we're using System.loadLibrary to load the library. 

## native

This is a rust project, that wraps the bn library (altbn128) in jni. This generates a shared native library with the appropriate JNI headers so that bridge between the bytecode running in our JVM and the native code. In order to 


 that complies with the JNI 

 JNI library exports the functions that a Java application must resolve at runtime


 we're using the rust jni library to do this (https://docs.rs/jni/0.13.1/jni/)

 to run, do 
 ```
 cargo build --release
 ```

you will find a target/release folder, with a file libbn_jni {.so (on linux), .dylib (on mac) or .dll (on windows)}. 

