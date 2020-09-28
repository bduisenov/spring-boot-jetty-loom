package com.example.demo;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication {

    @Bean
    JettyServletWebServerFactory JettyServletWebServerFactory() {
        return new JettyServletWebServerFactory() {
            @Override
            public ThreadPool getThreadPool() {
                return getLoomThreadPool();
            }
        };
    }

    private ThreadPool getLoomThreadPool() {
        final var executorService = Executors.newThreadExecutor(Thread.builder().name("vt-", 0).virtual().factory());
        return new ThreadPool() {
            @Override
            public void join() throws InterruptedException {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }

            @Override
            public int getThreads() {
                return 1;
            }

            @Override
            public int getIdleThreads() {
                return 1;
            }

            @Override
            public boolean isLowOnThreads() {
                return false;
            }

            @Override
            public void execute(Runnable command) {
                executorService.submit(command);
            }
        };
    }

    @RestController
    static class MyEndpoint {
        @GetMapping(path = "/", produces = "application/json")
        public String hi() {
            return "Hello World";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
