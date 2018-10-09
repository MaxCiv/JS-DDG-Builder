package com.maxciv.jsparse.cfg;

import jdk.nashorn.api.tree.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractBlockCFG implements BlockCFG {

    private List<BlockCFG> parentBlocks = new ArrayList<>();
    protected String stringRepresentation;
    private HashSet<String> dependentVariables = new HashSet<>();  // переменные, которые зависят от выражения
    private HashSet<String> dependencies = new HashSet<>(); // переменные, от которых зависит выражение
    // dependentVariable = dependency1 + (dependency2 * 2)

    private HashSet<BlockCFG> dependentBlocks = new HashSet<>();  // стрелки идут от this блока к списку блоков dependentBlocks

    @Override
    public void addParent(BlockCFG block) {
        parentBlocks.add(block);
    }

    @Override
    public void setParents(Collection<BlockCFG> block) {
        parentBlocks = new ArrayList<>(block);
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

    @Override
    public BlockCFG getChild() {
        return null;
    }

    @Override
    public BlockCFG getThenChild() {
        return null;
    }

    @Override
    public BlockCFG getElseChild() {
        return null;
    }

    @Override
    public String getStringRepresentation() {
        return stringRepresentation;
    }

    @Override
    public HashSet<String> getDependentVariables() {
        return dependentVariables;
    }

    @Override
    public HashSet<String> getDependencies() {
        return dependencies;
    }

    @Override
    public HashSet<BlockCFG> getDependentBlocks() {
        return dependentBlocks;
    }

    @Override
    public boolean addToDependentBlocks(BlockCFG blockCFG) {
        return dependentBlocks.add(blockCFG);
    }

    /**
     * Разбираем блок в соответствии с его типом и находим переменные,
     * от которых он зависит, и переменную, которую он представляет.
     */
    protected void fillVariables(Tree tree) {
        Tree.Kind kind = tree.getKind();
        switch (kind) {
            case FUNCTION:
                FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree) tree;
                functionDeclarationTree.getParameters().forEach(o -> addToVariables(o, dependentVariables));
                break;

            case EXPRESSION_STATEMENT:
                ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) tree;
                fillVariables(expressionStatementTree.getExpression());
                break;

            case VARIABLE:
                VariableTree variableTree = (VariableTree) tree;
                addToVariables(variableTree.getBinding(), dependentVariables);
                if (variableTree.getInitializer() != null)
                    addToVariables(variableTree.getInitializer(), dependencies);
                break;

            case RETURN:
                ReturnTree returnTree = (ReturnTree) tree;
                addToVariables(returnTree.getExpression(), dependencies);
                break;

            case ASSIGNMENT:
                AssignmentTree assignmentTree = (AssignmentTree) tree;
                addToVariables(assignmentTree.getVariable(), dependentVariables);
                addToVariables(assignmentTree.getExpression(), dependencies);
                break;

            case POSTFIX_INCREMENT:
            case POSTFIX_DECREMENT:
            case PREFIX_INCREMENT:
            case PREFIX_DECREMENT:
                UnaryTree unaryTree = (UnaryTree) tree;
                addToVariables(unaryTree.getExpression(), dependentVariables);
                addToVariables(unaryTree.getExpression(), dependencies);
                break;

            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUAL:
            case GREATER_THAN_EQUAL:
            case EQUAL_TO:
            case NOT_EQUAL_TO:
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
                BinaryTree binaryTree = (BinaryTree) tree;
                addToVariables(binaryTree.getLeftOperand(), dependencies);
                addToVariables(binaryTree.getRightOperand(), dependencies);
                break;

            case MULTIPLY_ASSIGNMENT:
            case DIVIDE_ASSIGNMENT:
            case REMAINDER_ASSIGNMENT:
            case PLUS_ASSIGNMENT:
            case MINUS_ASSIGNMENT:
                CompoundAssignmentTree compoundAssignmentTree = (CompoundAssignmentTree) tree;
                addToVariables(compoundAssignmentTree.getVariable(), dependentVariables);
                addToVariables(compoundAssignmentTree.getExpression(), dependencies);
                break;

            default:
                System.err.println("SOMETHING WENT WRONG (fillVariables) KIND = " + kind.name());
        }
    }

    /**
     * Разбираем дерево и все переменные добавляем в заданный список
     */
    private void addToVariables(Tree tree, HashSet<String> variables) {
        Tree.Kind kind = tree.getKind();
        switch (kind) {
            case IDENTIFIER:
                variables.add(((IdentifierTree) tree).getName());
                break;

            case COMMA:
            case MULTIPLY:
            case DIVIDE:
            case REMAINDER:
            case PLUS:
            case MINUS:
                BinaryTree binaryTree = (BinaryTree) tree;
                addToVariables(binaryTree.getLeftOperand(), variables);
                addToVariables(binaryTree.getRightOperand(), variables);
                break;

            default:
                System.out.println("IGNORED NODE (addToVariables) KIND = " + kind.name());
        }
    }

    /**
     * Возвращает строковое представление выражения
     */
    protected String getStringFromKind(Tree.Kind kind, Tree tree) {
        switch (kind) {
            case FUNCTION:
                FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree) tree;
                String params = functionDeclarationTree.getParameters().stream()
                        .map(o -> getStringFromKind(o.getKind(), o))
                        .reduce((s, s2) -> s + ", " + s2).orElse("");
                return functionDeclarationTree.getName().getName() + "(" + params + ")";

            case EXPRESSION_STATEMENT:
                ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) tree;
                return getStringFromKind(expressionStatementTree.getExpression().getKind(), expressionStatementTree.getExpression());

            case VARIABLE:
                VariableTree variableTree = (VariableTree) tree;
                String init = variableTree.getInitializer() == null ? ""
                        : " = " + getStringFromKind(variableTree.getInitializer().getKind(), variableTree.getInitializer());
                return "var " + getStringFromKind(variableTree.getBinding().getKind(), variableTree.getBinding()) + init;

            case RETURN:
                ReturnTree returnTree = (ReturnTree) tree;
                return "return " + getStringFromKind(returnTree.getExpression().getKind(), returnTree.getExpression());

            case ASSIGNMENT:
                AssignmentTree assignmentTree = (AssignmentTree) tree;
                return getStringFromKind(assignmentTree.getVariable().getKind(), assignmentTree.getVariable()) + " = "
                        + getStringFromKind(assignmentTree.getExpression().getKind(), assignmentTree.getExpression());

            case IDENTIFIER:
                return ((IdentifierTree) tree).getName();

            case BOOLEAN_LITERAL:
            case NUMBER_LITERAL:
            case STRING_LITERAL:
                return ((LiteralTree) tree).getValue().toString();
            case NULL_LITERAL:
                return "null";

            case POSTFIX_INCREMENT:
            case POSTFIX_DECREMENT:
                UnaryTree unaryTree1 = (UnaryTree) tree;
                return getStringFromKind(unaryTree1.getExpression().getKind(), unaryTree1.getExpression())
                        + getOperationString(kind);

            case PREFIX_INCREMENT:
            case PREFIX_DECREMENT:
            case UNARY_PLUS:
            case UNARY_MINUS:
            case LOGICAL_COMPLEMENT:
                UnaryTree unaryTree2 = (UnaryTree) tree;
                return getOperationString(kind)
                        + getStringFromKind(unaryTree2.getExpression().getKind(), unaryTree2.getExpression());

            case COMMA:
            case MULTIPLY:
            case DIVIDE:
            case REMAINDER:
            case PLUS:
            case MINUS:
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_EQUAL:
            case GREATER_THAN_EQUAL:
            case EQUAL_TO:
            case NOT_EQUAL_TO:
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
                BinaryTree binaryTree = (BinaryTree) tree;
                return getStringFromKind(binaryTree.getLeftOperand().getKind(), binaryTree.getLeftOperand())
                        + getOperationString(kind)
                        + getStringFromKind(binaryTree.getRightOperand().getKind(), binaryTree.getRightOperand());

            case MULTIPLY_ASSIGNMENT:
            case DIVIDE_ASSIGNMENT:
            case REMAINDER_ASSIGNMENT:
            case PLUS_ASSIGNMENT:
            case MINUS_ASSIGNMENT:
                CompoundAssignmentTree compoundAssignmentTree = (CompoundAssignmentTree) tree;
                return getStringFromKind(compoundAssignmentTree.getVariable().getKind(), compoundAssignmentTree.getVariable())
                        + getOperationString(kind)
                        + getStringFromKind(compoundAssignmentTree.getExpression().getKind(), compoundAssignmentTree.getExpression());

            default:
                return "SOMETHING WENT WRONG (getStringFromKind) KIND = " + kind.name();
        }
    }

    /**
     * Возвращает строку знака операции
     */
    private String getOperationString(Tree.Kind kind) {
        switch (kind) {
            case POSTFIX_INCREMENT:
                return "++ ";
            case POSTFIX_DECREMENT:
                return "-- ";
            case PREFIX_INCREMENT:
                return " ++";
            case PREFIX_DECREMENT:
                return " --";
            case UNARY_PLUS:
                return " +";
            case UNARY_MINUS:
                return " -";
            case LOGICAL_COMPLEMENT:
                return " !";
            case COMMA:
                return ", ";
            case MULTIPLY:
                return " * ";
            case DIVIDE:
                return " / ";
            case REMAINDER:
                return " % ";
            case PLUS:
                return " + ";
            case MINUS:
                return " + ";
            case LESS_THAN:
                return " < ";
            case GREATER_THAN:
                return " > ";
            case LESS_THAN_EQUAL:
                return " <= ";
            case GREATER_THAN_EQUAL:
                return " >= ";
            case EQUAL_TO:
                return " == ";
            case NOT_EQUAL_TO:
                return " != ";
            case CONDITIONAL_AND:
                return " && ";
            case CONDITIONAL_OR:
                return " || ";
            case MULTIPLY_ASSIGNMENT:
                return " *= ";
            case DIVIDE_ASSIGNMENT:
                return " /= ";
            case REMAINDER_ASSIGNMENT:
                return " %= ";
            case PLUS_ASSIGNMENT:
                return " += ";
            case MINUS_ASSIGNMENT:
                return " -= ";
            default:
                return "SOMETHING WENT WRONG (getOperationString) KIND = " + kind.name();
        }
    }
}
