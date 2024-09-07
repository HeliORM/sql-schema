package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record ChangeType(Column column) implements Action {
}
