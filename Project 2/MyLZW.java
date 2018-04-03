public class MyLZW
{
    private static final int R = 256;           // number of input chars
    private static int W = 9;                   // codeword width                  /*Starts at 9, will increase up to 16*/
    private static int L = (int) Math.pow(2,W);  // number of codewords = 2^W       /*Starts at 2^9, will increase with L up to 2^16*/
    private static char mode;

    public static void main(String[] args)
    {
        if(args[0].equals("-"))
        {
            if(args.length == 2)
            {
                if(args[1].equals("n"))
                    mode = 'n';
                else if(args[1].equals("r"))
                    mode = 'r';
                else if(args[1].equals("m"))
                    mode = 'm';
                else throw new IllegalArgumentException("No valid mode selected");
            }
            else
            {
                mode = 'n'; //By default, nothing mode
            }
            compress();
        }
        else if(args[0].equals("+"))
            expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    public static void compress()
    {
        int uncompressed = 0;
        int compressed = 0;
        double oldRatio = 0.0;
        double newRatio = 0.0;

        String input = BinaryStdIn.readString();                                /*Take the input file and convert it into binary*/
        TST<Integer> st = new TST<Integer>();                                   /*Create a symbol table of key value pairs*/
        for (int i = 0; i < R; i++)                                             /*For the entire alphabet (the initial codewords)*/
            st.put("" + (char) i, i);                                               /*Add each letter at its corresponding ASCII value*/
        int code = R+1;  // R is codeword for EOF                               /*The next codeword is set to be entered at the end of the alphabet*/

        BinaryStdOut.write((byte) mode);                                               //Encode the mode into the compressed file

        while (input.length() > 0)                                              /*While the file has not been read in its entirety*/
        {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.      /*Match the longest prefix*/
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.           /*Write out the new codeword with the specified codeword length*/
            int t = s.length();                                                     /*t is the length of the codeword*/

            uncompressed += t*16;   //size of string
            compressed += W;        //codeword size

            if(t < input.length())
            {
                String temp = input.substring(0, t + 1);

                if (code < L)    // Add s to symbol table.        /*if codeword is shorter than the file and the codebook isn't full*/            /*?*/
                {
                    st.put(temp, code++);                              /*add the codeword and the first letter of the next word to the codebook*/
                    oldRatio = (double)uncompressed/(double)compressed;
                }
                else if(code >= L)                                /*If the codebook is full*/
                {
                    if(W < 16)                                                              /*If the codeword size could get bigger*/
                    {
                        W++;                                                                    /*Increase the codeword size to accomodate more patterns*/
                        L = (int) Math.pow(2,W);                                                 /*Increase the codebook size accordingly*/
                        st.put(temp, code++);                              /*And add the word as per usual*/
                    }
                    else                                                                    /*Indicates that the maximum codeword size has been reached*/
                    {
                        /*Handle modes*/
                        if(mode == 'r')         //Reset mode
                        {
                            st = new TST<Integer>();        //Create a new codebook
                            for (int i = 0; i < R; i++)     //Reinitialize the codebook
                                st.put("" + (char) i, i);

                            W = 9;                          //Reset the codeword size to the starting size
                            L = (int) Math.pow(2,W);              //Reset the codebook size to the starting size
                            code = R+1;                     //Adjust the code int appropriately

                            st.put(temp, code++);  //add the word to the beginning of the book
                        }
                        else if(mode == 'm')    //Monitor mode
                        {
                            newRatio = (double)uncompressed/(double)compressed;
                            double ratio = oldRatio/newRatio;
                            if(ratio > 1.1)
                            {
                                //Reset code
                                st = new TST<Integer>();        //Create a new codebook
                                for (int i = 0; i < R; i++)     //Reinitialize the codebook
                                    st.put("" + (char) i, i);

                                W = 9;                          //Reset the codeword size to the starting size
                                L = (int) Math.pow(2,W);              //Reset the codebook size to the starting size
                                code = R+1;                     //Adjust the code int appropriately

                                st.put(temp, code++);  //add the word to the beginning of the book
                            }
                        }
                        else                    //Nothing mode
                        {
                            //Do nothing
                        }
                    }
                }
            }
            input = input.substring(t);            // Scan past s in input.         /*get the remainder of the input*/
        }
        BinaryStdOut.write(R, W);                                               /*Write the output to the stdout*/                                                  /*?*/
        BinaryStdOut.close();                                                   /*Close the output*/
    }


    public static void expand()
    {
        int uncompressed = 0;
        int compressed = 0;
        double oldRatio = 0.0;
        double newRatio = 0.0;

        String[] st = new String[(int) Math.pow(2,16)];                                            /*Create the codebook to hold the library of codewords*/
        int i; // next available codeword value

        mode = BinaryStdIn.readChar();  //Get the mode from the input file

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)                                                 /*Traverse the alphabet*/
            st[i] = "" + (char) i;                                                  /*And add the each corresponding letter to the codebook*/
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);                                  /*Get the next number to decode from the file*/
        if (codeword == R)                                                      /*If we reach the end of the file*/                                                 /*?*/
            return;           // expanded message is empty string
        String val = st[codeword];                                              /*Get the corresponding set of letters from the codebook*/
        while (true)                                                            /*Expand the rest of the codebook until we reach the end of the file*/
        {
            uncompressed += val.length()*16;   //codeword length * expansion(16-bit)
            compressed += W;                    //codeword size

            BinaryStdOut.write(val);                                                /*Output the letter pattern*/

            if(i >= L) /*If the codebook is full*/
            {
                if(W < 16)
                {
                    W++;                    /*Increase the codeword size to accomodate more letter*/
                    L = (int) Math.pow(2,W); /*Increase the codebook size accordingly*/
                    oldRatio = (double) uncompressed/(double) compressed;
                }
                else
                {
                    if(mode == 'r')         //Reset Mode
                    {
                        //Reinitialize the array
                        st = new String[(int) Math.pow(2,16)];
                        for(i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";

                        W = 9;                  //Reset the codeword size to the starting size
                        L = (int) Math.pow(2,W);      //Reset the codebook size to the starting size
                        i = R+1;                //Reset the pattern to only the alphabet
                    }
                    else if(mode == 'm')    //Monitor mode
                    {
                        newRatio = (double) uncompressed/(double) compressed;
                        double ratio = oldRatio/newRatio;
                        if(ratio > 1.1)
                        {
                            //Reinitialize the array
                            st = new String[(int) Math.pow(2,16)];
                            for(i = 0; i < R; i++)
                                st[i] = "" + (char) i;
                            st[i++] = "";

                            W = 9;                  //Reset the codeword size to the starting size
                            L = (int) Math.pow(2,W);      //Reset the codebook size to the starting size
                            i = R+1;                //Reset the pattern to only the alphabet
                        }
                    }
                    else                    //Nothing mode
                    {
                        //Do nothing
                    }
                }
            }

            codeword = BinaryStdIn.readInt(W);                                      /*Read in the next number to decode*/
            if (codeword == R)                                                      /*If we have reached the end of the file, */
                break;                                                                  /*exit*/
            String s = st[codeword];                                                /*Get the corresponding set of letters from the codebook*/
            if (i == codeword)                                                      /*If the word was just added to the codebook*/
                s = val + val.charAt(0);   // special case hack                         /*Handle the corner case, it is the last word plus the first character of this one*/
            if (i < L)                                                              /*If the codebook is not full*/
            {
                st[i++] = val + s.charAt(0);                                            /*Add the new codeword to the book*/
                oldRatio = (double) uncompressed/(double) compressed;
            }
            val = s;                                                                /*Keep the partial word to use for the next codeword*/
        }
        BinaryStdOut.close();                                                   /*Close the output*/
    }
}
