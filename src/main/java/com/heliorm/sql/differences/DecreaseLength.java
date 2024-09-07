package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record DecreaseLength(Column column) implements Action {
}
