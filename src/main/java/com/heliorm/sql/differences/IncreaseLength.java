package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record IncreaseLength(Column column) implements Action {
}
