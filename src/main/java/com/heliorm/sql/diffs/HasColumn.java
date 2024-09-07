package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record HasColumn(Column column) implements Diff {
}
