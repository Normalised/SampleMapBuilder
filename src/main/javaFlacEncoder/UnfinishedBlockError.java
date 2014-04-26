package javaFlacEncoder;

/**
 * Created by martin on 20/01/12 at 14:08
 */
public class UnfinishedBlockError extends Throwable {
    public UnfinishedBlockError(String s) {
        super(s);
    }
}
