package com.kaiburr.tasks.core;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class LocalCommandRunner {

    public Result run(String command) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");

        List<String> cmd = new ArrayList<>();
        if (isWindows) {
            cmd.add("cmd.exe");
            cmd.add("/c");
            cmd.add(command);
        } else {
            cmd.add("bash");
            cmd.add("-lc");
            cmd.add(command);
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true); // merge stderr into stdout

        Instant start = Instant.now();
        Process process = pb.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append(System.lineSeparator());
            }
        }
        int exit = process.waitFor();
        Instant end = Instant.now();

        return new Result(exit, out.toString(), start, end, Duration.between(start, end));
    }

    public static class Result {
        public final int exitCode;
        public final String output;
        public final Instant startTime;
        public final Instant endTime;
        public final Duration duration;

        public Result(int exitCode, String output, Instant startTime, Instant endTime, Duration duration) {
            this.exitCode = exitCode;
            this.output = output;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
        }
    }
}
