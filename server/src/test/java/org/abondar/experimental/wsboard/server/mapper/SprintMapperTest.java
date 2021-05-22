package org.abondar.experimental.wsboard.server.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SprintMapperTest extends MapperTest{

    @Test
    public void getSprintByIdTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());
        var res = sprintMapper.getSprintById(sprint.getId());
        assertEquals(sprint.getName(), res.getName());
    }

    @Test
    public void getSprintByNameTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());
        var res = sprintMapper.getSprintByName(sprint.getName());
        assertEquals(sprint.getName(), res.getName());

    }

    @Test
    public void getSprintsTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);
        createSprint(prId);
        createSprint(prId);

        var sprints = sprintMapper.getSprints(prId, 0, 3);
        assertEquals(3, sprints.size());

    }

    @Test
    public void getSprintsNullLimitTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);
        createSprint(prId);
        createSprint(prId);

        var sprints = sprintMapper.getSprints(prId, 0, null);
        assertEquals(3, sprints.size());

    }

    @Test
    public void getCurrentSprintTest() {
        cleanData();
        var prId = createProject().getId();
        var sprint = createSprint(prId);

        sprint.setCurrent(true);
        sprintMapper.updateSprint(sprint);

        var res = sprintMapper.getCurrentSprint(prId);

        assertTrue(res.isCurrent());
        assertEquals(sprint.getId(), res.getId());
    }

    @Test
    public void countSprintsTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);

        var res = sprintMapper.countSprints(prId);

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void deleteSprintTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());

        sprintMapper.deleteSprint(sprint.getId());

        var res = sprintMapper.getSprintById(sprint.getId());
        assertNull(res);

    }

    @Test
    public void deleteProjectSprints() {
        cleanData();
        var project = createProject();
        var sprint = createSprint(project.getId());

        sprintMapper.deleteProjectSprints(project.getId());

        var res = sprintMapper.getSprintById(sprint.getId());
        assertNull(res);

    }

}
