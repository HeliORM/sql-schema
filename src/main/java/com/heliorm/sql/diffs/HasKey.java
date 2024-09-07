package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record HasKey(Column column) implements Diff {
}
