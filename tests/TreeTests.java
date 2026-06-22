import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TreeTests {

    /**
     * Tests a right rotation on the root node when the tree has no additional children
     * Covers a parent-child pair with 0 shared children
     */
    @Test
    public void bstRootRightRotationNoSharedChildren() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);

        // Perform right rotation on the root and left child
        tree.rotate(tree.root.getLeft(), tree.root);

        Assertions.assertEquals("[ 10, 20 ]", tree.root.toLevelOrderString());
        Assertions.assertEquals(10, tree.root.getData());
        Assertions.assertEquals(20, tree.root.getRight().getData());
    }

    /**
     * Tests left rotation
     * The parent has a left subtree and the child has one right child
     */
    @Test
    public void bstNonRootLeftRotationTwoSharedChildren() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);

        // Rotate the root's right child into the root position
        tree.rotate(tree.root.getRight(), tree.root);

        Assertions.assertEquals("[ 70, 50, 80, 30, 60 ]", tree.root.toLevelOrderString());
        Assertions.assertEquals(70, tree.root.getData());
        Assertions.assertEquals(80, tree.root.getRight().getData());
        Assertions.assertEquals(60, tree.root.getLeft().getRight().getData());
    }

    /**
     * Tests a right rotation
     * The rotated subtree contains both parent and child children + the parent sibling
     */
    @Test
    public void bstNonRootRightRotationThreeSharedChildren() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(100);
        tree.insert(50);
        tree.insert(150);
        tree.insert(25);
        tree.insert(40);
        tree.insert(120);
        tree.insert(175);

        // Rotate a non-root left subtree around the parent-child pair
        tree.rotate(tree.root.getLeft().getLeft(), tree.root.getLeft());

        Assertions.assertEquals("[ 100, 25, 150, 50, 120, 175, 40 ]", tree.root.toLevelOrderString());
        Assertions.assertEquals(25, tree.root.getLeft().getData());
        Assertions.assertEquals(50, tree.root.getLeft().getRight().getData());
        Assertions.assertEquals(40, tree.root.getLeft().getRight().getLeft().getData());
    }

    /**
     * Tests red black insertion
     */
    @Test
    public void rbtInsertLeftLeftRotationExample() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();

        // Insert values that create a left-left red violation
        tree.insert(10);
        tree.insert(5);
        tree.insert(1);

        // After repair the root should be black and the structure should be balanced
        Assertions.assertEquals(10, tree.root.getData());
        Assertions.assertTrue(((RedBlackNode<Integer>) tree.root).isBlackNode());
        Assertions.assertEquals(5, tree.root.getLeft().getData());
        Assertions.assertEquals(1, tree.root.getLeft().getLeft().getData());
        Assertions.assertFalse(((RedBlackNode<Integer>) tree.root.getLeft()).isBlackNode());
    }

    /**
     * Tests red black insertion where the uncle is red and recoloring is required
     * This case verifies that ensureRedProperty performs the recolor repair rule
     */
    @Test
    public void rbtInsertRecolorCase() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();

        // Build an initial tree where the parent and uncle are red after insertion
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(5);
        tree.insert(15);

        // The parent and uncle should have been recolored, leaving the root black
        Assertions.assertTrue(((RedBlackNode<Integer>) tree.root).isBlackNode());
        Assertions.assertFalse(((RedBlackNode<Integer>) tree.root.getLeft()).isBlackNode());
        Assertions.assertFalse(((RedBlackNode<Integer>) tree.root.getRight()).isBlackNode());
        Assertions.assertEquals("[ 20.b, 10.r, 30.r, 5.r, 15.r ]", tree.root.toLevelOrderString());
    }

    /**
     * Tests a Red-Black Tree insertion example that exercises a rotation followed by recoloring
     */
    @Test
    public void rbtInsertRotationAndRecolorExample() {
        RedBlackTree<Integer> tree = new RedBlackTree<>();

        // Insert values that cause a red parent violation and require the repair algorithm
        tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(10);
        tree.insert(25);
        tree.insert(22);

        // Ensure the tree root is black and the inserted node is placed correctly
        Assertions.assertTrue(((RedBlackNode<Integer>) tree.root).isBlackNode());
        Assertions.assertEquals(30, tree.root.getData());
        Assertions.assertEquals(22, tree.root.getLeft().getRight().getLeft().getData());
        Assertions.assertFalse(((RedBlackNode<Integer>) tree.root.getLeft().getRight()).isBlackNode());
        Assertions.assertEquals("[ 30.b, 20.r, 40.r, 10.b, 25.b, 22.r ]", tree.root.toLevelOrderString());
    }
}
