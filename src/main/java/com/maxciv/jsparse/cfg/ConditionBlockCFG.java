package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.Tree;

public class ConditionBlockCFG extends AbstractBlockCFG {

    private Tree condition;
    private BlockCFG thenChild;
    private BlockCFG elseChild;

    public ConditionBlockCFG(Tree condition) {
        this.condition = condition;
        fillVariables(condition);
        stringRepresentation = getStringFromKind(condition.getKind(), condition);
    }

    @Override
    public void setThenChild(BlockCFG block) {
        thenChild = block;
    }

    @Override
    public void setElseChild(BlockCFG block) {
        elseChild = block;
    }

    @Override
    public void setCondition(Tree condition) {
        this.condition = condition;
    }

    @Override
    public Tree getExpression() {
        return condition;
    }

    @Override
    public BlockCFG getThenChild() {
        return thenChild;
    }

    @Override
    public BlockCFG getElseChild() {
        return elseChild;
    }
}
