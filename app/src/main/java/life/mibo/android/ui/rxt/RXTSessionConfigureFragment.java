package life.mibo.android.ui.rxt;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import life.mibo.android.R;
import life.mibo.android.core.API;
import life.mibo.android.core.Prefs;
import life.mibo.android.models.circuits.Circuit;
import life.mibo.android.models.circuits.CircuitResponse;
import life.mibo.android.models.circuits.SearchCircuitPost;
import life.mibo.android.models.login.Member;
import life.mibo.android.models.program.Program;
import life.mibo.android.models.workout.SearchWorkout;
import life.mibo.android.models.workout.SearchWorkoutPost;
import life.mibo.android.models.workout.Workout;
import life.mibo.android.ui.base.BaseFragment;
import life.mibo.android.ui.base.ItemClickListener;
import life.mibo.android.ui.main.Navigator;
import life.mibo.android.ui.rxt.model.Island;
import life.mibo.android.ui.rxt.model.Tile;
import life.mibo.android.ui.rxt.parser.RxtProgram;
import life.mibo.android.ui.rxt.score.SelectIslandDialog;
import life.mibo.android.ui.select_program.ProgramDialog;
import life.mibo.android.utils.Toasty;
import life.mibo.hardware.SessionManager;
import life.mibo.views.CircleView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RXTSessionConfigureFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rxt_select_island, container, false);
    }

    WorkoutAdapter workoutAdapter;
    View emptyView, next;
    //SwitchCompat emsSwitch;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeToRefresh;
    boolean isRefreshing = false;
    boolean isEmsChecked = false;
    boolean isMultiChecked = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.tv_empty);
        //View back = view.findViewById(R.id.btn_back);
        next = view.findViewById(R.id.btn_next);
        //emsSwitch = view.findViewById(R.id.switch_ems);

        setSwipeRefreshColors(swipeToRefresh);

        //setupViews();

        //back.setOnClickListener(v -> navigate(Navigator.CLEAR_HOME, null));
        next.setOnClickListener(v -> nextClicked());
