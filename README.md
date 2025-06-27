# Grouping Utility

Утилита для поиска и группировки связанных строк в текстовом файле по совпадениям непустых значений в одинаковых колонках.

## Описание

* Считывает входной файл (поддерживаются обычный текст и GZIP-сжатые `*.gz`).
* Пропускает некорректные строки с символом `"`.
* Формирует множество уникальных строк, игнорируя дубликаты.
* Находит группы строк, где любые две строки связаны через совпадения значений в одной колонке (транзитивно).
* Выводит только группы размера > 1, отсортированные по убыванию размера.
* Логирует ход выполнения через SLF4J + Logback.

## Требования

* JDK 23+ (совместимо с Gradle 8.x)
* Gradle Wrapper (`gradlew.bat` для Windows)
* Память: не более 1 ГБ (параметр `-Xmx1G`)
* Время работы: до 30 секунд

## Сборка

В Windows PowerShell или CMD, из корня проекта (где `build.gradle.kts`):

```powershell
# Генерация и сборка fat JAR со всеми зависимостями
.\gradlew.bat clean shadowJar
```

В результате в папке `build/libs/` появится файл:

```
NewJob-1.0-SNAPSHOT.jar
```

## Запуск

```powershell
java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar lng.txt

# Вариант с двумя аргументами
java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar input.txt output.txt
```

* `-Xmx1G` ограничивает кучу в 1 ГБ.

## Параметры

`Main` принимает два аргумента:

1. `<input-file>` — путь к входному файлу (текст или `.gz`).
2. (необязательно) `<output-file>` — если вы передадите второй аргумент, он будет использоваться вместо дефолтного (output.txt).

---

# Grouping Utility

A utility for searching and grouping related strings in a text file by matching non-empty values in identical columns.

**Description**

* Reads an input file (supports plain text and GZIP-compressed `*.gz`).
* Skips malformed lines containing the `"` character.
* Builds a set of unique lines, ignoring duplicates.
* Finds groups of lines where any two lines are connected by matching non-empty values in the same column (transitively).
* Outputs only groups with size > 1, sorted by descending group size.
* Logs execution steps using SLF4J + Logback.

**Requirements**

* JDK 23+ (compatible with Gradle 8.x)
* Gradle Wrapper (`gradlew.bat` on Windows)
* Maximum heap: 1 GB (`-Xmx1G`)
* Execution time limit: 30 seconds

**Build**

*From Windows PowerShell or CMD, in the project root (where `build.gradle.kts` resides):*

```powershell
# Generate and build fat JAR with all dependencies
.\gradlew.bat clean shadowJar
```

*As a result, in the folder `build/libs/` you will see:*

```
NewJob-1.0-SNAPSHOT.jar
```

**Usage**

```powershell
# Single argument (input file)
java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar input.txt

# Two arguments (input and output files)
java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar input.txt output.txt
```

* `-Xmx1G` limits the JVM heap to 1 GB.

**Arguments**

1. `<input-file>` — path to the input file (plain text or `.gz`).
2. `<output-file>` *(optional)* — if provided, the program writes results into this file instead of standard output.




