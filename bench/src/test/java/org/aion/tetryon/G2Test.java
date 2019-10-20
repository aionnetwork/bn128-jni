package org.aion.tetryon;

import org.junit.Test;

import java.math.BigInteger;

import static java.math.BigInteger.valueOf;
import static org.aion.tetryon.G2.*;
import static org.junit.Assert.*;

/**
 * ported from https://github.com/musalbas/solidity-BN256G2/blob/master/test_BN256G2.py
 */
public class G2Test {

    private static final BigInteger CURVE_ORDER = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");
    private static final BigInteger FIELD_MODULUS = new BigInteger("21888242871839275222246405745257275088696311157297823662689037894645226208583");
    private static final G2Point G2_P = new G2Point(
            new Fp2(
                    new BigInteger("10857046999023057135944570762232829481370756359578518086990519993285655852781"),
                    new BigInteger("11559732032986387107991004021392285783925812861821192530917403151452391805634")
            ),
            new Fp2(
                    new BigInteger("8495653923123431417604973247489272438418190587263600148770280649306958101930"),
                    new BigInteger("4082367875863433681332203403145435568316851327593401208105741076214120093531")
            )
    );
    private static final G2Point G2_INF = new G2Point(
            new Fp2(new BigInteger("0"), new BigInteger("0")),
            new Fp2(new BigInteger("0"), new BigInteger("0"))
    );

    @Test
    public void testDouble() {
        JacobianPoint p1 = toJacobian(G2_P);
        JacobianPoint p2 = ECTwistDouble(p1);

        String expected = "((18064657650266314310872833882734510304469342224960895762840452673540398385663, 15767209469806156760211170373387620603270210844572625444480773861832148323813), (18350387758438165722514006433324734852126505429007818091896560652419680779208, 2361120538552305763462588552289584032113666280040005078430227626567900900889), (11295011439305748432050915375304030887315222387144299555868531462511479541127, 21586188435259680269345972498488110271568572496353012928929868286300284796401))";
        assertEquals(expected, p2.toString());
    }

    @Test
    public void testAdd() {
        JacobianPoint p1 = ECTwistDouble(toJacobian(G2_P));
        JacobianPoint p2 = toJacobian(G2_P);
        JacobianPoint p3 = ECTwistAdd(p1, p2);

        String expected = "((7912621911344046544090818122946944996223428665299163752528348539039606688899, 15724406814493788826779887496390344832534940617768777573240421057383317182939), (13322067499183255867609246315000180006858679635201934212059142471448238352005, 6423608706841833443331370913387148748732339788563522814730203220772382863810), (11560267969212412564220373690161220931142500816471144704804537562496975911761, 12603241482527434617362395335043291778817189245825716702462554023647266434140))";
        assertEquals(expected, p3.toString());
    }

    @Test
    public void testMul() {
        JacobianPoint p1 = toJacobian(G2_P);
        BigInteger s = valueOf(5);
        JacobianPoint p2 = ECTwistMul(p1, s);

        String expected = "((9305799921822688552960666315070772167325859375666396917145944274406523226977, 5749331787630864737019143220370317628114010800637393311552432346012213433799), (2287699280032379263965130181381604401437574509304277534686134891600309992800, 7005392644235319202798370361855747223694273134278657774446422653439513896582), (5264441664643458557219975188781537374538057194409943993179558445592076226652, 2483717509673326227599146261818870909086110455040455451714555723550126901982))";
        assertEquals(expected, p2.toString());
    }

    @Test
    public void testG2() {
        assertEquals(
                ECTwistAdd(ECTwistAdd(ECTwistMul(G2_P, BigInteger.TWO), G2_P), G2_P),
                ECTwistMul(ECTwistMul(G2_P, BigInteger.TWO), BigInteger.TWO)
        );

        assertEquals(
                ECTwistAdd(ECTwistMul(G2_P, valueOf(9)), ECTwistMul(G2_P, valueOf(5))),
                ECTwistAdd(ECTwistMul(G2_P, valueOf(12)), ECTwistMul(G2_P, valueOf(2)))
        );

        assertTrue(isInfinity(ECTwistMul(G2_P, CURVE_ORDER)));
        assertFalse(isInfinity(ECTwistMul(G2_P, FIELD_MODULUS.multiply(valueOf(2)).subtract(CURVE_ORDER))));
        assertTrue(isInfinity(ECTwistAdd(ECTwistMul(G2_P, CURVE_ORDER), ECTwistMul(G2_P, CURVE_ORDER))));
        assertEquals(ECTwistAdd(ECTwistMul(G2_P, CURVE_ORDER), ECTwistMul(G2_P, valueOf(5))), ECTwistMul(G2_P, valueOf(5)));
        assertEquals(ECTwistAdd(ECTwistMul(G2_P, valueOf(5)), ECTwistMul(G2_P, CURVE_ORDER)), ECTwistMul(G2_P, valueOf(5)));
        assertTrue(isInfinity(ECTwistMul(ECTwistMul(G2_P, CURVE_ORDER), valueOf(1))));
        assertTrue(isInfinity(ECTwistMul(ECTwistMul(G2_P, CURVE_ORDER), valueOf(2))));
    }

    private boolean isInfinity(G2Point p) {
        return p.equals(G2_INF);
    }
}
