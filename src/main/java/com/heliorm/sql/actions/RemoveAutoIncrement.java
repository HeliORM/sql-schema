package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record RemoveAutoIncrement(Column column) implements Action {
}
