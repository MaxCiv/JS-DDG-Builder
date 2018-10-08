package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

public class ActionBlockCFG extends AbstractBlockCFG {

    private Tree expression;
    private BlockCFG child;

    public ActionBlockCFG(Tree expression) {
        this.expression = expression;
        if (expression != null) {
            fillVariables(expression);
            stringRepresentation = getStringFromKind(expression.getKind(), expression);
        } else {
            stringRepresentation = "[empty block]";
        }
    }

    @Override
    public void setChild(BlockCFG block) {
        if (expression != null && expression.getKind() == Tree.Kind.RETURN) return;
        child = block;
    }

    @Override
    public BlockCFG getChild() {
        return child;
    }
}
