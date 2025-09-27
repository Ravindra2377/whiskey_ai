package com.boozer.nexus.bci.models;

public class RefinedMotorIntention extends MotorIntention {
    private MotorIntention originalIntention;
    private boolean continuation;

    public MotorIntention getOriginalIntention() { return originalIntention; }
    public void setOriginalIntention(MotorIntention originalIntention) { this.originalIntention = originalIntention; }
    public boolean isContinuation() { return continuation; }
    public void setContinuation(boolean continuation) { this.continuation = continuation; }
}
