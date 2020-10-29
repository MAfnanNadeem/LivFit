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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.halilibo.bvpkotlin.BetterVideoPlayer;
import com.halilibo.bvpkotlin.VideoCallback;
import com.halilibo.bvpkotlin.VideoProgressCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import life.mibo.android.R;
import life.mibo.android.core.API;
import life.mibo.android.core.Prefs;
import life.mibo.android.models.base.ResponseStatus;
import life.mibo.android.models.circuits.Circuit;
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
//import life.mibo.android.ui.rxt.parser.RXTManager;
//import life.mibo.android.ui.rxt.parser.RxtIsland;
//import life.mibo.android.ui.rxt.parser.RxtProgram;
//import life.mibo.android.ui.rxt.parser.RxtTile;
//import life.mibo.android.ui.rxt.parser.core.RxtListener;
import life.mibo.android.ui.rxt.score.ScoreDialog;
//import life.mibo.android.ui.rxt.score.ScoreItem;
import life.mibo.android.ui.select_program.ProgramDialog;
import life.mibo.android.utils.Toasty;
import life.mibo.android.utils.Utils;
import life.mibo.hardware.events.RxlStatusEvent;
import life.mibo.hardware.events.RxlTapEvent;
import life.mibo.hardware.events.RxtStatusEvent;
import life.mibo.hardware.rxl.core.ScoreItem;
import life.mibo.hardware.rxt.RXTManager;
import life.mibo.hardware.rxt.RxtBlock;
import life.mibo.hardware.rxt.RxtIsland;
import life.mibo.hardware.rxt.RxtProgram;
import life.mibo.hardware.rxt.RxtTile;
import life.mibo.hardware.rxt.core.RxtListener;
import life.mibo.views.CircleView;
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

    TextView timer, islandName, islandTiles, workoutName, workoutDesc, tvSpeed, tvBlocks, tvBlocksInfo, tvProgramInfo;
    //ChipGroup chipGroup;
    CircleView colorPicker;
    int totalTime = 60;
    PlayButton playButton;
    CheckBox checkVoicePrompt;
    View noPlayer;
    BetterVideoPlayer videoPlayer;
    YouTubePlayerView utubePlayer;
    ProgressBar mediaPlayerProgress;
    RecyclerView recyclerView;
    private Workout workout;
    private Circuit circuit;
    private boolean isCircuitMode = false;
    private RXT rxtProgram;
    private EMS emsProgram;
    ImageView islandImage;
    private float speed = 1.0f;
    private float actionTime = 0;
    private float interval = 0.1f;
    private int islandId = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoPlayer = view.findViewById(R.id.player);
        utubePlayer = view.findViewById(R.id.utubePlayer);
        noPlayer = view.findViewById(R.id.tv_no_player);
        timer = view.findViewById(R.id.tv_timer);
        islandImage = view.findViewById(R.id.iv_island);
        islandName = view.findViewById(R.id.tv_island_name);
        islandTiles = view.findViewById(R.id.tv_island_tiles);
        workoutName = view.findViewById(R.id.tv_name);
        workoutDesc = view.findViewById(R.id.tv_desc);
        tvSpeed = view.findViewById(R.id.tv_plus_minus);
        tvBlocks = view.findViewById(R.id.tv_blocks);
        tvBlocksInfo = view.findViewById(R.id.tv_block_info);
        tvProgramInfo = view.findViewById(R.id.tv_program_info);
        //chipGroup = view.findViewById(R.id.chip_group);
        checkVoicePrompt = view.findViewById(R.id.check_voice_prompt);
        mediaPlayerProgress = view.findViewById(R.id.mediaPlayer_progress);
        colorPicker = view.findViewById(R.id.color_picker);
        recyclerView = view.findViewById(R.id.recyclerView);

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
            else {
                Toasty.info(getContext(), "Invalid Program!").show();
            }
        });
        //stop.setOnClickListener(v -> onStopProgram());
        if (getArguments() != null) {
            Object data = getArguments().getSerializable("workout_data");
            if (data instanceof Workout) {
                workout = (Workout) data;
                isCircuitMode = false;
            } else if (data instanceof Circuit) {
                circuit = (Circuit) data;
                isCircuitMode = true;
            }
            int color = getArguments().getInt("selected_color", 0);
            if (color != 0)
                updateColorPicker(color);
        }
        if (workout != null) {
            setupWorkout(workout);
        } else if (circuit != null) {
            setupWorkout(circuit);
        }
        playButton.setPlay(false);
        if (colorPicker != null)
            colorPicker.setOnClickListener(v -> {
                colorPickerDialog();
            });

        // utubePlayer;
        // getLifecycle().addObserver(utubePlayer);

