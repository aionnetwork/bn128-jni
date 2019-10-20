package org.aion.tetryon;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    Fp2Test.class,
    G2Test.class,
    G1Test.class,
    JniTest.class,
    PairingTest.class,
    G16SquarePreimageTest.class,
})
public class AllTests {}