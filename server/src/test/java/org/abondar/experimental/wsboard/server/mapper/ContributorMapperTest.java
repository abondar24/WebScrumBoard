package org.abondar.experimental.wsboard.server.mapper;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContributorMapperTest extends MapperTest{

    @Test
    public void insertContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        assertTrue(ctr.getId() > 0);

    }

    @Test
    public void getContributorByIdTest() {

        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);


        var res = contributorMapper.getContributorById(ctr.getId());
        assertEquals(ctr.getId(), res.getId());
    }

    @Test
    public void updateContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        ctr.setActive(false);
        contributorMapper.updateContributor(ctr);

        var res = contributorMapper.getContributorById(ctr.getId());

        assertFalse(res.isActive());
    }

    @Test
    public void getContributorByUserAndProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        var res = contributorMapper.getContributorByUserAndProject(user.getId(), project.getId());
        assertEquals(ctr.getId(), res.getId());

    }

    @Test
    public void getContributorByNameTest() {
        cleanData();

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        var res = contributorMapper.getContributorByLogin(project.getId(), user.getLogin());
        assertEquals(ctr.getId(), res.getId());
    }

    @Test
    public void countProjectContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = contributorMapper.countProjectContributors(project.getId());
        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void getContributorsByUserId() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = contributorMapper.getContributorsByUserId(user.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getUserId());

    }

    @Test
    public void deactivateUserContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        contributorMapper.deactivateUserContributors(user.getId());
        var res = contributorMapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }

    @Test
    public void deactivateProjectContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        contributorMapper.deactivateProjectContributors(project.getId());
        var res = contributorMapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }

    @Test
    public void deleteProjectContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);

        contributorMapper.deleteProjectContributors(project.getId());

        var res = contributorMapper.getContributorById(contributor.getId());
        assertNull(res);

    }

    @Test
    public void getContributorsForProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var project1 = createProject();
        var inactiveCtr = createContributor(user.getId(), project1.getId(), false);
        inactiveCtr.setActive(false);
        contributorMapper.insertContributor(inactiveCtr);

        var res = userMapper.getContributorsForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getId());

    }
}
