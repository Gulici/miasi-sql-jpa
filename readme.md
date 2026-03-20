# SQL → JPA Entity Generator

## Cel
Konwersja schematu bazy danych (SQL DDL) na encje Java zgodne z JPA (Hibernate).

**Proces:** `SQL (DDL) → Java Entities (@Entity)`

---

## Zakres

**Obsługiwane elementy SQL:**
- `CREATE TABLE`, `PRIMARY KEY`, `FOREIGN KEY`
- `UNIQUE`, `NOT NULL`
- Typy: `INT`, `BIGINT`, `VARCHAR`, `TEXT`, `TIMESTAMP`

**Generowany kod Java:**
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`
- Relacje: `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`

---

## Pipeline

SQL   
↓  
ANTLR Parser  
↓  
Parse Tree  
↓  
Visitor  
↓  
Schema Model  
↓  
Relationship Analyzer  
↓  
Entity Model  
↓  
Code Generator (StringTemplate)  
↓  
Java Code  


---

## Komponenty

| Komponent | Funkcja |
|-----------|---------|
| Parser (ANTLR) | Parsuje SQL DDL do drzewa składniowego |
| Schema Model | Reprezentacja tabel, kolumn, PK i FK |
| Relationship Analyzer | Wykrywa relacje (`ManyToOne`, `OneToMany`, `OneToOne`, `ManyToMany`) |
| Code Generator | Generuje kod Java przy użyciu StringTemplate |

---

## Mapowanie typów SQL → Java

| SQL       | Java          |
|-----------|---------------|
| INT       | Integer       |
| BIGINT    | Long          |
| VARCHAR   | String        |
| TEXT      | String        |
| TIMESTAMP | LocalDateTime |

---

## Przykłady relacji

```java
// ManyToOne
@ManyToOne
@JoinColumn(name="user_id")
User user;

// OneToMany
@OneToMany(mappedBy="user")
List<Post> posts;

// OneToOne
@OneToOne
@JoinColumn(name="user_id")
User user;

// ManyToMany
@ManyToMany
@JoinTable(...)
Set<Course> courses;