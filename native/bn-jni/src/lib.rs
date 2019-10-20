
extern crate bn;
extern crate rand;
extern crate hex;

use bn::{Group, Fr, Fq, Fq2, G1, G2, Gt, AffineG1, AffineG2, pairing, pairing_batch};
use std::os::raw::c_uchar;
use std::os::raw::c_ulong;
use std::os::raw::c_int;
use std::slice;
use std::fmt;
use std::error::Error;

use jni::JNIEnv;
use jni::objects::{JClass, JObject};
use jni::sys::{jint, jbyteArray, jboolean};

#[derive(Debug)]
struct PairingErr {
    code: i32,
    details: String
}

impl PairingErr {
    fn new(c: i32, msg: &str) -> PairingErr {
        PairingErr{code: c, details: msg.to_string()}
    }
}

impl fmt::Display for PairingErr {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f,"{}",self.details)
    }
}

impl Error for PairingErr {
    fn description(&self) -> &str {
        &self.details
    }
}

impl From<bn::arith::Error> for PairingErr {
    fn from(err: bn::arith::Error) -> Self {
        PairingErr::new(-1, &format!("{:?}", err))
    }
}

impl From<bn::FieldError> for PairingErr {
    fn from(err: bn::FieldError) -> Self {
        PairingErr::new(-2, &format!("{:?}", err))
    }
}

impl From<bn::GroupError> for PairingErr {
    fn from(err: bn::GroupError) -> Self {
        PairingErr::new(-3, &format!("{:?}", err))
    }
}


const FP_SIZE:usize = 32;


#[no_mangle]
pub extern "system" fn Java_org_aion_tetryon_AltBn128Jni_ping(env: JNIEnv, class: JClass)-> jint {
    1337 as jint
}

fn deserialize_g1(pt_byte: &[u8]) -> Result<G1, PairingErr> {
    let x = Fq::from_slice(&pt_byte[0..FP_SIZE])?;
    let y = Fq::from_slice(&pt_byte[FP_SIZE..FP_SIZE*2])?;
    let pt_af = AffineG1::new(x, y)?;
    Ok(G1::from(pt_af))
}

fn deserialize_g2(pt_byte: &[u8]) -> Result<G2, PairingErr> {
    let x1 = Fq::from_slice(&pt_byte[0..FP_SIZE])?;
    let x2 = Fq::from_slice(&pt_byte[FP_SIZE..FP_SIZE*2])?;

    let y1 = Fq::from_slice(&pt_byte[FP_SIZE*2..FP_SIZE*3])?;
    let y2 = Fq::from_slice(&pt_byte[FP_SIZE*3..FP_SIZE*4])?;

    let x = Fq2::new(x1, x2);
    let y = Fq2::new(y1, y2);
    let pt_af = AffineG2::new(x, y)?;
    Ok(G2::from(pt_af))
}


#[no_mangle]
pub extern "system" fn Java_org_aion_tetryon_AltBn128Jni_g1EcAdd(env: JNIEnv, class: JClass,
        point1_j: jbyteArray, point2_j: jbyteArray)-> jbyteArray {
    
    let p1_byte = env.convert_byte_array(point1_j).unwrap();
    let p2_byte = env.convert_byte_array(point2_j).unwrap();

    let mut p3_byte: [u8; FP_SIZE*2] = [0; FP_SIZE*2];
    let ret = alt_bn128_add_internal(&p1_byte, &p2_byte, &mut p3_byte);

    match ret {
        Err(e) => {
            env.throw(format!("{}", e)).unwrap();
            return JObject::null().into_inner();
        },
        Ok(_) => {
            let output = env.byte_array_from_slice(&p3_byte).unwrap();
            return output;
        },
    }
}

#[no_mangle]
pub extern "C" fn alt_bn128_add(
    point1: *const c_uchar,
    len1: c_ulong,
    point2: *const c_uchar,
    len2: c_ulong,
    result: *mut c_uchar,
    result_len: *mut c_ulong,
) -> c_int {
    let p1_byte = unsafe { slice::from_raw_parts(point1, len1 as usize) };
    let p2_byte = unsafe { slice::from_raw_parts(point2, len2 as usize) };
    let p3_byte = unsafe { slice::from_raw_parts_mut(result, *result_len as usize) };

    let ret =  alt_bn128_add_internal(p1_byte, p2_byte, p3_byte);
    match ret {
        Err(e) => return e.code,
        Ok(_) => return 0 as c_int,
    }
}

