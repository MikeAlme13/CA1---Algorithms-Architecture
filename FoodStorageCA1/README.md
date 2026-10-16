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

