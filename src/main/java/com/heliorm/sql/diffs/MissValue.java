package com.heliorm.sql.diffs;

import com.heliorm.sql.Column;

public record MissValue(Column column,String name) implements Diff {
}
