package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.FunctionDeclarationTree;
import jdk.nashorn.api.tree.Tree;

public class FunctionBlockCFG extends AbstractBlockCFG {

    private FunctionDeclarationTree node;
    private BlockCFG child;

    public FunctionBlockCFG(FunctionDeclarationTree node) {
        this.node = node;
        fillVariables(node);
        stringRepresentation = getStringFromKind(node.getKind(), node);
    }

    @Override
    public void setChild(BlockCFG block) {
        child = block;
    }

    @Override
    public Tree getExpression() {
        return node;
    }

    @Override
    public BlockCFG getChild() {
        return child;
    }
}
