package tictacgoal.client.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import tictacgoal.client.TicTacGoal;

import java.net.URISyntaxException;
import java.util.*;

public class AudioManager {

    private final Map<String, AudioTimeFrame> mp3s = new HashMap<>();

    public AudioManager() {
        // Main menu theme
        load("theme", 8000, 11000);

        // Game end sounds
        load("goal", 9250, 16000);
        load("messi", 9000, 26000);
        load("torres", 25000, 39000);
        load("omg", 8000, 11000);
    }

    public void random() {
//        List<String> files = new ArrayList<>(audio.keySet());
//        Random rand = new Random();
//        String file;
//        for (; ; ) {
//            file = files.get(rand.nextInt(files.size()));
//            if (last == null) {
//                last = file;
//                break;
//            }
//            if (!file.equals(last)) {
//                last = file;
//                break;
//            }
//        }
//        int[] interval = audio.get(file);
    }

    public MediaPlayer play(String mp3) {
        Media media = new Media(getUrl(mp3));
        MediaPlayer mp = new MediaPlayer(media);
        AudioTimeFrame frame = mp3s.get(mp3);
        if (frame != null) {
            mp.setStartTime(Duration.millis(frame.getStart()));
            mp.setStopTime(Duration.millis(frame.getEnd()));
        }
        mp.play();
        return mp;
    }

    private void load(String mp3) {
        mp3s.put(getUrl(mp3), null);
    }

    private void load(String mp3, long start, long end) {
        mp3s.put(getUrl(mp3), new AudioTimeFrame(start, end));
    }

    private String getUrl(String mp3) {
        try {
            return TicTacGoal.class.getResource("/" + mp3 + ".mp3").toURI().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final class AudioTimeFrame {
        private final long start, end;

        AudioTimeFrame(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }
    }
}
