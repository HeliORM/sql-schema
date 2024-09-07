package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record ChangeDefault(Column column) implements Action {
}
