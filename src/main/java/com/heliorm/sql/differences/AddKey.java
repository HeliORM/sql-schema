package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record AddKey(Column column) implements Action {
}
