package life.mibo.android.ui.rxt;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import life.mibo.android.R;
import life.mibo.android.ui.base.BaseFragment;
import life.mibo.hardware.CommunicationManager;
import life.mibo.hardware.SessionManager;
import life.mibo.hardware.events.ChangeColorEvent;
import life.mibo.hardware.events.RxlStatusEvent;
import life.mibo.hardware.models.Device;

public class RxtIDConfigurationsFragment extends BaseFragment {

    public static BaseFragment newInstance() {
        return new RxtIDConfigurationsFragment();
    }


    private String controllerUid = "";
    private AppCompatSpinner controllerSpinner;
    TextView tvStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxt_id_configure, container, false);

        tvStatus = view.findViewById(R.id.tv_status);
        View install = view.findViewById(R.id.btn_sequence);
        View back = view.findViewById(R.id.btn_back);
        controllerSpinner = view.findViewById(R.id.spinner_controller);


        controllerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                log("controllerSpinner onItemSelected " + position);
                try {
                    controllerUid = parent.getSelectedItem().toString();
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                    // setTileAdapters(controllerUid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                log("controllerSpinner onNothingSelected " + parent);
            }
        });

        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(controllerUid)) {
                    toast("Please select controller");
                    return;
                }
                Single.fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        CommunicationManager.getInstance().onRxtIdConfigurations(new ChangeColorEvent(new Device(), controllerUid));
                        return "";
                    }
                }).subscribeOn(Schedulers.io()).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }).subscribe();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getCompositionRoot().getScreensNavigator().toRxtHome();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControllerAdapters();
    }

    void setControllerAdapters() {

        ArrayList<Device> devices = SessionManager.getInstance().getSession().getConnectedDevices();
        ArrayList<String> list = new ArrayList<>();
        for (Device device : devices) {
            if (device.isRxt())
                list.add(device.getUid());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);

        controllerSpinner.setAdapter(arrayAdapter);

        if (list.size() == 0) {
            toast("No Device Found!");
        }
    }


    void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void EventReceived(final RxlStatusEvent event) {
        log("RxtInstallation RxlStatusEvent " + event);
        try {
            if (tvStatus != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  tvStatus.setText(event.getCommandString());
                    }
                });
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
