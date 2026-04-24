package com.carServices.backend.security.aop;

import com.carServices.backend.dtos.LoginDto;
import com.carServices.backend.model.*;
import com.carServices.backend.repository.*;
import com.carServices.backend.shared.Identifiable;
import com.carServices.backend.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLogAspect {

    private final ActivityLogRepository repository;
    private final UserRepository userRepository;

    @AfterReturning(value = "@annotation(trackActivity)", returning = "result")
    public void log(JoinPoint joinPoint, TrackActivity trackActivity, Object result) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElse(null);

        Long entityId = null;

        if (result instanceof Identifiable identifiable) {
            entityId = identifiable.getId();
        }

        if (entityId == null) {
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof Identifiable identifiable) {
                    entityId = identifiable.getId();
                    break;
                }
                if (arg instanceof Long id) {
                    entityId = id;
                    break;
                }
            }
        }

        if (user == null) {
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof LoginDto loginDto) {
                    user = userRepository
                            .findByEmailAndDeletedAtIsNull(EmailUtils.normalize(loginDto.getEmail()))
                            .orElse(null);
                    break;
                }
            }
        }

        ActivityLog log = ActivityLog.builder()
                .action(trackActivity.action())
                .entityType(trackActivity.entityType())
                .user(user)
                .entityId(entityId)
                .build();

        repository.save(log);
    }
}
