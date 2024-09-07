package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record DecreaseLength(Column column) implements Action {
}
