package org.aion.tetryon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class G1Test {

    @Test
    public void addTest() {
        // 1b4abac579a00d8edd49bce17e1b5db7e6ad416225e7c2aba670d32a8e9c4edf
        // 1066f7836b02e18147ad4957bb91bf2eef7dd962723cc40e64b349b47c216b6d
        // 0f1405b01d015a801544ace7ec4eaac07c5c75d4e560e004586cfd34f4e69332
        // 2451526db2a09e3c948f3381c8b73e930f51c82c11e9fef99201f1adc494a282
        // 11d1d762d8996b2912960dafeba739869f50683ea8da27ca8748f72b8b2103e6
        // 11ddab98366953a843783b67302cd3def9ab7131948553fac8e16440da3c8d40

        Fp ax = new Fp(new BigInteger("222480c9f95409bfa4ac6ae890b9c150bc88542b87b352e92950c340458b0c09", 16));
        Fp ay = new Fp(new BigInteger("2976efd698cf23b414ea622b3f720dd9080d679042482ff3668cb2e32cad8ae2", 16));
        Fp bx = new Fp(new BigInteger("1bd20beca3d8d28e536d2b5bd3bf36d76af68af5e6c96ca6e5519ba9ff8f5332", 16));
        Fp by = new Fp(new BigInteger("2a53edf6b48bcf5cb1c0b4ad1d36dfce06a79dcd6526f1c386a14d8ce4649844", 16));
        Fp cx = new Fp(new BigInteger("16c7c4042e3a725ddbacf197c519c3dcad2bc87dfd9ac7e1e1631154ee0b7d9c", 16));
        Fp cy = new Fp(new BigInteger("19cd640dd28c9811ebaaa095a16b16190d08d6906c4f926fce581985fe35be0e", 16));
        G1Point a = new G1Point(ax, ay);
        G1Point b = new G1Point(bx, by);

        G1Point c = G1Point.INF;

        try {
            c = G1.add(a, b);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertEquals(c.x, cx);
        assertEquals(c.y, cy);
    }

    @Test
    public void mulTest() {
        // pk1.x 1e462d01d1861f7ee499bf70ab12ade335d98586b52db847ee2ec1e790170e04
        // pk1.y 14bd807f4e64904b29e874fd824ff16e465b5798b19aafe0cae60a2dbcf91333
        // 277c29589e6ff42e08ac692539b258e5dddada5d7281ed0358c132c537b4de54
        // 2808222e9db87f1e2d717ceb13667cfdafd219738732c0de2e9de4fa19a07773
        // 11abfe850d93eacc766df6ea28ee6cfc83a549e37973bf0506b13ae9b51c7013
        // 1c2f5959afa3aca84326c35ef24e289f82e5a746fe5aed00eb6011c185b6772e
        // scalar: 30586f85e8fcea91c0db1ed30aacf7350e72efd4cf756b3ce309f2159e275ff9
        // p.x 15ea829def65cb28c5435094e1b8d06cb021a8671319cdad074ee89ce7c2c0bf
        // p.y 0b68b46b86de49221fe4dbdce9b88518812c9d48fb502ada0a2ad9fc28312c89

        Fp px = new Fp(new BigInteger("1e462d01d1861f7ee499bf70ab12ade335d98586b52db847ee2ec1e790170e04", 16));
        Fp py = new Fp(new BigInteger("14bd807f4e64904b29e874fd824ff16e465b5798b19aafe0cae60a2dbcf91333", 16));
        Fp qx = new Fp(new BigInteger("15ea829def65cb28c5435094e1b8d06cb021a8671319cdad074ee89ce7c2c0bf", 16));
        Fp qy = new Fp(new BigInteger("0b68b46b86de49221fe4dbdce9b88518812c9d48fb502ada0a2ad9fc28312c89", 16));
        G1Point p = new G1Point(px, py);
        BigInteger s = new BigInteger("30586f85e8fcea91c0db1ed30aacf7350e72efd4cf756b3ce309f2159e275ff9", 16);

        G1Point q = G1Point.INF;

        try {
            q = G1.mul(p, s);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertEquals(q.x, qx);
        assertEquals(q.y, qy);
    }
}