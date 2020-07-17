import java.util.Random;
import java.math.BigInteger;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};
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
		// canonocalize lengths
		HeftyInteger thisInt = this;
		if(thisInt.length() < other.length() )
		  thisInt = extendValue(other.length() - thisInt.length(), thisInt);
		if(thisInt.length() > other.length() )
		  other = extendValue(thisInt.length() - other.length(), other);

		// canonocalize signs to positive
		boolean thisNegative = thisInt.isNegative();
		boolean otherNegative = other.isNegative();
		if (thisNegative) thisInt = thisInt.negate();
		if (otherNegative) other = other.negate();

    // multiply positives
		HeftyInteger product = thisInt.multiplyPositives(other);

		// adjust signs
		if (thisNegative ^ otherNegative) {
			if (!product.isNegative()) product = product.negate();
		} else {
			if (product.isNegative()) product = product.negate();
		}
		return product;
	}

	public HeftyInteger multiplyNew(HeftyInteger other) {
		HeftyInteger product = zeroVal();
		HeftyInteger multiplier = HeftyInteger(this.val);
		HeftyInteger multiplicant = HeftyInteger(other.val);

		while(!multiplicant.isZero()) {
			if (multiplicant.isOdd()) {
				product = sum.add(multiplier);
			}
			multiplicant.shiftRight();
			multipliser.shiftLeft();
		}
		return product;
	}


  // assumes positives and same length
	private HeftyInteger multiplyPositives(HeftyInteger other) {
		// canonocalize lengths
		HeftyInteger thisInt = this;
		if(thisInt.length() < other.length() )
		  thisInt = extendValue(other.length() - thisInt.length(), thisInt);
		if(thisInt.length() > other.length() )
		  other = extendValue(thisInt.length() - other.length(), other);

		int n = thisInt.length();
		if(n == 1) return byteMultiply(thisInt.val[0], other.val[0]);

		int mid = n / 2;
		HeftyInteger Xl = getSubValue(thisInt, mid, n);
		HeftyInteger Xh = getSubValue(thisInt, 0, mid);
		HeftyInteger Yl = getSubValue(other, mid, n);
		HeftyInteger Yh = getSubValue(other, 0, mid);

		System.out.print(" xh: ");
		Xh.printVal() ;
		System.out.println();

		System.out.print(" xl: ");
		Xl.printVal();
		System.out.println();

		System.out.print(" Yh: ");
		Yh.printVal();
		System.out.println();

		System.out.print(" Yl: ");
		Yl.printVal();
		System.out.println();

		HeftyInteger M1 = Xh.multiplyPositives(Yh);
		HeftyInteger M2 = Xh.multiplyPositives(Yl);
		HeftyInteger M3 = Xl.multiplyPositives(Yh);
		HeftyInteger M4 = Xl.multiplyPositives(Yl);

		System.out.print(" M1: ");
		M1.printVal() ;
		System.out.println();

		System.out.print(" M2: ");
		M2.printVal();
		System.out.println();

		System.out.print(" M3: ");
		M3.printVal();
		System.out.println();

		System.out.print(" M4: ");
		M4.printVal();
		System.out.println();

		HeftyInteger M2M3toN = (M2.add(M3)).shiftBytes(n/2, true);
		HeftyInteger M1toN = M1.shiftBytes(n, true);

		System.out.print(" M1toN shifted: ");
		M1toN.printVal();
		System.out.println();

		System.out.print(" M2M3toN shifted: ");
		M2M3toN.printVal();
		System.out.println();

    return (M1toN.add(M2M3toN)).add(M4);
	}

	private HeftyInteger byteMultiply(byte a, byte b){
		int r = (a & 0xFF) * (b & 0xFF);
		String rs = Integer.toString(r);
		System.out.println(" a:" + (a & 0xFF) + " b:" + (b & 0xFF) + " r:" + r + " rs:" + rs);
		// System.out.println(" a:" + Integer.toBinaryString(a&0xFF) + " b:" + Integer.toBinaryString(b&0xFF) + " r:" + Integer.toBinaryString(r));

		return new HeftyInteger(new BigInteger(rs).toByteArray());
	}
	public HeftyInteger multiply2(HeftyInteger other) {
		// YOUR CODE HERE
		HeftyInteger thisInt = this;
		if(thisInt.length() < other.length() )
		  thisInt = extendValue(other.length() - thisInt.length(), thisInt);
		if(thisInt.length() > other.length() )
		  other = extendValue(thisInt.length() - other.length(), other);

		System.out.print(" thisInt: ");
		thisInt.printVal() ;
		System.out.println();

		System.out.print(" other: ");
		other.printVal() ;
		System.out.println();

    return smallMultiply(other);

		/*

		int n = thisInt.length();
		if(n == 0) return zeroVal(); //?? i think
		if(n == 1) return smallMultiply(other); //i think

		int mid = n / 2;

		HeftyInteger Xh = getSubValue(thisInt, mid, n);
		HeftyInteger Xl = getSubValue(thisInt, 0, mid);
		HeftyInteger Yh = getSubValue(other, mid, n);
		HeftyInteger Yl = getSubValue(other, 0, mid);

		HeftyInteger M1 = Xh.multiply(Yh);
		HeftyInteger M2 = Xh.multiply(Yl);
		HeftyInteger M3 = Xl.multiply(Yh);
		HeftyInteger M4 = Xl.multiply(Yl);

		HeftyInteger M1M2 = M1.add(M2);
		HeftyInteger M3M4 = M3.add(M4);
		return M1M2.add(M3M4);
		*/
	}

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
		return null;
	 }



}
