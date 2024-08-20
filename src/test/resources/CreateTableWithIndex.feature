Feature: Create a new table with an index

  Scenario Outline:
    Given we create a table 'Person' in a '<database>' database with these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    And we then add this index 'name_idx' to the table
      | column |
      | name   |
    Then the created table must have these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    And we the created table must have this index
      | column |
      | name   |
    Examples:
      | database   |
      | mysql      |
      | postgresql |

