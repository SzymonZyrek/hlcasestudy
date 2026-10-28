package edu.szyrek.hlcase.dto;

import edu.szyrek.hlcase.model.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// PUT retains the original example's partial-update semantics:
// id in the body, and only non-null age/gender are updated.
// A dedicated DTO is needed because @Valid on the JPA entity would reject
// omitted username/age/gender during partial updates.
public record UserAccountUpdateRequest(
        @NotNull Long id,
        @Min(1) @Max(200) Long age,
        Gender gender
) {}
