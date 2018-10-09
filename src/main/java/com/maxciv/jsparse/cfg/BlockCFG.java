package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

import java.util.Collection;
import java.util.HashSet;

public interface BlockCFG {

    void addParent(BlockCFG block);
    void addParents(Collection<BlockCFG> block);
    void setParents(Collection<BlockCFG> block);
    void setChild(BlockCFG block);

    void setThenChild(BlockCFG block);
    void setElseChild(BlockCFG block);
    void setCondition(Tree condition);

    Tree getExpression();
    BlockCFG getChild();
    BlockCFG getThenChild();
    BlockCFG getElseChild();
    String getStringRepresentation();
    HashSet<String> getDependentVariables();
    HashSet<String> getDependencies();
    HashSet<BlockCFG> getDependentBlocks();

    boolean addToDependentBlocks(BlockCFG blockCFG);
}
