package io.farragolabs.jsh;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class OSCallTest {

    @Test
    public void shellCall()
    {
        int shell = OSCall.shell("echo \"asd\"");
        Assertions.assertThat(shell).isEqualTo(0);
    }

    @Test
    public void shellWithStdOut()
    {
        Output output = OSCall.shellWithStdOut("echo 'asd'");

        Assertions.assertThat(output.getStatusCode()).isEqualTo(0);
        Assertions.assertThat(output.getOutput()).isEqualTo("asd");
    }
}