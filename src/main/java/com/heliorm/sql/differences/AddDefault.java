package com.heliorm.sql.differences;

import com.heliorm.sql.Column;

public record AddDefault(Column column) implements Action  {
}
