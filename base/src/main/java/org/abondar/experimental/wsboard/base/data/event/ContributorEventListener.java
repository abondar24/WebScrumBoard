package org.abondar.experimental.wsboard.base.data.event;

import org.abondar.experimental.wsboard.base.data.dao.ContributorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ContributorEventListener  implements ApplicationListener<UpdateContributorEvent> {

    @Autowired
    private ContributorDao contributorDao;

    @Override
    public void onApplicationEvent(UpdateContributorEvent event) {
            contributorDao.updateContributorAsOwner(event.getContributorId(),event.isOwner(),false);
    }
}
