package de.badmonkee.coronamq.core;

/**
 * Defines the states of a task.
 */
public enum TaskStatus {

    /**
     * A task has been added to the queue.
     */
    NEW,
    /**
     * A worker is working on the task but is not yet completed.
     */
    RUNNING,
    /**
     * The work has been completed. When a task is set in COMPLETED state, the task gets removed from the task queue.
     * Thus you'll never see tasks with COMPLETED status in the queue.
     */
    COMPLETED,
    /**
     * If a task's computation failed after set into RUNNING state.
     */
    FAILED;
}
