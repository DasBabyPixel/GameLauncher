package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public class PredicateConsumer<T> {

    private final GamePredicate<T> predicate;
    private final GameConsumer<T> consumer;

    /**
     * @param consumer
     */
    public PredicateConsumer(GameConsumer<T> consumer) {
        this(t -> true, consumer);
    }

    /**
     * @param predicate
     * @param consumer
     */
    public PredicateConsumer(GamePredicate<T> predicate, GameConsumer<T> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    /**
     * @param t
     * @throws GameException
     */
    public void handle(T t) throws GameException {
        if (predicate.test(t)) {
            consumer.accept(t);
        }
    }
}
