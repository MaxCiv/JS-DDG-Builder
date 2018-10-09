package com.maxciv.jsparse.cfg;

import java.util.HashSet;

public class CFG {

    private BlockCFG firstBlock;

    public CFG(BlockCFG firstBlock) {
        this.firstBlock = firstBlock;
    }

    public BlockCFG getFirstBlock() {
        return firstBlock;
    }

    public void buildDDG() {
        findDependentByBlock(firstBlock, new HashSet<>());
    }

    private void findDependentByBlock(BlockCFG currentBlock, HashSet<BlockCFG> allBlocks) {
        if (currentBlock == null)
            return; // если нет следующего блока -- выходим

        if (!allBlocks.add(currentBlock))
            return; // если блок уже есть в сете и он не добавился -- выходим

        currentBlock.getDependentVariables().forEach(variableName ->
                findDependentByVariable(variableName, currentBlock, currentBlock, new HashSet<>()));

        if (currentBlock instanceof ConditionBlockCFG) {
            findDependentByBlock(currentBlock.getThenChild(), allBlocks);
            findDependentByBlock(currentBlock.getElseChild(), allBlocks);
        } else {
            findDependentByBlock(currentBlock.getChild(), allBlocks);
        }
    }

    /**
     * Найти, зависит ли блок nextBlock от блока currentBlock по переменной variableName.
     */
    private void findDependentByVariable(final String variableName, BlockCFG currentBlock, BlockCFG nextBlock, HashSet<BlockCFG> allBlocks) {
        if (nextBlock == null)
            return; // если нет следующего блока -- выходим

        if (!allBlocks.add(nextBlock))
            return; // если блок уже есть в сете и он не добавился -- выходим

        if (nextBlock instanceof ConditionBlockCFG) {
            if (nextBlock.getDependencies().contains(variableName)) {
                if (!currentBlock.addToDependentBlocks(nextBlock))
                    return; // если блок уже есть в сете и он не добавился -- выходим
            }

            findDependentByVariable(variableName, currentBlock, nextBlock.getThenChild(), allBlocks);
            findDependentByVariable(variableName, currentBlock, nextBlock.getElseChild(), allBlocks);
        } else {
            if (nextBlock.getDependencies().contains(variableName)) {
                if (!currentBlock.addToDependentBlocks(nextBlock))
                    return; // если блок уже есть в сете и он не добавился -- выходим
            }

            if (nextBlock.getDependentVariables().contains(variableName) && currentBlock != nextBlock)
                return; // если переменная затирается новым значением -- выходим

            findDependentByVariable(variableName, currentBlock, nextBlock.getChild(), allBlocks);
        }
    }
}
