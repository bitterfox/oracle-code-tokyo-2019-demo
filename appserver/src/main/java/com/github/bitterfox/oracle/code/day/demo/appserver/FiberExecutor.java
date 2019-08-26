package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.util.concurrent.Executor;

import static com.github.bitterfox.oracle.code.day.demo.appserver.Metrics.APP_FIBER_EXECUTOR_TASK_COUNT;

public class FiberExecutor implements Executor {
//    private FiberScope scope = FiberScope.open();

    @Override
    public void execute(Runnable command) {
        APP_FIBER_EXECUTOR_TASK_COUNT.inc();
//        scope.schedule(command);
//        command.run();
        FiberScope.background().schedule(command);
    }
}
