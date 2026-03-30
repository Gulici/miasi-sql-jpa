# SQL -> JPA Entity Generator

## Cel projektu

Projekt konwertuje definicje schematu bazy danych zapisane w SQL DDL na klasy encji Java zgodne z JPA/Hibernate.
Zakres obecnej wersji odpowiada MVP przygotowanemu na potrzeby projektu semestralnego.

## Jak dziala aplikacja

Przeplyw przetwarzania:

`SQL -> ANTLR Parser -> Parse Tree -> Visitor -> Schema Model -> Relationship Analyzer -> Entity Model -> StringTemplate -> Java`

Opis krokow:

1. `Main` wczytuje argumenty CLI i zawartosc pliku SQL.
2. ANTLR parsuje SQL na podstawie gramatyki `Sql.g4`.
3. `SchemaVisitor` buduje model schematu (tabele, kolumny, PK, FK).
4. `RelationshipAnalyzer` wykrywa relacje encji na podstawie kluczy obcych.
5. `EntityGenerator` oraz szablon `entity.stg` generuja finalne klasy `.java`.

## Zakres funkcjonalny (MVP)

### Obslugiwane elementy DDL

- `CREATE TABLE`
- `PRIMARY KEY` (inline oraz table-level)
- `FOREIGN KEY ... REFERENCES ...` (inline oraz table-level)
- `UNIQUE`
- `NOT NULL`
- opcjonalne nazwy ograniczen `CONSTRAINT <name>`

### Mapowanie typow SQL -> Java

| SQL | Java |
|---|---|
| `INT` | `Integer` |
| `BIGINT` | `Long` |
| `VARCHAR(n)` | `String` |
| `TEXT` | `String` |
| `TIMESTAMP` | `LocalDateTime` |

### Wykrywane relacje JPA

- `ManyToOne`
- `OneToMany` (strona odwrotna)
- `OneToOne` (gdy FK jest jednoczesnie `UNIQUE` lub PK)
- `ManyToMany` przez tabele lacznikowe

## Struktura kodu

- `pom.xml` - konfiguracja buildu Maven (Java 17, ANTLR, ST4, testy)
- `src/main/java/pwr/miasi/Main.java` - punkt startowy CLI
- `src/main/java/pwr/miasi/antlr4/Sql.g4` - gramatyka SQL
- `src/main/java/pwr/miasi/parser/SchemaVisitor.java` - budowa modelu schematu
- `src/main/java/pwr/miasi/parser/SqlTypeMapper.java` - mapowanie typow SQL -> Java
- `src/main/java/pwr/miasi/model/*` - model schematu bazy
- `src/main/java/pwr/miasi/analyzer/RelationshipAnalyzer.java` - analiza relacji
- `src/main/java/pwr/miasi/entity/*` - model encji do generacji kodu
- `src/main/java/pwr/miasi/generator/EntityGenerator.java` - generator plikow Java
- `src/main/resources/templates/entity.stg` - szablon StringTemplate
- `src/test/java/pwr/miasi/*` - testy parsera, analyzera i przeplywu end-to-end

## Wymagania

- JDK 17+
- Maven 3.9+

Weryfikacja srodowiska:

```bash
java -version
mvn -v
```

## Uruchomienie

### 1) Testy automatyczne

```bash
mvn clean test
```

Po poprawnym uruchomieniu pojawia sie `BUILD SUCCESS`.
Raporty testow sa dostepne w `target/surefire-reports`.

### 2) Uruchomienie CLI bez zapisu plikow

```bash
mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=pwr.miasi.Main" "-Dexec.args=test.sql output --package=demo.entities --dry-run"
```

Tryb `--dry-run` pokazuje podsumowanie przetwarzania i nie zapisuje plikow.

### 3) Generowanie encji do katalogu output

```bash
mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=pwr.miasi.Main" "-Dexec.args=test.sql output --package=demo.entities"
```

Przykladowy wynik dla `test.sql`:
- `output/User.java`
- `output/Post.java`
- `output/Course.java`

W tym przypadku generowane sa 3 encje, poniewaz tabela `user_course` jest traktowana jako tabela lacznikowa relacji `ManyToMany`.

## Testy

- `SchemaVisitorTest` - walidacja budowy modelu schematu z PK/FK
- `RelationshipAnalyzerTest` - walidacja wykrywania relacji encji
- `GeneratorE2ETest` - test end-to-end od SQL do wygenerowanych plikow

## Ograniczenia i dalszy rozwoj

Aktualna wersja nie implementuje pelnego, vendor-specific DDL wszystkich silnikow baz danych.
Naturalnym kierunkiem rozwoju jest rozszerzenie gramatyki i analyzera o kolejne elementy, np. `ALTER TABLE`, `DEFAULT`, `CHECK`, `INDEX`.