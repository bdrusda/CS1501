import java.util.Random;
import java.math.BigInteger;

public class LargeInteger
{

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	//Create a deep copy of a LargeInteger
	public LargeInteger(LargeInteger b)
	{
		val = new byte[b.length()];
		byte[] temp = b.getVal();
		for(int i = 0; i < b.length(); i++)
		{
			val[i] = temp[i];
		}
	}

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b)
	{
		val = b;
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd)
	{
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}

	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal()
	{
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length()
	{
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension)
	{
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

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other)
	{
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

		LargeInteger res_li = new LargeInteger(res);

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

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate()
	{
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) // 0x80 is 10000000
		{
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++)
			{
				if (val[i] != (byte) 0)
				{
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex)
			{
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++)
		{
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other)
	{
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other)
	{
		boolean aNeg = false;
		boolean bNeg = false;

		LargeInteger a = new LargeInteger(this);					//Create a copy of this number
		LargeInteger b = new LargeInteger(other);					//Create a copy of the number to multiply by

		LargeInteger result;	//Create an intially 0 result LargeInteger
		byte[] resultArr;
		if(a.length() > b.length())
		{
			resultArr = new byte[a.length()*2];
		}
		else
		{
			resultArr = new byte[b.length()*2];
		}

		//Create result array that is twice the size of the larger + 1 byte
		for(int i = 0; i < resultArr.length; i++)	//Set everything equal to 0
		{
			resultArr[i] = 0x00;
		}

		result = new LargeInteger(resultArr);
		result.extend((byte) 0);

		if(a.isNegative())
		{
			a = a.negate();
			aNeg = true;
		}
		a.extend((byte) 0);	//Extend to ensure the sign is preserved

		if(b.isNegative())
		{
			b = b.negate();
			bNeg = true;
		}
		b.extend((byte) 0);

		for(int i = 0; i < b.length(); i++)							//For each byte in b
		{
			for(int j = 0; j < 8; j++)									//For each bit in the byte
			{
				if((b.getVal()[b.length()-1] & 1) == 1)						//Check each byte of b starting at the LSB
				{
					/*
					boolean positive = !(result.isNegative());			//Get the current sign
					*/
					result = result.add(a);
					/*
					if(positive != !(result.isNegative()))				//If the addition changes it
					{
						if(positive)
							result.extend((byte) 0);									//Extend to preserve the sign
						else
							result.extend((byte) 1);
					}
					*/
				}

				boolean negative = a.isNegative();
				a = leftShift(a);											//Left shift a for future additions
				if(negative != a.isNegative())	//Check if the sign has changed as a result of the shift
				{
					if(negative)
						a.extend((byte) 1);
					else
						a.extend((byte) 0);
				}
				b = rightShift(b);											//Right shift b for future LSB checks
			}
		}

		if(aNeg)
		{
			result = result.negate();
		}

		if(bNeg)
		{
			result = result.negate();
		}

		result = result.trimExtra();

		return result;
	}

	public LargeInteger divide(LargeInteger other)
	{
		LargeInteger a = new LargeInteger(this);
		LargeInteger b = new LargeInteger(other);
		LargeInteger quotient = new LargeInteger(new byte[] {0x00});	//Initialize to 0

		while(true)									//While b can still fit into a
		{
			if(a.greaterThan(b) != -1)
			{
				quotient = quotient.add(new LargeInteger(new byte[] {0x01}));				//Add one to quotient
			}
			else
			{
				break;
			}

			a = a.subtract(b);													//Take away b from a
		}

		return quotient;
	}

	public LargeInteger mod(LargeInteger other)
	{
		LargeInteger a = new LargeInteger(this);
		LargeInteger b = new LargeInteger(other);
		LargeInteger remainder = new LargeInteger(a.getVal());			//Initialize to 0

		while(a.greaterThan(b) != -1)									//While b can still fit into a
		{
			a = a.subtract(b);												//Take away b from a
			remainder = new LargeInteger(a.getVal());						//Save the remainder incase it becomes negative
		}

		return remainder;
	}
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other)
	 {
		 LargeInteger x;
		 LargeInteger y;
		 //Ensure x is the larger of the two
		 if(this.greaterThan(other) == 1)
		 {
			 x = new LargeInteger(this);
			 y = new LargeInteger(other);
		 }
		 else if(this.greaterThan(other) == -1)
		 {
			 x = new LargeInteger(other);
			 y = new LargeInteger(this);
		 }
		 else	//If they are equal, the greatest common divisor is themselves
		 {
			 x = new LargeInteger(new byte[] {0x01});
			 y = new LargeInteger(new byte[] {0x00});
			 return new LargeInteger[] {this, x, y};
		 }

		 return XGCDhelper(x, y);
	 }

	 private LargeInteger[] XGCDhelper(LargeInteger x, LargeInteger y)
	 {
		 //Base case
		 if(y.greaterThan(new LargeInteger(new byte[] {0x00})) == 0)	//Until y is 0
		 {
			 LargeInteger passX = new LargeInteger(new byte[] {0x01});
			 LargeInteger passY = new LargeInteger(new byte[] {0x00});
			 return new LargeInteger[] {x, passX, passY};	//Return an array containing the GCD and the x and y
		 }

		 //Compute Bezout Numbers
		 LargeInteger quotient = x.divide(y);	//calculate a/b here
		 LargeInteger remainder = x.mod(y);		//calculate a%b here

		 LargeInteger[] results = XGCDhelper(y, remainder);	//Find the GCD, it's x, and it's y recursively
		 //Now work the way back up after getting the GCD

		 //Unpack the results
		 LargeInteger theGCD = results[0];
		 LargeInteger xPrev = results[1];
		 LargeInteger yPrev = results[2];

		 //Compute new x and y
		 x = yPrev;											//Calculate the new x
		 y = xPrev.subtract(quotient.multiply(yPrev));		//Calculate the new y
/*
System.out.println("xprev - (a/b * yprev) = y");
System.out.println(xPrev.toDecimal()+" - "+"("+quotient.toDecimal()+" * "+yPrev.toDecimal()+" which is "+quotient.multiply(yPrev).toDecimal()+") = "+y.toDecimal());
System.out.println(xPrev+" - "+"("+quotient+" * "+yPrev+" which is "+quotient.multiply(yPrev)+") = "+y);
*/
		 //The left bezout number is misrepresented by decimal, however it is correct
		 return new LargeInteger[] {theGCD.trimExtra(), x.trimExtra(), y.trimExtra()};			//Return the results
	 }

	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	 public LargeInteger modularExp(LargeInteger y, LargeInteger n)
	 {
		 LargeInteger x = new LargeInteger(this);
		 String bitY = y.toString();

		 //Take x^y mod n
		 LargeInteger ans = new LargeInteger(new byte[] {0x01});	//Set answer to 1
		 for(int i = 0; i < bitY.length(); i++)						//Through each byte of y
		 {
			 ans = ans.multiply(ans).mod(n);							//Square ans modulo n

			 if((bitY.charAt(i)) == '1')								//If this bit of y is a 1
			 {
			 	ans = ans.multiply(x).mod(n);								//Multiply by x modulo n
			 }
		 }

		return ans;
	 }


	 //This doesn't exist already?
	public String toString()	//Print out a binary representation of the string
	{
		StringBuilder print = new StringBuilder();

		for(int i = 0; i < val.length; i++)
		{
			print.append(String.format("%8s", Integer.toBinaryString(val[i] & 0xFF)).replace(' ', '0'));
		}

		return print.toString();
	}

	public String toDecimal()		//Won't properly represent larger numbers, but useful for understanding the format of the LargeIntegers
	{
		LargeInteger temp = new LargeInteger(this);
		String minus = "";
		if(temp.isNegative())	//If this is a negative number
		{
			temp = temp.negate();
			minus = "-";
		}

		String binary = temp.toString();
		int dec = 0;

		for(int i = 0; i < binary.length(); i++)
		{
			dec = dec << 1;	//Multiply by two, as this is a new digit
			if(binary.charAt(i) == '1')
				dec++;
		}

		return minus+dec;
	}

	public LargeInteger trimExtra()
	{
		LargeInteger temp = new LargeInteger(this);
		boolean aNeg = false;

		if(isNegative())
		{
			temp = temp.negate();
			aNeg = true;
		}

		//a is now positive, check if there is a byte that is led with 0's
		byte[] tempB = temp.getVal();

		while(tempB.length > 2)
		{
			if(tempB[0] == 0 && tempB[1] == 0)
			{
				byte[] less = new byte[tempB.length-1];
				for(int i = 0; i < less.length; i++)
				{
					less[i] = tempB[i+1];
				}
				tempB = less;
			}
			else
			{
				break;
			}
		}

		temp = new LargeInteger(tempB);


		if(aNeg)
		{
			return temp.negate();
		}

		return temp;
	}

	public LargeInteger leftShift(LargeInteger a)
	{
		byte[] value = a.getVal();						//Store the number in a byte array
		//Expansion
		if((value[0] & 128) != 0)						//Check if an expansion is necessary (if the MSB is occupied)
		{
			byte[] b = new byte[value.length + 1];			//Create new array that holds one more byte
			for(int i = 0; i < value.length; i++)			//Copy data over
			{
				b[i+1] = value[i];
			}
			value = b;										//And restore it to value
		}

		//Left shift
		int prevMSB = 0;
		for (int i = 0; i < value.length; i++)
		{
			int currByte = value.length-1-i;
			byte currMSB = (byte) (value[currByte] & 128);		//Get the MSB of the current byte

			value[currByte] <<= 1;								//Work over from LSB
			if(prevMSB != 0)
				value[currByte] |= 1;							//Update the LSB of this byte with the MSB of the previous byte
			prevMSB = currMSB;									//Update the MSB
		}
		return new LargeInteger(value);
	}

	public LargeInteger rightShift(LargeInteger a)
	{
		byte[] value = a.getVal();						//Store the number in a byte array

		//Right shift
		int prevLSB = 0;
		for (int i = 0; i < value.length; i++)
		{
			int currByte = i;
			byte currLSB = (byte) (value[currByte] & 1);		//Get the LSB of the current byte

			value[currByte] >>= 1;								//Work over from MSB
			if(currByte > 0)	//if this isn't the first byte
			{
				if(prevLSB != 1)
					value[currByte] &= 127;							//Update the MSB of this byte with the LSB of the previous byte
				else
					value[currByte] |= 128;
			}
			else		//if this is the first byte
			{
				if((value[currByte] & 64) != 0)			//If the MSB was a 1
					value[currByte] |= 128;					//Preserve it in padding
				else									//Otherwise
					value[currByte] &= 127;					//Make sure it's a 0
			}

			prevLSB = currLSB;									//Update the LSB
		}
		return new LargeInteger(value);
	}

	public int greaterThan(LargeInteger a)
	{
		//Standardize the length of the numbers
		if(a.length() > length())
		{
			byte[] aB = a.getVal();				//Convert to byte arrays
			byte[] bB = val;

			int diff = aB.length - bB.length;	//Get the difference in sizes

			byte pad = (byte) 0;				//Determine how to pad
			if (bB[0] < 0)
			{
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[aB.length];	//Make a new array for b of a's length
			for (int i = 0; i < diff; i++)		//Pad the difference
			{
				newb[i] = pad;
			}

			for (int i = 0; i < bB.length; i++)	//And fill with the contents of b
			{
				newb[i + diff] = bB[i];
			}

			bB = newb;

			val = bB;							//Update this LargeInteger
		}
		else if(a.length() < length())
		{
			byte[] aB = a.getVal();				//Convert to byte arrays
			byte[] bB = val;

			int diff = bB.length - aB.length;	//Get the difference in sizes

			byte pad = (byte) 0;				//Determine how to pad
			if (aB[0] < 0)
			{
				pad = (byte) 0xFF;
			}

			byte[] newa = new byte[bB.length];	//Make a new array for a of b's length
			for (int i = 0; i < diff; i++)		//Pad the difference
			{
				newa[i] = pad;
			}

			for (int i = 0; i < aB.length; i++)	//And fill with the contents of a
			{
				newa[i + diff] = aB[i];
			}

			aB = newa;

			a = new LargeInteger(aB);			//Update this LargeInteger
		}

		String aBits = a.toString();
		String thisBits = toString();

		for(int i = 0; i < aBits.length(); i++)
		{
			if(thisBits.charAt(i) < aBits.charAt(i))
			{
				return -1;
			}
			else if(thisBits.charAt(i) > aBits.charAt(i))
			{
				return 1;
			}
		}

		return 0;
	}
}
