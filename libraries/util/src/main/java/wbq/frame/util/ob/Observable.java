package wbq.frame.util.ob;

/**
 * @author jerry
 * @created 2020/8/4 16:37
 */
public interface Observable<Observer> {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
