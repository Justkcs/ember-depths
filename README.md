# Ember Depths

A turn-based dungeon crawler written in Java. Choose a hero class, descend ten floors of a procedurally generated dungeon, fight monsters, collect loot, and defeat the Ember Dragon. Progress can be saved and resumed, and the best runs are kept in a persistent Hall of Fame.

The game has two interfaces that share the same domain, service and persistence code:

- **Console interface** (required by the assignment, default)
- **Graphical interface** (optional, Swing): animated dungeon scene with hand-drawn Java 2D characters, health and mana bars, floating damage numbers, a colored combat log and keyboard shortcuts 1-6

## Problem statement

**Users:** players who want a short, replayable strategy game that runs in any terminal.
**Goal:** survive a randomized dungeon by managing health, mana, items and combat style.
**Problem solved:** a lightweight game that needs no graphics, keeps a run across sessions (save/load) and records high scores between runs.

## Prerequisites

- JDK 17 or newer
- Maven 3.8 or newer

## Build, test and run

```bash
mvn clean package
java -jar target/ember-depths-1.0.0.jar          # console interface
java -jar target/ember-depths-1.0.0.jar --gui    # graphical interface
```

Run directly from sources:

```bash
mvn -q compile exec:java
mvn -q compile exec:java -Dexec.args=--gui
```

Run the full test suite:

```bash
mvn test
```

Game data is written to the `saves/` folder in the working directory (`savegame.dat` and `scores.txt`). Use `-Dember.data=<folder>` to change it.

## Sample usage

```
=========== MAIN MENU ===========
  1) New adventure
  2) Continue saved adventure
  3) Hall of Fame
  4) How to play
  5) Exit
> 1
Choose your class:
  1) Warrior  Sturdy fighter with high health and defense. Skill: Whirlwind Strike
  2) Mage     Fragile caster whose Fireball ignores armor. Skill: Fireball
  3) Rogue    Agile trickster with critical strikes and easy escapes. Skill: Backstab
> 1
Name your hero (2-16 letters or digits): Aria

------ Floor 1/10 | Rooms explored 2/30 ------
  Aria             HP [####################] 120/120  MP 30/30  [Balanced]
> 1
  Floor 1, room 3: you step into a monster lair.
  > A Goblin blocks your path!
  1) Attack  2) Whirlwind Strike (10 MP)  3) Use item  4) Combat style  5) Flee
> 2
  > Aria spins into a Whirlwind Strike for 25 damage!
```

## Implemented requirements

| Area | Where |
|---|---|
| Encapsulation | All fields are `private`; constructors validate state (`Creature`, `Hero`, `Room`, `ScoreEntry`); stats change only through methods such as `heal`, `receiveDamage`, `gainExperience`. `Inventory.getItems()` returns a read-only view. |
| Abstraction | Abstract classes `Creature`, `Hero`, `Monster`, `Item`; interfaces `AttackStrategy`, `GameRepository`, `ScoreRepository`, `GameEventListener`. |
| Inheritance and polymorphism | `Warrior`, `Mage`, `Rogue` override `performSkill`, `applyLevelUpBonus`, `getFleeChance`. `Goblin`, `Skeleton`, `Orc`, `Dragon` override `attack`. `HealthPotion`, `ManaPotion`, `StrengthElixir` override `use`. `GameService` only works with the parent types. |
| Relationships | `GameState` 1 -> 1 `Hero` and 1 -> 1 `Dungeon` (composition); `Dungeon` 1 -> 1..* `Room` (composition); `Room` 1 -> 0..1 `Monster` and 0..1 `Item`; `Hero` 1 -> 1 `Inventory` 1 -> 0..8 `Item` (aggregation); `Hero` -> 1 `AttackStrategy` (association). |
| Collections and generics | Generic class `Inventory<T extends Item>` backed by `ArrayList<T>`; `ScoreEntry implements Comparable<ScoreEntry>`; comparators and lambdas in `ScoreService.byDeepestFloor()`, `byName()` and the inventory sort menu. |
| Exceptions | Custom checked `InsufficientManaException` (domain rule: a skill needs enough mana), unchecked `InventoryFullException` (domain rule: bag capacity), checked `PersistenceException`, checked `InvalidInputException`, `InputClosedException`. All user input is validated and re-asked. |
| Persistence | Save game via Java serialization (`SerializedGameRepository`); Hall of Fame as a text file (`TextScoreRepository`). Both use try-with-resources; corrupted lines or files are handled. |
| Testing | JUnit 5 tests in `src/test/java` (`mvn test`). |
| Build | Maven project, conventional layout, runnable jar. |

