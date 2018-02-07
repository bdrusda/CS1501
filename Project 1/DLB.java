public class DLB
{
    Node root;                          //The first node - contains no data but has a single child

    public DLB()                        //Constructor for the DLB trie
    {
        root = new Node();                  //Create the root node
    }

    public void add_word(String word)   //Add a word to the trie
    {
        if(check_word(word))                //If the word is already added, don't add it again
            return;
        add_word(root, word);               //Start at root node
    }

    private void add_word(Node curr, String word)   //Helper
    {
        for(int i = 0; i < word.length(); i++)          //Add the word letter by letter
        {
            curr = add_char(curr, word.charAt(i));          //Add character(checking if it is already there)
        }

        Node term = new Node('^');                      //Create a node containing the terminator character
        if(curr.child == null)                          //If the word is not a prefix for another word
            curr.child = term;                              //Terminate the word
        else                                            //If it is a prefix
        {
            curr = curr.child;                              //Access the child
            while(curr.sibling != null)                     //Iterate through until there is no sibling left
            {
                curr = curr.sibling;
            }

            curr.sibling = term;                            //Terminate the word in the sibling location
        }
    }

    private Node add_char(Node node, char curr)    //Add an individual character and return its position
    {
        //If this is a new level in the tree, create a node, set it to the child, and return it
        if(node.child == null)
        {
            Node insert = new Node(curr);                   //Create a Node with the given char value
            node.child = insert;                            //Make this node the child
            return insert;                                  //Return it
        }
        //If this is an existing level, check each node to see if the value is present
        node = node.child;                              //This node exists and has a value
        if(node.value == curr)                          //If this node contains the value to be inserted
            return node;                                    //Return the node containing the character
        else                                            //If this node doesn't contain the value, check its siblings
        {
            while(node.sibling != null)                     //Traverse the sibling nodes to see if it is present
            {
                node = node.sibling;                            //Set node to the sibling node
                if(node.value == curr)                          //If the sibling contains the character
                    return node;                                    //Return the node containing the character
            }
        }
        //If the value has not been found, we are now at the rightmost node with no siblings
        Node insert = new Node(curr);   //Create a Node with the given char value
        node.sibling = insert;          //Make this node the sibling
        return insert;                  //Return it
    }

    public boolean check_word(String word)
    {
        Node curr = root;
        for(int i = 0; i < word.length(); i++)
        {
            if((curr = check_char(curr, word.charAt(i))) == null)
                return false;
        }
        //At this point we have traversed the entire word -- now we verify that there is a null terminator following it
        if(curr.child == null)              //If the node doesn't have a child, is can't be a full word complete with a null terminator
            return false;                       //So return false
        else                                //It may be a full word, but it might also just be a prefix
        {
            curr = curr.child;              //Check the child
            if(curr.value == '^')           //If the child is a null terminator
                return true;                    //Return that the word is there
            else                            //If the child isn't a null terminator
            {
                while(curr.sibling != null)     //Check all possible siblings for the null terminator
                {
                    curr = curr.sibling;
                    if(curr.value == '^')
                        return true;
                }
            }
        }
        return false;                       //If the null terminator wasn't found anywhere, it was just a prefix, return false
    }

    public Node check_char(Node node, char curr)
    {
        if(node.child != null)      //If the node has a child
        {
            node = node.child;          //Check the child
            if(node.value == curr)      //If this node matches
                return node;                //Return it
            else                        //If the node didn't match
            {
                while(node.sibling != null) //Check each of its siblings
                {
                    node = node.sibling;        //Check the sibling
                    if(node.value == curr)      //If the sibling matches
                        return node;                //Return that node
                }
                return null;               //If none of the siblings match, return null
            }
        }
        else                        //If the node doesn't have a child, the remainder of the word doesn't exist
            return null;                //So return null
    }

    public String[] get_predictions(String partial) //Get predictions from trie based on a partial word
    {
        Node curr = root;
        for(int i = 0; i < partial.length(); i++)       //Traverse through the DLB to the end of the partial word
        {
            curr = check_char(curr, partial.charAt(i));     //Ensure that each of the characters in the prefix are in the trie
            if(curr == null)                                //If the prefix isn't in the DLB
                return null;                                    //Return null
        }

        return get_predictions(partial, curr.child);    //Return the result of the helper method
    }

    public String[] get_predictions(String partial, Node curr)  //Helper
    {
        /*we have the end of the partial word if it exists*/
        String[] dict = new String[5];
        String[] temp = new String[5];
        if(curr == null)                    //If a null node was passed
        {
            //This means that the there are no suitable predictions
            return dict;                        //So just return what we have
        }

        StringBuilder pred = new StringBuilder(partial);    //Initialize a stringbuilder comprised of the partial word

        while(curr != null)                 //While we have not reached the end of a word
        {
            if(curr.sibling != null)            //If The node has a sibling
            {
                temp = get_predictions(pred.toString(), curr.sibling);  //Get the full list of predictions from the sibling (and it's siblings)
                int i = 0;
                int j = 0;
                while(i < 5 && temp[j] != null) //Add as many words to dict from temp as we can
                {
                    if(dict[i] == null)             //If there isn't an entry at this spot in the dictionary
                        dict[i] = temp[j++];            //Add this word from temp
                    i++;
                }
            }

            if(curr.value == '^')               //If the resulting prefix has a null terminator
            {
                int i = 0;
                //Shift over predictions to make room for the new one
                dict[4] = dict[3];
                dict[3] = dict[2];
                dict[2] = dict[1];
                dict[1] = dict[0];

                dict[0] = pred.toString();          //Add the word to the string
                break;                              //Leave the loop and return
            }
            pred.append(curr.value);            //Add this letter to the prediction

            curr = curr.child;                  //Get the child
        }

        return dict;                        //Return the array of predictions
    }
}
