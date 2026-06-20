public class RedBlackTree<T extends Comparable<T>> extends BinarySearchTree<T> {

    public RedBlackTree() {
        super();
    }

    @Override
    public void insert(T data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException("data cannot be null");
        }

        RedBlackNode<T> newNode = new RedBlackNode<>(data);
        this.insertHelper(newNode, this.root);

        if (this.root == null) {
            this.root = newNode;
        } else {
            this.ensureRedProperty(newNode);
        }

        ((RedBlackNode<T>) this.root).isBlackNode = true; // root must always be black
    }

    /**
     * Checks if a new red node in the RedBlackTree causes a red property violation
     * by having a red parent. If this is not the case, the method terminates without
     * making any changes to the tree. If a red property violation is detected, then
     * the method repairs this violation and any additional red property violations
     * that are generated as a result of the applied repair operation.
     * Using this method might cause nodes with a value equal to the value of one of
     * their ancestors to appear within the left and the right subtree of that ancestor,
     * even if the original insertion procedure consistently inserts such nodes into only
     * the left or the right subtree. But it will preserve the ordering of nodes within
     * the tree.
     * @param newNode a newly inserted red node, or a node turned red by previous repair
     */
    protected void ensureRedProperty(RedBlackNode<T> newNode) {
        if (newNode == null) {
            throw new IllegalArgumentException("newNode cannot be null");
        }

        RedBlackNode<T> parent = newNode.getUp();
        if (parent == null || parent.isBlackNode()) {
            return;
        }

        RedBlackNode<T> grandparent = parent.getUp();
        if (grandparent == null) {
            parent.isBlackNode = true;
            return;
        }

        RedBlackNode<T> uncle = this.getUncle(parent);
        if (uncle != null && !uncle.isBlackNode()) { // case 1: uncle is red
            parent.flipColor();
            uncle.flipColor();
            grandparent.flipColor();
            this.ensureRedProperty(grandparent);
            return;
        }

        if (parent == grandparent.getLeft()) { // case 2: uncle is black and parent is a left child
            if (newNode == parent.getRight()) {
                this.rotate(newNode, parent);
                parent = newNode;
            }
            this.rotate(parent, grandparent);
        } else { // case 3: uncle is black and parent is a right child
            if (newNode == parent.getLeft()) {
                this.rotate(newNode, parent);
                parent = newNode;
            }
            this.rotate(parent, grandparent);
        }

        parent.flipColor();
        grandparent.flipColor();
    }

    private RedBlackNode<T> getUncle(RedBlackNode<T> parent) {
        RedBlackNode<T> grandparent = parent.getUp();
        if (grandparent == null) {
            return null;
        }

        if (parent == grandparent.getLeft()) {
            return grandparent.getRight();
        }
        return grandparent.getLeft();
    }

}
