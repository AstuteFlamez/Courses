import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;

/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it
 * stores in sorted, ascending order.
 */
public class IterableRedBlackTree<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {

    // stores the minimum value for iterators produced by this tree (null for no minimum)
    private Comparable<T> iteratorMin = null;
    // stores the maximum value for iterators produced by this tree (null for no maximum)
    private Comparable<T> iteratorMax = null;

    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called,
     * every iterator created after it will use the minimum set by this method until this method
     * is called again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    public void setIteratorMin(Comparable<T> min) {
        this.iteratorMin = min;
    }
    
    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called,
     * every iterator created after it will use the maximum set by this method until this method
     * is called again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    public void setIteratorMax(Comparable<T> max) {
        this.iteratorMax = max;
    }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the
     * start (minimum) value set by a previous call to setIteratorMin, and the stop (maximum)
     * value set by a previous call to setIteratorMax. If setIteratorMin has not been called
     * before, or if it was called with a null argument, the iterator uses no minimum value
     * and starts with the lowest value that exists in the tree. If setIteratorMax has not been
     * called before, or if it was called with a null argument, the iterator uses no maximum
     * value and finishes with the highest value that exists in the tree.
     */
    public Iterator<T> iterator() {
        return new TreeIterator<T>(this.root, this.iteratorMin, this.iteratorMax);
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class TreeIterator<R extends Comparable<R>> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryNode<R>> stack = null;

        /**
         * Constructor for a new iterator if the tree with root as its root node, and
         * min as the start (minimum) value (or null if no start value) and max as the
         * stop (maximum) value (or null if no stop value) of the new iterator.
         * Time complexity should be O(log n).
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public TreeIterator(BinaryNode<R> root, Comparable<R> min, Comparable<R> max) {
            // store bounds
            this.min = min;
            this.max = max;

            this.stack = new Stack<>();
            this.updateStack(root);
        }

        /**
         * Helper method for initializing and updating the stack. This method both
         * - finds the next data value stored in the tree (or subtree) that is between
         * start(minimum) and stop(maximum) point (including start and stop points
         * themselves), and
         * - builds up the stack of ancestor nodes that contain values between
         * start(minimum) and stop(maximum) values (including start and stop values
         * themselves) so that those nodes can be visited in the future.
         *
         * @param node the root node of the subtree to process
         */
        private void updateStack(BinaryNode<R> node) {
            // base case
            if (node == null) return;

            // if node's value is smaller than the minimum bound, skip left subtree and node
            if (this.min != null && this.min.compareTo(node.getData()) > 0) {
                // node.data < min -> all left subtree and node are < min
                updateStack(node.getRight());
                return;
            }

            // node.data >= min: push current node and continue searching left
            this.stack.push(node);
            updateStack(node.getLeft());
            return;
        }

        /**
         * Returns true if the iterator has another value to return, and false otherwise.
         */
        public boolean hasNext() {
            if (this.stack == null || this.stack.isEmpty()) return false;
            if (this.max == null) return true;
            // if the smallest candidate is greater than max, there is no next
            BinaryNode<R> nextNode = this.stack.peek();
            return this.max.compareTo(nextNode.getData()) >= 0;
        }

        /**
         * Returns the next value of the iterator.
         * Amortized time complexity should be O(1).
         * Worst case time complexity should be O(log n).
         * Do not implement this method by linearly walking through the
         * entire tree from the smallest element until the start bound is reached.
         * That process should occur only once during construction of the
         * iterator object.
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        public R next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // pop the next node
            BinaryNode<R> current = this.stack.pop();
            R result = current.getData();

            updateStack(current.getRight());

            // ensure returned value respects the max bound
            if (this.max != null && this.max.compareTo(result) < 0) {
                throw new NoSuchElementException();
            }

            return result;
        }
    }

}
