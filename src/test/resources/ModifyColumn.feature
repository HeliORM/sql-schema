Feature: Modify a column in a table

  Scenario Outline:
    Given we create a table 'Person' in a '<database>' database with these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                |
      | id        | INTEGER  |        | false    |              | true | true          |                       |
      | name      | VARCHAR  | 42     | true     |              |      |               |                       |
      | age       | SMALLINT |        | true     |              |      |               |                       |
      | direction | ENUM     |        | true     |              |      |               | NORTH,EAST,SOUTH,WEST |
    And we modify these columns
      | name      | type     | length | nullable | defaultValue | key | autoincrement | values                                                                |
      | name      | VARCHAR  | 64     | true     |              |     |               |                                                                       |
      | age       | SMALLINT |        | false    |              |     |               |                                                                       |
      | direction | ENUM     |        | false    | NORTH        |     |               | NORTH,EAST,SOUTH,WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST |
    Then the created table must have these columns
      | name      | type     | length | nullable | defaultValue | key  | autoincrement | values                                                               |
      | id        | INTEGER  |        | false    |              | true | true          |                                                                      |
      | name      | VARCHAR  | 64     | true     |              |      |               |                                                                      |
      | age       | SMALLINT |        | false    |              |      |               |                                                                      |
      | direction | ENUM     |        | false    | NORTH        |      |               | NORTH,EAST,SOUTH,WEST,NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST |
    Examples:
      | database   |
      | mysql      |
      | postgresql |

