package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record HasDefault(Column column) implements Diff {
}
