package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class AfterFirstFilmCheckValidator implements ConstraintValidator<AfterFirstFilmCheck, LocalDate> {
    
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate firstFilmDate = LocalDate.of(1895, Month.DECEMBER, 28);
        return value != null && value.isAfter(firstFilmDate);
    }
}
