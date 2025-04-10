package maingatewayserver.request_process_sevice.Factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
//TODO change the int to Generic

/**
 * Factory Method Design Pattern
 * maps the commands to each Company
 * @param <K> the key in gateway server will be string
 * @param <D> the data to pass to the constructor
 * @param <T> the command
 */
public class Factory<K,D,T> implements FactoryInterface<K,D,T> {

    private final Map<K, Map<Integer, Function<D, ? extends T>>> map = new HashMap<>();

    @Override
    public T create(K key, D data, int companyID) {
        Map <Integer, Function<D, ? extends T>> companyCommandMap = map.get(key);
        Function<D, ? extends T> func = companyCommandMap.get(companyID);
        return func.apply(data);
    }

    @Override
    public void add(K key, Function<D, ? extends T> func, int companyID) {
        map.computeIfAbsent(key, k -> new HashMap<>()).put(companyID, func);
    }
}


