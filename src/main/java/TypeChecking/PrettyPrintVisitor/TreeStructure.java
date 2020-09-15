package TypeChecking.PrettyPrintVisitor;

import core.syntaxtree.Node;
import core.syntaxtree.NodeToken;
import core.util.LOGGER;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Build a tree structure by adding nodes. This class is mostly meant to be used by the depth first
 * core.visitor class such that when the visit method is called with a node, the addNode method adds the
 * node to the tree structure.
 */
public class TreeStructure {

  // for logging
  private static final LOGGER log = new LOGGER(TreeStructure.class.getSimpleName(), true);

  // stores a pointer to the current node at given depth
  private HashMap<Integer, TreeNode> nodeDepthPointer = new HashMap<>();
  private TreeNode tree = null;
  private int depth = -1;

  public TreeStructure() {}

  /**
   * Add a node to the tree structure
   * @param s
   */
  public void addNode(Node s) {
//    log.info(Integer.toString(Thread.currentThread().getStackTrace()[2].getLineNumber()));
    if (depth == 0) { // this is the root node
      tree = new TreeNode(s, null);
      nodeDepthPointer.put(depth, tree);

    } else {
      TreeNode nodeAtPreviousDepth = nodeDepthPointer.get(depth - 1);
      TreeNode node = new TreeNode(s, nodeAtPreviousDepth);
      nodeAtPreviousDepth.addChildren(node);
      nodeDepthPointer.put(depth, node);

    }
  }

  /**
   * Get the root node of the tree
   * @return
   */
  public TreeNode getRoot() {
    return tree;
  }


  /**
   * Print the tree structure on the terminal
   */
  public void print() {
    System.out.println(this.toString());
  }

  /**
   * Increase the depth at which to add the next node
   */
  public void incrementDepth() {
    depth++;
  }

  /**
   * Decrease the depth at which to add the node.
   */
  public void decrementDepth() {
    depth--;
  }


  /**
   * Get the @toString representation representation of the tree structure rooted at tree node
   * @return
   */
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    print(getRoot(), buffer, "", "");
    return buffer.toString();
  }

  /**
   * private method to recursively iterate through all the nodes in the tree structure and
   * generate a string out of that in StringBuilder.
   * @param t
   * @param buffer
   * @param prefix
   * @param childrenPrefix
   */
  private void print(TreeNode t, StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append(t.getName() + ((t.getNode() instanceof NodeToken)?" →  \""+((NodeToken)t.getNode()).tokenImage+'"':""));
    buffer.append('\n');
    // iterate over the children of @t
    for (Iterator<TreeNode> iterator = t.getChildren().iterator(); iterator.hasNext();) {
      TreeNode next = iterator.next(); // get the child of t
      if (iterator.hasNext()) { // if child is not leaf node
        print(next, buffer, childrenPrefix + "├─ ", childrenPrefix + "│   ");
      } else { // if child is leaf node
        print(next, buffer, childrenPrefix + "└─ ", childrenPrefix + "    ");
      }
    }
  }

}