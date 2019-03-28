package org.abondar.experimental.wsboard.base.data.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;


public class EventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishContributorUpdate(long contributorId, boolean isOwner){
        var upe = new UpdateContributorEvent(this);
        upe.setContributorId(contributorId);
        upe.setOwner(isOwner);

        eventPublisher.publishEvent(upe);
    }
}
