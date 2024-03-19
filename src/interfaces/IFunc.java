package interfaces;

import java.util.ArrayList;

public interface IFunc <T, V> {
    public V apply(ArrayList<T>... t);
}
