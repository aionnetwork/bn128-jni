package org.aion.tetryon;

public class AltBn128Jni {
    public native byte[] g1EcAdd(byte[] point1, byte[] point2);
    public native byte[] g1EcMul(byte[] point, byte[] scalar);
    public native boolean ecPair(byte[] g1_point_list, byte[] g2_point_list);
    public native int ping();
}
