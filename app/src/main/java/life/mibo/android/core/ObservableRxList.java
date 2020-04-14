package life.mibo.android.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * http://stackoverflow.com/questions/28816691/how-can-i-create-an-observer-over-a-dynamic-list-in-rxjava
 */
public class ObservableRxList<T> {

    protected final List<T> list;
    protected final PublishSubject<T> subject;

    public ObservableRxList() {
        this.list = new ArrayList<T>();
        this.subject = PublishSubject.create();
    }

    public void add(T value) {
        list.add(value);
        subject.onNext(value);
    }

    public void update(T value) {
        for (ListIterator<T> it = list.listIterator(); it.hasNext(); ) {
            if (value.equals(it.next())) {
                it.set(value);
                subject.onNext(value);
                return;
            }
        }
    }

    public void remove(T value) {
        list.remove(value);
        subject.onNext(value);
    }

    public Observable<T> getObservable() {
        return subject;
    }

    public Observable<T> getCurrentList() {
        //return Observable.just(list);
        return (Observable<T>) Observable.fromArray(list);
    }

}