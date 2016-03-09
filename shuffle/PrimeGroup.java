import java.math.BigInteger;
import java.security.SecureRandom;

public class PrimeGroup extends Group {
    public BigInteger generator;
    public BigInteger order;

    public PrimeGroup(int pbits) {
        SecureRandom rng = new SecureRandom();
        BigInteger seed = new BigInteger(pbits, rng);
        do {
            order = seed.nextProbablePrime();
        } while (!order.isProbablePrime(1));

        do {
            generator = new BigInteger(order.bitLength(), rng);
        } while (generator.compareTo(order) >= 0);

        // Simple test...
        BigInteger base = BigInteger.ONE;
        while (base.compareTo(order) < 0) {
            BigInteger inverse = base.modInverse(order);
            System.out.println(base + " inverse = " + inverse);
            base = base.add(BigInteger.ONE);
        }
    }

    public static void main(String[] args) {
        PrimeGroup group = new PrimeGroup(5);

        System.out.println(group.order);
        System.out.println(group.generator);
    }
}
