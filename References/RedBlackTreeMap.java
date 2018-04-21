import java.util.*;

// A Map ADT structure using a red-black tree, where keys must implement
// Comparable.
public class RedBlackTreeMap<TKey extends Comparable<TKey>, TValue> {


    static Scanner pause = new Scanner(System.in);
    // A Node class.
    private class Node {
        private TKey mKey;
        private TValue mValue;
        private Node mParent;
        private Node mLeft;
        private Node mRight;
        private boolean mIsRed;

        public Node(TKey key, TValue data, boolean isRed) {
            mKey = key;
            mValue = data;
            mIsRed = isRed;

            mLeft = NIL_NODE;
            mRight = NIL_NODE;
        }

        @Override
        public String toString() {
            return "(" + mKey + ", " + mValue + ")";
        }
    }

    private Node mRoot;
    private int mCount;



    // Rather than create a "blank" black Node for each NIL, we use one shared
    // node for all NIL leaves.
    private final Node NIL_NODE = new Node(null, null, false);

    //////////////////// I give you these utility functions for free.

    // Get the # of keys in the tree.
    public int getCount() {
        return mCount;
    }

    // Finds the value associated with the given key.
    public TValue find(TKey key) {
        Node n = bstFind(key, mRoot); // find the Node containing the key if any
        if (n == null || n == NIL_NODE)
            throw new RuntimeException("Key not found");
        return n.mValue;
    }


    /////////////////// You must finish the rest of these methods.

    // Inserts a key/value pair into the tree, updating the red/black balance
    // of nodes as necessary. Starts with a normal BST insert, then adjusts.
    public void insert(TKey key, TValue data) {
        Node n = new Node(key, data, true); // nodes start red

        // normal BST insert; n will be placed into its initial position.
        // returns false if an existing node was updated (no rebalancing needed)
        boolean insertedNew = bstInsert(n, mRoot); 
        if (!insertedNew)
            return;


        // check cases 1-5 for balance violations.
        checkBalance(n);

    }

    // Applies rules 1-5 to check the balance of a tree with newly inserted
    // node n.  
    private void checkBalance(Node n) {
        printStructure();
        if (n == mRoot) {
            // case 1: new node is root.            
            n.mIsRed = false;
        } else if (n.mParent.mIsRed){
            // case 3: parent and uncle root is red
            if (getUncle(n) != null && getUncle(n).mIsRed){
                if (getUncle(n).mIsRed){
                    n.mParent.mIsRed = false;
                    getUncle(n).mIsRed = false;
                    if (getGrandparent(n) != mRoot) getGrandparent(n).mIsRed = true;
                    checkBalance(getGrandparent(n));
                }
            }
            // case 4 & 5
            if (n.mParent.mIsRed){
                if (getGrandparent(n).mLeft == n.mParent){
                    if (n.mParent.mRight == n){
                        //LR
                        singleRotateLeft(n.mParent);
                        singleRotateRight(n.mParent);
                        n.mIsRed = false;
                        n.mRight.mIsRed = true;
                    } else if (n.mParent.mLeft == n){
                        //LL
                        singleRotateRight(getGrandparent(n));                          
                        n.mParent.mIsRed = false;
                        n.mParent.mRight.mIsRed = true;                        
                    }
                } else if (getGrandparent(n).mRight == n.mParent){
                    if (n.mParent.mLeft == n){
                        //RL
                        singleRotateRight(n.mParent);
                        singleRotateLeft(n.mParent);
                        n.mIsRed = false;
                        n.mLeft.mIsRed = true;
                    } else if (n.mParent.mRight == n){
                        //RR
                        singleRotateLeft(getGrandparent(n));                        
                        n.mParent.mIsRed = false;
                        n.mParent.mLeft.mIsRed = true;
                    }
                }
            }
        }
        printStructure();
    }

    // Returns true if the given key is in the tree.
    public boolean containsKey(TKey key) {
        if (bstFind(key, mRoot) != null) return true;
        return false;
    }

