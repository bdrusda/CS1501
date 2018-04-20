import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class RsaKeyGen
{
    public static void main(String[] args)
    {
        LargeInteger one = new LargeInteger(new byte[] {0x01});

        Random ran = new Random();
        //Pick p and q
        LargeInteger p = new LargeInteger(256, ran);
        LargeInteger q = new LargeInteger(256, ran);
        //Generate n
        LargeInteger n = p.multiply(q);
        LargeInteger phiN = p.subtract(one).multiply(q.subtract(one));

        //Choose an e that is coprime with phiN
        LargeInteger e = new LargeInteger(phiN);    //Get a random prime
        while((e.greaterThan(phiN) != -1) || (e.greaterThan(one) != 1)) //While e is not between 1 and phi of N
        {
            e = new LargeInteger(512, ran);                                 //Set e equal to a new large integer
        }

        //Determine d with XGCD
        LargeInteger d = (phiN.XGCD(e))[2];

        System.out.println("Writing the public and private keys to their respective files");
        try
        {
            ObjectOutputStream output;
            FileOutputStream pubFile = new FileOutputStream("pubkey.rsa");
            FileOutputStream privFile = new FileOutputStream("privkey.rsa");

			output = new ObjectOutputStream(pubFile);
			output.writeObject(e);
			output.writeObject(n);
			output.close();

			output = new ObjectOutputStream(privFile);
            output.writeObject(d);
			output.writeObject(n);
			output.close();
        }
        catch(IOException f)
        {
            f.printStackTrace();
        }
    }
}
