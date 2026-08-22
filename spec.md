# Specification: AI-Native Android Budget Tracker (Offline MVP)

This document serves as the single source of truth (`spec.md`) for building the offline-first Android Budget Tracker. It provides the absolute technical, logical, and visual blueprints so that modern AI developers (like Google Antigravity / Project IDX, Cursor, or Claude Code) can generate a production-ready, high-performance APK.

---

## 1. Executive Summary & Tech Stack

### 1.1 Objective
An offline-first, highly customizable Android application designed for ultra-fast, frictionless expense tracking. Unlike traditional static budgeting apps, this app treats budget categories as interactive, reorderable "Note Cards" (Zettel-Optik) on a modular dashboard. Users can customize layout positions, toggle view scopes (weekly vs. monthly), input transactions via an intelligent single-field parser, and visualize financial patterns using sleek, smoothed charts.

### 1.2 Target Platform & Tooling
*   **Target IDE:** Google Antigravity / Project IDX (Web-based environment with built-in Android emulators)
*   **Operating System:** Android (Minimum SDK: 26, Target SDK: 34)
*   **Programming Language:** Kotlin (Strong type safety, fully aligned with enterprise standards)
*   **UI Framework:** Jetpack Compose (Modern declarative UI, Material 3 Design)
*   **Local Database:** Room Database (SQLite wrapper for Android, completely local & secure)
*   **Charts/Visuals:** Jetpack Compose Canvas (custom-drawn) or lightweight local charting library

---

## 2. System Architecture & Local Database Schema

To maintain extreme speed and 100% data privacy, all computations and data storage are performed strictly on-device using a relational SQLite schema via Room.

```
       +-------------------------------------------------------+
       |                     DATABASE                          |
       +----------------------------+--------------------------+
                                    |
            +-----------------------+-----------------------+
            |                                               |
            v                                               v
+-----------------------+                       +-----------------------+
|    CategoryBudget     |                       |      Transaction      |
+-----------------------+                       +-----------------------+
| - id: Int (PK)        |<-- (1)         (N) -->| - id: Int (PK)        |
| - title: String       |                       | - amount: Double      |
| - limit: Double       |                       | - description: String |
| - type: String        |                       | - date: Long (Epoch)  |
|   (AUSGABE/RUECKLAGE) |                       | - categoryId: Int?    |
| - position: Int       |                       +-----------------------+
| - isExcluded: Boolean |
+-----------------------+
```

### 2.1 Entity: `CategoryBudget`
Represents a user-defined category or custom "Zettel/Card" on the dashboard.
```kotlin
@Entity(tableName = "category_budgets")
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,                // e.g., "Essen", "Auto", "Spar-Puffer"
    val limit: Double = 0.0,          // 0.0 means no limit set
    val type: String,                 // "AUSGABE" (deducted from total limit) or "RUECKLAGE" (savings/buffer)
    val position: Int,                // Order index for Drag & Drop UI sorting
    val isExcluded: Boolean = false   // If true, this budget is excluded from the main monthly overview calculation
)
```

### 2.2 Entity: `Transaction`
Represents an individual financial transaction (income or expense).
```kotlin
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryBudget::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,               // Negative for expenses, positive for income
    val description: String,          // e.g., "Lidl", "Tanken", "Aral"
    val date: Long,                   // Timestamp in Epoch milliseconds
    val categoryId: Int?              // Foreign key to CategoryBudget. Null if "unassigned/red"
)
```

---

## 3. UI/UX Specifications & Theming

### 3.1 Design Language (Zettel-Optik / Widgets)
*   **Palette:** Deep space dark mode by default (Dark gray background `#121212`, card background `#1E1E1E`, brand accent vibrant green `#00E676`, warning accent coral red `#FF5252`).
*   **Shape:** Rounded corners (16.dp corner radius) for cards to give a modern, "notecard" or widget feel.
*   **Layout Structure:**
    *   **Header Module:** High-contrast overall balance (Total limit vs. spent) showing a linear progress bar. Contains toggle button to switch view scope: **Weekly (Wöchentlich) vs. Monthly (Monatlich)**.
    *   **Smart Input Bar:** Located immediately below the header for quick entries.
    *   **Dashboard Grid (Drag & Drop):** An interactive grid of cards. Users can press-and-hold to drag cards to re-arrange their order (`position` property).
    *   **Analytics Tab:** Interactive bottom navigation sheet to view the 30-day smoothed line charts and calendar heatmaps.

### 3.2 Visual Component Mapping

#### Category Note Card (Zettel) Component
*   **Layout:** Box/Column container with a solid subtle border.
*   **Visual Indicators:**
    *   *Normal Mode:* Shows title, total spent, and remaining limit (e.g., "Essen & Trinken: Noch 70€ übrig").
    *   *Progress Bar:* A horizontal linear progress indicator. If limit is 300€ and spent is 230€, fills up to 76.6%. Over-drafting changes the progress bar color to warning coral red.
    *   *Calculation text:* Evaluates based on remaining days in the active view scope:
        $$\text{Verbleibend pro Tag} = \frac{\text{Verbleibendes Budget}}{\text{Verbleibende Tage}}$$
    *   *Unassigned Banner:* If a transaction was parsed without a clear category, the card displays a pulsing red badge: *"1 unzugeordnete Buchung - Bitte antippen zum Sortieren"*.

