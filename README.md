# Party Food Storage Unit — CA1 Algorithms & Data Structures

A Java console application that models the party food storage unit of a fast-food
restaurant, built on our own fixed-capacity **circular-array deque**.

| | |
|---|---|
| **Module** | Algorithms & Data Structures (CA1) |
| **Team** | Maycon and Grazielle |
| **Repository** | https://github.com/MikeAlme13/CA1---Algorithms-Architecture |
| **Java** | JDK 17 or newer |
| **Build** | Maven (NetBeans opens it with no setup) |
| **Tests** | 27 JUnit 5 tests, all passing |

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
FoodStorageCA1/src/main/java/foodstorage/
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

### Menu

```
1. Add a tray            4. Display all trays
2. Remove a tray         5. Change mode (Stack / Queue)
3. Peek at the top tray  6. Time complexity report
                         0. Exit (asks to confirm)
```

## 5. How to run it

### NetBeans (recommended)

1. Clone the repository (or download the ZIP) somewhere **outside OneDrive and
   Downloads**, for example `Documents\NetBeansProjects`.
2. **File → Open Project** and select the `FoodStorageCA1` folder inside it
   (it shows a Maven icon).
3. Wait for NetBeans to download JUnit the first time (progress bar, bottom right).
4. **Right-click the project → Run.** The menu appears in the Output window; type
   your answers there.
5. **Right-click the project → Test.** The Test Results window shows
   **27 tests passed**.

If NetBeans asks for a main class, choose `foodstorage.ui.FoodStorageApp`.

### Command line

```bash
cd FoodStorageCA1
mvn test                 # runs the 27 JUnit tests
mvn compile exec:java    # runs the application
```

## 6. Validation and testing

**27 JUnit 5 tests in 4 files:**

| File | Tests | Covers |
|---|---|---|
| `CircularDequeTest` | 7 | Stack order, queue order, the wrap-around over 100 passes, the full and empty errors, `get`/`toList` ordering |
| `FoodStorageTest` | 9 | Both chef modes, mode switching without reordering, the 9th-tray refusal, every empty-storage error |
| `FoodItemTest` | 6 | Every validation rule, including the exact boundaries (1 g, 5000 g, today, +14 days) |
| `StorageMenuTest` | 5 | Types scripted input into the **real** menu and checks the messages a user would see |

Input is validated twice on purpose: `InputHelper` re-asks until the input is
valid, and `FoodItem`'s constructor checks the same rules again, so an invalid
tray cannot exist even if a caller skips the helper.

### Manual test cases for the demo

| # | Input | Expected result |
|---|---|---|
| 1 | Option 2, 3 or 4 before adding anything | "Storage is EMPTY", app keeps running |
| 2 | Food name `cake`, then `9` | "not a food we store", asks again |
| 3 | Weight `abc`, then `-5`, then `99999` | "not a valid number" / "out of range" |
| 4 | Date `31/02/2026` | "not a valid date" (strict parsing) |
| 5 | Yesterday's date | "cannot be in the past" |
| 6 | A date 20 days ahead | "is too far ahead" |
| 7 | `pizza`, `350`, a date 5 days ahead | "Added at the FRONT" with full details and time added |
| 8 | Add 8 trays, then try a 9th | "Storage is FULL" — the details are not even asked |
| 9 | Menu option `42` or `x` | "out of range" / "not a whole number" |
| 10 | Remove in Stack mode, switch (5), remove in Queue mode | Front tray out, then back tray out |
| 11 | Option 6 | The complexity table printed at runtime |
| 12 | Option 0, answer `n`, then 0 and `y` | "Exit cancelled", then "Application terminated" |

## 7. Advantages and limitations

**Advantages**

- Add, remove and peek are O(1) — the fastest possible.
- One structure covers both of the chef's options; switching mode moves no tray.
- The 8-tray capacity is enforced by the structure itself, not by extra code.
- Queue mode sends the oldest food out first, which reduces waste.
- Generic, so the deque is reusable beyond this scenario.

**Limitations**

- Listing every tray is O(n); only the two ends are reachable in O(1).
- A tray cannot be taken from the middle — only from either end.
- There is no way to look a tray up by name, weight or date: the unit is not
  searchable, which is acceptable here because the chef always works from one
  of the two ends.
- The capacity cannot grow; a 9th tray is refused rather than resizing the array.
- Data is held in memory only, so it is lost when the application closes.

## 8. Team contributions

| Member | Responsible for |
|---|---|
| Maycon | `CircularDeque`, `FoodStorage`, the `model` package, both exception classes, and their JUnit tests |
| Grazielle | `FoodStorageApp`, `InputHelper`, `StorageMenu`, the menu tests, this README and the poster |

Commits were pushed from each member's own GitHub account — see the commit
history for the author of each change.

## 9. Video Presentation

The video presentation of our Party Food Storage Unit CA1 project is available on Google Drive.

**Watch our presentation:** [Click here to watch the video](https://drive.google.com/file/d/1zWEfIkToFqmOaI6mYo26WLw2Db1xfhca/view?usp=share_link)

**Presented by:** Grazielle Matsuyama and Maycon Almeida.
