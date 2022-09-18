package retr0.bedrockwaters.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A utility which promotes cleaner control flow at the cost of some performance (when compared to an if-else control
 * flow counterpart). Specifically designed for handling possible arguments for an instance method.
 *
 * @param handlerMappings A mapping from keys to single-argument handlers.
 * @param defaultValue A fallback value for when no mapping can be found.
 * @param <K> The key type used in handlerMappings.
 * @param <A> The argument type for the single-argument handlers.
 * @param <B> The return type for handlers.
 */
public record HandlerMap <K, A, B> (LinkedHashMap<K, Function<A, B>> handlerMappings, B defaultValue)
{
    /**
     * @see HandlerMap
     */
    public HandlerMap(Map<K, Function<A, B>> handlerMappings, B defaultValue) {
        this(new LinkedHashMap<>(handlerMappings), defaultValue);
    }



    /**
     * Handles a value based on the assigned mappings with its handler key determined by the specified selector.
     * @param value The value to be handled. This is the value to be passed into the proper handler if it exists.
     * @param selector A {@link Predicate<K>} which will determine the handler key to select.
     * @return The return value from the handler or the assigned default value if no handler could be found.
     */
    public B handle(A value, Predicate<K> selector) {
        var keys = handlerMappings.keySet().stream();
        var matchedKey = keys.filter(selector).findFirst();

        return (matchedKey.isEmpty()) ? defaultValue : handlerMappings.get(matchedKey.get()).apply(value);
    }
}
