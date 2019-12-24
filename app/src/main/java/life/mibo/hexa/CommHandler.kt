package life.mibo.hexa

import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.*
import life.mibo.hardware.models.program.Circuit
import life.mibo.hardware.network.TCPClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.NoSubscriberEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CommHandler(val activity: MainActivity) {

    fun regisiter() {
        log("regisiter ")
        EventBus.getDefault().register(this)
    }

    fun unregisiter() {
        log("unregisiter ")
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onSendProgramEvent(event: SendProgramEvent) {
        log("onSendProgramEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onSendProgramEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onSendProgramChangesHotEvent(event: SendProgramChangesHotEvent) {
        log("onSendProgramChangesHotEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onSendProgramChangesHotEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onSendCircuitEvent(event: SendCircuitEvent) {
        log("onSendCircuitEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onSendCircuitEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    private fun sendCircuitTCP(circuit: Circuit, TCPSocket: TCPClient) {
        log("sendCircuitTCP $circuit")
    }

    private fun sendCircuitGATT(circuit: Circuit, Uid: String) {
        //CommunicationManager.getInstance().sendCircuitGATT(circuit, Uid)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onChangeColorEvent(event: ChangeColorEvent) {
        log("onChangeColorEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onChangeColorEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onDeviceSearchEvent(event: DeviceSearchEvent) {
        log("onDeviceSearchEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onDeviceSearchEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onMainLevelEvent(event: SendMainLevelEvent) {
        log("onMainLevelEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onMainLevelEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onChannelsLevelEvent(event: SendChannelsLevelEvent) {
        log("onChannelsLevelEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onChannelsLevelEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onDevicePlayEvent(event: SendDevicePlayEvent) {
        log("onDevicePlayEvent ${event?.uid}")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onDevicePlayEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onDeviceStartEvent(event: SendDeviceStartEvent) {
        log("onDeviceStartEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onDeviceStartEvent(event)
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onDeviceStopEvent(event: SendDeviceStopEvent) {
        log("onDeviceStopEvent $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onDeviceStopEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onNoSubscriberEvent(event: NoSubscriberEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        //EventBus.getDefault().removeStickyEvent(event);
        log("onNoSubscriberEvent " + event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    private fun onBleConnect(event: BleConnection) {
        log("onBleConnect $event")
        EventBus.getDefault().removeStickyEvent(event)
        CommunicationManager.getInstance().onBleConnect(event)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    private fun onGetMainLevelEvent(event: GetMainLevelEvent) {
        log("onBleConnect $event")
        EventBus.getDefault().removeStickyEvent(event)
        android.os.Handler(activity.mainLooper).postDelayed({
            CommunicationManager.getInstance().onMainLevelEvent(
                SendMainLevelEvent(
                    SessionManager.getInstance().userSession.user.mainLevel,
                    SessionManager.getInstance().userSession.device.uid
                )
            )
        }, 200)

    }

    fun log(msg: String) {
        Logger.e("CommHandler: $msg")
    }

}