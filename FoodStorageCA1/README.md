# CA1---Algorithms-Architecture
Group CA
# Party Food Storage Unit — CA1 Algorithms & Data Structures

A Java console application that models the party food storage unit of a fast-food
restaurant, built on our own fixed-capacity **circular-array deque**.


| **Module** | Algorithms & Data Structures (CA1) |
| **Team** | Maycon and Grazielle |
| **Repository** | https://github.com/MikeAlme13/FoodStorageCA1 |

## 1. The scenario

The storage unit holds **at most 8 trays** of party food. Each tray records:

- the **food**: Burger, Pizza, Fries, Sandwich or Hot Dog;
- its **weight** in grams (1–5000);
- its **best-before date**, today up to **14 days** ahead;
- the **time it was stored**, stamped automatically.

The chef works in one of two ways, and the brief allows both:

- **front side for adding and removing** → last in, first out → a **stack**;
- **front for adding, opposite side for removing** → first in, first out → a **queue**.

## 2. The data structure we chose

A **deque** (double-ended queue) on a **circular array of 8**.

A deque adds and removes at *both* ends in O(1), so it is a stack and a queue at
the same time. The chef picks a mode at start-up and can switch it at any time
with menu option 5 — and **not one tray moves when the mode changes**, because
only the end that `remove()` reads changes.

### Why not something else

| Option | Problem for this scenario |
|---|---|
| Stack only | Covers just one of the two chef options |
| Queue only | Covers just the other one |
| `ArrayList` | Removing from the front shifts every element — O(n); no built-in limit of 8 |
| `LinkedList` / `java.util.ArrayDeque` | Grow without limit, so the 8-tray rule has to be bolted on; hides how the structure works |
| Plain array, no wrap-around | Adding at the front needs shifting — O(n) |
| **Circular-array deque (ours)** | **O(1) at both ends, capacity 8 built in, no shifting** |

The storage unit physically has two sides; a deque has two ends; the fixed array
of 8 *is* the physical capacity. **Queue (FIFO) is the recommended default**,
because food with a best-before date should leave in the order it arrived, which
reduces waste.

### How the circle works

The deque stores only two numbers: `front` (the array index of the first tray)
and `size`. The back is **calculated, never stored**. The `%` operator makes
index 0 follow index 7, so the array behaves like a ring and nothing ever shifts:

```
addFirst:     front = (front - 1 + 8) % 8
removeFirst:  front = (front + 1) % 8
back:         (front + size - 1) % 8
addLast:      (front + size) % 8
```

The `+ 8` stops the index going negative: from index 0, one step left is the last
cell of the array, not `-1`.

Trace from empty (`front = 0`, `size = 0`, capacity 8):

| Step | Operation | Calculation | front | size | Array (0–7) | Front → back |
|---|---|---|---|---|---|---|
| 1 | add Burger | `(0 - 1 + 8) % 8 = 7` | 7 | 1 | `_ _ _ _ _ _ _ Bu` | Burger |
| 2 | add Pizza | `(7 - 1 + 8) % 8 = 6` | 6 | 2 | `_ _ _ _ _ _ Pi Bu` | Pizza, Burger |
| 3 | add Fries | `(6 - 1 + 8) % 8 = 5` | 5 | 3 | `_ _ _ _ _ Fr Pi Bu` | Fries, Pizza, Burger |
| 4 | remove, **Queue** | `back = (5 + 3 - 1) % 8 = 7` → Burger out | 5 | 2 | `_ _ _ _ _ Fr Pi _` | Fries, Pizza |
| 5 | remove, **Stack** | Fries out, `front = (5 + 1) % 8 = 6` | 6 | 1 | `_ _ _ _ _ _ Pi _` | Pizza |

Step 1 is the wrap-around. Step 4 shows Queue mode removing the oldest tray
(Burger, stored first); step 5 shows Stack mode removing the newest (Fries).
The test `wrapsAroundCorrectlyManyTimes` pushes 100 trays through the 8 cells to
prove the indexes stay correct.

