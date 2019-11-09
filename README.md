# Alt-Bn 128 JNI Wrapper

We wrap the [Parity implementation](https://github.com/paritytech/bn) of the Barreto–Naehrig curve introduced in [BCTV13](https://eprint.iacr.org/2013/879.pdf), using the [Java Native Interface (JNI)](https://docs.oracle.com/en/java/javase/13/docs/specs/jni/index.html). This enables arithmetic and pairing operations over the Alt-BN 128 curve from Java programs. The Alt-Bn 128 curve (which is parameterized differently than the BN 128 curve) is completely specified in section E.1 of the [Ethereum Yellow-paper](https://ethereum.github.io/yellowpaper/paper.pdf).

This repository contains two projects. First, labelled `native`, generates the native library, and the second, labelled `bench`, tests the JNI functionality.

## Native Library

The [`native`](https://github.com/aionnetwork/bn128-jni/tree/master/native) directory contains a rust project that leverages the [rust jni bindings](https://docs.rs/jni/0.13.1/jni/) to generate a shared native library. The native library includes the appropriate JNI headers to bridge bytecode running in a JVM and the native code implementing the curve operations. 

In order to build the shared library, first, initialize the project submodules. Then, in the `native` directory, run the following command: 
```
cargo build --release
```
The build output (available in the `target/release` folder) is `libbn_jni.so`. The file extension will be `.so` on linux, `.dylib` on mac or `.dll` on windows. 

## Java Testbench

The [`bench`](https://github.com/aionnetwork/bn128-jni/tree/master/bench) folder contains a Java testbench to validate invocation of native library from Java. 

Before executing the testbench, make sure to build the native library, since the testbench sets the `java.library.path` to the `native/target/release/` folder. The project uses gradle, so the tests can be executed by running the following command in the `bench` directory:
 ```
./gradlew test
 ``` 

## Improvements and Benchmarking

The Parity implementation of the Alt-Bn 128 curve was chosen since this implementation boasted the best performance of the well-known open-source implementations for the bn128 curve: 
 * [Matter Labs' (Rust)](https://github.com/matter-labs/pairing/tree/master/src/bn256)
 * [Cloudflare's (Go)](https://github.com/cloudflare/bn256)
 * [EthereumJ's (Java)](https://github.com/ethereum/ethereumj/tree/develop/ethereumj-core/src/main/java/org/ethereum/crypto/zksnark)
 * [LibSnarks' Reference](https://github.com/scipr-lab/libff/tree/master/libff/algebra/curves/alt_bn128)
 
No serious performance benchmarking was done for this JNI wrapper. Overhead incurred in the JVM while calling native libraries through JNI is well understood (see [this paper](https://pdfs.semanticscholar.org/2b7e/9b075e51c5eb51bb035b39b17617f7428247.pdf) & [this paper](https://hal.archives-ouvertes.fr/hal-01277940/document)). [Better benchmarking](https://stackoverflow.com/questions/49823418/performance-overhead-jni-vs-java-vs-native-c) needs to be conducted on this library to determine: 
* The real overhead incurred by JNI vs native performance (which was observed not to be preserved from the rudimentary benchmarking conducted), and
* Comparison of a pure-Java implementation of curve operations versus a native implementation accessed via JNI.
 