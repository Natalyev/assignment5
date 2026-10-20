# Assignment 4 - Restaurant Kitchen Facade

## Domain

Restaurant Kitchen.

The system has three independent subsystems:

- **Subsystem A - Order intake:** Order Queue + Inventory Checker
- **Subsystem B - Kitchen processing:** Cooking Station Allocator + Quality Checker
- **Subsystem C - Finalization:** Billing Module + Delivery Dispatcher

The main `RestaurantFacade` gives the client one operation for the whole A -> B -> C chain.

## Facades

### RestaurantFacade

Common workflow:

1. Subsystem A validates and confirms the order.
2. Subsystem B checks quality and reserves a cooking station.
3. Subsystem C calculates the bill and dispatches delivery.
4. Chain progress is saved to disk after each committed stage.

### RestaurantAdminFacade

Narrow administrative workflow:

- inspect persisted state;
- trigger manual compensation.

## Durable storage

The project uses plain CSV files inside the `data` folder:

- `orders.csv`
- `kitchen.csv`
- `finalization.csv`
- `progress.csv`

The state survives an application restart.

The progress statuses are:

- `PENDING_B`
- `PENDING_C`
- `COMPLETED`
- `COMPENSATED`

## Compensation

### B fails after A committed

Example: no cooking station is available.

Result:

- A: `CONFIRMED -> CANCELLED`
- B: nothing is committed
- progress: `COMPENSATED`

### C fails after A and B committed

Example: delivery dispatcher is unavailable.

Result:

- C partial `NEW` record becomes `CANCELLED`
- B: `RESERVED -> RELEASED`
- A: `CONFIRMED -> CANCELLED`
- progress: `COMPENSATED`

The facade reloads the progress from persisted storage before deciding what to undo.

## Further failure case

Invalid input or unavailable inventory can fail in Subsystem A before anything is committed.

Examples:

- empty item name;
- quantity <= 0;
- item name `soldout`.

## Principle of Least Knowledge

### Before Facade

Without the facade, a client would need to know many subsystem objects:

```java
inventoryChecker.check(item, quantity);
orderService.createAndConfirm(id, item, quantity);

qualityChecker.check(item);
kitchenService.reserve(id, item, false);

billingModule.calculateTotal(quantity);
finalizationService.finalizeOrder(id, quantity, false);
```

The client knows too many details and the correct call order.

### After Facade

```java
restaurantFacade.placeOrder("burger", 2, false, false);
```

The client has one immediate friend: the facade. The facade coordinates the subsystem objects.

## Facade vs Adapter

Facade is appropriate because the problem is **complex coordination**, not incompatible interfaces.

All three restaurant subsystems already have usable interfaces. The difficulty is that the client would need to know the correct order of many calls and how to compensate earlier committed steps when a later subsystem fails. `RestaurantFacade` gives one high-level operation and hides that coordination.

Adapter would be appropriate if one subsystem had an incompatible interface that had to be translated into another expected interface. That is not the main problem here.

## Unit tests

Tests use JUnit 5 and follow F.I.R.S.T.:

- Fast: file storage is small and local.
- Independent: every test uses a new JUnit temporary directory.
- Repeatable: tests do not depend on execution order or external services.
- Self-validating: assertions decide pass/fail.
- Timely: commit tests together with the production code they verify.

`RestaurantScenario` is a small domain-specific testing helper. Tests can read like:

```java
String id = s.aConfirmedOrderExists("burger", 1);
s.whenCFailureHappens(id);
```

Mandatory scenarios are covered:

- B failure after A;
- C failure after A and B;
- persistence reload after restart;
- normal happy path.

## Build and test

```bash
mvn clean verify
```

JaCoCo generates the full HTML report at:

```text
target/site/jacoco/index.html
```

A CSV summary is also generated at:

```text
target/site/jacoco/jacoco.csv
```

## Run the live demo

```bash
mvn exec:java
```

The CLI menu lets the examiner:

1. run a successful A -> B -> C chain;
2. force B failure;
3. force C failure;
4. stop after A and simulate restart;
5. resume a saved order;
6. inspect persisted state;
7. trigger manual compensation;
8. reset persisted storage.

## Reset before a fresh demo

Use option `8` in the CLI menu.

It deletes the four CSV data files. They are recreated automatically on the next operation.

## UML

See:

```text
docs/uml.svg
```