//        utubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId = "P7abvZXogTg";
//                youTubePlayer.loadVideo(videoId, 0);
//            }
//        });
        // YouTubePlayerUtils.loadOrCueVideo(utubePlayer, getLifecycle(), "", 0);
//        if (MiboApplication.Companion.getTEST()) {
//            actionTime = 2000;
//            setSpeedText();
//        }
    }


    private void colorPickerDialog() {
        if (isProgramStarted)
            return;
        new ProgramDialog(requireContext(), new ArrayList<>(), (program, position) -> {
            if (program != null)
                updateColorPicker(program.getId());
        }, 2).showColors();
    }

    void plusClicked() {
        if (isProgramStarted)
            return;
        if (speed < 3.0) {
            speed += interval;
            //tvSpeed.setText(String.format(getString(R.string.rxt_action_speed), ((actionTime * speed) / 1000)));
            setSpeedText();
        }
    }

    void minusClicked() {
        if (isProgramStarted)
            return;
        if (speed > 0.2) {
            speed -= interval;
            if (speed > 0.2)
                setSpeedText();
            //else {
            //  Toasty.info(getContext(), "Invalid action time").show();
            //}
        }
    }

    void setSpeedText() {
        //tvSpeed.setText(String.format(getString(R.string.rxt_action_speed), ((actionTime * speed) / 1000)));
        if (actionTime == 0)
            actionTime = 2000;
        tvSpeed.setText(getString(R.string.rxt_action_speed, ((actionTime * speed) / 1000)));
    }

    void updateColorPicker(int color) {
        if (colorPicker != null) {
            colorPicker.setCircleColor(color);
            selectedColor = color;
        }
    }

    void updateBlockText(String text) {
        if (tvBlocksInfo != null) {
            if (tvBlocksInfo.getVisibility() != View.VISIBLE)
                tvBlocksInfo.setVisibility(View.VISIBLE);
            tvBlocksInfo.setText(text);
        }
    }

    private boolean isVoicePrompt = false;
    //private boolean isVoiceEnabled = false;

    void setupWorkout(Workout workout) {
        if (isCircuitMode) {
            setupWorkout(circuit);
            return;
        }
        setupPlayer(workout.getVideoLink());
        //setupPlayer("https://binialbert.yondo.com/video/69613/embed?code=1FiW8zOs");
        rxtProgram = workout.getRxt();
        timer.setText(workout.getDuration());
        totalTime = workout.getDurationSec();
        timeElapsed = workout.getDurationSec();
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
                //List<RXT.RXTBlock> blocks = rxtProgram.getBlocks();
                islandId = island.getID();
                islandName.setText(island.getName());
                islandTiles.setText("Tiles " + island.getTotalTiles() + " \n " + island.getIslandWidth() + "x" + island.getIslandHeight());
                //tvBlocks.setText("Blocks:");
                ArrayList<Workout> wkks = new ArrayList<>();
                wkks.add(workout);
                setBlocks(wkks);
//                if (blocks != null && !blocks.isEmpty()) {
//                    actionTime = blocks.get(0).getAction();
//                    setSpeedText();
//                    setBlocks(workout);
//                    for (RXT.RXTBlock b : blocks) {
//                        Chip chip = new Chip(chipGroup.getContext());
//                        chip.setText(b.getRXTType() + " x " + b.getRounds() + " - " + b.getRXTTotalDuration() + " sec");
//                        chip.setCheckable(false);
//                        chip.setCheckedIconVisible(false);
//                        chip.setCloseIconVisible(false);
//                        chip.setChipIconVisible(false);
//                        chip.setClickable(false);
//                        chipGroup.addView(chip);
//                    }
//                }
                log("Image Glide " + island.getIslandImage());
                Glide.with(getContext()).load(island.getIslandImage()).error(R.drawable.ic_broken_image).fallback(R.drawable.ic_broken_image).into(islandImage);
            }
            workoutName.setText(workout.getName());
            workoutDesc.setText(workout.getDescription());

        }
    }

    private boolean isValidCircuit = true;

    void setupWorkout(Circuit circuit) {
        List<Workout> workouts = circuit.getWorkout();
        if (workouts != null && workouts.size() > 0) {
            Workout workout = workouts.get(0);

            timer.setText(circuit.getDuration());
            timeElapsed = circuit.getDurationSec();
            workoutName.setText(circuit.getName());
            workoutDesc.setText(circuit.getDescription());
            totalTime = circuit.getDurationSec();
            //String url = workout.getVideoLink();
            setupPlayer(workout.getVideoLink());
            rxtProgram = workout.getRxt();

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
                    //List<RXT.RXTBlock> blocks = rxtProgram.getBlocks();
                    islandId = island.getID();
                    islandName.setText(island.getName());
                    islandTiles.setText("Tiles " + island.getTotalTiles() + " \n " + island.getIslandWidth() + "x" + island.getIslandHeight());
                    ///tvBlocks.setText("Blocks:");

                    setBlocks(workouts);
                    log("Image Glide " + island.getIslandImage());
                    Glide.with(getContext()).load(island.getIslandImage()).error(R.drawable.ic_broken_image).fallback(R.drawable.ic_broken_image).into(islandImage);
                }


            }

            try {
                for (Workout www : workouts) {
                    RXT r = www.getRxt();
                    if (r != null) {
                        RXT.RXTIsland s = r.getRXTIsland();
                        if (s != null) {
                            if (islandId != s.getID()) {
                                isValidCircuit = false;
                                Toasty.error(getContext(), R.string.invalid_circuit_text).show();
                                break;
                            }
                        }
                    }
                }

            } catch (Exception ee) {

            }


        } else {
            Toasty.info(getContext(), "Circuit workouts can not be empty").show();
        }
    }

    void setupPlayer(String url) {
        //Toasty.info(getContext(), "Video URL " + url).show();
        if (url == null)
            return;
        log("videoPlayer url : " + url);
        if (url.toLowerCase().contains("youtube.com")) {
            log("url is youtube");
            String[] urls = url.split("watch\\?v=", 2);
            //log("youtube urls " + Arrays.toString(urls));
            //log("youtube urls " + urls.length);
            if (urls.length > 1) {
                utubePlayer.setVisibility(View.VISIBLE);
                noPlayer.setVisibility(View.GONE);
                getLifecycle().addObserver(utubePlayer);

                utubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        String videoId = urls[1];
                        try {
                            if (videoId.contains("&"))
                                youTubePlayer.loadVideo(videoId.split("&")[0], 0);
                            else youTubePlayer.loadVideo(videoId, 0);
                        } catch (Exception e) {
                            youTubePlayer.loadVideo(videoId, 0);
                        }

                        //log("youtube videoId " + videoId);
                    }
                });
            }
            return;
        } else {
            log("url is not a youtube");
        }

        videoPlayer.setVisibility(View.VISIBLE);
        noPlayer.setVisibility(View.GONE);

        if (url.length() > 1)
            videoPlayer.setSource(Uri.parse(url));
        // log("videoPlayer url2 : "+videoPlayer.);


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
                log("videoPlayer onBuffering " + i);
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

    void setBlocks(List<Workout> workouts) {
        if (workouts != null) {
            List<Blocks> blocks = new ArrayList<>();

            for (Workout workout : workouts) {
                RXT rxt = workout.getRxt();
                if (rxt == null)
                    continue;
                List<RXT.RXTBlock> rxtBlocks = rxt.getBlocks();
                boolean first = true;
                for (RXT.RXTBlock b : rxtBlocks) {
                    if (first)
                        blocks.add(new Blocks(workout.getName(), workout.getDuration(), b.getRXTType(), "" + b.getDuration(), "" + b.getRXTAction(), "" + b.getRXTRound(), "" + b.getRXTDelay()));
                    else
                        blocks.add(new Blocks("", "", b.getRXTType(), "" + b.getDuration(), "" + b.getRXTAction(), "" + b.getRXTRound(), "" + b.getRXTDelay()));
                    first = false;
                }
            }
            if (blocks.size() > 0)
                actionTime = getInt(blocks.get(0).getAction()) * 1000;
            setSpeedText();
            WorkoutAdapter adapter = new WorkoutAdapter(blocks, null);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }


    void updateTimer(String time) {
        if (timer != null) {
            getActivity().runOnUiThread(() -> timer.setText(time));
        }
    }

    private int selectedColor = Color.RED;
    private boolean isProgramStarted = false;
    private String tempTileIds = "";

    private boolean onStartRxt(String tiles) {
        log("onStartRxt $tiles " + tiles);
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
                if (isCircuitMode && circuit != null) {
                    islands.add(new RxtIsland(11, RxtProgram.Companion.empty(), rxtTiles, workoutName.getText().toString(), "Player " + 11, selectedColor).circuit(from(circuit)));
                } else {
                    islands.add(new RxtIsland(11, from(rxtProgram), rxtTiles, workoutName.getText().toString(), "Player " + 11, selectedColor));
                }
                RXTManager.Companion.getManager().with(islands, listener).startNow(totalTime, speed);
                isProgramStarted = true;
                playButton.setPlay(true);
                tempTileIds = tiles;
                return true;
            }
        }

        return false;
    }


    public RxtProgram from(RXT rxt) {

        ArrayList<RxtBlock> list = new ArrayList<>();
        List<RXT.RXTBlock> blocks = rxt.getBlocks();
        if (blocks != null && blocks.size() > 0) {
            for (RXT.RXTBlock i : blocks) {
                if (i != null) {
                    // i.pattern = "1,2-3,4,5-6,7,8,9-10,8,7,6-5,4,3-2"
                    //i.pattern = "1,5,9,11,13,17,21,23,19,17,15,11,7,5,3,2,1"
                    //i.pattern = "1-2,4-5,7-8,10-11,13-14,16-17"
                    //i.pattern = "1-2"
                    //i.rXTAction = 2
                    String pattern = i.getPattern();
                    if (pattern == null)
                        pattern = "";
                    RxtBlock b = new RxtBlock(i.getAction(), i.getDuration(), i.getLogicType(), 0, pattern);
                    b.setDelay(i.getActionDelay());
                    b.setBlockPause(i.getBlockPause());
                    b.setRound(i.getRounds());
                    list.add(b);
                }
            }
        }
        return new RxtProgram("Rxt ", 0, 0, list);

    }

    public ArrayList<RxtProgram> from(Circuit circuit) {

        ArrayList<RxtProgram> programs = new ArrayList<>();
        List<Workout> workouts = circuit.getWorkout();
        if (workouts != null && workouts.size() > 0) {
            for (Workout w : workouts) {
                if (w != null) {
                    RXT rxt = w.getRxt();
                    if (rxt != null) {
                        ArrayList<RxtBlock> list = new ArrayList<>();
                        List<RXT.RXTBlock> blocks = rxt.getBlocks();
                        if (blocks != null && blocks.size() > 0) {
                            for (RXT.RXTBlock i : blocks) {
                                if (i != null) {
                                    String pattern = i.getPattern();
                                    if (pattern == null)
                                        pattern = "";
                                    RxtBlock b = new RxtBlock(i.getAction(), i.getDuration(), i.getLogicType(), 0, pattern);
                                    b.setDelay(i.getActionDelay());
                                    b.setBlockPause(i.getBlockPause());
                                    b.setRound(i.getRounds());
                                    list.add(b);
                                }
                            }
                        }

                        RxtProgram prg = new RxtProgram(w.getName(), 0, 0, list);
                        prg.setWorkoutDuration(w.getDurationSec());
                        prg.setWorkoutPause(w.getPause());
                        programs.add(prg);

                    }

                }

            }
        }

        return programs;

    }

    //        fun from(rxt: RXT): RxtProgram {
