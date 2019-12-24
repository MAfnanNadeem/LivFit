package life.mibo.hexa.ui.ch6

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.core.Logger
import life.mibo.hexa.ui.ch6.adapter.Channel6Listener
import life.mibo.hexa.ui.ch6.adapter.Channel6Model
import life.mibo.hexa.ui.ch6.adapter.ChannelAdapter

class Channel6Controller(val fragment: Channel6Fragment) : Channel6Listener,
    Channel6Fragment.Listener {

    var list = ArrayList<Channel6Model>()
    var adapter: ChannelAdapter? = null

    private fun onPlayClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Channel6Model) {
                    t.isPlay = true
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    private fun onStopClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Channel6Model) {
                    t.isPlay = false
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    private fun onPlusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                    log("iv_plus onSubscribe")
                }

                override fun onNext(t: Channel6Model) {
                    log("iv_plus onNext")
//                        t.percentChannel?.observe(this@Channel6Fragment, Observer {
//                            it.plus(1)
//                        })
                    t.incChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    log("iv_plus onComplete")
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    private fun onMinusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Channel6Model) {
                    t.decChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    override fun onMainPlusClicked() {

    }

    override fun onMainMinusClicked() {

    }

    override fun onMainPlayClicked() {

    }

    override fun onMainStopClicked() {

    }

    override fun onClick(data: Channel6Model) {

    }

    override fun onPlusClicked(data: Channel6Model) {

    }

    override fun onMinusClicked(data: Channel6Model) {

    }

    override fun onPlayPauseClicked(data: Channel6Model, isPlay: Boolean) {

    }

    fun log(msg: String, throwable: Throwable? = null) {
        Logger.e("${this::javaClass.name} : $msg", throwable)
    }

}