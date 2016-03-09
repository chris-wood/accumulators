import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;

public class CompositeModulus {
    List<BigInteger> factors;
    BigInteger modulus;

    public CompositeModulus() {
        factors = new ArrayList<BigInteger>();
        modulus = BigInteger.ONE;
    }

    public void addFactor(BigInteger f) {
        factors.add(f);
        value = value.multiple(f);
    }
}