fn alt_bn128_add_internal(p1_byte: &[u8], p2_byte: &[u8], p3_byte: &mut [u8]) -> Result<(), PairingErr> {
    let p1 = deserialize_g1(p1_byte)?;
    let p2 = deserialize_g1(p2_byte)?;

    let p3 = p1 + p2;
    if let Some(p3_af) = AffineG1::from_jacobian(p3) {
        p3_af.x().into_u256().to_big_endian(&mut p3_byte[0..FP_SIZE])?;
        p3_af.y().into_u256().to_big_endian(&mut p3_byte[FP_SIZE..FP_SIZE*2])?;
    }

    Ok(())
}

#[no_mangle]
pub extern "system" fn Java_org_aion_tetryon_AltBn128Jni_g1EcMul(env: JNIEnv, class: JClass,
        point_j: jbyteArray, scalar_j: jbyteArray)-> jbyteArray {
    
    let pt_byte = env.convert_byte_array(point_j).unwrap();
    let scalar_byte = env.convert_byte_array(scalar_j).unwrap();

    let mut p3_byte: [u8; FP_SIZE*2] = [0; FP_SIZE*2];
    let ret = alt_bn128_mul_internal(&pt_byte, &scalar_byte, &mut p3_byte);

    let output = env.byte_array_from_slice(&p3_byte).unwrap();
    output
}

#[no_mangle]
pub extern "C" fn alt_bn128_mul(
    point: *const c_uchar,
    len: c_ulong,
    scalar: *const c_uchar,
    scalar_len: c_ulong,
    result: *mut c_uchar,
    result_len: *mut c_ulong,
) -> c_int {
    let pt_byte = unsafe { slice::from_raw_parts(point, len as usize) };
    let scalar_byte = unsafe { slice::from_raw_parts(scalar, scalar_len as usize) };
    let p3_byte = unsafe { slice::from_raw_parts_mut(result, *result_len as usize) };

    let ret = alt_bn128_mul_internal(pt_byte, scalar_byte, p3_byte);
    match ret {
        Err(e) => return e.code,
        Ok(_) => return 0 as c_int
    }
}

fn alt_bn128_mul_internal(pt_byte: &[u8], scalar_byte: &[u8], p3_byte: &mut [u8]) -> Result<(), PairingErr> {
    let pt = deserialize_g1(pt_byte)?;
    let s = Fr::from_slice(&scalar_byte[0..FP_SIZE])?;

    let p3 = pt * s;
    if let Some(p3_af) = AffineG1::from_jacobian(p3) {
        p3_af.x().into_u256().to_big_endian(&mut p3_byte[0..FP_SIZE])?;
        p3_af.y().into_u256().to_big_endian(&mut p3_byte[FP_SIZE..FP_SIZE*2])?;
    }
    // println!("{}", hex::encode(p3_byte));

    Ok(())
}

#[no_mangle]
pub extern "system" fn Java_org_aion_tetryon_AltBn128Jni_ecPair(env: JNIEnv, class: JClass,
         g1_point_list: jbyteArray, g2_point_list: jbyteArray)-> jboolean {

    let g1_list_byte = env.convert_byte_array(g1_point_list).unwrap();
    let g2_list_byte = env.convert_byte_array(g2_point_list).unwrap();

    let ret = alt_bn128_pair_internal(&g1_list_byte, &g2_list_byte, g1_list_byte.len()/(FP_SIZE*2));
    match ret {
        Err(e) => return 0 as jboolean,
        Ok(val) => {
            val as jboolean
        }
    }
}

