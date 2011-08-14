package de.mobile.siteops;

public enum HostStateType {
	QUEUED(0), IN_PROGRESS(1), DEPLOYED(2), ERROR(3), CANCELLED(4), ABORTED(5);

    private int priority;

    private HostStateType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isHigherPriority(HostStateType other) {
        return other.priority > this.priority;
    }

    public static boolean isFailed(HostStateType other) {
        return other == ABORTED || other == ERROR || other == CANCELLED;
    }
}
