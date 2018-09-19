package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractBlockCFG implements BlockCFG {

    private List<BlockCFG> parentBlocks = new ArrayList<>();

    @Override
    public void addParent(BlockCFG block) {
        parentBlocks.add(block);
    }

    @Override
    public void addParents(Collection<BlockCFG> block) {
        parentBlocks.addAll(block);
    }

    @Override
    public void setChild(BlockCFG block) {
    }

    @Override
    public void setThenChild(BlockCFG block) {
    }

    @Override
    public void setElseChild(BlockCFG block) {
    }

    @Override
    public void setCondition(Tree condition) {
    }
}
