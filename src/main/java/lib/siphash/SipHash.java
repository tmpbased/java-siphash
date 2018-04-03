package lib.siphash;

import java.nio.ByteBuffer;

public class SipHash {
  private final int c, d;
  private long v0, v1, v2, v3;

  private long len;
  private long m;
  private int mi;

  public SipHash(final int c, final int d, final long k0, final long k1) {
    this.c = c;
    this.d = d;
    this.v0 = k0 ^ 0x736f6d6570736575L;
    this.v1 = k1 ^ 0x646f72616e646f6dL;
    this.v2 = k0 ^ 0x6c7967656e657261L;
    this.v3 = k1 ^ 0x7465646279746573L;
    this.len = 0L;
    this.m = 0L;
    this.mi = 0;
  }

  private void round() {
    this.v0 += this.v1;
    this.v2 += this.v3;
    this.v1 = Long.rotateLeft(this.v1, 13);
    this.v3 = Long.rotateLeft(this.v3, 16);
    this.v1 ^= this.v0;
    this.v3 ^= this.v2;
    this.v0 = Long.rotateLeft(this.v0, 32);
    this.v2 += this.v1;
    this.v0 += this.v3;
    this.v1 = Long.rotateLeft(this.v1, 17);
    this.v3 = Long.rotateLeft(this.v3, 21);
    this.v1 ^= this.v2;
    this.v3 ^= this.v0;
    this.v2 = Long.rotateLeft(this.v2, 32);
  }

  public void update(final byte b) {
    this.len++;
    this.m |= (b & 0xFFL) << (this.mi * Byte.SIZE);
    this.mi++;
    if (this.mi == 8) {
      this.v3 ^= this.m;
      for (int i = 0; i < this.c; i++) {
        round();
      }
      this.v0 ^= this.m;
      this.m = 0;
      this.mi = 0;
    }
  }

  public void update(final ByteBuffer b) {
    while (b.hasRemaining()) {
      update(b.get());
    }
  }

  public long finish() {
    final long len = this.len;
    while (this.mi < 7) {
      update((byte) 0);
    }
    update((byte) (len & 0xFFL));
    this.v2 ^= 0xFFL;
    for (int i = 0; i < this.d; i++) {
      round();
    }
    return this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
  }
}
