package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record MissColumn(Column column) implements Diff {
}