    // Prints a pre-order traversal of the tree's nodes, printing the key, value,
    // and color of each node.
    public void printStructure() {
        System.out.println("========Print Structure Begin========");
        System.out.println("Root: ");
        preorder(mRoot);
        System.out.println("========Print Structure ends========");
        System.out.println("");
    }
    public void preorder(Node n){
        if (n != null){
            if (n.mLeft == null && n.mRight == null){
                System.out.println("Leaf");
            } else {
                if (n.mIsRed) System.out.println(n.mKey + " : " + n.mValue + " ( Red )");
                else System.out.println(n.mKey + " : " + n.mValue + " ( Black )");
                System.out.print("Left: ");
                preorder(n.mLeft);
                System.out.print("Right: ");
                preorder(n.mRight);
            }
        }
    }

    // Retuns the Node containing the given key. Recursive.
    private Node bstFind(TKey key, Node currentNode) {
        int compare = currentNode.mKey.compareTo(key);
        if (compare < 0) {
            if (currentNode.mRight == NIL_NODE) return null;
            return bstFind(key, currentNode.mRight);
        } else if (compare > 0) {
            if (currentNode.mLeft == NIL_NODE) return null;
            return bstFind(key, currentNode.mLeft);
        } else if (compare == 0){
            return currentNode;
        }
        return null;
    }


    //////////////// These functions are needed for insertion cases.

    // Gets the grandparent of n.
    private Node getGrandparent(Node n) {
        return n.mParent.mParent;
    }

    // Gets the uncle (parent's sibling) of n.
    private Node getUncle(Node n) {
        Node g = getGrandparent(n);
        if (g.mLeft.equals(n.mParent)) return g.mRight;
        else if (g.mRight.equals(n.mParent)) return g.mLeft;
        
        return null;
    }


 // Rotate the tree right at the given node.
    private void singleRotateRight(Node n) {
        Node l = n.mLeft, lr = l.mRight, p = n.mParent;
        n.mLeft = lr;
        lr.mParent = n;
        l.mRight = n;
        if (n == mRoot) {
            mRoot = l;
            l.mParent = null;
        }
        else if (p.mLeft == n) {
            p.mLeft = l;
            l.mParent = p;
        }
        else {
            p.mRight = l;
            l.mParent = p;
        }
        n.mParent = l;
    }
    
    // Rotate the tree left at the given node.
    private void singleRotateLeft(Node n) {
        Node r = n.mRight, rl = r.mLeft, p = n.mParent;
        n.mRight = rl;
        rl.mParent = n;
        r.mLeft = n;
        if (n == mRoot) {
            mRoot = r;
            r.mParent = null;
        }
        else if (p.mRight == n) {
            p.mRight = r;
            r.mParent = p;
        } else {
            p.mLeft = r;
            r.mParent = p;
        }
        n.mParent = r;
    }
    
    // This method is used by insert. It is complete.
    // Inserts the key/value into the BST, and returns true if the key wasn't 
    // previously in the tree.
    private boolean bstInsert(Node newNode, Node currentNode) {
        if (mRoot == null) {
            // case 1
            mRoot = newNode;
            return true;
        }
        else{
            int compare = currentNode.mKey.compareTo(newNode.mKey);
            if (compare < 0) {
                // newNode is larger; go right.
                if (currentNode.mRight != NIL_NODE)
                    return bstInsert(newNode, currentNode.mRight);
                else {
                    currentNode.mRight = newNode;
                    newNode.mParent = currentNode;
                    mCount++;
                    return true;
                }
            }
            else if (compare > 0) {
                if (currentNode.mLeft != NIL_NODE)
                    return bstInsert(newNode, currentNode.mLeft);
                else {
                    currentNode.mLeft = newNode;
                    newNode.mParent = currentNode;
                    mCount++;
                    return true;
                }
            }
            else {
                // found a node with the given key; update value.
                currentNode.mValue = newNode.mValue;
                return false; // did NOT insert a new node.
            }
        }
    }
}
