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
            previousBlocks.forEach(blockCFG -> blockCFG.setChild(newBlocks.get(0)));
            for (BlockCFG blockCFG : newBlocks) {
                blockCFG.addParents(previousBlocks);
            }
            previousBlocks = newBlocks;
        }
        return previousBlocks;
    }

    @Override
    public List<BlockCFG> visitIf(IfTree node, List<BlockCFG> r) {
        ConditionBlockCFG conditionBlockCFG = new ConditionBlockCFG(node.getCondition());
        r.forEach(blockCFG -> blockCFG.setChild(conditionBlockCFG));
        conditionBlockCFG.addParents(r);
        ActionBlockCFG thenBlockCFG = new ActionBlockCFG(node.getCondition());
        ActionBlockCFG elseBlockCFG = new ActionBlockCFG(node.getCondition());

        List<BlockCFG> thenBlocks = node.getThenStatement().accept(this, Collections.singletonList(thenBlockCFG));
        conditionBlockCFG.setThenChild(thenBlockCFG.getChild());
        thenBlockCFG.getChild().setParents(Collections.singletonList(conditionBlockCFG));

        List<BlockCFG> elseBlocks = new ArrayList<>();
        if (node.getElseStatement() != null) {
            elseBlocks.addAll(node.getElseStatement().accept(this, Collections.singletonList(elseBlockCFG)));
            conditionBlockCFG.setElseChild(elseBlockCFG.getChild());
            elseBlockCFG.getChild().setParents(Collections.singletonList(conditionBlockCFG));
        }

        List<BlockCFG> returnList = new ArrayList<>(thenBlocks);
        returnList.addAll(elseBlocks);
        return returnList;
    }

    @Override
    public List<BlockCFG> visitForLoop(ForLoopTree node, List<BlockCFG> r) {
        final Tree init = node.getInitializer();
        if (init != null) {
            init.accept(this, r);
        }

        final Tree cond = node.getCondition();
        if (cond != null) {
            cond.accept(this, r);
        }

        final Tree update = node.getUpdate();
        if (update != null) {
            update.accept(this, r);
        }

        node.getStatement().accept(this, r);

        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);

        return Collections.singletonList(actionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitVariable(VariableTree node, List<BlockCFG> r) {
        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);

        return Collections.singletonList(actionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitExpressionStatement(ExpressionStatementTree node, List<BlockCFG> r) {
//        return super.visitExpressionStatement(node, r);

        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);

        return Collections.singletonList(actionBlockCFG);
    }

    @Override
    public List<BlockCFG> visitReturn(ReturnTree node, List<BlockCFG> r) {
        final Tree retExpr = node.getExpression();
        if (retExpr != null) {
            retExpr.accept(this, r);
        }

        ActionBlockCFG actionBlockCFG = new ActionBlockCFG(node);

        return Collections.singletonList(actionBlockCFG);
    }
}