//        emsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            isEmsChecked = isChecked;
//            getWorkouts(isEmsChecked);
//        });

        swipeToRefresh.setOnRefreshListener(() -> {
            getWorkouts(isEmsChecked, isMultiChecked);
        });

        //getWorkouts(isEmsChecked, isMultiChecked);
        setHasOptionsMenu(true);
        next.setVisibility(View.INVISIBLE);
        islandDialog();
    }

    private int islandId = 0;

    private SelectIslandDialog selectIslandDialog;

    void islandDialog() {
        int id = Prefs.get(getContext()).get("island_id_", 0);
        if (id > 0) {
            getWorkouts(isEmsChecked, isMultiChecked);
            return;
        }
        selectIslandDialog = new SelectIslandDialog(getActivity(), (island, position) -> {
            if (island != null && island.getId() != null) {
                islandId = island.getId();
                if (selectIslandDialog != null)
                    selectIslandDialog.dismiss();
                getWorkouts(isEmsChecked, isMultiChecked);
                Prefs.get(getContext()).set(Prefs.ISLAND_ID, islandId);
                Prefs.get(getContext()).set(Prefs.ISLAND_IMAGE, island.getIslandImage());
                Prefs.get(getContext()).set(Prefs.ISLAND_NAME, island.getName());
                Prefs.get(getContext()).set(Prefs.ISLAND_WIDTH, "" + island.getIslandWidth());
                Prefs.get(getContext()).set(Prefs.ISLAND_HEIGHT, "" + island.getIslandHeight());

            }
        });

        selectIslandDialog.show();
    }


    private void getWorkouts(boolean ems, boolean isMulti) {
        String type = ems ? "ems,rxt" : "rxt";
        Member trainer = Prefs.get(getContext()).getMember();
        if (trainer == null) {
            Toasty.info(getContext(), "Trainer is Null, please login again").show();
            return;
        }
        //String island = Prefs.get(getContext()).get("rxt_island");
        //log("okhttp Trainer "+trainer);

        SearchWorkoutPost data = new SearchWorkoutPost(new SearchWorkoutPost.Data(type, "" + trainer.getId(), "1", "50", trainer.isMember() ? "member" : "trainer", trainer.getLocationID(), "", ""), trainer.getAccessToken());
        getDialog().show();
        Call<SearchWorkout> api = API.Companion.getRequest().getApi().searchWorkout(data);
        api.enqueue(new Callback<SearchWorkout>() {
            @Override
            public void onResponse(@NonNull Call<SearchWorkout> call, @NonNull Response<SearchWorkout> response) {
                isRefreshing = false;
                updateRefresh();
                try {
                    getDialog().dismiss();
                    SearchWorkout workout = response.body();
                    if (workout != null && "Success".equals(workout.getStatus())) {
                        if (workout.getData() != null) {
                            parseData(workout.getData().getWorkout());
                            return;
                        }
                    }

                    Toasty.info(getContext(), R.string.no_data_found).show();
                } catch (Exception e) {
                    if (isAdded())
                        Toasty.info(getContext(), R.string.no_data_found).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<SearchWorkout> call, @NonNull Throwable t) {
                isRefreshing = false;
                t.printStackTrace();
                updateRefresh();
                getDialog().dismiss();
                Toasty.info(getContext(), R.string.error_occurred).show();
            }
        });
    }

    private void getCircuits(boolean ems, boolean isMulti) {
        String type = ems ? "ems,rxt" : "rxt";
        Member trainer = Prefs.get(getContext()).getMember();
        if (trainer == null) {
            Toasty.info(getContext(), "Trainer is Null, please login again").show();
            return;
        }
        //String island = Prefs.get(getContext()).get("rxt_island");
        //log("okhttp Trainer "+trainer);

        SearchCircuitPost data = new SearchCircuitPost(new SearchCircuitPost.Data(type, "" + trainer.getId(), "1", "50", trainer.isMember() ? "member" : "trainer", trainer.getLocationID(), "", ""), trainer.getAccessToken());
        getDialog().show();
        Call<CircuitResponse> api = API.Companion.getRequest().getApi().getCircuits(data);
        api.enqueue(new Callback<CircuitResponse>() {
            @Override
            public void onResponse(@NonNull Call<CircuitResponse> call, @NonNull Response<CircuitResponse> response) {
                log("getCircuits onResponse " + response.body());
                isRefreshing = false;
                updateRefresh();
                try {
                    getDialog().dismiss();
                    CircuitResponse workout = response.body();
                    if (workout != null && "Success".equals(workout.getStatus())) {
                        if (workout.getData() != null) {
                            parseCircuit(workout.getData().getCircuits());
                            return;
                        }
                    }

                    Toasty.info(getContext(), R.string.no_data_found).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isAdded())
                        Toasty.info(getContext(), R.string.no_data_found).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CircuitResponse> call, @NonNull Throwable t) {
                log("getCircuits onFailure " + t);
                isRefreshing = false;
                t.printStackTrace();
                updateRefresh();
                getDialog().dismiss();
                Toasty.info(getContext(), R.string.error_occurred).show();
            }
        });
    }

    void updateRefresh() {
        if (swipeToRefresh != null)
            swipeToRefresh.setRefreshing(isRefreshing);
    }

    private void nextClicked() {
        try {
            if (isCircuitMode) {
                //List<Workout> workouts = islandAdapter.getChecked();
                return;
            }
            List<Workout> workouts = workoutAdapter.getChecked();
            if (workouts.isEmpty()) {
                //Snackbar.make(next, "Please select at-least one island", Snackbar.LENGTH_LONG).show();
                Toasty.snackbar(next, "Please select at-least one island");
                return;
            }
            for (Workout i : workouts) {
                if (i.getProgram() == null) {
                    // Toasty.snackbarTop(next, "Please assign program to " + i.getName());
                    //return;
                }
                if (i.getColor() == 0) {
                    Toasty.snackbar(recyclerView, "Please assign color to " + i.getName());
                    return;
                }
                //if (i.getTiles() == null || i.getTiles().isEmpty()) {
                //   Toasty.snackbarTop(recyclerView, "Please assign tiles to " + i.getName());
                //   return;
                //  }
            }
            SessionManager.getInstance().addWorkouts(Collections.singletonList(workouts));
            navigate(Navigator.CLEAR_HOME, null);
            //getCompositionRoot().getScreensNavigator().toRxtIslandsPlay();
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }

    void parseData(List<Workout> list) {
        if (list == null || list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        workoutAdapter = new WorkoutAdapter(list, this::onItemClick, isEmsChecked, isMultiChecked);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(workoutAdapter);
    }

    void parseCircuit(List<Circuit> list) {
        if (list == null || list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        workoutAdapter = new WorkoutAdapter(list, this::onCircuitItemClick);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(workoutAdapter);
    }

    void setupViews() {
//        List<Island> islands = RXTUtils.getIslands(getContext());
//
//        if (islands.isEmpty()) {
//            emptyView.setVisibility(View.VISIBLE);
//        }
//        islandAdapter = new IslandAdapter(islands, this::onItemClick);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerView.setAdapter(islandAdapter);
    }

    void onItemClick(Object object, int position) {
        if (object instanceof Workout) {
            Workout island = (Workout) object;
            if (position == IslandAdapter.PLAY) {
                //playSequence(island.getTiles());
            } else if (position == IslandAdapter.COLOR) {
                showColorDialog(island);
            } else if (position == IslandAdapter.PROGRAM) {
                // showProgramDialog(island);
            } else if (position == IslandAdapter.CHECK) {
                updateCheck(island);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("workout_data", island);
                bundle.putInt("selected_color", island.getColor());
                navigate(Navigator.RXT_START_WORKOUT, bundle);
                //getCompositionRoot().getScreensNavigator().toRxtSingleSession(bundle);
                //deleteIsland(island, position);
            }
        } else if (object instanceof Island) {
            Island island = (Island) object;
            if (position == IslandAdapter.PLAY) {
                //playSequence(island.getTiles());
            } else if (position == IslandAdapter.COLOR) {
                //showColorDialog(island);
            } else if (position == IslandAdapter.PROGRAM) {
                //  showProgramDialog(island);
            } else if (position == IslandAdapter.CHECK) {
                //  updateCheck(island);
            } else {
                //deleteIsland(island, position);
            }
        }
    }

    void onCircuitItemClick(Object object, int position) {
        if (object instanceof Circuit) {
            Circuit island = (Circuit) object;
            if (position == IslandAdapter.PLAY) {
                //playSequence(island.getTiles());
            } else if (position == IslandAdapter.COLOR) {
                showColorDialog(island);
            } else if (position == IslandAdapter.PROGRAM) {
                // showProgramDialog(island);
            } else if (position == IslandAdapter.CHECK) {
                updateCheck(island);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("workout_data", island);
                bundle.putInt("selected_color", island.getColor());
                navigate(Navigator.RXT_START_WORKOUT, bundle);
                //getCompositionRoot().getScreensNavigator().toRxtSingleSession(bundle);
                //deleteIsland(island, position);
            }
        }
    }


    void deleteIsland(Island island, int position) {
//        new AlertDialog.Builder(getContext()).setTitle("Delete?")
//                .setMessage("Delete?")
//                .setPositiveButton("Delete", (dialog, which) -> {
//                    Prefs.get(getContext()).remove(island.getKey());
//                    Prefs.get(getContext()).remove(island.getKey() + "_name");
//                    Toasty.snackbar(recyclerView, "Deleted!");
//                    islandAdapter.remove(position);
//                })
//                .setNeutralButton("No", (dialog, which) -> {
//
//                })
//                .show();
    }

    void playSequence(final List<Tile> tiles) {
        RXTUtils.playSequence(tiles);
    }

    // ProgramDialog colorDialog;

    void showColorDialog(Workout island) {
//        if (colorDialog == null) {
//            colorDialog = new ColorDialog(getContext(), new ArrayList<>(), (item, position) -> {
//                if (item != null)
//                    updateColor(island, item, position);
//            }, 0).showColors();
//        }
//
//        colorDialog.showColors();

        new ProgramDialog(requireContext(), new ArrayList<>(), (program, position) -> {
            if (program != null)
                updateColor(island, program.getId(), position);
        }, 2).showColors();

//        new ColorDialog(getContext(), new ArrayList<>(), (item, position) -> {
//            if (item != null)
//                updateColor(island, item, position);
//        }, 0).showColors();
    }

    void showColorDialog(Circuit island) {
        new ProgramDialog(requireContext(), new ArrayList<>(), (program, position) -> {
            if (program != null)
                updateColor(island, program.getId(), position);
        }, 2).showColors();

    }

    void updateCheck(Workout island) {
        try {
            workoutAdapter.updateCheck(island);
        } catch (Exception e) {

        }
    }

    void updateCheck(Circuit island) {
        try {
            workoutAdapter.updateCheck(island);
        } catch (Exception e) {

        }
    }

    private void updateColor(Workout island, int color, int alpha) {
        try {
            workoutAdapter.updateColor(island, color, alpha);
        } catch (Exception e) {

        }
    }

    private void updateColor(Circuit island, int color, int alpha) {
        try {
            workoutAdapter.updateColor(island, color, alpha);
        } catch (Exception e) {

        }
    }

    private void showProgramDialog(Workout island) {
        List<Program> programs = new ArrayList<>();

        new ProgramDialog(requireContext(), new ArrayList<>(), (program, position) -> {
            if (program != null)
                updateColor(island, program.getId(), position);
        }, 1).showColors();

    }


    private void updateProgram(Workout island, RxtProgram program) {
        try {
            //  islandAdapter.updateProgram(island, program);
        } catch (Exception e) {

        }
    }

    private void updateProgram(Workout island, Program program) {
        try {
            //  islandAdapter.updateProgram(island, program);
        } catch (Exception e) {

        }
    }

    public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutHolder> {

        public static final int PLAY = 1000;
        public static final int COLOR = 2000;
        public static final int PROGRAM = 3000;
        public static final int CHECK = 4000;
        public static final int ITEM = 5000;

        List<Workout> islands = new ArrayList<>();
        List<Circuit> circuits = new ArrayList<>();
        ItemClickListener<Object> listener;
        private boolean isEms = false;
        private boolean isMulti = false;
        private boolean isCircuit = false;

        public WorkoutAdapter(List<Workout> list, ItemClickListener<Object> listener, boolean isEms, boolean isMulti) {
            islands.addAll(list);
            this.listener = listener;
            this.isEms = isEms;
            this.isMulti = isMulti;
            isCircuit = false;
        }

        public WorkoutAdapter(List<Circuit> list, ItemClickListener<Object> listener) {
            circuits.addAll(list);
            this.listener = listener;
            this.isEms = isEms;
            this.isMulti = isMulti;
            isCircuit = true;
        }

        @NonNull
        @Override
        public WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WorkoutHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rxt_play_island2, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutHolder holder, int position) {
            if (isCircuit)
                holder.bind(circuits.get(position), listener, isEms, isMulti);
            else holder.bind(islands.get(position), listener, isEms, isMulti);
        }

        @Override
        public int getItemCount() {
            if (isCircuit) return circuits.size();
            return islands.size();
        }

        void remove(int pos) {
            if (islands.isEmpty())
                return;
            islands.remove(pos);
            notifyItemRemoved(pos);
        }

        List<Workout> getChecked() {
            List<Workout> checked = new ArrayList<>();
            for (Workout island : islands) {
                if (island.isSelected())
                    checked.add(island);
            }
            return checked;
        }

        public void updateCheck(Workout island) {
            int pos = -1;
            int count = 0;
            for (Workout i : islands) {
                if (i.getId() == island.getId()) {
                    i.updateCheck();
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateCheck(Circuit island) {
            int pos = -1;
            int count = 0;
            for (Circuit i : circuits) {
                if (i.getId() == island.getId()) {
                    i.updateCheck();
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateColor(Workout island, int color, int alpha) {
            int pos = -1;
            int count = 0;
            for (Workout i : islands) {
                if (i.getId() == island.getId()) {
                    i.setColor(color);
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateColor(Circuit island, int color, int alpha) {
            int pos = -1;
            int count = 0;
            for (Circuit c : circuits) {
                if (c.getId() == island.getId()) {
                    c.setColor(color);
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateProgram(Workout island, RxtProgram program) {
            int pos = -1;
            int count = 0;
            for (Workout i : islands) {
                if (i.getId() == island.getId()) {
                    i.setRxtProgram(program);
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        class WorkoutHolder extends RecyclerView.ViewHolder {
            TextView title, tiles, dur;
            View play, program, check;
            ImageView imageView;
            CheckBox checkBox;
            CircleView color;
            ChipGroup chips;

            public WorkoutHolder(View view) {
                super(view);
                tiles = view.findViewById(R.id.tv_desc);
                title = view.findViewById(R.id.tv_name);
                dur = view.findViewById(R.id.tv_duration);
                imageView = view.findViewById(R.id.imageView);
                chips = view.findViewById(R.id.chip_group);
                program = view.findViewById(R.id.btn_program);
                play = view.findViewById(R.id.btn_test);
                color = view.findViewById(R.id.btn_color);
                check = view.findViewById(R.id.btn_check);
                checkBox = view.findViewById(R.id.btn_checkbox);
            }

            void bind(Workout island, ItemClickListener<Object> listener, boolean isEms, boolean isMulti) {
                if (island == null)
                    return;
                title.setText(island.getName());
                tiles.setText(island.getDescription());
                dur.setText(island.getDuration());

                if (isEms)
                    program.setVisibility(View.VISIBLE);
                else program.setVisibility(View.GONE);

                if (isMulti) {
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(island.isSelected());
                } else {
                    checkBox.setVisibility(View.INVISIBLE);
                }
                if (island.getIcon() != null) {
                    Glide.with(imageView).load(island.getIcon()).fitCenter()
                            .error(R.drawable.ic_broken_image).fallback(R.drawable.ic_broken_image).into(imageView);
                }

                if (island.getColor() != 0)
                    color.setCircleColor(island.getColor());
                else color.setCircleColor(Color.RED);


                play.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.PLAY);
                });
                color.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.COLOR);
                });

                if (program != null)
                    program.setOnClickListener(v -> {
                        if (listener != null)
                            listener.onItemClicked(island, IslandAdapter.PROGRAM);
                    });

                check.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.CHECK);
                });

                itemView.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, getAdapterPosition());
                });

                List<String> list = island.getTags();
                if (list != null && !list.isEmpty()) {
                    for (String s : list) {
                        Chip chip = new Chip(chips.getContext());
                        chip.setText(s);
                        chip.setCheckable(false);
                        chip.setCheckedIconVisible(false);
                        chip.setCloseIconVisible(false);
                        chip.setChipIconVisible(false);
                        chip.setClickable(false);
                        chips.addView(chip);
                    }
                }
            }

            void bind(Circuit island, ItemClickListener<Object> listener, boolean isEms, boolean isMulti) {
                if (island == null)
                    return;
                title.setText(island.getName());
                tiles.setText(island.getDescription());
                dur.setText(island.getDuration());

                if (isEms)
                    program.setVisibility(View.VISIBLE);
                else program.setVisibility(View.GONE);

                if (isMulti) {
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(island.isSelected());
                } else {
                    checkBox.setVisibility(View.INVISIBLE);
                }


                if (island.getColor() != 0)
                    color.setCircleColor(island.getColor());
                else color.setCircleColor(Color.RED);


                play.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.PLAY);
                });
                color.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.COLOR);
                });

                if (program != null)
                    program.setOnClickListener(v -> {
                        if (listener != null)
                            listener.onItemClicked(island, IslandAdapter.PROGRAM);
                    });

                check.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, IslandAdapter.CHECK);
                });

                itemView.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onItemClicked(island, getAdapterPosition());
                });

                List<Workout> list = island.getWorkout();
                if (list != null && !list.isEmpty()) {
                    Workout ws = list.get(0);
                    if (ws.getIcon() != null) {
                        Glide.with(imageView).load(ws.getIcon()).fitCenter().error(R.drawable.ic_broken_image).fallback(R.drawable.ic_broken_image).into(imageView);
                    }

                    for (Workout s : list) {
                        Chip chip = new Chip(chips.getContext());
                        chip.setText(s.getName());
                        chip.setCheckable(false);
                        chip.setCheckedIconVisible(false);
                        chip.setCloseIconVisible(false);
                        chip.setChipIconVisible(false);
                        chip.setClickable(false);
                        chips.addView(chip);
                    }
                }
            }

        }

    }

    public class IslandAdapter extends RecyclerView.Adapter<IslandHolder> {

        public static final int PLAY = 1000;
        public static final int COLOR = 2000;
        public static final int PROGRAM = 3000;
        public static final int CHECK = 4000;
        public static final int ITEM = 5000;

        List<Island> islands = new ArrayList<>();
        ItemClickListener<Island> listener;

        public IslandAdapter(List<Island> list, ItemClickListener<Island> listener) {
            islands.addAll(list);
            this.listener = listener;
        }

        @NonNull
        @Override
        public IslandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IslandHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rxt_play_island, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull IslandHolder holder, int position) {
            holder.bind(islands.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return islands.size();
        }

        void remove(int pos) {
            if (islands.isEmpty())
                return;
            islands.remove(pos);
            notifyItemRemoved(pos);
        }

        List<Island> getChecked() {
            List<Island> checked = new ArrayList<>();
            for (Island island : islands) {
                if (island.isSelected())
                    checked.add(island);
            }
            return checked;
        }

        public void updateCheck(Island island) {
            int pos = -1;
            int count = 0;
            for (Island i : islands) {
                if (i.getKey().equals(island.getKey())) {
                    i.updateCheck();
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateColor(Island island, int color, int alpha) {
            int pos = -1;
            int count = 0;
            for (Island i : islands) {
                if (i.getKey().equals(island.getKey())) {
                    i.setColor(color);
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }

        public void updateProgram(Island island, RxtProgram program) {
            int pos = -1;
            int count = 0;
            for (Island i : islands) {
                if (i.getKey().equals(island.getKey())) {
                    i.setProgram(program);
                    pos = count;
                    break;
                }
                count++;
            }
            if (pos >= 0)
                notifyItemChanged(pos);
        }
    }

    class IslandHolder extends RecyclerView.ViewHolder {
        TextView title, tiles;
        View play, program, check;
        CheckBox checkBox;
        CircleView color;

        public IslandHolder(View view) {
            super(view);
            tiles = view.findViewById(R.id.tv_tiles);
            title = view.findViewById(R.id.tv_name);
            program = view.findViewById(R.id.btn_program);
            play = view.findViewById(R.id.btn_test);
            color = view.findViewById(R.id.btn_color);
            check = view.findViewById(R.id.btn_check);
            checkBox = view.findViewById(R.id.btn_checkbox);
        }

        void bind(Island island, ItemClickListener<Island> listener) {
            if (island == null)
                return;
            title.setText(island.getName());
            tiles.setText("Tiles: " + island.getSize());

            if (island.getColor() != 0)
                color.setCircleColor(island.getColor());
            else color.setCircleColor(Color.RED);
            checkBox.setChecked(island.isSelected());

            play.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, IslandAdapter.PLAY);
            });
            color.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, IslandAdapter.COLOR);
            });

            program.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, IslandAdapter.PROGRAM);
            });

            check.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, IslandAdapter.CHECK);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, getAdapterPosition());
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_reactions_fragment, menu);