## 3. Time and space complexity

`n` = trays stored (at most 8); `k` = number of matches returned.

| Operation | Method | Time | Space | Reason |
|---|---|---|---|---|
| Add | `add` → `addFirst` | **O(1)** | O(1) | One index calculation and one array write, no shifting |
| Remove (Stack/LIFO) | `removeFirst` | **O(1)** | O(1) | Read one cell, move `front` by one |
| Remove (Queue/FIFO) | `removeLast` | **O(1)** | O(1) | Calculate `back`, read one cell, `size--` |
| Peek top / next out | `peekFirst` / `peekLast` | **O(1)** | O(1) | Direct array access by index |
| Read by position | `get(i)` | **O(1)** | O(1) | `(front + i) % capacity` |
| Is empty / is full | compare `size` | **O(1)** | O(1) | `size == 0` or `size == 8` |
| Change mode | `setMode` | **O(1)** | O(1) | Only a variable changes; no tray moves |
| Display all | `toList` | O(n) | O(n) | Visits every tray once and copies it to a list |

Overall memory is **O(8)**: the array is allocated once, at a fixed size.

**Why is only "display all" O(n)?** It is the one operation that has to visit
every tray, copying each into a list to be printed. Every other operation
touches a single cell of the array, found by one index calculation.

Menu option **6** prints this whole table at runtime.

## 4. Project structure

Four packages, so each class has exactly one job. The menu never touches the
array — it only calls `FoodStorage`.

```
src/main/java/foodstorage/
├── model/
│   ├── FoodType.java            enum: the 5 foods, parses user input
│   ├── FoodItem.java            one tray: type, weight, best-before, time added
│   └── StorageMode.java         enum: STACK_LIFO or QUEUE_FIFO
├── datastructure/
│   ├── CircularDeque.java       the generic data structure
│   └── FoodStorage.java         the storage unit with the chef's rules
├── exception/
│   ├── StorageFullException.java   thrown on a 9th tray
│   └── StorageEmptyException.java  thrown on remove/peek/display when empty
└── ui/
    ├── InputHelper.java         reads and validates all typed input
    ├── StorageMenu.java         the console menu, options 0–6
    └── FoodStorageApp.java      main method
```

| Class | What it does | Key points |
|---|---|---|
| `FoodType` | The 5 foods | An enum makes an invalid food impossible. `fromInput()` accepts `pizza`, `PIZZA`, `hot dog`, `hotdog` or a number 1–5 |
| `FoodItem` | One tray | Fields are `private final`. The constructor validates everything, so an invalid tray can never exist. `timeAdded` is set with `LocalDateTime.now()` |
| `StorageMode` | The chef's two options | `other()` gives the opposite mode for option 5 |
| `CircularDeque<T>` | The data structure | Generic, fixed array, `front` + `size`. `addFirst`, `addLast`, `removeFirst`, `removeLast`, `peekFirst`, `peekLast`, `get(i)`, `toList`, `isEmpty`, `isFull` |
| `FoodStorage` | The unit with the rules | Always adds at the front. `remove()` uses the front in Stack mode, the back in Queue mode. `CAPACITY = 8` |
| `StorageFullException` | 9th tray | Checked exception, so a full unit must be handled |
| `StorageEmptyException` | No data | Covers the "error handling when there is no data" requirement |
| `InputHelper` | Validation | Every method loops until the input is valid, so bad input never crashes the app |
| `StorageMenu` | The menu | Options 0–6, catches both custom exceptions |
| `FoodStorageApp` | `main` | Opens a `Scanner` and starts the menu |

**OOP principles used:** encapsulation (`private final` fields, getters only),
abstraction (the menu uses `FoodStorage` without knowing about the array),
generics (`CircularDeque<T>` works with any type), enums, custom checked
exceptions, records, and separation of concerns (UI, logic and data in different
packages).

