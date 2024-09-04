package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record AddAutoIncrement(Column column) implements Action {
}
