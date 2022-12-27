package org.programs.math.nodes;

import org.programs.math.types.ComplexNum;
import org.programs.math.parser.SymbolTable;

/**
 * <p>A node represents a "leaf" of the AST (Abstract Syntax Tree).
 *    Each leaf contains a value, along with other nodes extending below it.
 * <p>To learn about tree data structures, read it at <a href="https://bit.ly/3ihhUyc">https://bit.ly/3ihhUyc</a>
 */
public interface Node {
    /**
     * An abstract method which traverses all its child nodes below it. This is implemented on a per class basis.
     * @param st The symbol table of this scope.
     * @return The number evaluated from the nodes.
     */
    ComplexNum visit(SymbolTable st);
}
