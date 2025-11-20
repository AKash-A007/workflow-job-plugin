package org.jenkinsci.plugins.workflow.job;

import hudson.model.Queue;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.test.JenkinsRuleExt;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowRunQueueIdTest {

    @Test
    public void queueIdIsPersistedFromQueue() throws Exception {
        JenkinsRule j = JenkinsRuleExt.create();

        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "unluckyJob");
        job.setDefinition(new CpsFlowDefinition("sleep 5", true));

        Queue.Task task = job;
        Queue.Item queueItem = j.jenkins.getQueue().schedule2(task, 0).getItem();
        assertNotNull(queueItem, "Queue should contain the item");

        // Retrieve run AFTER queue creation
        WorkflowRun run = job.getBuildByNumber(1);
        assertNotNull(run, "Run should be created");

        // The current behavior is: queueId may mismatch
        long queueIdFromRun = run.getQueueId();
        long queueIdFromQueue = queueItem.getId();

        // By default this assertion FAILS → proves bug exists
        assertEquals(queueIdFromQueue, queueIdFromRun,
                "WorkflowRun.queueId should match Queue.Item.id");
    }
}
