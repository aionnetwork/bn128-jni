package org.aion.tetryon;

import org.junit.Test;

import java.math.BigInteger;
import static org.junit.Assert.assertTrue;

public class PairingTest {
    @Test
    public void pairingProd2Test() {
        // g1_1.x 2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e
        // g1_1.y 23db84b7ae4e35681e833b6a1f6903e28291d154af3ec5ddc787e0e6cb058912
        // g1_2.x 2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e
        // g1_2.y 0c88c9bb32e36ac199cd0a4c6218547b14ef993cb93304af7498ab300d777435
        // g2.x1 27d2525616cd883a2e952616138e052125201826d45e179a9ae28655338ca2be
        // g2.x2 2167ff55d36a2ed92eb480b1b9365382ea2facea90c860d63211827f122fdc29
        // g2.y1 2c6e8b5d5da9a03f2d6b57bf2338168eca1e43409693b43659fe834149e506a9
        // g2.y2 020401d78e6fe746fe3d9512f9b4eedcfdd7eb5d08e307f1d6ee5d38f9a253ec

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

        assertTrue(Pairing.pairingProd2(g11, g2, g12, g2));
    }

    @Test
    public void pairingProd1Test() {
        Fp g1x = new Fp(new BigInteger("07f4a1ab12b1211149fa0aed8ade3442b774893dcd1caffb8693ade54999c164", 16));
        Fp g1y = new Fp(new BigInteger("23b7f10c5e1aeaffafa088f1412c0f307969ba3f8f9d5920214a4cb91693fab5", 16));
        G1Point g1 = new G1Point(g1x, g1y);

        Fp2 g2x = new Fp2(new BigInteger("1f6cc814cf1df1ceb663378c496f168bcd21e19bb529e90fcf3721f8df6b4128", 16),
                          new BigInteger("079ee30e2c79e15be67645838a3177f681ab111edacf6f4867e8eed753ed9681", 16));
        Fp2 g2y = new Fp2(new BigInteger("2779dd0accaa1391e29ad54bf065819cac3129edda4eaf909d6ea2c7495a47f7", 16),
                          new BigInteger("20105b11ae5fbdc7067102d4260c8913cdcb512632680221d7644f9928a7e51d", 16));
        G2Point g2 = new G2Point(g2x, g2y);

        boolean r = Pairing.pairingProd1(g1, g2);
        assertTrue(r);
    }
}
