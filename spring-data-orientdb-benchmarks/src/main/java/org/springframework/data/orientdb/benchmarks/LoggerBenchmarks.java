package org.springframework.data.orientdb.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 5)
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class LoggerBenchmarks {
    @Param({"sync", "async", "durable"})
    public String loggerName;
    private Logger logger;
    private AtomicLong counter;

    @Setup
    public void setup() throws IOException {
        counter = new AtomicLong();
        logger = LoggerFactory.getLogger(loggerName);
    }

    @TearDown
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get("target", "jmh.log"));
        Files.deleteIfExists(Paths.get("target", "durable.data"));
        Files.deleteIfExists(Paths.get("target", "durable.index"));
    }

    @Benchmark
    public void asyncAppenderToFile(Blackhole bh) throws InterruptedException {
        long l = counter.incrementAndGet();
        logger.info("count: {}", l);
        bh.consume(l);
    }
}