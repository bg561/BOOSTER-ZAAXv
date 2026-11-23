
private void updateCpu() {

    if (cpuText == null) return;

    try (BufferedReader reader = new BufferedReader(
            new FileReader("/proc/stat"))) {

        String line = reader.readLine();

        if (line == null || !line.startsWith("cpu ")) {
            cpuText.setText("CPU N/A");
            return;
        }

        String[] values = line.trim().split("\\s+");

        if (values.length < 5) {
            cpuText.setText("CPU N/A");
            return;
        }

        long total = 0;

        for (int i = 1; i < values.length; i++) {
            total += Long.parseLong(values[i]);
        }

        long idle = Long.parseLong(values[4]);

        if (values.length > 5) {
            idle += Long.parseLong(values[5]);
        }

        if (previousTotal == 0) {
            previousTotal = total;
            previousIdle = idle;
            cpuText.setText("CPU --");
            return;
        }

        long totalDiff = total - previousTotal;
        long idleDiff = idle - previousIdle;

        previousTotal = total;
        previousIdle = idle;

        if (totalDiff <= 0 || idleDiff < 0) {
            cpuText.setText("CPU --");
            return;
        }

        int usage = (int) Math.round(
                100.0 * (totalDiff - idleDiff) / totalDiff
        );

        usage = Math.max(0, Math.min(100, usage));

        cpuText.setText("CPU " + usage + "%");

    } catch (Exception e) {
        cpuText.setText("CPU N/A");
        previousTotal = 0;
        previousIdle = 0;
    }
}
