package drumkitbuilder2

import com.relivethefuture.audio.AudioFilePlayer

/**
 * Created by martin on 09/04/13 at 21:47
 *
 */
class AudioPlayerService {
    AudioFilePlayer audioFilePlayer;

    void serviceInit() {
        audioFilePlayer = new AudioFilePlayer()
    }

    void serviceDestroy() {
        audioFilePlayer = null
    }

    def play(File file) {
        audioFilePlayer.play(file)
    }
}
