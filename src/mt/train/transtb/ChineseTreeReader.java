package mt.train.transtb;

import mt.classifyde.*;

import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.international.pennchinese.*;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.*;

public class ChineseTreeReader extends AbstractTreeReader {
  private ChineseEscaper ce_;

  public ChineseTreeReader() {
    trees_ = new ArrayList<Tree>();
    tlpp_ = new ChineseTreebankParserParams();
    treeprint_ = new TreePrint("words,penn,typedDependencies", "removeTopBracket,basicDependencies", tlpp_.treebankLanguagePack());
    tt_ = new ChineseTreeTransformer();
    ce_ = new ChineseEscaper();
  }

  public ChineseTreeReader(String filename) throws IOException {
    this();
    readMoreTrees(filename);
  }
  
  public List<Tree> getTreesWithWords(String[] sent) {
    return getTreesWithWords(sent, false);
  }

  public List<Tree> getTreesWithWords(String[] sent, Boolean DEBUG) {
    String sentStr = StringUtils.join(sent, "");
    List<Tree> trees = new ArrayList<Tree>();
    for(Tree t : trees_) {
      StringBuilder sb = new StringBuilder();
      Sentence<HasWord> hws = getWords(t);
      //for (HasWord hw : hws) {
      for(int i = 0; i < hws.size(); i++) {
        HasWord hw = hws.get(i);
        sb.append(hw.word());
      }
      // the tree should already be normalized
      String treeStr = sb.toString();
      if (DEBUG) {
        System.err.println("sentStr="+sentStr);
        System.err.println("treeStr="+treeStr);
      }
      if (sentStr.equals(treeStr)) {
        trees.add(t);
      }
    }
    return trees;
  }


  
  public static void main(String args[]) throws IOException {
    
    ChineseTreeReader ctr = new ChineseTreeReader();
    for(int i = 1; i <= 325; i++) {
      String name = 
        String.format("/afs/ir/data/linguistic-data/Chinese-Treebank/6/data/utf8/bracketed/chtb_%04d.fid", i);
      System.err.println(name);
      ctr.readMoreTrees(name);
      System.err.println("number of trees="+ctr.size());
    }

    ctr.printAllTrees();
  }
}

class ChineseTreeTransformer implements TreeTransformer {
  ChineseEscaper ce;
  public ChineseTreeTransformer() {
    ce = new ChineseEscaper();
  }

  public Tree transformTree(Tree tree) {
    tree = tree.deepCopy();
    
    List<Tree> leaves = tree.getLeaves();

    List<HasWord> words = new ArrayList<HasWord>();
    for (Tree leaf : leaves) {
      words.add(new Word(leaf.value()));
    }
    words = ce.apply(words);

    for (int i = 0; i < leaves.size(); i++) {
      leaves.get(i).setValue(words.get(i).word());
    }

    return tree;
  }
}
