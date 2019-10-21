package org.aion.tetryon;

import org.junit.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class PairingTest {
    @Test
    public void pairingProd2Test() {
        Fp g11x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
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

        boolean r = false;

        try {
            long start = System.nanoTime();
            r = Pairing.pairingProd2(g11, g2, g12, g2);
            long ms = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            System.out.println("ecPair pairingProd2 test took " + ms + " ms");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertTrue(r);
    }
}
