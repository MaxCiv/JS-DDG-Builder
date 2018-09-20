package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

public class ActionBlockCFG extends AbstractBlockCFG {

    private Tree expression;
    private BlockCFG child;

    public ActionBlockCFG(Tree expression) {
        this.expression = expression;
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