//            val list = ArrayList<RxtBlock>()
//            val blocks = rxt.blocks
//            if (blocks != null && blocks.isNotEmpty()) {
//                for (i in blocks) {
//                    if (i != null) {
//                        // i.pattern = "1,2-3,4,5-6,7,8,9-10,8,7,6-5,4,3-2"
//                        //i.pattern = "1,5,9,11,13,17,21,23,19,17,15,11,7,5,3,2,1"
//                        //i.pattern = "1-2,4-5,7-8,10-11,13-14,16-17"
//                        //i.pattern = "1-2"
//                        //i.rXTAction = 2
//                        val b = RxtBlock(
//                            i.getAction(),
//                            i.getDuration(),
//                            i.getLogicType(),
//                            0,
//                            i.pattern ?: ""
//                        )
//                        b.delay = i.getActionDelay()
//                        b.blockPause = i.getBlockPause()
//                        b.round = i.getRounds()
//                        list.add(b)
//                    }
//                }
//            }
//            return RxtProgram("Rxt ${rxt.category}", 0, 0, list)
//        }
//
//        fun from(circuit: Circuit): ArrayList<RxtProgram> {
//            val programs = ArrayList<RxtProgram>()
//            val workouts = circuit.workout
//            if (workouts != null && workouts.isNotEmpty()) {
//                for (w in workouts) {
//                    val rxt = w?.rxt
//                    if (rxt != null) {
//                        val list = ArrayList<RxtBlock>()
//                        val blocks = rxt.blocks
//                        if (blocks != null && blocks.isNotEmpty()) {
//                            for (i in blocks) {
//                                if (i != null) {
//                                    //i.pattern = "1,2-3,4,5-6,7,8,9-10,8,7,6-5,4,3-2"
//                                    //i.rXTAction = 2
//                                    val b = RxtBlock(
//                                        i.getAction(),
//                                        i.getDuration(),
//                                        i.getLogicType(),
//                                        0,
//                                        i.pattern ?: ""
//                                    )
//                                    b.delay = i.getActionDelay()
//                                    b.blockPause = i.getBlockPause()
//                                    //b.workoutPause = w.getPause()
//                                    b.round = i.getRounds()
//                                    list.add(b)
//                                }
//                            }
//                        }
//                        val prg = RxtProgram(w.name ?: "RXT", 0, 0, list)
//                        prg.workoutDuration = w.getDurationSec()
//                        prg.workoutPause = w.getPause()
//                        programs.add(prg)
//                    }
//                }
//            }
//
//            return programs;
//        }


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
        if (!Utils.isEmpty(tempTileIds)) {
            if (onStartRxt(tempTileIds))
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
                ConfigureIslandActivity.Companion.launch(RXTStartSingleSessionFragment.this, island.getName(), island.getID(), island.getX(), island.getY(), island.getTotal(), 0);
            }

            @Override
            public void onFailure(@NonNull Call<GetIslandTiles> call, @NonNull Throwable t) {
                getDialog().dismiss();
                Toasty.info(getContext(), "Error " + t.getMessage()).show();
            }
        });
    }

    void saveScore(List<ScoreItem> list) {
        //log("saveScore timeElapsed " + timeElapsed + " totalTime "+totalTime + " -- "+(totalTime - timeElapsed));
        //if (MiboApplication.Companion.getDEBUG())
        //   return;
        Member trainer = Prefs.get(getContext()).getMember();
        if (trainer == null)
            return;
        //getDialog().show();
        String loc = trainer.getLocationID();
        if (loc == null)
            loc = "0";

        int wId = 0;
        if (isCircuitMode) {
            if (circuit != null)
                wId = circuit.getId();
        } else {
            if (workout != null)
                wId = workout.getId();
        }

        // log("Time >>>>>>>>> " + totalTime);
        // log("Time >>>>>>>>> " + timeElapsed);
        // log("Time >>>>>>>>> " + (totalTime - timeElapsed));
        //ateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        List<SaveMemberScores.Score> scores = new ArrayList<>();
        if (list != null) {
            for (ScoreItem item : list) {
                scores.add(new SaveMemberScores.Score(date, "rxt", "" + item.getHits(), loc, "" + trainer.id(), "" + item.getMissed(), "" + item.getTotal(), "" + trainer.id(), "0", "-", "" + wId, "" + (totalTime - timeElapsed)));
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

    private int timeElapsed;
    private int lastBlock = -1;
    private String currentProgramName = "";
    private int currentProgramId = 0;
    RxtListener listener = new RxtListener() {

        @Override
        public void onCircuitProgramStart(@NotNull String name, int programId, int pause) {
            currentProgramId = programId;
            if (tvProgramInfo != null)
                getActivity().runOnUiThread(() -> {
                    if (pause > 0) {
                        currentProgramName = getString(R.string.pause_circuit, pause);
                    } else {
                        currentProgramName = name;
                    }
                    tvProgramInfo.setText(currentProgramName);
                    tvProgramInfo.setVisibility(View.VISIBLE);
                });
        }

        @Override
        public void onBlockStart(int block, int cycle) {
            updateBlockText("Block " + (block + 1) + " - Cycle " + cycle);
//            try {
//                if (block == lastBlock)
//                    return;
//                //log("onBlockStart " + block);
//                Chip chip = (Chip) chipGroup.getChildAt(block);
//                //log("chip setBackgroundColor " + chip);
//                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
//                // chip.setCheckedIconVisible(true);
//                //chip.setChecked(true);
//
//                //log("chip setBackgroundColor lastBlock " + lastBlock);
//                if (lastBlock >= 0) {
//                    Chip chip2 = (Chip) chipGroup.getChildAt(lastBlock);
//                    chip2.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white_eee)));
//                    //log("chip setBackgroundColor chip2 " + chip2);
//                }
//                lastBlock = block;
//            } catch (Exception ee) {
//                //log("chip error " + ee);
//                //ee.printStackTrace();
//            }
        }

        @Override
        public void onTime(int id, long t) {
            log("RxtListener onTime " + t);
            try {
                int time = (int) (totalTime - t);
                String txt = String.format("%02d : %02d", ((int) time / 60), ((int) time % 60));
                log("onTime " + time + " - " + txt);
                updateTimer(txt);
                timeElapsed = time;
                if (t == 0)
                    timeElapsed = 0;
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
            ArrayList<ScoreItem> list = RXTManager.Companion.getManager().getScore();
            log("ScoreItem list " + list);
            String name = "Test Program...";
            if (isCircuitMode)
                name = circuit.getName();
            else if (workout != null)
                name = workout.getName();
            String finalName = name;
            ArrayList<life.mibo.android.ui.rxt.score.ScoreItem> scoreItems = new ArrayList<>();
            for (ScoreItem s : list) {
                scoreItems.add(new life.mibo.android.ui.rxt.score.ScoreItem(s.getId(), s.getName(), s.getHits(), s.getMissed(), s.getColor(), s.getTotal(), s.getTotalInSeconds()));
            }

            requireActivity().runOnUiThread(() -> new ScoreDialog(requireActivity(), finalName, scoreItems).show());
            saveScore(list);
        } catch (Exception ee) {
            ee.printStackTrace();
            if (isAdded())
                Toasty.info(getContext(), "Score Error! " + ee.getMessage()).show();
        }
    }

    private void onStopProgram() {
        RXTManager.Companion.getManager().unregister();
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
        //if (event.isTap())
        //   RXTManager.Companion.getInstance().postDirect(event);
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
        RXTManager.Companion.getManager().unregister();
        if (utubePlayer != null)
            utubePlayer.release();
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

    public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutHolder> {

        public static final int PLAY = 1000;
        public static final int COLOR = 2000;
        public static final int PROGRAM = 3000;
        public static final int CHECK = 4000;
        public static final int ITEM = 5000;

        List<Blocks> islands = new ArrayList<>();
        ItemClickListener<Object> listener;

        public WorkoutAdapter(List<Blocks> list, ItemClickListener<Object> listener) {
            islands.addAll(list);
            this.listener = listener;
        }


        @NonNull
        @Override
        public WorkoutAdapter.WorkoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WorkoutAdapter.WorkoutHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rxt_circuits_workouts, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutAdapter.WorkoutHolder holder, int position) {
            holder.bind(islands.get(position));
        }

        @Override
        public int getItemCount() {
            return islands.size();
        }


        class WorkoutHolder extends RecyclerView.ViewHolder {
            TextView title, blocks, dur, action, repeat, delay, wDur;

            public WorkoutHolder(View view) {
                super(view);
                blocks = view.findViewById(R.id.tv_block_name);
                title = view.findViewById(R.id.tv_name);
                dur = view.findViewById(R.id.tv_duration);
                action = view.findViewById(R.id.tv_action);
                delay = view.findViewById(R.id.tv_delay);
                repeat = view.findViewById(R.id.tv_rounds);
                wDur = view.findViewById(R.id.tv_name_dur);
            }

            void bind(Blocks block) {
                if (block == null)
                    return;
                if (Utils.isEmpty(block.getTitle())) {
                    title.setVisibility(View.GONE);
                    wDur.setVisibility(View.GONE);
                } else {
                    wDur.setText(block.getWorkoutDur());
                    title.setText(block.getTitle());
                }
                blocks.setText(block.getBlock());
                dur.setText(block.getDur());
                action.setText(block.getAction());
                delay.setText(block.getDelay());
                repeat.setText(block.getRepeat());

            }

        }

    }

    class Blocks {
        String title, block, dur, action, repeat, delay, workoutDur;

        public Blocks(String title, String workoutDur, String block, String dur, String action, String repeat, String delay) {
            this.title = title;
            this.block = block;
            this.dur = dur;
            this.workoutDur = workoutDur;
            this.action = action;
            this.repeat = repeat;
            this.delay = delay;
        }

        public String getWorkoutDur() {
            return workoutDur;
        }

        public String getTitle() {
            return title;
        }

        public String getBlock() {
            return block;
        }

        public String getDur() {
            return dur;
        }

        public String getAction() {
            return action;
        }

        public String getRepeat() {
            return repeat;
        }

        public String getDelay() {
            return delay;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        RXTManager.Companion.getManager().unregister();
    }

    @Override
    public void onDetach() {
        RXTManager.Companion.getManager().unregister();
        super.onDetach();
    }
}
