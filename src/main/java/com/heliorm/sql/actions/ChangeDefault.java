package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record ChangeDefault(Column column) implements Action {
}
