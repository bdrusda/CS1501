import java.util.Random;
import java.nio.ByteBuffer;

public class LargeIntTester
{
    public static void main(String[] args)
    {
        Random ran = new Random();
        LargeInteger a = new LargeInteger(21, ran);
        LargeInteger b = new LargeInteger(21, ran);
        LargeInteger ab = a.add(b);
/*
        System.out.println("\nTesting Basic Math");

        System.out.println("\nA is:\t "+a+" or "+a.toDecimal());
        System.out.println("B is:\t "+b+" or "+b.toDecimal());
        System.out.println("---------------------------------------------");
        System.out.println("A + B is "+ab+" or "+ab.toDecimal());

        ab = a.multiply(b);

        System.out.println("\nA is:\t "+a+" or "+a.toDecimal());
        System.out.println("B is:\t "+b+" or "+b.toDecimal());
        System.out.println("---------------------------------------------");
        System.out.println("A * B is "+ab+" or "+ab.toDecimal());

        ab = a.divide(b);
        System.out.println("\nA is:\t "+a+" or "+a.toDecimal());
        System.out.println("B is:\t "+b+" or "+b.toDecimal());
        System.out.println("---------------------------------------------");
        System.out.println("A / B is "+ab+" or "+ab.toDecimal());
        ab = a.mod(b);
        System.out.println("A % B is "+ab+" or "+ab.toDecimal());

        System.out.println("\n\nTesting XGCD\n");
*/

        //a = new LargeInteger(ByteBuffer.allocate(4).putInt(1633361).array());
        //b = new LargeInteger(ByteBuffer.allocate(4).putInt(1238597).array());

        LargeInteger[] xgcd = a.XGCD(b);
        System.out.println("The GCD of "+a.toDecimal()+" and "+b.toDecimal()+" is "+xgcd[0].toDecimal());
        System.out.println("That is, "+xgcd[0].toDecimal()+" is "+a.toDecimal()+"*"+xgcd[1].toDecimal()+" + "+b.toDecimal()+"*"+xgcd[2].toDecimal());
        System.out.println("That is, "+xgcd[0]+" is "+a.toDecimal()+"*"+xgcd[1]+" + "+b.toDecimal()+"*"+xgcd[2]);

/*
        a = new LargeInteger(ByteBuffer.allocate(4).putInt(7).array());
        b = new LargeInteger(ByteBuffer.allocate(4).putInt(8679).array());
        ab = a.multiply(b);
        LargeInteger c = new LargeInteger(ByteBuffer.allocate(4).putInt(-2338).array());

        System.out.println("\n\n"+a.toDecimal()+"*"+b.toDecimal()+"="+ab.toDecimal());
        System.out.println(c.toDecimal()+"-"+ab.toDecimal()+"="+c.subtract(ab).toDecimal());
*/





/*
        System.out.println("\n\nTesting Modular Exponentation\n");

        a = new LargeInteger(new byte[] {0x05});                //5
        b = new LargeInteger(new byte[] {0x20});                //32
        LargeInteger c = new LargeInteger(new byte[] {0x11});   //17

        System.out.println(a.toDecimal()+"^"+b.toDecimal()+" % "+c.toDecimal()+" = "+a.modularExp(b, c).toDecimal());
*/
    }
}
