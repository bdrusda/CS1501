import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;

public class RsaSign
{
    public static void main(String[] args)
    {
        if(args.length != 2)
        {
            System.out.println("Invalid number of arguments\nCorrect format: java RsaSign s|v file.txt");
            System.exit(-1);
        }

        File file = new File(args[1]);
        if(!file.exists())
        {
            System.out.println("Specified file does not exist");
            System.exit(-1);
        }

        if(args[0].equals("s"))
        {
            //Generate a SHA-256 hash of the contents
            try
            {
                // read in the file to hash
                Path path = file.toPath();
                byte[] data = Files.readAllBytes(path);

                // create class instance to create SHA-256 hash
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // process the file
                md.update(data);
                // generate a has of the file
                byte[] digest = md.digest();

                //Decrypt the has value using the private key stored in privkey.rsa
                //If privkey.rsa is not in the directory, raise an error
                File pKey = new File("privkey.rsa");
                if(!pKey.exists())
                {
                    System.out.println("Private key not found");
                    System.exit(-1);
                }

    			ObjectInputStream in = new ObjectInputStream(new FileInputStream("privkey.rsa"));
    			LargeInteger d = (LargeInteger) in.readObject();
    			LargeInteger n = (LargeInteger) in.readObject();
                in.close();

    			LargeInteger message = n.modularExp(d, n);

                //Write out the signature to a file named as the original, with an extra .sig extension
    			FileOutputStream signed = new FileOutputStream(file.getName() + ".sig");
    			ObjectOutputStream output = new ObjectOutputStream(signed);
    			output.writeObject(data);
    			output.writeObject(message);
    			output.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(args[0].equals("v"))
        {
            try
            {
                //Read the contents of the original file
    			MessageDigest md = MessageDigest.getInstance("SHA-256");
    			md.update(Files.readAllBytes(file.toPath()));
    			byte[] hash = md.digest();

                //Read the signed hash from the corresponding .sig File
                    //If the .sig file is not in the directory, raise an error
    			File key = new File(file.getName() + ".sig");
    			if(!key.exists())   //make sure the signature file is in the directory
                {
    				System.out.println(".sig file doesn't exist");
    				System.exit(-1);
    			}

    			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.getName() + ".sig"));
    			byte[] data = (byte[]) in.readObject();
    			LargeInteger message = (LargeInteger) in.readObject();
    			in.close();

                //Generate a SHA-256 hash of contents
    			md = MessageDigest.getInstance("SHA-256");
    			md.update(data);
    			hash = md.digest();
    			LargeInteger hashLI = new LargeInteger(hash);    //init hash

                //Encrypt the value with the key from pubkey.rsa
                //If pubkey.rsa is not in the directory, raise an error
    			File pubKey = new File("pubkey.rsa");
    			if(!pubKey.exists())
                {
    				System.out.println("Public key not found");
    				System.exit(-1);
    			}

    			FileInputStream publicKey = new FileInputStream("pubkey.rsa");
    			ObjectInputStream keyReader = new ObjectInputStream(publicKey);
    			LargeInteger e = (LargeInteger) keyReader.readObject();
    			LargeInteger n = (LargeInteger) keyReader.readObject();
    			keyReader.close();

    			LargeInteger encryptedData = message.modularExp(e, n);

//Might not be necessary
    			encryptedData = encryptedData.trimExtra();
    			hashLI = hashLI.trimExtra();

                //Compare the hash value that was generated from myfile.txt to the one that was recovered from the signature
                //Print a message indicating whether it is valid
    			if(encryptedData.greaterThan(hashLI) == 0)
                {
                    System.out.println("The signature on this file is valid");
                }
			    else
                {
                    System.out.println("The signature on this file is not valid");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Invalid mode choice: s|v");
            System.exit(-1);
        }
    }
}
