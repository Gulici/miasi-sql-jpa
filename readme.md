# SQL -> JPA Entity Generator

Konwerter SQL DDL do encji Java (`@Entity`) zgodnych z JPA/Hibernate.

## Co to robi

Narzędzie analizuje plik `.sql` z definicjami tabel i generuje klasy encji Java wraz z mapowaniem:

- kolumn (`@Column`, `@Id`),
- kluczy (`PRIMARY KEY`, `FOREIGN KEY`),
- relacji (`@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`).

Pipeline:

`SQL -> ANTLR Parser -> Parse Tree -> Visitor -> Schema Model -> Relationship Analyzer -> Entity Model -> StringTemplate -> Java`

## Wymagania

- JDK 17+
- Maven 3.9+
- system: Windows/Linux/macOS

Sprawdzenie:

```bash
java -version
mvn -v
```

## Szybki start

W katalogu projektu:

```bash
mvn clean test
```

Jeśli dostaniesz `BUILD SUCCESS`, parser, analyzer i generator przechodzą testy.

## Uruchamianie z CLI (generowanie encji)

Wejscie:

`<input.sql> <outputDir> [--package=...] [--dry-run]`

Przyklad (podglad bez zapisu plikow):

```bash
mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=pwr.miasi.Main" "-Dexec.args=test.sql output --package=demo.entities --dry-run"
```

Przyklad (realna generacja plikow):

```bash
mvn -q org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=pwr.miasi.Main" "-Dexec.args=test.sql output --package=demo.entities"
```

Wynik znajdziesz w katalogu podanym jako `outputDir`, np.:

- `output/User.java`
- `output/Post.java`
- `output/Course.java`

## Co jest zaimplementowane (MVP)

### SQL DDL

- `CREATE TABLE`
- `PRIMARY KEY` (inline i table-level)
- `FOREIGN KEY ... REFERENCES ...` (inline i table-level)
- `UNIQUE`, `NOT NULL`
- opcjonalne `CONSTRAINT <nazwa>`

### Typy SQL -> Java

| SQL | Java |
|---|---|
| `INT` | `Integer` |
| `BIGINT` | `Long` |
| `VARCHAR(n)` | `String` |
| `TEXT` | `String` |
| `TIMESTAMP` | `LocalDateTime` |

### Relacje JPA

- `ManyToOne` dla standardowych FK
- `OneToMany` jako odwrotna strona `ManyToOne`
- `OneToOne` gdy FK jest jednoczesnie `UNIQUE` lub PK
- `ManyToMany` przez tabele lacznikowe (2 FK i brak dodatkowych pol biznesowych)

## Struktura projektu

- Gramatyka ANTLR: `src/main/java/pwr/miasi/antlr4/Sql.g4`
- Visitor: `src/main/java/pwr/miasi/parser/SchemaVisitor.java`
- Model schematu: `src/main/java/pwr/miasi/model/*`
- Analiza relacji: `src/main/java/pwr/miasi/analyzer/RelationshipAnalyzer.java`
- Generator kodu: `src/main/java/pwr/miasi/generator/EntityGenerator.java`
- Template ST4: `src/main/resources/templates/entity.stg`
- CLI: `src/main/java/pwr/miasi/Main.java`
- Testy: `src/test/java/pwr/miasi/*`

## Testy i raporty

Uruchamianie:

```bash
mvn clean test
```

Raporty Maven Surefire:

- `target/surefire-reports`

Testy pokrywaja:

- parser + visitor (`SchemaVisitorTest`)
- analize relacji (`RelationshipAnalyzerTest`)
- przeplyw end-to-end z generacja plikow (`GeneratorE2ETest`)

## Najczestsze problemy

### `mvn: command not found` / `mvn is not recognized`

Maven nie jest w `PATH`. Dodaj `.../maven/bin` do zmiennej `Path`.

### `release version 19 not supported`

Projekt jest ustawiony na Java 17. Upewnij sie, ze Maven uruchamia sie na JDK 17+:

```bash
java -version
mvn -v
```

### Brak wygenerowanych plikow po uruchomieniu

Sprawdz, czy nie uzyles `--dry-run` (ten tryb niczego nie zapisuje).

## Ograniczenia

To jest MVP na potrzeby projektu, nie pelny parser wszystkich dialektow SQL.
W przypadku niestandardowego DDL (vendor-specific) wymagane beda rozszerzenia gramatyki i analyzera.