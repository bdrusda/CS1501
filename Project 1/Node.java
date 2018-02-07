class Node
{
    Node sibling;
    Node child;
    char value;

    public Node()                                       //Constructor with nothing
    {

    }

    public Node(char value)                             //Constructor with a character
    {
        this.sibling = null;
        this.child = null;
        this.value = value;
    }

    public Node(Node sibling, Node child, char value)   //Constructor with a sibling, child, and character
    {
        this.sibling = sibling;
        this.child = child;
        this.value = value;
    }
}
