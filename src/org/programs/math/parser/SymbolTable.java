package org.programs.math.parser;

import org.programs.math.exceptions.RTException;
import org.programs.math.types.Value;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The symbol table is used to store all the identifier names, mapped by their values with which they are holding.
 * @see Value
 */
public final class SymbolTable {
    public static final HashSet<String> globalIdentifiers;
    private static final HashSet<String> builtIns;

    static {
        globalIdentifiers = new HashSet<>();
        builtIns = new HashSet<>();
    }

    private static SymbolTable global;

    /**
     * The symbols this scope has, mapped by their values.
     */
    private final HashMap<String, Value> symbols;

    /**
     * Constructs a symbol table.
     */
    public SymbolTable() {
        symbols = new HashMap<>();

        if (global == null) {
            global = this;
        }
    }

    /**
     * Checks if a variable is contained in the global, or a separate scope.
     * @param name The name of the identifier.
     * @param additional Addition scope.
     * @return {@code true} if the variable exists.
     */
    public static boolean hasVar(String name, HashSet<String> additional) {
        return globalIdentifiers.contains(name) || additional.contains(name);
    }

    /**
     * Store the builtin functions and identifiers. This is called only once
     * after the built-ins are loaded.
     * @param st The symbol table, which is a global.
     */
    public static void saveBuiltIns(SymbolTable st) {
        builtIns.addAll(st.symbols.keySet());
    }

    /**
     * Gets the value of this identifier, if present.
     * @param id The identifier
     * @return The value if present, or {@code null}.
     */
    public Value get(String id, boolean isGlobal) {
        if (isGlobal) {
            return global.symbols.get(id);
        } else {
            return symbols.get(id);
        }
    }

    /**
     * Sets an identifier => value in this symbol table.
     * @param id The identifier name.
     * @param x The value.
     */
    public void set(String id, Value x) {
        if (builtIns.contains(id) && isGlobal()) {
            throw new RTException(id + " is a built in function/variable.");
        }

        symbols.put(id, x);
    }

    /**
     * Checks if this identifier exists in the symbol table.
     * @param id The identifier name.
     * @return {@code true} if exists, {@code false} otherwise.
     */
    public boolean contains(String id, boolean isGlobal) {
        return isGlobal && global.symbols.containsKey(id) || symbols.containsKey(id);
    }

    /**
     * Removes the identifier from the symbol table.
     * @param id The identifier name.
     */
    public void remove(String id) {
        symbols.remove(id);
    }

    /**
     * Checks if this symbol table is global.
     * @return {@code true} if the condition satisfies.
     */
    public boolean isGlobal() {
        return this == global;
    }

    /**
     * A string representation of this symbol table.
     * In the format (Parent={ParentSymbolTable} or null; Symbols={Symbols in this scope})
     * @return The string representation.
     */

    public String toString() {
        return symbols.toString();
    }
}
