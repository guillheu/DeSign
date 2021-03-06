package util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.ArrayUtils;

import kotlin.Pair;

/**
 * MerkleTree is an implementation of a Merkle binary hash tree where the leaves
 * are signatures (hashes, digests, CRCs, etc.) of some underlying data structure
 * that is not explicitly part of the tree.
 * 
 * The internal leaves of the tree are signatures of its two child nodes. If an
 * internal node has only one child, the the signature of the child node is
 * adopted ("promoted").
 * 
 * MerkleTree knows how to serialize itself to a binary format, but does not
 * implement the Java Serializer interface.  The {@link #serialize()} method
 * returns a byte array, which should be passed to 
 * {@link MerkleDeserializer#deserialize(byte[])} in order to hydrate into
 * a MerkleTree in memory.
 * 
 * This MerkleTree is intentionally ignorant of the hashing/checksum algorithm
 * used to generate the leaf signatures. It uses Adler32 CRC to generate
 * signatures for all internal node signatures (other than those "promoted"
 * that have only one child).
 * 
 * The Adler32 CRC is not cryptographically secure, so this implementation
 * should NOT be used in scenarios where the data is being received from
 * an untrusted source.
 */
public class MerkleTree {

  public static final int MAGIC_HDR = 0xcdaace99;
  public static final int INT_BYTES = 4;
  public static final int LONG_BYTES = 8;
  public static final byte LEAF_SIG_TYPE = 0x0;
  public static final byte INTERNAL_SIG_TYPE = 0x01;
  public static final byte ROOT_SIG_TYPE = 0x02;
  
	
  private List<String> leafSigs;
  private List<Node> leafNodes = new ArrayList<Node>();
  private Node root;
  private int depth;
  private int nnodes;
  
  private MessageDigest hashAlgo;
  
  /**
   * Use this constructor to create a MerkleTree from a list of leaf signatures.
   * The Merkle tree is built from the bottom up.
   * @param leafSignatures
   */
  public MerkleTree(List<String> leafSignatures, MessageDigest hashAlgo) {
	this.hashAlgo = hashAlgo;
	
    constructTree(leafSignatures);
  }
  


/**
   * Use this constructor when you have already constructed the tree of Nodes 
   * (from deserialization).
   * @param treeRoot
   * @param numNodes
   * @param height
   * @param leafSignatures
   */
  public MerkleTree(Node treeRoot, int numNodes, int height, List<String> leafSignatures) {
    root = treeRoot;
    nnodes = numNodes;
    depth = height;
    leafSigs = leafSignatures;
  }
  
  
  /**
   * Serialization format:
   * (magicheader:int)(numnodes:int)[(nodetype:byte)(siglength:int)(signature:[]byte)]
   * @return
   */
  public byte[] serialize() {
    int magicHeaderSz = INT_BYTES;
    int nnodesSz = INT_BYTES;
    int hdrSz = magicHeaderSz + nnodesSz;

    int typeByteSz = 1;
    int siglength = INT_BYTES;
    
    int parentSigSz = LONG_BYTES;
    int leafSigSz = leafSigs.get(0).getBytes(StandardCharsets.UTF_8).length;

    // some of the internal nodes may use leaf signatures (when "promoted")
    // so ensure that the ByteBuffer overestimates how much space is needed
    // since ByteBuffer does not expand on demand
    int maxSigSz = leafSigSz;
    if (parentSigSz > maxSigSz) {
      maxSigSz = parentSigSz;
    }
        
    int spaceForNodes = (typeByteSz + siglength + maxSigSz) * nnodes; 
    
    int cap = hdrSz + spaceForNodes;
    ByteBuffer buf = ByteBuffer.allocate(cap);
    
    buf.putInt(MAGIC_HDR).putInt(nnodes);  // header
    serializeBreadthFirst(buf);

    // the ByteBuf allocated space is likely more than was needed
    // so copy to a byte array of the exact size necesssary
    byte[] serializedTree = new byte[buf.position()];
    buf.rewind();
    buf.get(serializedTree);
    return serializedTree;
  }
  

  /**
   * Serialization format after the header section:
   * [(nodetype:byte)(siglength:int)(signature:[]byte)]
   * @param buf
   */
  void serializeBreadthFirst(ByteBuffer buf) {
    Queue<Node> q = new ArrayDeque<Node>((nnodes / 2) + 1);
    q.add(root);
    
    while (!q.isEmpty()) {
      Node nd = q.remove();
      buf.put(nd.type).putInt(nd.sig.length).put(nd.sig);
      
      if (nd.left != null) {
        q.add(nd.left);
      }
      if (nd.right != null) {
        q.add(nd.right);
      }
    }
  }