---

## 4. Smart Input Parsing Logic (Frictionless Tracking)

To achieve maximum tracking speed, the app features a smart quick-input bar. The parser utilizes deterministic rules and regex instead of heavy cloud AI resources for optimal offline efficiency.

### 4.1 Parsing Steps
When the user types a string into the Quick Input (e.g., `"15 lidl"`) and taps [OK]:

1.  **Extract Amount (Regex Parsing):**
    *   Scan for numeric values (integer or decimal). E.g., `15`, `9.5`, `9,5`.
    *   Convert decimals containing commas `,` to period `.` for float parsing.
    *   Assign extracted float as `amount`. If no number is detected, display error.
2.  **Extract Description / Note:**
    *   Remove the numeric portion from the string.
    *   Clean remaining trailing/leading spaces.
    *   Assign leftover text as `description` (e.g., `"lidl"`). If empty, fallback to `"Ausgabe"`.
3.  **Resolve Date:**
    *   If words like `"gestern"` or `"yesterday"` are in the input, set date to `Today - 24 hours`.
    *   Otherwise, default the timestamp to current epoch time (`now()`).
4.  **Resolve Category:**
    *   Since AI classification is omitted for the MVP, the category is initialized as `null` (Unassigned).
    *   The transaction is saved, and a red pulsing banner is immediately appended to the Unassigned Transactions List or highlighted directly on the dashboard.
    *   Tapping the transaction displays a quick-select modal of current `CategoryBudgets` to map it instantly with one tap.

### 4.2 Fallback Form
If the user prefers traditional inputs, tapping an expand button [▼] reveals a structured form with 4 explicit fields:
*   `Datum` (DatePicker, defaults to today)
*   `Betrag (€)` (Numeric keyboard)
*   `Kategorie` (Dropdown menu of active budgets)
*   `Beschreibung` (Plain text input, optional)

---

## 5. Analytics & Visualizations

### 5.1 Smoothed Line Chart (Yazio-Style)
*   **Rendering:** Uses Jetpack Compose Canvas to draw a cubic-bezier path connecting daily aggregated expenses over a 30-day window.
*   **Interaction:** 
    *   Users can select/deselect categories using interactive tag chips beneath the graph (e.g., view only "Essen" and "Freizeit" overlayed, while hiding "Miete").
    *   A draggable vertical indicator line allows the user to scrub across the days to see the exact aggregated expense value for that specific date.

### 5.2 Calendar Heatmap
*   **Grid Layout:** A standard calendar month grid (7 columns for Monday–Sunday).
*   **Color Mapping:** 
    *   Days with 0€ expenses: Transparent / Dark Gray (No background).
    *   Days with minimal expenses (< 10€): Light green / subtle olive.
    *   Days with heavy expenses (e.g., weekends, payday rent): Scale of yellow to deep warning red.
*   **Purpose:** Allows instant behavior analysis to recognize high-spending clusters at a glance.

---

## 6. Phase-by-Phase AI Implementation Plan

To ensure Google Antigravity/IDX AI builds this app cleanly without breaking context, the development is divided into sequential, self-testing milestones.

```
+-------------------------------------------------------------+
| WEEK 1: Project Setup, Local Database (Room) & Domain Models|
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
| WEEK 2: Smart Input Parsing Engine & Manual Fallback Form   |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
| WEEK 3: Note-Card Dashboard UI & Drag-and-Drop Reordering   |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
| WEEK 4: Canvas Charts (Smoothed Line & Calendar Heatmap)     |
+-------------------------------------------------------------+
```

### Milestone 1: Data Infrastructure & Setup
*   Generate modern Kotlin gradle dependencies in Antigravity.
*   Write Room database entity structures (`CategoryBudget`, `Transaction`).
*   Establish mock data generator to populate database with 30 days of synthetic transactions for local testing.

### Milestone 2: Smart Input Engine
*   Build the regex parsing unit tests to verify inputs (e.g. `"15 lidl"`, `"9,5 schuhe gestern"`, `"tanken 60"`).
*   Create UI for the smart text field + immediate unassigned transactional categorization modal.

### Milestone 3: Draggable Dashboard Grid
*   Implement Jetpack Compose `LazyVerticalGrid`.
*   Build custom cards with active limit progress calculations.
*   Enable drag gestures to dynamic positions (`position` database column updates).

### Milestone 4: Analytics Engine
*   Build Compose Canvas drawing code for smoothed bezier line graphs.
*   Build Calendar Heatmap calculation logic mapped against monthly transaction data.
*   Bundle, run tests inside Project IDX Android Emulator, and compile into a standalone `.apk` for hardware testing.
