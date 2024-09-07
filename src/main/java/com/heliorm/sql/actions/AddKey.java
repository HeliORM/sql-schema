package com.heliorm.sql.actions;

import com.heliorm.sql.Column;

public record AddKey(Column column) implements Action {
}
