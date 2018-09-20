package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.FunctionDeclarationTree;

public class FunctionBlockCFG extends AbstractBlockCFG {

    private FunctionDeclarationTree node;
    private BlockCFG child;

    public FunctionBlockCFG(FunctionDeclarationTree node) {
        this.node = node;
    }

    @Override
    public void setChild(BlockCFG block) {
        child = block;
    }

    @Override
    public BlockCFG getChild() {
        return child;
    }
}
