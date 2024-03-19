package com.spring.task.util;

import com.spring.task.enumration.AppRole;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommonUtils {

    public static List<String> fetchAllAppRoles() {

        return Arrays.stream(AppRole.values())
                .map(AppRole::getRoleName)
                .toList();
    }
}
