package maingatewayserver.request_process_sevice.Factory;

import java.util.function.Function;

interface FactoryInterface<K,D,T>{

    T create(K key, D data, int CompanyID);
    void add(K key , Function< D , ? extends T> func, int insideKey);
}

