package com.spring.task.util;

import com.spring.task.enumration.AppRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonUtilsTest {

    @Test
    public void testFetchAllAppRoles() {
        List<String> roles = CommonUtils.fetchAllAppRoles();

        assertEquals(AppRole.values().length, roles.size());
        for (AppRole appRole : AppRole.values()) {
            assert(roles.contains(appRole.getRoleName()));
        }
    }
}
