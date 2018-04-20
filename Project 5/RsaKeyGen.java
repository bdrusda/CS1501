import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

        BufferedWriter output;

        File file = new File("pubkey.rsa");
        try
        {
            output = new BufferedWriter(new FileWriter(file));
            String out = new String(e.getVal(), "UTF-8");
            output.write(out);
            output.write("\n");
            out = new String(n.getVal(), "UTF-8");
            output.write(out);
            output.close();

            file = new File("privkey.rsa");
            output = new BufferedWriter(new FileWriter(file));
            out = new String(d.getVal(), "UTF-8");
            output.write(out);
            output.write("\n");
            out = new String(n.getVal(), "UTF-8");
            output.write(out);
            output.close();
        }
        catch(IOException f)
        {
            f.printStackTrace();
        }
    }
}
