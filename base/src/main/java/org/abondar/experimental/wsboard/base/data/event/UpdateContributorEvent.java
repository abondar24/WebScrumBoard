package org.abondar.experimental.wsboard.base.data.event;

import org.springframework.context.ApplicationEvent;

public class UpdateContributorEvent extends ApplicationEvent {

    private Long contributorId;
    private boolean isOwner;

    public UpdateContributorEvent(Object source) {
        super(source);
    }

    public void setContributorId(Long contributorId) {
        this.contributorId = contributorId;
    }

    public Long getContributorId() {
        return contributorId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
