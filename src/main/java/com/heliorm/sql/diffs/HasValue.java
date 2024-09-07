package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record HasValue(Column column, String name) implements Diff {
}
