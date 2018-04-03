package lib.siphash;

import java.util.Random;

public class SipHashFactory {
  private long k0, k1;

  public SipHashFactory(final Random rand) {
    this.k0 = rand.nextLong();
    this.k1 = rand.nextLong();
  }

  public SipHash newHash() {
    return new SipHash(2, 4, this.k0, this.k1);
  }
}
