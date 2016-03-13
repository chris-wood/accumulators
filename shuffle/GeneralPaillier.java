import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.security.SecureRandom;

public class GeneralPaillier {

    BigInteger N;
    BigInteger Np;
    BigInteger N2;
    List<BigInteger> mods;
    BigInteger p;
    BigInteger q;
    BigInteger lambda;
    BigInteger mu;
    BigInteger g;
    int s;

    public GeneralPaillier(int bitLength, int s) {
        CompositeModulus mod = PrimeGenerator.generateCompositeModulus(bitLength - 1);
        p = mod.factors.get(0);
        q = mod.factors.get(1);
        N = mod.composite;
        Np = N;

        // Ns = N^{s + 1}
        //    s = 2 in the standard Paillier scheme
        mods = new ArrayList<BigInteger>();
        N2 = N.multiply(N);
        this.s = s;
        for (int i = 2; i < s; i++) { // if s = 3
            Np = N2.multiply(BigInteger.ONE); // Np = N^2
            mods.add(Np);
            N2 = N2.multiply(N); // N2 = N^2*N = N^3
        }

        // \lambda = \phi(n)
        BigInteger p1 = p.subtract(BigInteger.ONE);
        BigInteger q1 = q.subtract(BigInteger.ONE);
        lambda = p1.multiply(q1);

        // \mu = \phi(N)^{-1} mod N = lambda^{-1} mod N
        mu = lambda.modInverse(N);

        // g = (1 + n)^j x mod n^{s + 1}
        BigInteger j = randomElementInN();
        BigInteger x = randomElementInN();
        g = N.add(BigInteger.ONE).modPow(j, N2).multiply(x).mod(N2);
    }

    public BigInteger factorial(int k) {
        BigInteger f = BigInteger.ONE;
        for (int i = 1; i <= k; i++) {
            //
        }
        return f;
    }

    public BigInteger L(BigInteger u) {
        BigInteger i = BigInteger.ZERO;
        
        //return u.subtract(BigInteger.ONE).divide(N);
        for (int j = 1; j <= s; j++) {
            BigInteger a = u.mod(mods.get(j)); // mod n^{j+1}
            BigInteger t1 = L(a);
            BigInteger t2 = i.multiply(BigInteger.ONE);
            for (int k = 2; k <= j; k++) {
                i = i.subtract(BigInteger.ONE);
                t2 = t2.multiply(i).mod(mods.get(j - 1)); // mod n^j
                
                BigInteger numerator = t2.multiply(mods.get(k)); // mod n^{k-1}
                BigInteger fraction = numerator.divide(factorial(k));
                t1 = t1.subtract(fraction).mod(mods.get(j)); // mod n^j
            }
        }
        
        return i;
    }

    public BigInteger encrypt(BigInteger m) {
        BigInteger r = randomElementInN();
        while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(N) >= 0) {
            r = randomElementInN();
        }

        BigInteger gm = g.modPow(m, N2);
        BigInteger rn = r.modPow(Np, N2);
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

    public BigInteger randomPlaintextElement() {
        return randomElementIn(Np);
    }

    public static void main(String[] args) {
        GeneralPaillier p = new GeneralPaillier(Integer.parseInt(args[0]), 2);

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
