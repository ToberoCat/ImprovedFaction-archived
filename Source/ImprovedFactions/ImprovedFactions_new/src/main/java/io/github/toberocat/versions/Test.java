package io.github.toberocat.versions;

import io.github.toberocat.core.utility.gitreport.GitReport;

import java.io.*;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;


public class Test {
    public static void main(String[] args) {
        try {
           throw new RuntimeException();
        } catch (Exception e) {
            GitReport.reportIssue(e);
        }
    }
}
