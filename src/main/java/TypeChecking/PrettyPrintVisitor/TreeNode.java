package TypeChecking.PrettyPrintVisitor;

import core.syntaxtree.Node;

import java.util.ArrayList;

/**
 * This class contains the tree nodes associated with a tree structure.
 */
public class TreeNode {
  private TreeNode parent;
  private Node node;
  private ArrayList<TreeNode> children;

  public TreeNode(Node node, TreeNode parent) {
    this.node = node;
    this.parent = parent;
    this.children = new ArrayList<>();
  }

  /**
   * Get the node object of the current tree node
   * @return
   */
  public Node getNode() {
    return node;
  }

  /**
   * get the parent of the current tree node
   * @return
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * Get the string name of the current tree node
   * @return
   */
  public String getName() {
    return node.getClass().getSimpleName();
  }

  /**
   * Add a child to the current tree node
   * @param node
   */
  public void addChildren(TreeNode node) {
    children.add(node);
  }

  /**
   *
   * @return
   */
  public ArrayList<TreeNode> getChildren() {
    return children;
  }


}