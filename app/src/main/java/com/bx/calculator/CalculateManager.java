package com.bx.calculator;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.bx.calculator.calc.CExpression;
import com.bx.calculator.calc.CParams;
import com.bx.calculator.calc.CResult;
import com.bx.calculator.calc.CUnit;
import com.bx.calculator.calc.Calculate;
import com.bx.calculator.calc.exception.VariableException;
import com.bx.calculator.calc.exception.OutOfRangeException;
import com.bx.calculator.calc.exception.SyntaxException;
import com.bx.calculator.calc.exception.UndefinedException;
import com.bx.calculator.calc.math.AngleUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ch.obermuhlner.math.big.BigComplex;

public final class CalculateManager {

    private static final String TAG = "CalculateManager";

    private static final int CALCULATE_STARTED = 1;
    private static final int CALCULATE_COMPLETE = 2;

    private static final CalculateManager instance = new CalculateManager();

    private final ExecutorService executor;
    private final Handler handler;
    private CalculateTask currentTask;
    private CParams params;

    private CalculateManager() {
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final CalculateTask task = (CalculateTask) msg.obj;
                switch (msg.what) {
                    case CALCULATE_STARTED:
                        if (task.onStarted != null) {
                            task.onStarted.run();
                        }
                        break;
                    case CALCULATE_COMPLETE:
                        try {
                            final CResult result = task.future.get();
                            if (task.onComplete != null) {
                                task.onComplete.accept(result);
                            }
                        } catch (ExecutionException e) {
                            if (task.onException != null && e.getCause() instanceof Exception) {
                                task.onException.accept((Exception) e.getCause());
                            }
                        } catch (Exception e) {
                            if (task.onException != null) {
                                task.onException.accept(e);
                            }
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public static CalculateManager getInstance() {
        return instance;
    }

    @NonNull
    public synchronized CParams getParams() {
        if (params == null) {
            params = new CParams();
        }
        return params;
    }

    /**
     * @param params Parameters for calculation. Set to {@code null} for default.
     */
    public void setParams(@Nullable CParams params) {
        synchronized (this) {
            this.params = params;
        }
    }

    public void setParamsAngleUnit(AngleUnit angleUnit) {
        synchronized (this) {
            this.params = new CParams(angleUnit, getParams().getVariableMap());
        }
    }

    public void setParamsAnswer(BigComplex answer) {
        synchronized (this) {
            final CParams oldParams = getParams();
            final Map<CUnit, BigComplex> oldVariables = new HashMap<>(oldParams.getVariableMap());
            oldVariables.put(CUnit.ANS, answer);
            this.params = new CParams(oldParams.getAngleUnit(), oldVariables);
        }
    }

    public void setParamsVariables(@Nullable Map<CUnit, BigComplex> variables) {
        synchronized (this) {
            this.params = new CParams(getParams().getAngleUnit(), variables);
        }
    }

    /**
     * Calculates a result if no other calculation is executing, then handle result afterwards.
     *
     * @param input Sequence to calculate.
     * @param params Parameters for this calculation.
     * @param onComplete Action to perform on main thread after calculation complete.
     * @param onException Action to perform on main thread if an exception is thrown during calculation.
     * @return true if begin calculation, false if another calculation is already executing.
     * @throws NullPointerException {@code input} or a {@link CUnit} is {@code null}.
     */
    public synchronized boolean calculate(@NonNull CUnit[] input, @NonNull CParams params, @Nullable Runnable onStarted,
                                          @Nullable Consumer<CResult> onComplete, @Nullable Consumer<Exception> onException)
            throws NullPointerException {
        if (currentTask != null && !currentTask.isDone()) {
            return false;
        }
        currentTask = new CalculateTask(input, params, onStarted, onComplete, onException);
        currentTask.future = executor.submit(currentTask.callable);
        return true;
    }

    public synchronized boolean calculate(@NonNull CUnit[] input, @Nullable Runnable onStarted, @Nullable Consumer<CResult> onComplete,
                             @Nullable Consumer<Exception> onException) throws NullPointerException {
        return calculate(input, getParams(), onStarted, onComplete, onException);
    }

    /**
     * Handles sending {@link Message}s to the {@link Handler} based on the given state
     *
     * @param state One of {@link #CALCULATE_STARTED} or {@link #CALCULATE_COMPLETE}.
     */
    private void handleState(int state) {
        switch (state) {
            case CALCULATE_COMPLETE:
                synchronized (this) {
                    handler.obtainMessage(state, currentTask).sendToTarget();
                    currentTask = null;
                }
                break;
            default:        // CALCULATE_STARTED
                handler.obtainMessage(state, currentTask).sendToTarget();
        }
    }

//    /**
//     * @return true if there are no active tasks.
//     */
//    public boolean isDone() {
//        return currentFuture == null || currentFuture.isDone();
//    }

    /**
     * Contains a callable and the action to execute after result is calculated.
     */
    private static class CalculateTask {

        final CalculateManager manager;
        final Callable<CResult> callable;

        final CUnit[] input;
        final CParams params;
        final Runnable onStarted;
        final Consumer<CResult> onComplete;
        final Consumer<Exception> onException;
        /**
         * The future from the submit method of the executorService, set immediately after submitted.
         */
        Future<CResult> future;

        CalculateTask(@NonNull CUnit[] input, @NonNull CParams params, @Nullable Runnable onStarted,
                      @Nullable Consumer<CResult> onComplete, @Nullable Consumer<Exception> onException)
                throws NullPointerException {
            for (CUnit u: Objects.requireNonNull(input)) {
                Objects.requireNonNull(u);
            }
            manager = CalculateManager.getInstance();
            callable = new CalculateCallable(this);
            this.input = input;
            this.params = params;
            this.onStarted = onStarted;
            this.onComplete = onComplete;
            this.onException = onException;
        }

        void handleState(int state) {
            manager.handleState(state);
        }

        boolean isDone() {
            return future == null || future.isDone();
        }
    }

    private static class CalculateCallable implements Callable<CResult> {

        final CalculateTask task;

        CalculateCallable(@NonNull CalculateTask task) {
            this.task = task;
        }

        @Override
        public CResult call() throws InterruptedException,
                VariableException, UndefinedException, SyntaxException, OutOfRangeException {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            task.handleState(CALCULATE_STARTED);
            try {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                final CResult result = Calculate.calculate(new CExpression(task.input), task.params);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                return result;
            } finally {
                // exceptions are thrown after state is handled
                task.handleState(CALCULATE_COMPLETE);
            }
        }
    }
}
