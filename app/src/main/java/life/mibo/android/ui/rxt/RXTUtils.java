package life.mibo.android.ui.rxt;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import life.mibo.android.core.Prefs;
import life.mibo.android.ui.rxt.model.Island;
import life.mibo.android.ui.rxt.model.Tile;
import life.mibo.hardware.CommunicationManager;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.events.ChangeColorEvent;
import life.mibo.android.ui.rxt.parser.RxtBlock;
import life.mibo.android.ui.rxt.parser.RxtProgram;

public class RXTUtils {

    public static void savePrograms(Context context, RxtProgram program, List<RxtBlock> blocks) {

    }

    public static ArrayList<RxtProgram> getPrograms(Context context) {
        ArrayList<RxtProgram> programs = new ArrayList<>();
        try {
            List<String> keys = new ArrayList<>();
            Prefs prefs = Prefs.get(context);
            Map<String, ?> all = prefs.getAll();

            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key != null && key.startsWith("program_")) {
                    if (key.endsWith("_blocks")) {
                    } else {
                        keys.add(key);
                    }
                }
            }
            if (keys.size() > 0) {
                for (String key : keys) {
                    List<RxtBlock> blocks = prefs.getJsonList(key + "_blocks", RxtBlock.class);
                    RxtProgram program = prefs.getJson(key, RxtProgram.class);
                    program.setKey(key);
                    program.setBlocks(blocks);
                    programs.add(program);
                    //prefs.remove(key+"_blocks");
                    //prefs.remove(key);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return programs;
    }

    public static ArrayList<Island> getIslands(Context context) {

        ArrayList<Island> islands = new ArrayList<>();
        try {
            List<String> keys = new ArrayList<>();
            Prefs prefs = Prefs.get(context);
            Map<String, ?> all = prefs.getAll();

            for (Map.Entry<String, ?> entry : all.entrySet()) {
                log("map values " + entry.getKey() + ": " + entry.getValue().toString());
                String key = entry.getKey();
                if (key != null && key.startsWith("island_")) {
                    if (key.endsWith("_name")) {
                        log("KEY NAME : " + key);
                    } else {
                        log("KEY : " + key);
                        keys.add(key);
                    }
                } else {
                    log("KEY UNKNOWN : " + key);
                }
            }
            if (keys.size() > 0) {
                for (String key : keys) {
                    String name = prefs.get(key + "_name");
                    List<Tile> tiles = prefs.getJsonList(key, Tile.class);
                    log("TILES :: " + tiles.size());
                    islands.add(new Island(name, tiles, key));
                }
            }


        } catch (Exception e) {

        }


        if (!islands.isEmpty()) {
            Collections.sort(islands, (o1, o2) -> {
                if (o1 != null && o2 != null)
                    return o1.getName().compareTo(o2.getName());
                return 0;
            });
        }

        return islands;
    }

    public static void playSequence(final List<Tile> tiles) {
        if (tiles.isEmpty()) {
            return;
        }
        int color = Color.BLUE;
        Observable.fromIterable(tiles)
                .doOnError(throwable -> log("playSequence doOnError accept " + throwable))
                .subscribeOn(Schedulers.io()).doOnComplete(() -> log("playSequence doOnComplete run..."))
                .doOnNext(tile -> {
                    log("playSequence doOnNext accept : " + tile);
                    try {
                        CommunicationManager.getInstance().onChangeRxtColorEvent(new ChangeColorEvent(tile.getUid(), "" + tile.getTileId(), color, 250, 0));
                        Thread.sleep(250);
                    } catch (Exception e) {
                        log("playSequence doOnNext error : " + e.getMessage());
                    }
                }).subscribe();
    }

    public static void log(String msg) {
        Logger.e("RXTUtils", msg);
    }
}