#[no_mangle]
pub extern "C" fn alt_bn128_pair(
    g1_point_list: *const c_uchar,
    g2_point_list: *const c_uchar,
    point_list_len: c_ulong,
    is_one: *mut c_int,
) -> c_int {
    let g1_list_byte = unsafe { slice::from_raw_parts(g1_point_list, (point_list_len as usize)*FP_SIZE*2 as usize) };
    let g2_list_byte = unsafe { slice::from_raw_parts(g2_point_list, (point_list_len as usize)*FP_SIZE*4 as usize) };

    let ret = alt_bn128_pair_internal(g1_list_byte, g2_list_byte, point_list_len as usize);
    match ret {
        Err(e) => return e.code,
        Ok(val) => {
            if val {
                unsafe {*is_one = 1};
            } else {
                unsafe {*is_one = 0};
            }
            0 as c_int
        }
    }
}

fn alt_bn128_pair_internal(g1_list_byte: &[u8], g2_list_byte: &[u8], point_list_len: usize) -> Result<bool, PairingErr> {
    let mut pair_list : Vec<(G1, G2)> = vec![];
    for i in 0..point_list_len {
        let g1_byte = &g1_list_byte[FP_SIZE*2*i..FP_SIZE*2*(i+1)];
        let g2_byte = &g2_list_byte[FP_SIZE*4*i..FP_SIZE*4*(i+1)];

        let g1 = deserialize_g1(g1_byte)?;
        let g2 = deserialize_g2(g2_byte)?;

        pair_list.push((g1,g2));
    }

    let gt = pairing_batch(&pair_list);

    Ok(gt == Gt::one())
}


#[no_mangle]
pub extern "C" fn call_test_from_c() {
    let rng = &mut rand::thread_rng();
    let alice_sk = Fr::random(rng);
    let bob_sk = Fr::random(rng);
    let carol_sk = Fr::random(rng);

    // Generate public keys in G1 and G2
    let (alice_pk1, alice_pk2) = (G1::one() * alice_sk, G2::one() * alice_sk);
    let (bob_pk1, bob_pk2) = (G1::one() * bob_sk, G2::one() * bob_sk);
    let (carol_pk1, carol_pk2) = (G1::one() * carol_sk, G2::one() * carol_sk);

    // Each party computes the shared secret
    let alice_ss = pairing(bob_pk1, carol_pk2).pow(alice_sk);
    let bob_ss = pairing(carol_pk1, alice_pk2).pow(bob_sk);
    let carol_ss = pairing(alice_pk1, bob_pk2).pow(carol_sk);

    assert!(alice_ss == bob_ss && bob_ss == carol_ss);
}



#[cfg(test)]
// extern crate hex;

mod tests {
    use super::*;

    #[test]
    fn it_works() {

    let rng = &mut rand::thread_rng();
        // Generate private keys
    let alice_sk = Fr::random(rng);
    let bob_sk = Fr::random(rng);
    let carol_sk = Fr::random(rng);

    // Generate public keys in G1 and G2
    let (alice_pk1, alice_pk2) = (G1::one() * alice_sk, G2::one() * alice_sk);
    let (bob_pk1, bob_pk2) = (G1::one() * bob_sk, G2::one() * bob_sk);
    let (carol_pk1, carol_pk2) = (G1::one() * carol_sk, G2::one() * carol_sk);

    // Each party computes the shared secret
    let alice_ss = pairing(bob_pk1, carol_pk2).pow(alice_sk);
    let bob_ss = pairing(carol_pk1, alice_pk2).pow(bob_sk);
    let carol_ss = pairing(alice_pk1, bob_pk2).pow(carol_sk);

    assert!(alice_ss == bob_ss && bob_ss == carol_ss);
    }

    #[test]
    fn serialize() {
        let rng = &mut rand::thread_rng();
        let alice_sk = Fr::random(rng);
        let (alice_pk1, alice_pk2) = (G1::one() * alice_sk, G2::one() * alice_sk);
        let alice_af_pk1 = AffineG1::from_jacobian(alice_pk1).unwrap();
        let alice_af_pk2 = AffineG2::from_jacobian(alice_pk2).unwrap();
        let mut buffer: [u8; 32] = [0; 32];
        alice_af_pk1.x().to_big_endian(&mut buffer).unwrap();
        alice_af_pk2.x().real().to_big_endian(&mut buffer).unwrap();
        alice_af_pk2.x().imaginary().to_big_endian(&mut buffer).unwrap();
        assert!(buffer.len() == 32)
    }

