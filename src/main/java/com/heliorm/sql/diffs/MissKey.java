package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record MissKey(Column column) implements Diff {
}
