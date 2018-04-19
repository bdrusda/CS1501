import java.util.Random;

public class RsaKeyGen
{
    public static void main(String[] args)
    {
        LargeInteger one = new LargeInteger(new byte[] {0x01});

        Random ran = new Random();
        //LargeInteger p = new LargeInteger(512, ran);
        LargeInteger p = new LargeInteger(512, ran);
        LargeInteger q = new LargeInteger(512, ran);
        //LargeInteger q = new LargeInteger(512, ran);
System.out.println("p: "+p.toDecimal()+"\tq: "+q.toDecimal());
        LargeInteger n = p.multiply(q);
        LargeInteger phiN = p.subtract(one).multiply(q.subtract(one));
System.out.println("n: "+n.toDecimal()+"\tphi of n: "+phiN.toDecimal());
        //Choose an e that is coprime with phiN
        LargeInteger e = new LargeInteger(new byte[] {0x3});    //Make e 3
System.out.print("e: "+e.toDecimal());
        //Determine d with XGCD
        LargeInteger d = (phiN.XGCD(e))[2];
System.out.println("\td: "+d.toDecimal());
    }
}
