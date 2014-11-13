

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import java.util.concurrent.TimeUnit;

import org.isouth.commons.future.impl.DefaultTaskPromise;
import org.junit.Before;
import org.junit.Test;

public class DefaultTaskPromiseTest {

    private TaskPromise promise;
    private TaskFuture future;

    @Before
    public void setUp() {
        promise = new DefaultTaskPromise();
        future = promise.getFuture();
    }

    @Test
    public void testSetSuccessWithNull() throws InterruptedException, ExecutionException {
        promise.setSuccess(null);
        assertTrue(future.isDone());
        assertTrue(future.isSuccess());
        assertTrue(null == future.get());
        assertTrue(null == future.getNow());
    }

    @Test
    public void testSuccessWithObj() throws InterruptedException, ExecutionException {
        Object result = new Object();
        promise.setSuccess(result);
        assertTrue(future.isDone());
        assertTrue(future.isSuccess());
        assertTrue(result == future.get());
        assertTrue(result == future.getNow());
    }

    @Test
    public void testSetFailure() throws InterruptedException {
        Throwable cause = new IllegalStateException("test");
        promise.setFailure(cause);
        assertTrue(future.isDone());
        assertFalse(future.isSuccess());
        assertTrue(null == future.getNow());
        try {
            future.get();
            fail();
        } catch (ExecutionException e) {
        }
    }

    @Test
    public void testTrySuccessFail() {
        promise.setSuccess(null);
        assertFalse(promise.trySuccess(null));
    }

    @Test
    public void testAddAndRunCallback() {
        final String right = "right";
        final String[] result = new String[1];
        future.onComplete(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });
        promise.setSuccess(null);
        assertEquals(right, result[0]);
    }

    @Test
    public void testAddCallbackAfterComplete() {
        final String right = "right";
        final String[] result = new String[1];
        promise.setSuccess(null);

        future.onComplete(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });

        assertTrue(future.isDone());
        assertEquals(right, result[0]);
    }

    @Test
    public void testOnSuccess() {
        final String right = "right";
        final String[] result = new String[1];

        future.onSuccess(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });

        promise.setSuccess(null);
        assertEquals(right, result[0]);
    }

    @Test
    public void testOnSuccessNotRun() {
        final String right = "right";
        final String[] result = new String[1];

        future.onSuccess(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });

        promise.setFailure(new IllegalStateException());
        assertNull(result[0]);
    }

    @Test
    public void testOnFailure() {
        final String right = "right";
        final String[] result = new String[1];

        future.onFailure(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });

        promise.setFailure(new IllegalStateException("test"));
        assertEquals(right, result[0]);
    }

    @Test
    public void testOnFailureNotRun() {
        final String right = "right";
        final String[] result = new String[1];

        future.onFailure(new TaskCallback() {
            @Override
            public TaskFuture apply(TaskFuture f) {
                result[0] = right;
                return f;
            }
        });

        promise.setSuccess(null);
        assertNull(result[0]);
    }

    @Test
    public void testAwait() throws InterruptedException {
        final String[] result = new String[1];
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                promise.setSuccess(null);
            }
        });
        t.start();
        future.await();
        assertTrue(future.isSuccess());
        assertTrue(null == result[0]);
    }

    @Test
    public void testAwaitTimeout() throws InterruptedException {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                }
                promise.setSuccess(null);
            }
        });
        t.start();
        assertFalse(future.await(1, TimeUnit.SECONDS));
        assertFalse(future.isDone());
        t.interrupt();
    }
}