//        try {
//            SwitchCompat mySwitch = menu.findItem(R.id.action_ems).getActionView().findViewById(R.id.switch_ems_menu);
//            mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                isEmsChecked = isChecked;
//                getWorkouts(isEmsChecked, isMultiChecked);
//            });
//            AppCompatCheckBox checkBox = menu.findItem(R.id.action_ems_check).getActionView().findViewById(R.id.switch_ems_menu);
//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                isMultiChecked = isChecked;
//                getWorkouts(isEmsChecked, isMultiChecked);
//            });
//        } catch (Exception ee) {
//            ee.printStackTrace();
//        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        log("onOptionsItemSelected " + item.getItemId());
        if (item.getItemId() == R.id.action_filter) {
            new RXTFilterDialog(getContext(), "0-0", (data, position) -> {
                onItemClick(position);
            }).show();
            return true;
        }

        if (item.getItemId() == R.id.action_ems) {
            log("action ems " + item.isChecked());

            return true;
        }
        if (item.getItemId() == R.id.switch_ems_menu) {
            log("action switch ems " + item.isChecked());
            return true;
        }
//        if (item.getItemId() == R.id.action_devices) {
//            navigate(R.id.navigation_devices, DeviceScanFragment.Companion.rxtBundle());
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    boolean isCircuitMode = false;

    void onItemClick(int option) {
        if (option == 10) {
            getWorkouts(isEmsChecked, isMultiChecked);
            isCircuitMode = false;
        }
        if (option == 20) {
            getCircuits(isEmsChecked, isMultiChecked);
            isCircuitMode = true;
        }
    }
}