    #[test]
    fn serialize_fr() {

        let rng = &mut rand::thread_rng();
        // Generate private keys
        let alice_sk = Fr::random(rng);
        let mut buffer: [u8; 32] = [0; 32];
        alice_sk.into_u256().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));
        let sk = Fr::from_slice(&buffer).unwrap();
        sk.into_u256().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));
        println!("{}", alice_sk == sk);
    }

    #[test]
    fn gen_test_case() {

        let rng = &mut rand::thread_rng();
        // Generate private keys
        let alice_sk = Fr::random(rng);
        let bob_sk = Fr::random(rng);

        // Generate public keys in G1 and G2
        let alice_pk1 = G1::one() * alice_sk;
        let bob_pk1 = G1::one() * bob_sk;

        let alice_af_pk1 = AffineG1::from_jacobian(alice_pk1).unwrap();
        let bob_af_pk1 = AffineG1::from_jacobian(bob_pk1).unwrap();

        let pk3 = alice_pk1 + bob_pk1;
        let af_pk3 = AffineG1::from_jacobian(pk3).unwrap();
        let mut buffer: [u8; 32] = [0; 32];
        alice_af_pk1.x().to_big_endian(&mut buffer).unwrap();
        println!("pk1.x {}", hex::encode(&buffer));
        alice_af_pk1.y().to_big_endian(&mut buffer).unwrap();
        println!("pk1.y {}", hex::encode(&buffer));
        bob_af_pk1.x().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));
        bob_af_pk1.y().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));
        af_pk3.x().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));
        af_pk3.y().to_big_endian(&mut buffer).unwrap();
        println!("{}", hex::encode(&buffer));

        let p4 = alice_pk1 * bob_sk;
        bob_sk.into_u256().to_big_endian(&mut buffer).unwrap();
        println!("scalar: {}", hex::encode(&buffer));

        let af_p4 = AffineG1::from_jacobian(p4).unwrap();
        af_p4.x().to_big_endian(&mut buffer).unwrap();
        println!("p.x {}", hex::encode(&buffer));
        af_p4.y().to_big_endian(&mut buffer).unwrap();
        println!("p.y {}", hex::encode(&buffer));


        assert!(true);
    }

    #[test]
    fn gen_pair_test() {

        let rng = &mut rand::thread_rng();
            // Generate private keys
        let alice_sk = Fr::random(rng);
        let bob_sk = Fr::random(rng);

        let (alice_pk1, _) = (G1::one() * alice_sk, G2::one() * alice_sk);
        let (_, bob_pk2) = (G1::one() * bob_sk, G2::one() * bob_sk);

        let mut vals = Vec::new();
        let alice_pk1_2 = -alice_pk1;
        vals.push((alice_pk1, bob_pk2));
        vals.push((alice_pk1_2, bob_pk2));
        let result = pairing_batch(&vals);

        let mut buffer: [u8; 32] = [0; 32];
        let alice_af_pk1 = AffineG1::from_jacobian(alice_pk1).unwrap();
        let alice_af_pk1_2 = AffineG1::from_jacobian(alice_pk1_2).unwrap();
        alice_af_pk1.x().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g1_1.x {}", hex::encode(&buffer));
        alice_af_pk1.y().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g1_1.y {}", hex::encode(&buffer));
        alice_af_pk1_2.x().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g1_2.x {}", hex::encode(&buffer));
        alice_af_pk1_2.y().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g1_2.y {}", hex::encode(&buffer));
        let bob_af_pk2 = AffineG2::from_jacobian(bob_pk2).unwrap();
        bob_af_pk2.x().real().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g2.x1 {}", hex::encode(&buffer));
        bob_af_pk2.x().imaginary().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g2.x2 {}", hex::encode(&buffer));
        bob_af_pk2.y().real().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g2.y1 {}", hex::encode(&buffer));
        bob_af_pk2.y().imaginary().into_u256().to_big_endian(&mut buffer).unwrap();
        println!("g2.y2 {}", hex::encode(&buffer));
        println!("{}", Gt::one() == result);

    }

    #[test]
    fn zero_test() {
        let buffer: [u8; 32] = [0; 32];
        let s = Fr::from_slice(&buffer).unwrap();
        let ret = G1::one() * s;
        let af_ret = AffineG1::from_jacobian(ret);
        assert!(af_ret.is_none());
    }

}
