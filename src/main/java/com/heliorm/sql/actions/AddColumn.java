package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record AddColumn(Column column) implements Action {
}
