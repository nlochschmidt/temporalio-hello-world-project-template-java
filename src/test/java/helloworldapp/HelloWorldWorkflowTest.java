// @@@SNIPSTART hello-world-project-template-java-workflow-test
package helloworldapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HelloWorldWorkflowTest {

    @RegisterExtension
    public static final TestWorkflowExtension workflowExtension =
            TestWorkflowExtension.newBuilder()
                    .setWorkflowTypes(HelloWorldWorkflowImpl.class)
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testGetGreeting(
            Worker worker,
            TestWorkflowEnvironment environment,
            WorkflowClient client
    ) {
        worker.registerActivitiesImplementations(new FormatImpl());
        environment.start();

        HelloWorldWorkflow workflow =
                client.newWorkflowStub(
                        HelloWorldWorkflow.class,
                        WorkflowOptions.newBuilder().setTaskQueue(worker.getTaskQueue()).build());
        String greeting = workflow.getGreeting("John");
        assertEquals("Hello John!", greeting);
        environment.shutdown();
    }

    @Test
    public void testMockedGetGreeting(
            Worker worker,
            TestWorkflowEnvironment environment,
            WorkflowClient client
    ) {
        Format formatActivities = mock(Format.class, withSettings().withoutAnnotations());
        when(formatActivities.composeGreeting(anyString())).thenReturn("Hello World!");
        worker.registerActivitiesImplementations(formatActivities);
        environment.start();

        HelloWorldWorkflow workflow =
                client.newWorkflowStub(
                        HelloWorldWorkflow.class,
                        WorkflowOptions.newBuilder().setTaskQueue(worker.getTaskQueue()).build());
        String greeting = workflow.getGreeting("World");
        assertEquals("Hello World!", greeting);
        environment.shutdown();
    }
}
// @@@SNIPEND
