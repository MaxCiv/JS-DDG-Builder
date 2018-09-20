package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

import java.util.Collection;

public interface BlockCFG {

    void addParent(BlockCFG block);
    void addParents(Collection<BlockCFG> block);
    void setParents(Collection<BlockCFG> block);
    void setChild(BlockCFG block);

    void setThenChild(BlockCFG block);
    void setElseChild(BlockCFG block);
    void setCondition(Tree condition);

    BlockCFG getChild();
}
