package org.aion.tetryon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class JniTest {

    @Test
    public void jni() {
        int ret = AltBn128.ping();
        assertEquals(ret, 1337);
    }
}