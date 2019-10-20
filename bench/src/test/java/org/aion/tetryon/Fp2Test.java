package org.aion.tetryon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Fp2Test {

    @Test
    public void testInverse() {
        Fp2 p1 = new Fp2(BigInteger.TWO, BigInteger.TEN);
        Fp2 p2 = p1.inverse();
        assertEquals(Fp2.one(), p1.multiply(p2));
    }
}
