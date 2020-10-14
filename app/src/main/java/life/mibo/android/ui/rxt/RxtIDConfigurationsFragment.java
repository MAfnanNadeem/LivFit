package life.mibo.android.ui.rxt;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import life.mibo.android.R;
import life.mibo.android.core.Prefs;
import life.mibo.android.models.login.Member;
import life.mibo.android.ui.base.BaseFragment;
import life.mibo.android.ui.devices.DeviceScanFragment;
import life.mibo.android.ui.main.Navigator;
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
        //View back = view.findViewById(R.id.btn_back);
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

        install.setOnClickListener(v -> {
            if (TextUtils.isEmpty(controllerUid)) {
                toast("Please select controller");
                return;
            }
            resetController(controllerUid);
        });


        setHasOptionsMenu(true);
        return view;
    }

    private void resetController(String controller) {
        Member prefs = Prefs.get(getContext()).getMember();
        if (prefs == null)
            return;
        if (prefs.isSuperTrainer()) {
            Single.fromCallable(() -> {
                CommunicationManager.getInstance().onRxtIdConfigurations(new ChangeColorEvent(new Device(), controller));
                return "";
            }).subscribeOn(Schedulers.io()).doOnError(throwable -> {

            }).subscribe();

        } else {
            resett(controller);
//            new AlertDialog.Builder(getActivity())
//                    .setTitle(R.string.permission_denied)
//                    .setMessage(R.string.id_config_permission)
//                    .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss()).show();
        }
    }

    private void resett(String controller) {
        Single.fromCallable(() -> {
            CommunicationManager.getInstance().onRxtIdConfigurations(new ChangeColorEvent(new Device(), controller));
            return "";
        }).subscribeOn(Schedulers.io()).doOnError(throwable -> {

        }).subscribe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControllerAdapters();
    }

    void setControllerAdapters() {
        log("setControllerAdapters........");
        ArrayList<Device> devices = SessionManager.getInstance().getUserSession().getDevices();
        log("setControllerAdapters........ " + devices.size());
        ArrayList<String> list = new ArrayList<>();
        for (Device device : devices) {
            if (device.isRxt())
                list.add(device.getUid());
        }
        log("setControllerAdapters........ list " + list.size());
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_devices, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_rxl", true);
            bundle.putInt("is_search_type", DeviceScanFragment.RXT);
            navigate(Navigator.RXT_SCAN, bundle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
