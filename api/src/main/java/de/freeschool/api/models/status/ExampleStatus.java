package de.freeschool.api.models.status;

public enum ExampleStatus {
    DRAFT, SUBMITTED, QUEUED, PROCESSED, CANCELLED, REJECTED;   // there is a Problem

    @Override
    public String toString() {
        return this.name();
    }
}
