package io.farragolabs.jsh;

public class Output {

    private String output;
    private int statusCode;

    public Output(String output, int statusCode) {
        this.output = output;
        this.statusCode = statusCode;
    }

    public String getOutput() {
        return output;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
