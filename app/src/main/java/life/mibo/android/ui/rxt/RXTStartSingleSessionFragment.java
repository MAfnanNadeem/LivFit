package life.mibo.android.ui.rxt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.halilibo.bvpkotlin.BetterVideoPlayer;
import com.halilibo.bvpkotlin.VideoCallback;
import com.halilibo.bvpkotlin.VideoProgressCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import life.mibo.android.R;
import life.mibo.android.core.API;
import life.mibo.android.core.Prefs;
import life.mibo.android.models.base.ResponseStatus;
import life.mibo.android.models.login.Member;
import life.mibo.android.models.rxt.GetIslandPost;
import life.mibo.android.models.rxt.GetIslandTiles;
import life.mibo.android.models.workout.EMS;
import life.mibo.android.models.workout.RXT;
import life.mibo.android.models.workout.SaveMemberScores;
import life.mibo.android.models.workout.Workout;
import life.mibo.android.ui.base.BaseFragment;
import life.mibo.android.ui.base.ItemClickListener;
import life.mibo.android.ui.rxt.model.Island;
import life.mibo.android.ui.rxt.model.Tile;
import life.mibo.android.ui.rxt.parser.RXTManager;
import life.mibo.android.ui.rxt.parser.RxtIsland;
import life.mibo.android.ui.rxt.parser.RxtProgram;
import life.mibo.android.ui.rxt.parser.RxtTile;
import life.mibo.android.ui.rxt.parser.core.RxtListener;
import life.mibo.android.ui.rxt.score.ScoreDialog;
import life.mibo.android.ui.rxt.score.ScoreItem;
import life.mibo.android.utils.Toasty;
import life.mibo.android.utils.Utils;
import life.mibo.hardware.SessionManager;
import life.mibo.hardware.events.RxlStatusEvent;
import life.mibo.hardware.events.RxlTapEvent;
import life.mibo.hardware.events.RxtStatusEvent;
import life.mibo.views.PlayButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import life.mibo.miboproapp.network.rxt.RXTManager;

