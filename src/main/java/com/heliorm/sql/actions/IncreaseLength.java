package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record IncreaseLength(Column column) implements Action {
}
