package com.maxciv.jsparse;

import com.maxciv.jsparse.cfg.ActionBlockCFG;
import com.maxciv.jsparse.cfg.BlockCFG;
import com.maxciv.jsparse.cfg.ConditionBlockCFG;
import com.maxciv.jsparse.cfg.FunctionBlockCFG;
import jdk.nashorn.api.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Visitor extends SimpleTreeVisitorES5_1<List<BlockCFG>, List<BlockCFG>> {  // <return список последних блоков, список родителей>

    public FunctionBlockCFG functionBlockCFG;

    @Override
    public List<BlockCFG> visitFunctionDeclaration(FunctionDeclarationTree node, List<BlockCFG> r) {
        functionBlockCFG = new FunctionBlockCFG(node);

        return node.getBody().accept(this, Collections.singletonList(functionBlockCFG));
    }

    @Override
    public List<BlockCFG> visitBlock(BlockTree node, List<BlockCFG> r) {
        List<BlockCFG> previousBlocks = r;
        for (StatementTree tree : node.getStatements()) {
            List<BlockCFG> newBlocks = tree.accept(this, previousBlocks);
            if (newBlocks == null) continue;
            previousBlocks = newBlocks;
        }
        return previousBlocks;
    }

    @Override
    public List<BlockCFG> visitIf(IfTree node, List<BlockCFG> r) {
        ConditionBlockCFG conditionBlockCFG = new ConditionBlockCFG(node.getCondition());
        setParentsAndChildren(r, Collections.singletonList(conditionBlockCFG));

        ActionBlockCFG thenCommonBlockCFG = new ActionBlockCFG(node.getCondition());
        ActionBlockCFG elseCommonBlockCFG = new ActionBlockCFG(node.getCondition());

        List<BlockCFG> thenBlocks = node.getThenStatement().accept(this, Collections.singletonList(thenCommonBlockCFG));
        conditionBlockCFG.setThenChild(thenCommonBlockCFG.getChild());
        thenCommonBlockCFG.getChild().setParents(Collections.singletonList(conditionBlockCFG));

        List<BlockCFG> elseBlocks = new ArrayList<>();
        if (node.getElseStatement() != null) {
            elseBlocks.addAll(node.getElseStatement().accept(this, Collections.singletonList(elseCommonBlockCFG)));
            conditionBlockCFG.setElseChild(elseCommonBlockCFG.getChild());
            elseCommonBlockCFG.getChild().setParents(Collections.singletonList(conditionBlockCFG));
        } else {
            ActionBlockCFG emptyBlock = new ActionBlockCFG(null);
            elseBlocks.add(emptyBlock);
            conditionBlockCFG.setElseChild(emptyBlock);
            emptyBlock.setParents(Collections.singletonList(conditionBlockCFG));
        }

        List<BlockCFG> returnList = new ArrayList<>();
        returnList.addAll(thenBlocks);
        returnList.addAll(elseBlocks);
        return returnList;
    }

    @Override
    public List<BlockCFG> visitForLoop(ForLoopTree node, List<BlockCFG> r) {
        final Tree init = node.getInitializer();
        ActionBlockCFG initActionBlockCFG = null;
        if (init != null) {
            initActionBlockCFG = new ActionBlockCFG(init);
            setParentsAndChildren(r, Collections.singletonList(initActionBlockCFG));
        }

        final Tree cond = node.getCondition();
        ConditionBlockCFG conditionBlockCFG = new ConditionBlockCFG(cond);
        if (initActionBlockCFG == null) {
            setParentsAndChildren(r, Collections.singletonList(conditionBlockCFG));
        } else
            setParentsAndChildren(Collections.singletonList(initActionBlockCFG), Collections.singletonList(conditionBlockCFG));

        final Tree statement = node.getStatement();
        ActionBlockCFG commonBlockCFG = new ActionBlockCFG(node.getCondition());

        List<BlockCFG> endBlocks = statement.accept(this, Collections.singletonList(commonBlockCFG));
        conditionBlockCFG.setThenChild(commonBlockCFG.getChild());
        commonBlockCFG.getChild().setParents(Collections.singletonList(conditionBlockCFG));

        final Tree update = node.getUpdate();
        ActionBlockCFG updateActionBlockCFG = null;
        if (update != null) {
            updateActionBlockCFG = new ActionBlockCFG(update);
            setParentsAndChildren(endBlocks, Collections.singletonList(updateActionBlockCFG));
        }
        if (updateActionBlockCFG == null) {
            setParentsAndChildren(endBlocks, Collections.singletonList(conditionBlockCFG));
        } else
            setParentsAndChildren(Collections.singletonList(updateActionBlockCFG), Collections.singletonList(conditionBlockCFG));

        ActionBlockCFG exitActionBlockCFG = new ActionBlockCFG(null);
        exitActionBlockCFG.addParent(conditionBlockCFG);
        conditionBlockCFG.setElseChild(exitActionBlockCFG);
        return Collections.singletonList(exitActionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitVariable(VariableTree node, List<BlockCFG> r) {
        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);
        setParentsAndChildren(r, Collections.singletonList(actionBlockCFG));
        return Collections.singletonList(actionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitExpressionStatement(ExpressionStatementTree node, List<BlockCFG> r) {
        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);
        setParentsAndChildren(r, Collections.singletonList(actionBlockCFG));
        return Collections.singletonList(actionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitReturn(ReturnTree node, List<BlockCFG> r) {
        final Tree retExpr = node.getExpression();
        if (retExpr != null) {
            retExpr.accept(this, r);
        }

        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);
        setParentsAndChildren(r, Collections.singletonList(actionBlockCFG));
        return Collections.singletonList(actionBlockCFG);
    }

    /**
     * Установить родителям детей и детям родителей
     */
    private void setParentsAndChildren(List<BlockCFG> parents, List<BlockCFG> children) {
        if (children == null || parents == null) return;
        parents.forEach(parent -> parent.setChild(children.get(0)));
        children.forEach(child -> child.addParents(parents));
    }
}
