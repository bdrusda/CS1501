class Node
{
    Node sibling;
    Node child;
    char value;

    public Node()
    {

    }

    public Node(char value)
    {
        this.sibling = null;
        this.child = null;
        this.value = value;
    }

    public Node(Node sibling, Node child, char value)
    {
        this.sibling = sibling;
        this.child = child;
        this.value = value;
    }
}
