package Core.Util;

public class ProgressBar {
    private static final int PROGRESS_BAR_LENGTH = 33;
    public static String progressBar(double percent) {
        StringBuilder progress = new StringBuilder("[");
        int length = (int) (percent * PROGRESS_BAR_LENGTH);
        int remainder = PROGRESS_BAR_LENGTH - length;
        int extra = remainder / 4; // Needed to equalize # and " -" lengths;
        if (length % 2 == 0) {
            extra--;
        }
        progress.append("#".repeat(length));
        progress.append(" -".repeat(remainder + (extra / 2)));

        if (length == PROGRESS_BAR_LENGTH) {
            progress.append("]");
        } else {
            progress.append(" ]");
        }

        return progress.toString();
    }
}