public class RXTStartSingleSessionFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rxt_play_island_new, container, false);
    }


    TextView timer, islandName, islandTiles, workoutName, workoutDesc, tvSpeed, tvBlocks;
    ChipGroup chipGroup;
    int totalTime = 60;
    PlayButton playButton;
    CheckBox checkVoicePrompt;
    BetterVideoPlayer videoPlayer;
    ProgressBar mediaPlayerProgress;
    private Workout workout;
    private RXT rxtProgram;
    private EMS emsProgram;
    ImageView islandImage;
    private float speed = 1.0f;
    private float interval = 0.1f;
    private int islandId = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoPlayer = view.findViewById(R.id.player);
        timer = view.findViewById(R.id.tv_timer);
        islandImage = view.findViewById(R.id.iv_island);
        islandName = view.findViewById(R.id.tv_island_name);
        islandTiles = view.findViewById(R.id.tv_island_tiles);
        workoutName = view.findViewById(R.id.tv_name);
        workoutDesc = view.findViewById(R.id.tv_desc);
        tvSpeed = view.findViewById(R.id.tv_plus_minus);
        tvBlocks = view.findViewById(R.id.tv_blocks);
        chipGroup = view.findViewById(R.id.chip_group);
        checkVoicePrompt = view.findViewById(R.id.check_voice_prompt);
        mediaPlayerProgress = view.findViewById(R.id.mediaPlayer_progress);

        // View back = view.findViewById(R.id.btn_back);
        playButton = view.findViewById(R.id.btn_play);
        // View stop = view.findViewById(R.id.btn_stop);
        View plus = view.findViewById(R.id.btn_plus);
        View minus = view.findViewById(R.id.btn_minus);

        // back.setOnClickListener(v -> navigate(Navigator.CLEAR_HOME, null));
        plus.setOnClickListener(v -> plusClicked());
        minus.setOnClickListener(v -> minusClicked());
        playButton.setOnClickListener(v -> {
            if (rxtProgram != null)
                getTilesApi(rxtProgram.getRXTIsland());
        });
        //stop.setOnClickListener(v -> onStopProgram());
        if (getArguments() != null) {
            workout = (Workout) getArguments().getSerializable("workout_data");
            int color = getArguments().getInt("selected_color", 0);
            if (color != 0)
                selectedColor = color;
        }
        if (workout != null) {
            setupWorkout(workout);
        }
        playButton.setPlay(false);
    }

    void plusClicked() {
        if (isProgramStarted)
            return;
        if (speed < 3.0) {
            speed += interval;
            tvSpeed.setText(String.format("%.01f", speed));
        }
    }

    void minusClicked() {
        if (isProgramStarted)
            return;
        if (speed > 0.1) {
            speed -= interval;
            tvSpeed.setText(String.format("%.01f", speed));
        }
    }

    private boolean isVoicePrompt = false;
    //private boolean isVoiceEnabled = false;

    void setupWorkout(Workout workout) {
        String url = workout.getVideoLink();
        if (url != null && url.length() > 1)
            videoPlayer.setSource(Uri.parse(url));

        rxtProgram = workout.getRxt();
        timer.setText(workout.getDuration());
        totalTime = workout.getDurationSec();
        if (rxtProgram != null) {
            RXT.RXTIsland island = rxtProgram.getRXTIsland();
            if (Utils.isEmpty(rxtProgram.getVoicePrompt())) {
                checkVoicePrompt.setVisibility(View.INVISIBLE);
                isVoicePrompt = false;
            } else {
                checkVoicePrompt.setVisibility(View.VISIBLE);
                isVoicePrompt = true;
            }

            if (island != null) {
                List<RXT.RXTBlock> blocks = rxtProgram.getBlocks();
                islandId = island.getID();
                islandName.setText(island.getName());
                islandTiles.setText("Tiles " + island.getTotalTiles() + " \n " + island.getIslandWidth() + "x" + island.getIslandHeight());
                tvBlocks.setText("Blocks:");

                if (blocks != null && !blocks.isEmpty()) {
                    for (RXT.RXTBlock b : blocks) {
                        Chip chip = new Chip(chipGroup.getContext());
                        chip.setText(b.getRXTType() + " x " + b.getRounds() + " - " + b.getRXTTotalDuration() + " sec");
                        chip.setCheckable(false);
                        chip.setCheckedIconVisible(false);
                        chip.setCloseIconVisible(false);
                        chip.setChipIconVisible(false);
                        chip.setClickable(false);
                        chipGroup.addView(chip);
                    }
                }
                log("Image Glide " + island.getIslandImage());
                Glide.with(getContext()).load(island.getIslandImage()).error(R.drawable.ic_broken_image_black_24dp).fallback(R.drawable.ic_broken_image_black_24dp).into(islandImage);
            }
            workoutName.setText(workout.getName());
            workoutDesc.setText(workout.getDescription());

        }

        videoPlayer.setCallback(new VideoCallback() {
            @Override
            public void onStarted(@NotNull BetterVideoPlayer betterVideoPlayer) {
                log("videoPlayer onStarted ");
            }

            @Override
            public void onPaused(@NotNull BetterVideoPlayer betterVideoPlayer) {
                log("videoPlayer onPaused ");
            }

            @Override
            public void onPreparing(@NotNull BetterVideoPlayer betterVideoPlayer) {
                log("videoPlayer onPreparing ");
            }

            @Override
            public void onPrepared(@NotNull BetterVideoPlayer betterVideoPlayer) {
                log("videoPlayer onPrepared ");
            }

            @Override
            public void onBuffering(int i) {
                log("videoPlayer onBuffering ");
            }

            @Override
            public void onError(@NotNull BetterVideoPlayer betterVideoPlayer, @NotNull Exception e) {
                log("videoPlayer onError " + e);
            }

            @Override
            public void onCompletion(@NotNull BetterVideoPlayer betterVideoPlayer) {
                log("videoPlayer onCompletion ");
            }

            @Override
            public void onToggleControls(@NotNull BetterVideoPlayer betterVideoPlayer, boolean b) {
                log("videoPlayer onToggleControls " + b);
            }
        });

        videoPlayer.setProgressCallback(new VideoProgressCallback() {
            @Override
            public void onProgressUpdate(int i, int i1) {
                log("videoPlayer onProgressUpdate " + i + " : " + i);
            }
        });


        videoPlayer.start();
    }

    void updateTimer(String time) {
        if (timer != null) {
            getActivity().runOnUiThread(() -> timer.setText(time));
        }
    }

    private int selectedColor = Color.RED;
    private boolean isProgramStarted = false;

    private boolean onStartRxt(String tiles) {
        // RXTManager.Companion.getInstance().with(RX)
        if (tiles != null && tiles.length() > 10) {
            String[] array = tiles.split(",");
            List<RxtTile> rxtTiles = new ArrayList<>();
            String conUid = "";
            for (String s : array) {
                String[] uid = s.split("-");
                if (uid.length > 1) {
                    rxtTiles.add(new RxtTile(uid[0], getInt(uid[1])));
                }
            }

            if (rxtTiles.size() > 0) {
                List<RxtIsland> islands = new ArrayList<>();
                islands.add(new RxtIsland(11, RxtProgram.Companion.from(rxtProgram), rxtTiles, workoutName.getText().toString(), "Player " + 11, selectedColor));
                RXTManager.Companion.getInstance().with(islands, listener).startNow(totalTime, speed);
                isProgramStarted = true;
                playButton.setPlay(true);
                return true;
            }
        }

        return false;
    }

    private void onStartProgram(List<RxtTile> tiles) {
        // RXTManager.Companion.getInstance().with(RX)
        List<Workout> list = new ArrayList<Workout>((Collection<? extends Workout>) SessionManager.getInstance().getWorkouts());

        List<RxtIsland> islands = new ArrayList<>();
        // List<RxtBlock> blocks = new ArrayList<>();
        //blocks.add(new RxtBlock(500, 30, 1, 0, ""));
        // RxtProgram program = new RxtProgram("Dummy 1", Color.BLUE, 5, blocks);
        // RxtProgram program2 = new RxtProgram("Dummy 1", Color.MAGENTA, 5, blocks);
        // RxtProgram program3 = new RxtProgram("Dummy 1", Color.CYAN, 5, blocks);
        //RxtProgram test = RXTManager.Companion.getInstance().testProgram();
        log("onStartProgram list : " + list);
        int count = 11;
        for (Workout i : list) {
            //List<RxtBlock> blocks = new ArrayList<>();
            //blocks.addAll(i.getProgram());
            //islands.add(new RxtIsland(count, i.getProgram(), i.getRxtTiles(), i.getName(), "Player " + count, i.getColor()));
            islands.add(new RxtIsland(count, RxtProgram.Companion.from(i.getRxt()), tiles, i.getName(), "Player " + count, i.getColor()));
            count++;
            // log("Adding Program " + i.getProgram());
        }
        log("onStartProgram islands : " + islands);
        RXTManager.Companion.getInstance().with(islands, listener).startNow(totalTime);
    }

    void getTilesApi(RXT.RXTIsland island) {
        if (isProgramStarted) {
            onStopProgram();
            return;
        }
        log("getTilesApi " + island);
        if (island == null) {
            Toasty.info(getContext(), "Invalid RXT Island").show();
            return;
        }
        Member trainer = Prefs.get(getContext()).getMember();
        if (trainer == null)
            return;
        getDialog().show();
        String loc = trainer.getLocationID();
        if (loc == null)
            loc = "0";

        GetIslandPost post = new GetIslandPost(new GetIslandPost.Data(islandId, trainer.getId(), loc), trainer.getAccessToken());
        API.Companion.getRequest().getApi().getIslandTiles(post).enqueue(new Callback<GetIslandTiles>() {
            @Override
            public void onResponse(@NonNull Call<GetIslandTiles> call, @NonNull Response<GetIslandTiles> response) {
                getDialog().dismiss();
                //Toasty.info(getContext(), "Response " + response.body()).show();
                GetIslandTiles body = response.body();
                if (body != null && body.getData() != null) {
                    String tiles = body.getData().getTiles();
                    if (tiles != null && tiles.length() > 10) {
                        onStartRxt(tiles);
                        return;
                    }
                }

                //log("total " + island.getTotal());
                playButton.setPlay(false);
                ConfigureIslandActivity.Companion.launch(RXTStartSingleSessionFragment.this, island.getName(), island.getID(), island.getX(), island.getY(), island.getTotal());
            }

            @Override
            public void onFailure(@NonNull Call<GetIslandTiles> call, @NonNull Throwable t) {
                getDialog().dismiss();
                Toasty.info(getContext(), "Error " + t.getMessage()).show();
            }
        });
    }

    void saveScore(List<ScoreItem> list) {
        Member trainer = Prefs.get(getContext()).getMember();
        if (trainer == null)
            return;
        //getDialog().show();
        String loc = trainer.getLocationID();
        if (loc == null)
            loc = "0";

        //ateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        List<SaveMemberScores.Score> scores = new ArrayList<>();
        if (list != null) {
            for (ScoreItem item : list) {
                scores.add(new SaveMemberScores.Score(date, "rxt", "" + item.getHits(), loc, "" + trainer.id(), "" + item.getMissed(), ""+item.getTotal(), "" + trainer.id(), "0", "-", "" + workout.getId()));
            }
        }
        if (scores.isEmpty())
            return;
        SaveMemberScores post = new SaveMemberScores(new SaveMemberScores.Data(scores), trainer.getAccessToken());
        API.Companion.getRequest().getApi().saveScore(post).enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(@NonNull Call<ResponseStatus> call, @NonNull Response<ResponseStatus> response) {
                getDialog().dismiss();
                //Toasty.info(getContext(), "Response " + response.body()).show();
                ResponseStatus body = response.body();
                if (body != null && body.isSuccess()) {
                    Toasty.info(getContext(), "Session Saved!").show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseStatus> call, @NonNull Throwable t) {
                getDialog().dismiss();
                Toasty.info(getContext(), "Error " + t.getMessage()).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConfigureIslandActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String tiles = "";
                if (data != null)
                    tiles = data.getStringExtra("tiles_config");

                if (tiles == null || tiles.length() < 10) {
                    if (rxtProgram != null)
                        getTilesApi(rxtProgram.getRXTIsland());
                } else {
                    onStartRxt(tiles);
                }
            }
        }
    }

    int getInt(@Nullable String s) {
        try {
            if (s != null)
                return Integer.parseInt(s);

        } catch (Exception e) {

        }
        return 0;
    }

    RxtListener listener = new RxtListener() {

        @Override
        public void onTime(int id, long t) {
            log("RxtListener onTime " + t);
            try {
                int time = (int) (totalTime - t);
                String txt = String.format("%02d : %02d", ((int) time / 60), ((int) time % 60));
                log("onTime " + time + " - " + txt);
                updateTimer(txt);
            } catch (Exception e) {
                log("onTime eeee " + e);
                e.printStackTrace();
            }
        }

        @Override
        public void onDispose() {
            log("RxtListener onDispose ");
        }

        @Override
        public void startProgram(int cycle, int duration) {
            log("RxtListener startProgram " + cycle);
            try {
                playVoicePrompt();
            } catch (Exception ee) {
                log("RxtListener playVoicePrompt error " + ee);
                ee.printStackTrace();
            }
        }

        @Override
        public void nextCycle(int cycle, int pause, int duration) {
            log("RxtListener nextCycle " + cycle);
        }

        @Override
        public void sendColorEvent(@NotNull RxtTile device, int color, int action, int playerId, boolean observe) {
            log("RxtListener sendColorEvent " + device);
        }

        @Override
        public void sendDelayColorEvent(@NotNull RxtTile device, int color, int action, int playerId, int delay, boolean observe) {
            log("RxtListener sendDelayColorEvent " + device);
        }

        @Override
        public void endProgram(int cycle, int duration) {
            log("RxtListener endProgram " + cycle);
            //onStopProgram();
            try {
                isProgramStarted = false;
                showScoreDialog();
                stopVoicePrompt();
                playButton.setPlay(false);
            } catch (Exception ee) {

            }
        }
    };

    private MediaPlayer mediaPlayer;
    private int mediaFileDuration;

    private void playVoicePrompt() {
        log("playVoicePrompt isVoicePrompt " + isVoicePrompt);
        if (isVoicePrompt) {
            getActivity().runOnUiThread(() -> {
                if (checkVoicePrompt.isChecked()) {
                    log("playVoicePrompt checkVoicePrompt enable");
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                            log("setOnBufferingUpdateListener mediaPlayer " + percent);
                            isMediaPlaying = true;
                            mediaPlayerProgress.setSecondaryProgress(percent);

                        });
                        mediaPlayer.setOnCompletionListener(mp -> {
                            log("setOnCompletionListener mediaPlayer ");
                            mediaPlayerProgress.setProgress(100);
                            isMediaPlaying = false;
                        });
                    } else {
                        // TODO Later
                        mediaPlayer.reset();
                    }

                    try {
                        mediaPlayer.setDataSource(rxtProgram.getVoicePrompt());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaFileDuration = mediaPlayer.getDuration();
                        log("playVoicePrompt mediaPlayer starting....");
                        mediaPlayerProgress.setVisibility(View.VISIBLE);
                        isMediaPlaying = true;
                        mediaProgressUpdater();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void stopVoicePrompt() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                isMediaPlaying = false;
                mediaPlayer.stop();
                mediaPlayerProgress.setProgress(1);
            }
        } catch (Exception e) {

        }
    }

    private final Handler handler = new Handler();
    private boolean isMediaPlaying = false;

    private void mediaProgressUpdater() {
        if (!isMediaPlaying)
            return;
        try {
            if (mediaFileDuration > 0)
                mediaPlayerProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileDuration) * 100));
            else
                mediaPlayerProgress.setProgress(5);
            if (mediaPlayer.isPlaying()) {
                Runnable notification = this::mediaProgressUpdater;
                handler.postDelayed(notification, 1000);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void showScoreDialog() {
        try {
            ArrayList<ScoreItem> list = RXTManager.Companion.getInstance().getScore();
            log("ScoreItem list " + list);
            String name = "Test Program...";
            if (workout != null)
                name = workout.getName();
            String finalName = name;
            requireActivity().runOnUiThread(() -> new ScoreDialog(requireActivity(), finalName, list).show());
            saveScore(list);
        } catch (Exception ee) {
            ee.printStackTrace();
            if (isAdded())
                Toasty.info(getContext(), "Score Error! " + ee.getMessage()).show();
        }
    }

    private void onStopProgram() {
        RXTManager.Companion.getInstance().unregister();
        Toasty.info(getContext(), "Stopped!....").show();
        playButton.setPlay(false);
        isProgramStarted = false;
    }

    private void nextClicked() {
       // getCompositionRoot().getScreensNavigator().toRxtHome();
    }


    void onItemClick(Object object, int position) {
        if (object instanceof Island) {

        }
    }


    void playSequence(final List<Tile> tiles) {
        RXTUtils.playSequence(tiles);
    }

    public class IslandAdapter extends RecyclerView.Adapter<IslandHolder> {

        public static final int TILE = 1001;

        List<Island> devices = new ArrayList<>();
        ItemClickListener<Island> listener;

        public IslandAdapter(List<Island> list, ItemClickListener<Island> listener) {
            devices.addAll(list);
            this.listener = listener;
        }

        @NonNull
        @Override
        public IslandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IslandHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rxt_play, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull IslandHolder holder, int position) {
            holder.bind(devices.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        void remove(int pos) {
            if (devices.isEmpty())
                return;
            devices.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    class IslandHolder extends RecyclerView.ViewHolder {
        TextView tiles, name, program, time;
        View play, view1;

        public IslandHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            tiles = view.findViewById(R.id.tv_tiles);
            program = view.findViewById(R.id.tv_program);
            time = view.findViewById(R.id.tv_time);
            play = view.findViewById(R.id.btn_play);
            view1 = view.findViewById(R.id.constraintLayout);
        }

        void bind(Island island, ItemClickListener<Island> listener) {
            if (island == null)
                return;
            name.setText(island.getName());
            if (island.getProgram() != null) {
                program.setText(island.getProgram().getName());
                time.setText("" + island.getProgram().getTotalDuration() + " Sec");
            } else {
                program.setText("");
                time.setText("");
            }
            tiles.setText("Tiles: " + island.getTileCount());


            int color = island.getColor();
            Drawable background = view1.getBackground();
            if (background instanceof GradientDrawable) {
                // ((ShapeDrawable) background).getPaint().setColor(color);
                //((ShapeDrawable) background).getPaint().setColor(color);
                ((GradientDrawable) background).setStroke((int) itemView.getResources().getDimension(R.dimen.grid_1x), color);
            } else if (background instanceof ShapeDrawable) {
                // ((ShapeDrawable) background).getPaint().setColor(color);
                ((ShapeDrawable) background).getPaint().setColor(color);
            } else if (background instanceof ColorDrawable) {
                // ((ColorDrawable) background).setColor(color);
                ((ColorDrawable) background).setAlpha(100);
                ((ColorDrawable) background).setColor(color);
            }

            play.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClicked(island, 1000);
            });

        }
    }


    @Subscribe
    public void onEvent(RxlTapEvent event) {
        log("onEvent $event " + event);
    }

    @Subscribe
    public void onEvent(RxlStatusEvent event) {
        log("onEvent $event " + event);
        // if (event.isTap())
        //    RXTHelper.Companion.getInstance().postDirect(event);
    }

    @Subscribe
    public void onEvent(RxtStatusEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        log("onEvent RxtStatusEvent " + event);
        if (event.isTap())
            RXTManager.Companion.getInstance().postDirect(event);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        onBoosterScreen(true);
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    @Override
    public void onDestroy() {
        onBoosterScreen(false);
        EventBus.getDefault().unregister(this);
        RXTManager.Companion.getInstance().unregister();
        super.onDestroy();
    }

    void onBoosterScreen(boolean on) {
        try {
            if (on)
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onBackPressed() {
        if (isProgramStarted)
            return false;
        return super.onBackPressed();
    }
}