  /**
   * Create a tree from the bottom up starting from the leaf signatures.
   * @param signatures
   */
  void constructTree(List<String> signatures) {
    if (signatures.size() <= 1) {
      throw new IllegalArgumentException("Must be at least two signatures to construct a Merkle tree");
    }
    
    leafSigs = signatures;
    nnodes = signatures.size();
    List<Node> parents = bottomLevel(signatures);
    nnodes += parents.size();
    depth = 1;
    
    while (parents.size() > 1) {
      parents = internalLevel(parents);
      depth++;
      nnodes += parents.size();
    }
    
    root = parents.get(0);
    root.type = ROOT_SIG_TYPE;
  }

  
  public int getNumNodes() {
    return nnodes;
  }
  
  public Node getRoot() {
    return root;
  }
  
  public int getHeight() {
    return depth;
  }
  

  /**
   * Constructs an internal level of the tree
   */
  List<Node> internalLevel(List<Node> children) {
    List<Node> parents = new ArrayList<Node>(children.size() / 2);
    
    for (int i = 0; i < children.size() - 1; i += 2) {
      Node child1 = children.get(i);
      Node child2 = children.get(i+1);
      
      Node parent = constructInternalNode(child1, child2);
      parents.add(parent);
    }
    
    if (children.size() % 2 != 0) {
      Node child = children.get(children.size()-1);
      Node parent = constructInternalNode(child, null);
      parents.add(parent);
    }
    
    return parents;
  }

  
  /**
   * Constructs the bottom part of the tree - the leaf nodes and their
   * immediate parents.  Returns a list of the parent nodes.
   */
  List<Node> bottomLevel(List<String> signatures) {
    List<Node> parents = new ArrayList<Node>(signatures.size() / 2);
    
    for (int i = 0; i < signatures.size() - 1; i += 2) {
      Node leaf1 = constructLeafNode(signatures.get(i));
      Node leaf2 = constructLeafNode(signatures.get(i+1));
      
      Node parent = constructInternalNode(leaf1, leaf2);
      leafNodes.add(leaf1);
      leafNodes.add(leaf2);
      parents.add(parent);
    }
    
    // if odd number of leafs, handle last entry
    if (signatures.size() % 2 != 0) {
      Node leaf = constructLeafNode(signatures.get(signatures.size() - 1));      
      Node parent = constructInternalNode(leaf, null);
      leafNodes.add(leaf);
      parents.add(parent);
    }
    
    return parents;
  }

  private Node constructInternalNode(Node child1, Node child2) {
    Node parent = new Node();
    parent.type = INTERNAL_SIG_TYPE;
    
    if (child2 == null) {
      parent.sig = child1.sig;
      child1.parent = parent;
    } else {
      parent.sig = internalHash(child1.sig, child2.sig);
      child1.parent = parent;
      child2.parent = parent;
    }
    
    parent.left = child1;
    parent.right = child2;
    return parent;
  }

  private static Node constructLeafNode(String signature) {
    Node leaf = new Node();
    leaf.type = LEAF_SIG_TYPE;
    leaf.sig = BytesUtils.hexStringToByteArray(signature);
    return leaf;
  }
  
  byte[] internalHash(byte[] leftChildSig, byte[] rightChildSig) {
    return hashAlgo.digest(ArrayUtils.addAll(leftChildSig, rightChildSig));
  }

  
  /* ---[ Node class ]--- */
  
  /**
   * The Node class should be treated as immutable, though immutable
   * is not enforced in the current design.
   * 
   * A Node knows whether it is an internal or leaf node and its signature.
   * 
   * Internal Nodes will have at least one child (always on the left).
   * Leaf Nodes will have no children (left = right = null).
   */
  public static class Node {
    public byte type;  // INTERNAL_SIG_TYPE or LEAF_SIG_TYPE or ROOT_SIG_TYPE
    public byte[] sig; // signature of the node
    public Node left;
    public Node right;
    public Node parent;
    

  }


public List<Pair<String, String>> getMerklePath(String leafSig) throws Exception {
	if(leafNodes.size() == 0)
		return null;
	List<Pair<String, String>> r = new ArrayList<Pair<String,String>>();
	Node current;
	Node parent;
	Node brother;
	String fromSide;
	int index = leafSigs.indexOf(leafSig);
	current = leafNodes.get(index);
	while(current.type != ROOT_SIG_TYPE) {
		parent = current.parent;
		String parentLeftSig = BytesUtils.bytesToHexString(parent.left.sig);
		String parentRightSig = BytesUtils.bytesToHexString(parent.right.sig);
		String currentSig = BytesUtils.bytesToHexString(current.sig);
		if(parentLeftSig.equals(currentSig)) {
			brother = parent.right;
			fromSide = "LEFT";
		}
		else if(parentRightSig.equals(currentSig)) {
			brother = parent.left;
			fromSide = "RIGHT";
		}
		else {
			throw new Exception("Unknown error ; current sig not referenced by it's parent");
		}
		r.add(new Pair<String, String>(fromSide, BytesUtils.bytesToHexString(brother.sig)));
		current = parent;
	}
	return r;
}  



}