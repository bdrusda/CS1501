public class DLB
{
    Node root;                          //the first node - contains no data but has a single child

    public DLB()                        //Constructor for the DLB trie
    {
        root = new Node();                  //Create the root node
    }

    public void add_word(String word)   //Add a word to the trie
    {
        add_word(root, word);         //Start at root node
    }

    private void add_word(Node curr, String word)   //Helper
    {
        for(int i = 0; i < word.length(); i++)    //Add the word letter by letter
        {
            curr = add_char(curr, word.charAt(i));   //Add character(checking if it is already there)
        }

        Node term = new Node('^');              //Create a node containing the terminator character
        if(curr.child == null)                  //If the word is not a prefix for another word
        {
            curr.child = term;                      //Terminate the word
        }
        else                                    //If it is a prefix
        {
            curr = curr.child;                      //Access the child
            while(curr.sibling != null)             //Iterate through until there is no sibling left
            {
                curr = curr.sibling;
            }

            curr.sibling = term;                    //Terminate the word in the sibling location
        }
    }

    private Node add_char(Node node, char curr)    //Add an individual character and return its position
    {
        //If this is a new level in the tree, create a node, set it to the child, and return it
        if(node.child == null)
        {
            Node insert = new Node(curr);                   //create a Node with the given char value
            node.child = insert;                            //make this node the child
            return insert;                                  //return it
        }
        //If this is an existing level, check each node to see if the value is present
        node = node.child;                              //This node exists and has a value
        if(node.value == curr)                          //If this node contains the value to be inserted
        {
            return node;                                    //return the node containing the character
        }
        else                                            //If this node doesn't contain the value, check its siblings
        {
            while(node.sibling != null)                     //traverse the sibling nodes to see if it is present
            {
                node = node.sibling;                            //set node to the sibling node
                if(node.value == curr)                          //if the sibling contains the character
                {
                    return node;                                    //return the node containing the character
                }
            }
        }
        //If the value has not been found, we are now at the rightmost node with no siblings
        Node insert = new Node(curr);   //create a Node with the given char value
        node.sibling = insert;          //make this node the sibling
        return insert;                  //return it
    }

    public boolean check_word(String word)
    {
        Node curr = root;
        for(int i = 0; i < word.length(); i++)
        {
            if((curr = check_char(curr, word.charAt(i))) == null)
                return false;
        }
        //at this point we have traversed the entire word -- now we verify that there is a null terminator following it
        if(curr.child == null)
        {
            return false;
        }
        else
        {
            curr = curr.child;  //check the child
            if(curr.value == '^')
            {
                return true;
            }
            else
            {
                while(curr.sibling != null)
                {
                    curr = curr.sibling;
                    if(curr.value == '^')
                        return true;
                }
            }
        }
        return false;
    }

    public Node check_char(Node node, char curr)
    {
        if(node.child != null)  //if the node has a child
        {
            node = node.child;      //check the child
            if(node.value == curr)  //if this node matches
            {
                return node;            //return it
            }
            else                    //if the node didn't match
            {
                while(node.sibling != null) //check each of its siblings
                {
                    node = node.sibling;    //check the sibling
                    if(node.value == curr)  //if the sibling matches
                    {
                        return node;            //return that node
                    }
                }
                return null;               //if none of the siblings match, return null
            }
        }
        else                    //if the node doesn't have a child, the remainder of the word doesn't exist
        {
            return null;
        }
    }

    public String[] get_predictions(String partial) //Get predictions from trie based on a partial word
    {
        Node curr = root;
        for(int i = 0; i < partial.length(); i++)       /*traverse through the DLB to the end of the partial word*/
        {
            curr = check_char(curr, partial.charAt(i));
            if(curr == null)     //if the prefix isn't in the DLB, return null
                return null;
        }

        return get_predictions(partial, curr.child);
    }

    public String[] get_predictions(String partial, Node curr)  //Helper
    {
        /*we have the end of the partial word if it exists*/
        String[] dict = new String[5];
        String[] temp = new String[5];
        if(curr == null)
        {
            /*this means that the there are no suitable predictions*/
            return dict;    /*so just return what we have*/
        }

        StringBuilder pred = new StringBuilder(partial);    //Initialize a stringbuilder comprised of the partial word

        while(curr != null)
        {
            if(curr.sibling != null)    //if it has a sibling
            {
                temp = get_predictions(pred.toString(), curr.sibling);  //get the full list of predictions from the sibling (and it's siblings)
                int i = 0;
                int j = 0;
                while(i < 5 && temp[j] != null)
                {
                    if(dict[i] == null)
                    {
                        dict[i] = temp[j++];
                    }
                    i++;
                }
            }

            if(curr.value == '^')
            {
                int i = 0;
//Shift over predictions to make room for the new one
                dict[4] = dict[3];
                dict[3] = dict[2];
                dict[2] = dict[1];
                dict[1] = dict[0];

                dict[0] = pred.toString();      //add the word to the string
                break;
            }
            pred.append(curr.value);    //add this letter to the prediction

            curr = curr.child;          //get the child
        }

        return dict;

        /*First consult the user's history to see if this prefix has been used before*/
            /*We need to make a trie that contains user results*/
                /*and their frequency*/
        /*Suggest 5 words from dictionary.txt that have the current partial*/
            /*If 5 do not exist, enter as many as possible*/
    }
}
