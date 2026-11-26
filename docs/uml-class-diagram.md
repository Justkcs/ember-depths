# UML Class Diagram

```mermaid
classDiagram
    direction TB

    class Creature {
        <<abstract>>
        -String name
        -int maxHealth
        -int health
        -int attack
        -int defense
        +receiveDamage(int) int
        +receiveTrueDamage(int) int
        +heal(int) int
        +isAlive() boolean
        #getEffectiveDefense() int
        +getTypeName()* String
    }

    class Hero {
        <<abstract>>
        -int level
        -int experience
        -int gold
        -int mana
        -int maxMana
        +rollAttackDamage(Random) int
        +useSkill(Creature, Random) SkillResult
        #performSkill(Creature, Random)* SkillResult
        #applyLevelUpBonus()*
        +getFleeChance() int
        +gainExperience(int) int
        +setStrategy(AttackStrategy)
    }
    class Warrior
    class Mage
    class Rogue

    class Monster {
        <<abstract>>
        -int experienceReward
        -int goldReward
        +attack(Hero, Random)* AttackOutcome
        +getType()* MonsterType
        +isBoss() boolean
        +isCharging() boolean
    }
    class Goblin
    class Skeleton
    class Orc
    class Dragon {
        -int turnCounter
    }

    class Item {
        <<abstract>>
        -String name
        -int value
        +use(Hero)* String
        +getDescription()* String
    }
    class HealthPotion
    class ManaPotion
    class StrengthElixir

    class Inventory~T extends Item~ {
        -int capacity
        -ArrayList~T~ items
        +add(T)
        +removeAt(int) T
        +sort(Comparator)
        +getItems() List~T~
    }

    class AttackStrategy {
        <<interface>>
        +getName() String
        +calculateDamage(int, Random) int
        +adjustDefense(int) int
    }
    class BalancedStrategy
    class AggressiveStrategy
    class DefensiveStrategy

    class Dungeon {
        -ArrayList~Room~ rooms
        -int currentIndex
        +advance() Room
        +hasNextRoom() boolean
    }
    class Room {
        -int floor
        -int number
        -RoomType type
        -int gold
    }
    class GameState {
        -int monstersDefeated
        -boolean bossDefeated
        +recordMonsterDefeated(Monster)
    }
    class ScoreEntry {
        -String heroName
        -int score
        +compareTo(ScoreEntry) int
        +toLine() String
        +fromLine(String)$ ScoreEntry
    }

    class GameEventListener {
        <<interface>>
        +onEvent(GameEvent)
    }
    class GameService {
        -Monster currentEnemy
        +enterNextRoom() Room
        +attack()
        +useSkill()
        +useItem(int) Item
        +flee() boolean
        +changeStrategy(AttackStrategy)
        +calculateScore() int
    }
    class ScoreService {
        +record(ScoreEntry) int
        +getLeaderboard(Comparator) List~ScoreEntry~
    }

    class GameRepository {
        <<interface>>
        +save(GameState)
        +load() GameState
    }
    class SerializedGameRepository
    class ScoreRepository {
        <<interface>>
        +loadAll() List~ScoreEntry~
        +saveAll(List~ScoreEntry~)
    }
    class TextScoreRepository

    class HeroFactory {
        +create(HeroClass, String) Hero
    }
    class MonsterFactory {
        +createForFloor(int) Monster
        +createBoss() Monster
    }
    class ItemFactory {
        +createRandom() Item
    }
    class DungeonFactory {
        +create(int, int) Dungeon
    }

    class GameFactory {
        +createNewGame(Hero) GameState
    }

    class ConsoleUI
    class ConsoleInput

    class GameWindow
    class GamePanel
    class ScenePanel
    class SpriteRenderer {
        +drawHero(Graphics2D, HeroClass, ...)$
        +drawMonster(Graphics2D, Monster, ...)$
    }
    class MenuPanel
    class CreateHeroPanel
    class LeaderboardPanel

    Creature <|-- Hero
    Creature <|-- Monster
    Hero <|-- Warrior
    Hero <|-- Mage
    Hero <|-- Rogue
    Monster <|-- Goblin
    Monster <|-- Skeleton
    Monster <|-- Orc
    Monster <|-- Dragon
    Item <|-- HealthPotion
    Item <|-- ManaPotion
    Item <|-- StrengthElixir
    AttackStrategy <|.. BalancedStrategy
    AttackStrategy <|.. AggressiveStrategy
    AttackStrategy <|.. DefensiveStrategy
    GameRepository <|.. SerializedGameRepository
    ScoreRepository <|.. TextScoreRepository
    GameEventListener <|.. ConsoleUI

    Hero "1" *-- "1" Inventory : owns
    Inventory "1" o-- "0..8" Item : holds
    Hero "1" --> "1" AttackStrategy : uses
    GameState "1" *-- "1" Hero
    GameState "1" *-- "1" Dungeon
    Dungeon "1" *-- "1..*" Room
    Room "1" o-- "0..1" Monster
    Room "1" o-- "0..1" Item

    GameService "1" --> "1" GameState
    GameService "1" --> "0..*" GameEventListener : notifies
    ScoreService --> ScoreRepository
    ScoreService ..> ScoreEntry
    ConsoleUI --> GameService
    ConsoleUI --> ScoreService
    ConsoleUI --> GameRepository
    ConsoleUI --> ConsoleInput
    ConsoleUI ..> HeroFactory
    ConsoleUI ..> DungeonFactory
    DungeonFactory --> MonsterFactory
    DungeonFactory --> ItemFactory
    GameFactory --> DungeonFactory
    ConsoleUI --> GameFactory
    GameWindow --> GameFactory

    GameEventListener <|.. GamePanel
    GameWindow "1" *-- "1" MenuPanel
    GameWindow "1" *-- "1" CreateHeroPanel
    GameWindow "1" *-- "1" GamePanel
    GameWindow "1" *-- "1" LeaderboardPanel
    GamePanel "1" *-- "1" ScenePanel
    GamePanel --> GameService
    ScenePanel ..> SpriteRenderer
    LeaderboardPanel --> ScoreService
```

## Pattern mapping

| Pattern | Role | Classes |
|---|---|---|
| Strategy | Strategy | `AttackStrategy` |
| | Concrete strategies | `BalancedStrategy`, `AggressiveStrategy`, `DefensiveStrategy` |
| | Context | `Hero` (`rollAttackDamage`, `getEffectiveDefense`) |
| Factory | Creators | `HeroFactory`, `MonsterFactory`, `ItemFactory`, `DungeonFactory` |
| | Products | `Hero`, `Monster`, `Item`, `Dungeon` |
| Observer | Subject | `GameService` |
| | Observer | `GameEventListener`, implemented by `ConsoleUI` and `GamePanel` |
| Factory | Run creation | `GameFactory` used by both interfaces |
