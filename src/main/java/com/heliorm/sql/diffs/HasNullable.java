package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record HasNullable(Column column) implements Diff {
}
