package com.aidanmars;

import com.aidanmars.bench.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class Main {
    static void main() {
        var options = new OptionsBuilder()
//                .include(FillBench.class.getSimpleName())
//                .include(GetSetDirectBench.class.getSimpleName())
//                .include(GetSetBench.class.getSimpleName())
//                .include(GetAllBench.class.getSimpleName())
//                .include(ReplaceAllBench.class.getSimpleName())
//                .include(SetAllBench.class.getSimpleName())
                .include(OptimizationBench.class.getSimpleName())
                .forks(3)
                .warmupIterations(5)
                .measurementIterations(10)
                .warmupTime(new TimeValue(2, TimeUnit.SECONDS))
                .measurementTime(new TimeValue(2, TimeUnit.SECONDS))
                .build();

        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }
    }
}
