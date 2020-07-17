import java.util.Random;
import java.math.BigInteger;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};
	private byte[] val;

	public HeftyInteger(byte[] b) {
		val = b;
	}

	public byte[] getVal() {
		return val;
	}


	//returns length of the val byte array
	public int length() {
		return val.length;
	}


	//Add a new byte as the most significant in this
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}


	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}


	//Negate val using two's complement representation
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	//Implement subtraction as simply negation and addition
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		// canonocalize signs to positive
		HeftyInteger thisInt = this;
		boolean thisNegative = thisInt.isNegative();
		boolean otherNegative = other.isNegative();
		if (thisNegative) thisInt = thisInt.negate();
		if (otherNegative) other = other.negate();

		HeftyInteger product = zeroVal();
		HeftyInteger multiplier = new HeftyInteger(thisInt.val);
		HeftyInteger multiplicant = new HeftyInteger(other.val);

		/* System.out.print(" this: ");
		thisInt.printVal() ;
		System.out.println();

		System.out.print(" Other: ");
		other.printVal();
		System.out.println(); */

		while(!multiplicant.isZero()) {
			if (multiplicant.isOdd()) {
				HeftyInteger m = new HeftyInteger(multiplier.getVal());
				m.extend((byte) 0);
				product = product.add(m);
			}

			multiplicant = multiplicant.shiftRight();
			multiplier = multiplier.shiftLeft();
		}
		// adjust signs
		if (thisNegative ^ otherNegative) {
			if (!product.isNegative()) product = product.negate();
		} else {
			if (product.isNegative()) product = product.negate();
		}
		return product;
	}

	private boolean isOdd()
	{
		HeftyInteger x = this;
		if(this.isNegative() ) x = x.negate();

		int lsbCell = x.length() - 1;
		byte lsb = (byte) (x.val[lsbCell] & 0x1);

		return lsb == 1;
	}

	private HeftyInteger shiftLeft()
	{
		int grow = ((this.val[0] & 0x80) != 0) ? 1 : 0;
		byte[] newv = new byte[this.val.length + grow];
		for (int i = 0; i < this.val.length; i++)
		{
			newv[i+grow] = this.val[i];
		}

		if (grow > 0) newv[0] = 0;

		boolean carry_from_prev = false;
		for (int i = newv.length -1; i >= 0; i--)
		{
			int last_bit = newv[i] & 0x80;
			newv[i] <<= 1;
			if (carry_from_prev) newv[i] |= 0x1;
			carry_from_prev = (last_bit != 0);
		}

		return new HeftyInteger(newv);
	}

	private HeftyInteger shiftRight()
	{
		HeftyInteger result = new HeftyInteger(this.val);
		boolean carry_from_prev = false;
		for (int i = 0; i < result.val.length; ++i)
		{
			 int first_bit = result.val[i] & 0x1;
			 result.val[i] = (byte) ((result.val[i] & 0xFF) >> 1);
			 if (carry_from_prev) result.val[i] |= 0x80;
			 carry_from_prev = (first_bit != 0);
		}
		return result;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		HeftyInteger diff = this.subtract(other);
		if (diff.isNegative()) {
			return gcd(other, this);
		} else {
			return gcd(this, other);
		}
	 }

	 private HeftyInteger[] gcd(HeftyInteger a, HeftyInteger b)
	 {
		 if (b.isZero())
		 {
			 return new HeftyInteger[] {a, new HeftyInteger(ONE), new HeftyInteger(ZERO)};
		 }

		 HeftyInteger[] modvals = a.mods(b);
		 HeftyInteger[] vals = gcd(b, modvals[1]);

		 HeftyInteger g = vals[0];
 	 	 HeftyInteger x = zeroVal().add(vals[2]);
		 HeftyInteger temp = (a.mods(b))[0];
		 temp = temp.multiply(vals[2]);
 	 	 HeftyInteger y = vals[1].subtract(temp);

 	 	 return new HeftyInteger[] {g, x, y};
	 }

	 private HeftyInteger[] mods(HeftyInteger other)
	 {
		 HeftyInteger rem = new HeftyInteger(this.getVal());
		 HeftyInteger q = zeroVal();
		 HeftyInteger valOne = new HeftyInteger(ONE);
		 if (!rem.isNegative())
		 {
			 while (!rem.isNegative())
			 {
				 rem = rem.subtract(other);
				 q = q.add(valOne);
			 }
		 	rem = rem.add(other);
			q = q.subtract(valOne);
		} else {
			while (rem.isNegative())
			{
				rem = rem.add(other);
				q = q.add(valOne);
			}
		}

    /* System.out.print(" this: ");
		this.printVal() ;
		System.out.println();

		System.out.print(" other: ");
		other.printVal() ;
		System.out.println();

		System.out.print(" q: ");
		q.printVal() ;
		System.out.println();

		System.out.print(" rem: ");
		rem.printVal() ;
		System.out.println(); */

		return new HeftyInteger[] {q, rem};
	 }

	/*******old********/
	public HeftyInteger extendValue(int extendBy, HeftyInteger value)
	{
	  int msb = (value.val[0]) & 0x01; //should be 0 or 1
		if (msb == 0x01) {
			msb = 0xFF;
		} else {
			msb = 0x00;
		}

	  for(int i = 0; i < extendBy; i++)
	  {
	    value.extend((byte) msb);
	  }
	  return value;
	}

	public HeftyInteger shiftBytes(int shiftBy, boolean positive) {
		int b = positive ? 0x00 : 0xFF;
		byte[] newv = new byte[this.val.length + shiftBy];
		for (int i = 0; i < this.val.length; i++) {
			newv[i] = this.val[i];
		}
	  for(int i = val.length; i < this.val.length + shiftBy; i++) {
			newv[i] = (byte) b;
	  }
		this.val = newv;
	  return this;
	}


	public HeftyInteger getSubValue(HeftyInteger value, int from, int to)
	{
	  // int subLength = to - from;
	  HeftyInteger subValue = new HeftyInteger(new byte[to - from]);
    for (int i = from; i < to; i++) {
			subValue.val[i-from] = value.val[i];
		}
    /*
	  for(int i = 0; i < subLength; i++)
	  {
	    subValue.val[i] = value.val[i + from];
	  } */

	  return subValue;
	}

	public HeftyInteger smallMultiply(HeftyInteger other)
	{
		if (this.isZero() || other.isZero())
		{
			return zeroVal();
		}
		boolean thisSign = this.isNegative();
		boolean otherSign = other.isNegative();
		if(otherSign) other = other.negate();

		HeftyInteger x = this;
		HeftyInteger product = x;
		other = other.subtractByOne();
		while(! other.isZero() )
		{
			product = product.add(x);
			other = other.subtractByOne();
		}

    if (thisSign ^ otherSign) {
			if (!product.isNegative()) product = product.negate();
		} else {
			if (product.isNegative()) product = product.negate();
		}
		return product;
	}

	//checks each bit to make sure value is zero
	public boolean isZero()
	{
		for(int i = 0; i < val.length; i++)
		{
			if(val[i] != 0) return false;
		}

		return true;
	}

	public HeftyInteger subtractByOne()
	{
		HeftyInteger one = new HeftyInteger(ONE);
		one.getVal()[0] = 1;
		return subtract(one);
	}

	public HeftyInteger zeroVal()
	{
		HeftyInteger zero = new HeftyInteger(new byte[1]);
		zero.getVal()[0] = 0;
		return zero;
	}

	public void printVal()
	{
		System.out.print("Len: " + val.length + " ");
		for(int i = 0; i < val.length; i++)
		{
			System.out.print(" " + (val[i] & 0xFF));
		}
		System.out.println(" => " + new BigInteger(this.getVal()).toString());
	}
}
