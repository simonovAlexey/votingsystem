package com.simonov.voting.util.validation;

import com.simonov.voting.controller.HasId;
import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.Role;
import com.simonov.voting.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;

import java.util.Collections;

@UtilityClass
public class ValidationUtil {

    public static void checkNew(HasId hasId) {
        if (!hasId.isNew()) {
            throw new IllegalRequestException(hasId.getClass().getSimpleName() + " must be new ( id must be null )");
        }
    }

    public static void checkUserRole(User user) {
        if (user.getRoles() == null) {
            user.setRoles(Collections.singleton(Role.USER));
        } else if (user.getRoles().contains(Role.ADMIN)) {
            throw new IllegalRequestException("User can't register as admin");
        }
    }

    public static void checkAndSetHasId(HasId hasId, int id) {
        if (hasId.isNew()) {
            hasId.setId(id);
        } else if (hasId.id() != id) {
            throw new IllegalRequestException(hasId.getClass().getSimpleName() + " id must be the same: " + id + " <-> " + hasId.id());
        }
    }

    public static void checkCount(int count, int id) {
        if (count == 0) {
            throw new IllegalRequestException("Not found record with id= " + id);
        }
    }

    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }
}