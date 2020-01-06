package life.mibo.hexa.core;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class RxJava {

    public <T> void from(ArrayList<T> list) {
        Observable.fromArray(list).flatMapIterable(x -> x).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T t) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                return Observable.just("");
            }
        }).observeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(() -> {
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static <T> void execute(Callable<T> callable) {

        Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                return Observable.just("");
            }
        }).observeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(() -> {
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T> void execute(T t, final Observer<T> observer) {

        Observable.defer((Callable<ObservableSource<T>>) () -> Observable.just(t)).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public Observer<String> getSubscribe() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public static <T> Disposable singleCall(Callable<T> call, Consumer<? super T> action) {
        return Single.fromCallable(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static <T> Disposable async(Callable<T> call, Consumer<? super T> action) {
        return Observable.fromCallable(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static <T> Disposable defer(Callable<? extends ObservableSource<? extends T>> call, Consumer<? super T> action) {
        return Observable.defer(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static <T> Disposable deferDelay(Callable<? extends ObservableSource<? extends T>> call, Consumer<? super T> action, int ms) {
        return Observable.defer(call).delay(ms, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static <T> Disposable deferSingle(Callable<? extends SingleSource<? extends T>> call, Consumer<? super T> action) {
        return Single.defer(call)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static Disposable timer(int delay, int repeat, Consumer<Long> action) {
        return Single.timer(delay, TimeUnit.MILLISECONDS).repeat(repeat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    public static Disposable interval(int delay, int toSec, Consumer<Long> onNext, Action action) {
        return Observable.interval(delay, TimeUnit.MILLISECONDS).takeUntil(time -> time >= toSec)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, Functions.ON_ERROR_MISSING, action, Functions.emptyConsumer());
    }


}
