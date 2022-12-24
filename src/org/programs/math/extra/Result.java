package org.programs.math.extra;

import java.util.function.Function;

/**
 * <p>The {@code Result} type is a value based type.
 * <p>It can represent values with two possibilities, a successful result or an error.
 * <p>If it is a result, the {@code error} property is {@code null} and vice-versa.
 * @param <T> The type of the result.
 * @param <E> The type of the error.
 */
public final class Result<T, E> {

    /**
     * The result.
     */
    public final T result;

    /**
     * The error.
     */
    public final E error;

    /**
     * Creates a result. This constructor is for internal purpose only.
     * @param t The result.
     * @param e The error.
     */
    private Result(T t, E e) {
        result = t;
        error = e;
    }

    /**
     * Creates a result indicating success.
     * @param t The result.
     * @param <T> The type of result.
     * @param <E> The type of error. ({@code null} value).
     * @return The success result.
     */
    public static <T, E> Result<T, E> success(T t) {
        return new Result<>(t, null);
    }

    /**
     * Creates a result indicating failure or an error.
     * @param e The error.
     * @param <T> The type of result. ({@code null} value).
     * @param <E> The type of error.
     * @return The failure result.
     */
    public static <T, E> Result<T, E> failure(E e) {
        return new Result<>(null, e);
    }

    /**
     * Helper function to check if this result is a success or an error.
     * @return {@code true} if this result is an error, {@code false} otherwise.
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * <p>A function which transforms this result's state.
     * <p>If this result is an error, then the error is returned, otherwise
     * the result value gets mapped to a new result by the transformer.
     * @param transform The transformer function.
     * @return A new result containing the new value or the error.
     * @param <U> The type of the new value if the transformation succeeds.
     */
    public <U> Result<U, E> run(Function<T, Result<U, E>> transform) {
        if (isError()) {
            return failure(error);
        }

        return transform.apply(result);
    }

    public String toString() {
        if (error == null) {
            return result.toString();
        }
        return "Error: " + error;
    }
}
