package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterFirstFilmCheckValidator.class)
@Documented
public @interface AfterFirstFilmCheck {
    
    String message() default "AfterFirstFilmValidator.invalid";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
