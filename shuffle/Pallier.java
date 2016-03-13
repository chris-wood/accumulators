import java.math.BigInteger;
import java.util.List;
import java.security.SecureRandom;

public class Pallier {

    BigInteger N;
    BigInteger N2;
    BigInteger p;
    BigInteger q;
    BigInteger lambda;
    BigInteger mu;
    BigInteger g;

    public Pallier(int bitLength) {
        CompositeModulus mod = PrimeGenerator.generateCompositeModulus(bitLength - 1);
        p = mod.factors.get(0);
        q = mod.factors.get(1);
        N = mod.composite;
        N2 = N.multiply(N);

        // \lambda = \phi(n)
        BigInteger p1 = p.subtract(BigInteger.ONE);
        BigInteger q1 = q.subtract(BigInteger.ONE);
        lambda = p1.multiply(q1);

        // \mu = \phi(N)^{-1} mod N = lambda^{-1} mod N
        mu = lambda.modInverse(N);

        // g = N + 1
        g = N.add(BigInteger.ONE);
    }

    public BigInteger L(BigInteger u) {
        return u.subtract(BigInteger.ONE).divide(N);
    }

    public BigInteger encrypt(BigInteger m) {
        BigInteger r = randomElementInN();
        while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(N) >= 0) {
            r = randomElementInN();
        }

        BigInteger gm = g.modPow(m, N2);
        BigInteger rn = r.modPow(N, N2);
        BigInteger c = gm.multiply(rn).mod(N2);

        return c;
    }

    public BigInteger decrypt(BigInteger ct) {
        BigInteger cl = ct.modPow(lambda, N2);
        BigInteger pt = L(cl).multiply(mu).mod(N);
        return pt;
    }

    public BigInteger add(BigInteger x, BigInteger y) {
        return x.multiply(y).mod(N2);
    }

    public BigInteger multiply(BigInteger x, BigInteger y) {
        return x.modPow(y, N2); // k * m
    }

    public BigInteger randomElementIn(BigInteger max) {
        SecureRandom rng = new SecureRandom();
        BigInteger g = null;
        int numBits = max.bitLength();

        do {
            g = new BigInteger(numBits, rng);
        } while (g.compareTo(max) >= 0);

        return g;
    }

    public BigInteger randomElementInN2() {
        return randomElementIn(N2);
    }

    public BigInteger randomElementInN() {
        return randomElementIn(N);
    }

    public static void main(String[] args) {
        Pallier p = new Pallier(Integer.parseInt(args[0]));

        BigInteger m1 = p.randomElementInN();
        System.out.println("Encrypting: " + m1);

        BigInteger c1 = p.encrypt(m1);
        System.out.println("... " + c1);

        BigInteger m2 = p.randomElementInN();
        BigInteger c2 = p.encrypt(m2);

        BigInteger c3 = p.add(c1, c2);

        BigInteger m1m = p.decrypt(c1);
        System.out.println("Decryption: " + m1m);
        BigInteger m2m = p.decrypt(c2);
        System.out.println("Decryption: " + m2m);

        BigInteger m3m = p.decrypt(c3);
        System.out.println("Homomorphic decryption: " + m3m);
        System.out.println("Plaintext result:       " + m1.add(m2).mod(p.N));
    }
}
