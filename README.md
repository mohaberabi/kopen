# Kopen — Make Kotlin Classes Open *On Demand*

Kopen is a tiny Kotlin compiler plugin + Gradle convention that makes **all classes & functions open
only when you enable it**.

Production stays `final` — but with one flag, everything becomes **extendable, mockable,
override-friendly**, which is especially valuable during **unit testing, mocking,

---

### 📌 Enable inside any module

```kotlin
kopen {
    kopenEnabled = true // When enabled → all final becomes open
}
```

```bash

./gradlew build -PenableKopen=true
./gradlew test -PenableKopen=true

```

