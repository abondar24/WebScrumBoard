package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.datamodel.TaskState;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);

    private DataMapper mapper;

    private Map<UserRole, List<TaskState>> roleStates;

    public TaskDao(DataMapper mapper) {
        this.mapper = mapper;
        this.roleStates = new HashMap<>();
        fillPermitted();
    }



    //TODO: for task state update in case of manager check if he is owner of the project,check if user deleted

    private void fillPermitted(){
        for (UserRole ur: EnumSet.allOf(UserRole.class)){
            switch (ur){
                case Developer:
                    var devStates = List.of(TaskState.InDevelopment,TaskState.Paused,
                            TaskState.InCodeReview,TaskState.InTest);
                    roleStates.put(ur, devStates);
                    break;
                case QA:
                    var qaStates = List.of(TaskState.InTest,TaskState.InDeployment,TaskState.InDevelopment,TaskState.Paused,TaskState.Completed);
                    roleStates.put(ur, qaStates);
                    break;
                case DevOps:
                    var devOpsStates = List.of(TaskState.InDeployment,TaskState.InDevelopment,
                            TaskState.Paused,TaskState.Completed);
                    roleStates.put(ur, devOpsStates);
                    break;
                case Manager:
                    var managerStates = List.of(TaskState.InDevelopment,
                            TaskState.InDeployment,TaskState.Paused,
                            TaskState.InCodeReview,TaskState.InTest,TaskState.Completed);
                    roleStates.put(ur, managerStates);
                    break;
            }
        }
    }


}
