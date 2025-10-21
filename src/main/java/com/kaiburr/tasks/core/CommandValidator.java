package com.kaiburr.tasks.core;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Pragmatic command validator for Task‑1.
 * Rejects metacharacters and a deny‑list of risky commands, and only allows a small allow‑list
 * of benign utilities. It's not intended to be a full sandbox, but it's predictable and safe.
 */
@Component
public class CommandValidator {

    // Reject obvious shell metacharacters and expansions
    private static final Pattern META = Pattern.compile("[;&|`$><*{}\\]");

    // Very small allow‑list on purpose — expand if needed
    private static final Set<String> ALLOW = new HashSet<>(Arrays.asList(
            "echo", "printf", "date", "whoami", "uname", "ls", "pwd", "cat"
    ));

    // Quick‑n‑dirty deny‑list
    private static final Set<String> DENY = new HashSet<>(Arrays.asList(
            "rm", "sudo", "su", "reboot", "poweroff", "shutdown", "kill",
            "mkfs", "dd", "chmod", "chown", "useradd", "userdel",
            "curl", "wget", "nc", "netcat", "python", "perl", "ruby", "java",
            "kubectl", "docker", "podman", "bash", "sh"
    ));

    public void validate(String command) {
        if (command == null || command.isBlank()) {
            throw new CommandValidationException("command must not be blank");
        }
        if (META.matcher(command).find()) {
            throw new CommandValidationException("command contains forbidden shell metacharacters");
        }
        String first = firstToken(command);
        if (first.isEmpty()) {
            throw new CommandValidationException("command must start with an executable");
        }
        String f = first.toLowerCase(Locale.ROOT);
        if (DENY.contains(f)) {
            throw new CommandValidationException("command is not allowed: " + f);
        }
        if (!ALLOW.contains(f)) {
            throw new CommandValidationException("only a limited set of safe commands is allowed in Task‑1: " + ALLOW);
        }
    }

    private static String firstToken(String s) {
        String trimmed = s.trim();
        int i = 0;
        while (i < trimmed.length() && !Character.isWhitespace(trimmed.charAt(i))) i++;
        return trimmed.substring(0, i);
    }
}
