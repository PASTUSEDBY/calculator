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

    /**
     * Create a variable name which can be found in the table.
     * @param id The identifier name.
     * @param fn The function name where the identifier resides (if any).
     * @return A string with {@code fn-id} format.
     */
    public static String makeVarName(String id, String fn) {
        if (fn != null) {
            id = fn + "-" + id;
        }

        return id;
    }

    /**
     * The outer scope symbol table.
     */
    private final SymbolTable parent;
    private static SymbolTable global;

    /**
     * The symbols this scope has, mapped by their values.
     */
    private final HashMap<String, Value> symbols;

    /**
     * Constructs a symbol table with an outer scoped symbol table.
     * @param p The outer scope symbol table.
     */
    public SymbolTable(SymbolTable p) {
        parent = p;
        symbols = new HashMap<>();

        if (parent == null) {
            global = this;
        }
    }

    /**
     * Same as @{code SymbolTable(SymbolTable)}, except sets no outer scope symbol table.
     * This should be used when setting the global symbol table, mostly.
     * @see SymbolTable#SymbolTable(SymbolTable)
     */
    public SymbolTable() {
        this(null);
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
        Value v = getLocalVar(id);
        if (!isGlobal || v != null) return v;

        return global.symbols.get(id);
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
        boolean has = hasLocalVar(id);
        if (!isGlobal) return has;

        return global.symbols.containsKey(id);
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
        return parent == null;
    }

    /**
     * Gets the local variable from the symbol table.
     * @param x The identifier name.
     * @return The value associated with this. {@code null} if nothing.
     */
    private Value getLocalVar(String x) {
        SymbolTable temp = this;
        while (!temp.isGlobal()) {
            Value v = temp.symbols.get(x);
            temp = temp.parent;
            if (v != null) return v;
        }

        return null;
    }

    /**
     * Checks if local variable exists in the symbol table.
     * @param x The identifier name.
     * @return {@code true} if the variable exists.
     */
    private boolean hasLocalVar(String x) {
        SymbolTable temp = this;
        while (!temp.isGlobal()) {
            if (temp.symbols.containsKey(x)) return true;
            temp = temp.parent;
        }

        return false;
    }

    /**
     * A string representation of this symbol table.
     * In the format (Parent={ParentSymbolTable} or null; Symbols={Symbols in this scope})
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "(Parent=" + parent + "; Symbols=" + symbols + ")";
    }
}
