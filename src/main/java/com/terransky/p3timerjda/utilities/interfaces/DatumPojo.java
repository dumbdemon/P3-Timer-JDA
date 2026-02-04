package com.terransky.p3timerjda.utilities.interfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A base {@link Pojo} that's used when the JSON is just an array.
 *
 * @param datum A list of type T
 * @param <T>   Any object type accepted by {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DatumPojo<T>(Collection<T> datum) implements Pojo {

    /**
     * Get the first element of a filtered stream.
     *
     * @param predicate a non-interfering, stateless predicate to apply to each element to determine if it should be included
     * @return An {@link Optional} containing an object of type {@link T} from a filtered stream.
     */
    @NotNull
    public Optional<T> first(Predicate<? super T> predicate) {
        return datum.stream().filter(predicate).findFirst();
    }

    /**
     * Get the first result of the list.
     *
     * @return An {@link Optional} containing an object of type {@link T}.
     */
    @NotNull
    public Optional<T> first() {
        return datum.stream().findFirst();
    }

    /**
     * Creates a new DatumPojo with elements that match the filter
     *
     * @param filter a predicate to apply to each element to determine if it should be included
     * @return A new DatumPojo
     */
    @NotNull
    public DatumPojo<T> filter(Predicate<? super T> filter) {
        return new DatumPojo<>(datum.stream().filter(filter).toList());
    }

    /**
     * Convert this DatumPojo into a different data type
     *
     * @param mapper a function to apply to each element
     * @param <R>    The element type of the new DatumPojo
     * @return The DatumPojo with the new element list
     */
    @NotNull
    public <R> DatumPojo<R> map(@NotNull Function<? super T, R> mapper) {
        return new DatumPojo<>(datum.stream().map(mapper).toList());
    }

    @NotNull
    public List<T> toList() {
        return datum.stream().toList();
    }

    @Override
    public String getAsJsonString() throws JsonProcessingException {
        return Pojo.getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(datum);
    }
}
