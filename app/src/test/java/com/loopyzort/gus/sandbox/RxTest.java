package com.loopyzort.gus.sandbox;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

import static org.junit.Assert.assertEquals;

public class RxTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void helloWorld() {
        Observable<String> myObservable = Observable.create(
                new OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("Hello Nurse!");
                        subscriber.onCompleted();
                        ;
                    }
                }
        );
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        };
        myObservable.subscribe(mySubscriber);
    }

    @Test
    public void smallerHelloWorld() {
        Observable.just("Hello, world")
                .subscribe(s -> System.out.println(s));
    }

    @Test
    public void maps() {
        Observable.just("Hello nurse!")
                .map(s -> s + " - Todd")
                .subscribe(s -> System.out.println(s));

        Observable.just("Hello nurse!")
                .map(s -> s.hashCode())
                .subscribe(x -> System.out.println(Integer.toString(x)));

        Observable.just("Hello nurse!")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }

    private Observable<List<String>> query(String text) {
        List<String> urls = new ArrayList<>();
        urls.add("url1");
        urls.add("url2");
        urls.add("url3");
        return Observable.just(urls);
    }

    private Observable<String> getTitle(String URL) {
        return Observable.just("Weekend at Bernie's");
    }

    @Test
    public void from() {
        query("Hello, world")
                .subscribe(urls -> {
                    Observable.from(urls)
                            .subscribe(url -> System.out.println(url));
                });
        query("Hello, world")
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                })
                .subscribe(url -> System.out.println(url));

        query("Hello, world")
                .flatMap(urls -> Observable.from(urls))
                .subscribe(url -> System.out.println(url));
    }

    @Test
    public void flatMap() {
        query("snuh")
                .flatMap(urls -> Observable.from(urls))
                .flatMap(url -> getTitle(url))
                .subscribe(title -> System.out.println(title));
    }

    @Test
    public void filter() {
        query("snuh")
                .flatMap(urls -> Observable.from(urls))
                .flatMap(url -> getTitle(url))
                .filter(title -> title != null)
                .take(2)
                .subscribe(thing -> System.out.println(thing));
    }

    private String potentialException(String s) {
        throw new RuntimeException("Something went wrong");
    }

    @Test
    public void exception() {
        Observable.just("Snuh")
                .map(s -> potentialException(s))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Done");

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Got exception: " + e.getMessage());

                    }
                });
    }



}