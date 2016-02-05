package io.farragolabs.jsh;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class OSCall {
    public static int shell(String command) {

        CommandLine cmdLine = CommandLine.parse(command);
        int execute = 127;
        DefaultExecutor executor = new DefaultExecutor();

        try {
            //   handler.setProcessOutputStream(System.in);
            execute = executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return execute;
    }

    @Unstable
    public static Output complexShell(String command) {
        long l = System.currentTimeMillis();

        String output = "";

        String jobFileName = "job_" + l + ".sh";
        File jobFile = new File(jobFileName);
        try {
            FileUtils.writeStringToFile(jobFile, command.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jobFile.setExecutable(true);

        CommandLine cmdLine = CommandLine.parse("./" + jobFileName);
        int execute = 127;
        DefaultExecutor executor = new DefaultExecutor();

        try {
            execute = executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.forceDelete(jobFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Output(output, execute);
    }

    public static Output shellWithStdOut(String command) {

        CommandLine cmdLine = CommandLine.parse(command);
        int execute = 127;
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteStreamHandler handler = executor.getStreamHandler();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        executor.setStreamHandler(psh);

        try {
            handler.setProcessOutputStream(System.in);
            execute = executor.execute(cmdLine);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Output(stdout.toString().trim(), execute);
    }

    public static Output streamShell(String command) {

        //todo
        return null;
    }


    @Unstable
    public static Output shellWithTee(String command) {
        long l = System.currentTimeMillis();
        String pathname = "bedekken" + l + ".log";

        String output = "";

        String line = command.trim() + " 2>&1 | tee -a " + pathname;

        String jobFileName = "job_" + l + ".sh";
        File jobFile = new File(jobFileName);
        try {
            FileUtils.writeStringToFile(jobFile, line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jobFile.setExecutable(true);

        System.out.println(line);
        CommandLine cmdLine = CommandLine.parse("./" + jobFileName);
        int execute = 127;
        DefaultExecutor executor = new DefaultExecutor();

        try {
            execute = executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File file = new File(pathname);
            output = FileUtils.readFileToString(file);
            FileUtils.forceDelete(file);
            FileUtils.forceDelete(jobFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Output(output, execute);
    }

    @Unstable
    public static Output pig(String pigScript) {
        String pathname = createTempPigScript(pigScript);
        String command = "pig -stop_on_failure " + pathname;

        Output pigShellOutput = shellWithTee(command);

        shell("rm " + pathname);

        return pigShellOutput;
    }

    private static String createTempPigScript(String pigScript) {
        String pathname = "temp_pig_" + System.currentTimeMillis() + ".pig";
        try {
            FileUtils.writeStringToFile(new File(pathname), pigScript);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathname;
    }

    @Unstable
    public static Output hbase(String s) throws IOException {
        String HBASE_QUERY_WRAPPER = "hbase_%s.hbase";
        String HBASE_SHELL_CALL = "hbase shell %s";

        String pathname = String.format(HBASE_QUERY_WRAPPER, System.currentTimeMillis());
        File file = new File(pathname);
        FileUtils.writeStringToFile(file, s);
        String command = String.format(HBASE_SHELL_CALL, pathname);
        Output shell = shellWithStdOut(command);
        FileUtils.forceDelete(file);
        return shell;
    }
}