## Design patterns

### Strategy (main pattern)
- **Participants:** `AttackStrategy` (strategy interface), `BalancedStrategy`, `AggressiveStrategy`, `DefensiveStrategy` (concrete strategies), `Hero` (context), `GameService.changeStrategy` / `ConsoleUI.strategyMenu` (client).
- **Problem:** the player must be able to change how the hero fights at any time, which changes both outgoing damage and incoming damage reduction.
- **Benefit:** `Hero.rollAttackDamage` and `Hero.getEffectiveDefense` delegate to the current strategy, so new combat styles can be added without touching `Hero`, `GameService` or any monster class. The strategy is swapped at runtime, even in the middle of a fight.

### Factory
- **Participants:** `HeroFactory`, `MonsterFactory`, `ItemFactory`, `DungeonFactory`.
- **Benefit:** the UI and the service never call `new Goblin(...)` or `new Warrior(...)` directly. Monster selection by floor, item drop chances and dungeon layout are centralized in one place each.

### Observer
- **Participants:** `GameEventListener` (observer), `GameService` (subject), `ConsoleUI` (concrete observer), `GameEvent` / `EventType` (message).
- **Participants (GUI):** `GamePanel` is a second concrete observer that writes the same events into a colored log.
- **Benefit:** the game logic never prints anything. It publishes events, and each view decides how to display them. This keeps the service testable (tests subscribe with a lambda) and made it possible to add the Swing interface without changing a single line of `GameService`.

### Polymorphism in the GUI
`SpriteRenderer` draws every hero and monster with Java 2D. It chooses the picture from `Hero.getHeroClass()` and `Monster.getType()`, and the dragon glows when the overridden `Monster.isCharging()` says its fire breath is coming.

## Package structure

```
com.emberdepths
 ├─ App                      entry point
 ├─ domain                   Creature, GameState, ScoreEntry
 │   ├─ hero                 Hero, Warrior, Mage, Rogue, HeroClass, SkillResult
 │   ├─ monster              Monster, Goblin, Skeleton, Orc, Dragon, MonsterType, AttackOutcome
 │   ├─ item                 Item, HealthPotion, ManaPotion, StrengthElixir, Inventory<T>
 │   └─ world                Dungeon, Room, RoomType
 ├─ strategy                 AttackStrategy and its implementations
 ├─ factory                  HeroFactory, MonsterFactory, ItemFactory, DungeonFactory
 ├─ service                  GameService, ScoreService
 │   └─ event                GameEvent, EventType, GameEventListener
 ├─ persistence              GameRepository, SerializedGameRepository, ScoreRepository, TextScoreRepository
 ├─ exception                custom exceptions
 ├─ ui                       ConsoleUI, ConsoleInput (console interface)
 └─ gui                      GameWindow, MenuPanel, CreateHeroPanel, GamePanel, ScenePanel,
                             LeaderboardPanel, SpriteRenderer, Theme (Swing interface)
```

## Design decisions

- `GameFactory` builds a new run (dungeon and starting items) so both interfaces start games the same way.
- The whole dungeon is generated at the start of a run and stored in `GameState`, so a saved game resumes with the exact same rooms.
- Saving is only possible outside of combat, which keeps the save format simple.
- `Random` is injected everywhere, which makes combat deterministic in tests (`FixedRandom`).
- The Hall of Fame keeps only the top 10 runs; malformed lines in `scores.txt` are skipped instead of crashing the game.

## Known limitations

- The GUI uses shapes drawn in code instead of image files, so the art style is simple.
- One save slot.
- Save files created by a different version of the classes may not load (reported as a friendly error).
- Balance is tuned by hand; a careless attack-only run usually dies to the dragon.

## Documentation

- UML class diagram: [`docs/uml-class-diagram.md`](docs/uml-class-diagram.md) (Mermaid, renders on GitHub/GitLab) and editable source [`docs/uml-class-diagram.puml`](docs/uml-class-diagram.puml) (PlantUML)
- User stories: [`docs/user-stories.md`](docs/user-stories.md)
- Release notes: [`RELEASE_NOTES.md`](RELEASE_NOTES.md)

## Contributors

- Kemal Can Servi (Neptun: QSXM3M), GitHub [@Justkcs](https://github.com/Justkcs)

## License

MIT, see [LICENSE](LICENSE).
