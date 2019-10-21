package org.aion.tetryon;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ErrorHandlingTest {

    @Test
    public void g1EcMulBadPoint() {
        Fp px = new Fp(new BigInteger("0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f", 16));
        Fp py = new Fp(new BigInteger("14bd807f4e64904b29e874fd824ff16e465b5798b19aafe0cae60a2dbcf91333", 16));
        G1Point p = new G1Point(px, py);

        BigInteger s = new BigInteger("30586f85e8fcea91c0db1ed30aacf7350e72efd4cf756b3ce309f2159e275ff9", 16);

        byte[] r = new byte[0];
        try {
            r = AltBn128.g1EcMul(Util.serializeG1(p), s);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        G1Point answer = Util.deserializeG1(r);
    }

    @Test (expected = Exception.class)
    public void g1EcAddBadPoint() throws Exception {
        Fp ax = new Fp(new BigInteger("0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f", 16));
        Fp ay = new Fp(new BigInteger("1066f7836b02e18147ad4957bb91bf2eef7dd962723cc40e64b349b47c216b6d", 16));
        Fp bx = new Fp(new BigInteger("0f1405b01d015a801544ace7ec4eaac07c5c75d4e560e004586cfd34f4e69332", 16));
        Fp by = new Fp(new BigInteger("2451526db2a09e3c948f3381c8b73e930f51c82c11e9fef99201f1adc494a282", 16));

        G1Point a = new G1Point(ax, ay);
        G1Point b = new G1Point(bx, by);

        try {
            G1.add(a, b);
        } catch (Exception e) {
            assertTrue(e.getMessage().equalsIgnoreCase("NotOnCurve"));
            throw e;
        }
    }

    @Test
    public void pairingProd2BadPointG1() throws Exception {
        Fp g11x = new Fp(new BigInteger("0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f", 16));
        Fp g11y = new Fp(new BigInteger("23db84b7ae4e35681e833b6a1f6903e28291d154af3ec5ddc787e0e6cb058912", 16));
        G1Point g11 = new G1Point(g11x, g11y);

        Fp g12x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g12y = new Fp(new BigInteger("0c88c9bb32e36ac199cd0a4c6218547b14ef993cb93304af7498ab300d777435", 16));
        G1Point g12 = new G1Point(g12x, g12y);

        Fp2 g2x = new Fp2(new BigInteger("27d2525616cd883a2e952616138e052125201826d45e179a9ae28655338ca2be", 16),
                new BigInteger("2167ff55d36a2ed92eb480b1b9365382ea2facea90c860d63211827f122fdc29", 16));
        Fp2 g2y = new Fp2(new BigInteger("2c6e8b5d5da9a03f2d6b57bf2338168eca1e43409693b43659fe834149e506a9", 16),
                new BigInteger("020401d78e6fe746fe3d9512f9b4eedcfdd7eb5d08e307f1d6ee5d38f9a253ec", 16));
        G2Point g2 = new G2Point(g2x, g2y);

        boolean r = Pairing.pairingProd2(g11, g2, g12, g2);
        assertFalse(r);
    }

    @Test
    public void pairingProd2BadPointG2() throws Exception {
        Fp g11x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g11y = new Fp(new BigInteger("23db84b7ae4e35681e833b6a1f6903e28291d154af3ec5ddc787e0e6cb058912", 16));
        G1Point g11 = new G1Point(g11x, g11y);

        Fp g12x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g12y = new Fp(new BigInteger("0c88c9bb32e36ac199cd0a4c6218547b14ef993cb93304af7498ab300d777435", 16));
        G1Point g12 = new G1Point(g12x, g12y);

        Fp2 g2x = new Fp2(new BigInteger("0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f", 16),
                new BigInteger("2167ff55d36a2ed92eb480b1b9365382ea2facea90c860d63211827f122fdc29", 16));
        Fp2 g2y = new Fp2(new BigInteger("2c6e8b5d5da9a03f2d6b57bf2338168eca1e43409693b43659fe834149e506a9", 16),
                new BigInteger("020401d78e6fe746fe3d9512f9b4eedcfdd7eb5d08e307f1d6ee5d38f9a253ec", 16));
        G2Point g2 = new G2Point(g2x, g2y);

        boolean r = Pairing.pairingProd2(g11, g2, g12, g2);
        assertFalse(r);
    }


}
