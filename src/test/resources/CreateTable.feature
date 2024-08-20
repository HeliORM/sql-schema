Feature: Create a new table

  Scenario Outline:
    Given we create a table 'Person' in a '<database>' database with these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    Then the created table must have these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    | 0            | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    Examples:
      | database   |
      | mysql      |
      | postgresql |
