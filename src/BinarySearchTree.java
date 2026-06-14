public class BinarySearchTree<T extends Comparable<T>> implements SortedCollection<T> {

  protected BinaryNode<T> root = null;
  
  public void insert(T data) throws NullPointerException {
    if (data == null) throw new NullPointerException("data cannot be null");
    BinaryNode<T> newNode = new BinaryNode<>(data);
    this.insertHelper(newNode, this.root);
    if (this.root == null) {
      this.root = newNode;
    }
  }

  /**
   * Performs the naive binary search tree insert algorithm to recursively
   * insert the provided newNode (which has already been initialized with a
   * data value) into the provided tree/subtree. When the provided subtree
   * is null, this method does nothing. 
   */
  protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree) {
    if (subtree == null) return;
    if (subtree.getData().compareTo(newNode.getData()) < 0) {
      // newNode contains data that is larger than the current node
      // go right
      if (subtree.getRight() == null) {
        subtree.setRight(newNode);
        newNode.setUp(subtree);
      } else {
        this.insertHelper(newNode, subtree.getRight());
      }
    } else {
      // newNode contains data that is either equal to or smaller than the current node
      // go left
      if (subtree.getLeft() == null) {
        subtree.setLeft(newNode);
        newNode.setUp(subtree);
      } else {
        this.insertHelper(newNode, subtree.getLeft());
      }
    }
  }

  public boolean contains(Comparable<T> data) throws NullPointerException {
    BinaryNode<T> current = this.root;
    while (current != null) {
      int comp = data.compareTo(current.getData());
      if (comp < 0) {
        // data is smaller than current, go left
        current = current.getLeft();
      } else if (comp > 0) {
        // data is larger than current, go right
        current = current.getRight();
      } else {
        return true;
      }
    }
    return false;
  }

  public int size() {
    return sizeHelper(this.root);
  }

  protected int sizeHelper(BinaryNode<T> current) {
    if (current == null) return 0;
    return sizeHelper(current.getLeft()) + sizeHelper(current.getRight()) + 1;
  }

  public boolean isEmpty() {
    return this.root == null;
  }

  public void clear() {
    this.root = null;
  }

}
