# SQLAlchemy Guide

The examples here use the SQLAlchemy 2.0 style API, which is recommended for modern applications.

## 1. Understanding the SQLAlchemy Session

The Session is arguably the most important SQLAlchemy concept to understand.

Think of a Session as your application's workspace for interacting with the database.

- It:

  - Retrieves objects from the database.
  - Tracks changes to objects.
  - Adds new objects.
  - Deletes objects.
  - Coordinates database transactions.
  - Keeps track of objects it has loaded.

Example:
```student = self.session.get(Student, 1)```


SQLAlchemy checks whether Student ID 1 is already in its identity map.
- If appropriate, queries the database.
- Converts the database row into a Python Student object.
- Returns that object.
- The Session also tracks changes to the returned object.

For example:
```
student = self.session.get(Student, 1)
student.name = "John Smith"
self.session.commit()
```

You didn't explicitly write an UPDATE statement.

SQLAlchemy detected that you modified the object and generated the appropriate SQL UPDATE when you committed.

This is called change tracking.

## 2. Default Session Methods

These are the methods you'll probably use most often when implementing repositories.

## 2.1 Retrieving Records
get():

```
student = self.session.get(Student, student_id)
```

Retrieves a record by its primary key.

Equivalent SQL:

```
SELECT * FROM students WHERE id = 1;
```

scalars():

```
students = self.session.scalars(
    select(Student)
).all()
```

Returns ORM objects rather than complete SQLAlchemy result rows.

Example:

```
students = self.session.scalars(select(Student)).all()

for student in students:
    print(student.name)
```

execute():
```
result = self.session.execute(select(Student))
```

Executes a SQL statement and returns a Result object.

You can retrieve results using:

```
result.all()
result.first()
result.one()
result.one_or_none()
result.scalar()
result.scalars()
```

- all()	Return all results
- first()	Return first result or None
- one()	Require exactly one result
- one_or_none()	Require zero or one result

Important: first() does not automatically add a SQL LIMIT clause. Use .limit(1) if you want the database to limit the result.

## 2.2 Adding Records
add()
```
student = Student(name="John")
self.session.add(student)
```

This tells SQLAlchemy to start tracking the object as a new database record.

Important: add() does not immediately insert the record into the database.

The object enters the Session's pending state.

The INSERT happens when SQLAlchemy flushes the Session, which usually happens during commit().

```
self.session.add(student)
self.session.commit()
```

add_all()

Adds multiple objects.
```
students = [
    Student(name="John"),
    Student(name="Jane"),
    Student(name="Mike")
]

self.session.add_all(students)
self.session.commit()
```


## 2.3 Updating Records

There isn't necessarily a dedicated update() method for ORM objects.

You typically retrieve an object and modify its attributes.

```
student = self.session.get(Student, student_id)

if student is None:
    return None

student.name = "Updated Name"

self.session.commit()
```

SQLAlchemy tracks the modification and generates an UPDATE statement.

There is also a bulk UPDATE approach:

```
from sqlalchemy import update

statement = (
    update(Student)
    .where(Student.id == student_id)
    .values(name="Updated Name")
)

self.session.execute(statement)
self.session.commit()
```

The second approach is useful when you want to update records without loading individual ORM objects.

## 2.4 Deleting Records
delete()
```
student = self.session.get(Student, student_id)

if student is not None:
    self.session.delete(student)
    self.session.commit()
```

This marks the object for deletion.

The actual SQL DELETE occurs during flush.

You can also use a bulk DELETE:

```
from sqlalchemy import delete

statement = delete(Student).where(Student.id == student_id)

self.session.execute(statement)
self.session.commit()
```

## 2.5 Other Session Methods

- flush()	Sends pending SQL changes to the database without committing
- rollback()	Rolls back the current transaction
- refresh()	Reloads an object's attributes from the database
- expire()	Marks object attributes as needing reloading
- expunge()	Removes an object from the Session
- close()	Closes the Session

## 3. Understanding Custom Queries

```
def get_by_name(self, name: str) -> list[Student]:
    statement = select(Student).where(Student.name == name)
    return self.session.scalars(statement).all()
```

There are three important concepts here.

## 3.1 select(Student)
```
statement = select(Student)
```

This tells SQLAlchemy:

"I want to select Student objects."

Equivalent SQL:

```
SELECT * FROM students;
```

Notice that you aren't specifying column names.

SQLAlchemy understands the mapping between your Python Student class and your database table.

## 3.2 .where()
```
statement = select(Student).where(Student.name == name)
```

This adds a filtering condition.

Equivalent SQL:

```
SELECT *
FROM students
WHERE name = 'John';
```

You could also do:

```
Student.id == 5
Student.name != "John"
Student.age > 18
Student.age >= 21
```

## 3.3 .scalars().all()
```
self.session.scalars(statement).all()
```

Let's separate these:

```
result = self.session.scalars(statement)
```

Executes the query and produces a scalar result containing Student objects.

```
students = result.all()
```

Returns all matching students.

```
def get_by_name(self, name: str) -> list[Student]:
    statement = select(Student).where(Student.name == name)
    return self.session.scalars(statement).all()
```

## 4. Building Custom Queries

SQLAlchemy lets you build fairly complex SQL queries using Python expressions.

Let's use a hypothetical Student model:

```
class Student:
    id: int
    name: str
    age: int
    grade: str
    is_active: bool
```

## 4.1 Multiple WHERE Conditions

Find students older than 18 who are active.

```
statement = select(Student).where(
    Student.age > 18,
    Student.is_active == True
)
```

Multiple arguments to .where() are combined with AND.

Equivalent SQL:

```
SELECT *
FROM students
WHERE age > 18
AND is_active = TRUE;
```

You can also explicitly use and_():

```
from sqlalchemy import and_

statement = select(Student).where(
    and_(
        Student.age > 18,
        Student.is_active == True
    )
)
```

## 4.2 OR Conditions

Find students who are either under 18 or inactive.

```
from sqlalchemy import or_

statement = select(Student).where(
    or_(
        Student.age < 18,
        Student.is_active == False
    )
)
```

Equivalent SQL:

```
SELECT *
FROM students
WHERE age < 18 OR is_active = FALSE;
```

You can also use the | operator:

```
statement = select(Student).where(
    (Student.age < 18) | (Student.is_active == False)
)
```

Parentheses matter when using & and |.

## 4.3 IN Conditions

Find students belonging to particular grades.

```
statement = select(Student).where(
    Student.grade.in_(["A", "B", "C"])
)
```

Equivalent SQL:

```
SELECT *
FROM students
WHERE grade IN ('A', 'B', 'C');
```

## 4.4 LIKE Conditions

Find students whose names start with John.

```
statement = select(Student).where(
    Student.name.like("John%")
)
```

Common SQL wildcards:

- Pattern	Meaning
- "John%"	Starts with John
- "%John"	Ends with John
- "%John%"	Contains John
- "J_hn"	One wildcard character

For case-insensitive matching, SQLAlchemy also supports:

```
Student.name.ilike("%john%")
```

Note that ilike() support and behavior can vary by database dialect.

## 4.5 Sorting with ORDER BY

Find students sorted by age.

```
statement = select(Student).order_by(Student.age)
```

Descending:

```
statement = select(Student).order_by(
    Student.age.desc()
)
```

You can sort by multiple columns:

```
statement = select(Student).order_by(
    Student.grade,
    Student.name
)
```

## 4.6 LIMIT and OFFSET

Suppose you want pagination.

```
statement = (
    select(Student)
    .order_by(Student.id)
    .limit(10)
    .offset(20)
)
```

Equivalent SQL:

```
SELECT *
FROM students
ORDER BY id
LIMIT 10 OFFSET 20;
```

This retrieves 10 students starting after the first 20.

## 4.7 COUNT

Sometimes you want a count instead of retrieving objects.

```
from sqlalchemy import func, select

statement = select(func.count()).select_from(Student)

count = self.session.scalar(statement)
```

With a filter:

```
statement = (
    select(func.count())
    .select_from(Student)
    .where(Student.is_active == True)
)

count = self.session.scalar(statement)
```

## 4.8 Selecting Specific Columns

You don't always have to select entire ORM objects.

```
statement = select(Student.name, Student.age)

results = self.session.execute(statement).all()
```

You get rows containing the selected values rather than complete Student objects.

For example:

```
[
    ("John", 20),
    ("Jane", 22)
]
```

## 5. Putting Everything Together: Student Repository

```
from sqlalchemy import func, select
from sqlalchemy.orm import Session

from python_fast_api_app.domain.student import Student
from python_fast_api_app.repositories.student_repo import StudentRepositoryProtocol


class SqlAlchemyStudentRepository(StudentRepositoryProtocol):

    def __init__(self, session: Session):
        self.session = session

    def get_by_id(self, student_id: int) -> Student | None:
        return self.session.get(Student, student_id)

    def get_by_name(self, name: str) -> list[Student]:
        statement = select(Student).where(
            Student.name == name
        )
        return self.session.scalars(statement).all()

    def get_active_students(self) -> list[Student]:
        statement = select(Student).where(
            Student.is_active == True
        )
        return self.session.scalars(statement).all()

    def get_students_by_age_range(
        self,
        min_age: int,
        max_age: int
    ) -> list[Student]:

        statement = select(Student).where(
            Student.age >= min_age,
            Student.age <= max_age
        )

        return self.session.scalars(statement).all()

    def get_students_paginated(
        self,
        limit: int,
        offset: int
    ) -> list[Student]:

        statement = (
            select(Student)
            .order_by(Student.id)
            .limit(limit)
            .offset(offset)
        )

        return self.session.scalars(statement).all()

    def count_students(self) -> int:
        statement = select(func.count()).select_from(Student)
        return self.session.scalar(statement) or 0

    def add_student(self, student: Student) -> Student:
        self.session.add(student)
        self.session.commit()
        self.session.refresh(student)
        return student

    def delete_student(self, student_id: int) -> bool:
        student = self.session.get(Student, student_id)

        if student is None:
            return False

        self.session.delete(student)
        self.session.commit()
        return True
```

## 6. Three Particularly Important Concepts

These are things that will save you headaches when building your FastAPI application.

## 6.1 flush() vs commit()

This is one of the most important distinctions.

Imagine adding a student:

```
student = Student(name="John")

self.session.add(student)
```

At this point, the object is pending. It hasn't necessarily been inserted into the database.

Now:

```
self.session.flush()
```

SQLAlchemy sends the INSERT statement to the database, and the object will typically receive its generated primary key.

However, the transaction is not committed.

You can still roll it back:

```
self.session.rollback()
```

Compare:

| Operation | Sends SQL? | Commits transaction? |
| - | - | - |
| add() | Not necessarily | No |
| flush() | Yes | No |
| commit() | Flushes first | Yes |
| rollback() | Rolls back transaction | No |

Why does this matter?

Imagine a service needs to create a student and an enrollment record.

You might want both operations to succeed or neither to succeed.

```
student = Student(name="John")
self.session.add(student)
self.session.flush()

enrollment = Enrollment(student_id=student.id)
self.session.add(enrollment)

self.session.commit()
```

One transaction covers both inserts.

This is generally better than having every repository method independently commit.

## 6.2 commit() and Transaction Ownership

Example repo implementation:

```
def add_student(self, student: Student) -> Student:
    self.session.add(student)
    self.session.commit()
    self.session.refresh(student)
    return student
```

This is perfectly understandable for a small application.

However, as your application grows, consider moving transaction ownership to the service layer.

For example:

Repository:

```
def add_student(self, student: Student) -> Student:
    self.session.add(student)
    self.session.flush()
    return student
```

Service:

```
def register_student(self, student: Student) -> Student:
    try:
        self.student_repository.add_student(student)

        # Other related business operations...

        self.session.commit()
        return student

    except Exception:
        self.session.rollback()
        raise
```

Why?

Because the service knows which operations belong together from a business perspective.

The repository should generally focus on persistence rather than deciding when an entire business transaction is complete.

One caveat: this assumes your service has access to the same Session, typically through dependency injection.

## 6.3 Session Lifecycle in FastAPI

A Session should generally be created per request and closed afterward.

A typical FastAPI dependency looks like this:

```
from sqlalchemy.orm import Session

def get_db():
    db = SessionLocal()

    try:
        yield db
    finally:
        db.close()
```

Then inject it:

```
from fastapi import Depends

@app.get("/students/{student_id}")
def get_student(
    student_id: int,
    db: Session = Depends(get_db)
):
    repository = SqlAlchemyStudentRepository(db)

    return repository.get_by_id(student_id)
```

The dependency manages the Session lifecycle.

Important: Closing a Session does not automatically mean that uncommitted work was successfully saved. Make sure transaction commits and rollbacks are handled explicitly.

## 7. A Mental Model for SQLAlchemy

Here's how everything fits together in a layered FastAPI application.

```
Your FastAPI endpoint (via routers)
    |
    v
Service layer
    Business logic and transaction coordination
    |
    v
Repository layer
    get(), add(), delete(), select(), where()
    |
    v
SQLAlchemy Session
    Identity map, change tracking, transactions, SQL execution
    |
    v
Database
    PostgreSQL, SQLite, MySQL, etc.
```

Remember these distinctions:

Session is your interface for interacting with the database.
select() constructs queries.
.where(), .order_by(), .limit() modify those queries.
.execute() and .scalars() execute queries and retrieve results.
add(), delete(), and modifying object attributes manage persistence.
flush() sends pending changes to the database.
commit() makes the transaction permanent.

## 8. Relationships
Relationships allow you to connect different database models together.

For example, imagine you have two models:

A Student can have multiple Enrollment records.

Each Enrollment belongs to one Student.

This is a one-to-many relationship.

## 8.1 Defining the Models
Let's define the two models using SQLAlchemy 2.0 style mappings.


```
from sqlalchemy import ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

class Student(Base):
    __tablename__ = "students"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str]

    enrollments: Mapped[list["Enrollment"]] = relationship(
        back_populates="student"
    )


class Enrollment(Base):
    __tablename__ = "enrollments"

    id: Mapped[int] = mapped_column(primary_key=True)
    course_name: Mapped[str]

    student_id: Mapped[int] = mapped_column(
        ForeignKey("students.id")
    )

    student: Mapped["Student"] = relationship(
        back_populates="enrollments"
    )
```

There are a few important things happening here.

ForeignKey:

```
student_id: Mapped[int] = mapped_column(
    ForeignKey("students.id")
)
```
This creates a foreign key constraint in the database.

It tells the database that enrollments.student_id references students.id.

relationship():


```
enrollments: Mapped[list["Enrollment"]] = relationship(
    back_populates="student"
)
```
This defines the ORM relationship between the two Python models.

It allows you to access related objects without manually writing a query.

back_populates:


```
student: Mapped["Student"] = relationship(
    back_populates="enrollments"
)
```

This connects the two sides of the relationship.

Student.enrollments provides access to a student's enrollments.

Enrollment.student provides access to the student associated with an enrollment.

Important distinction:

ForeignKey defines the database-level relationship.

relationship() defines the ORM-level relationship.

They serve different purposes and are commonly used together.

## 8.2 Accessing Related Objects
Once the relationship is defined, SQLAlchemy lets you navigate between objects.

For example:


```
student = self.session.get(Student, 1)

if student is not None:
    for enrollment in student.enrollments:
        print(enrollment.course_name)
```

Notice that you don't need to explicitly query the Enrollment table.

SQLAlchemy understands the relationship and retrieves the associated enrollments when the attribute is accessed.

You can also navigate in the opposite direction:


```
enrollment = self.session.get(Enrollment, 1)

if enrollment is not None:
    print(enrollment.student.name)
```

SQLAlchemy retrieves the associated Student object.

By default, relationships generally use lazy loading, meaning related records are loaded when you first access the relationship attribute.

This is convenient, but it can lead to performance problems if you're accessing relationships across many objects.

We'll cover eager loading later.

## 8.3 Creating Related Objects
You can create related objects by assigning them to relationship attributes.

For example:


```
student = Student(name="John")

enrollment1 = Enrollment(course_name="Mathematics")
enrollment2 = Enrollment(course_name="Science")

student.enrollments = [
    enrollment1,
    enrollment2
]

self.session.add(student)
self.session.commit()
```

SQLAlchemy understands that the enrollments belong to the student.

Because of the relationship configuration, SQLAlchemy can automatically populate the foreign keys when it persists the objects.

You don't need to manually assign student_id to each enrollment.

You can also create an enrollment through the other side of the relationship:


```
student = self.session.get(Student, 1)

if student is not None:
    enrollment = Enrollment(
        course_name="History",
        student=student
    )

    self.session.add(enrollment)
    self.session.commit()
```

## 8.4 One-to-One Relationships
A one-to-one relationship is similar to a one-to-many relationship, except each record is associated with at most one record on the other side.

For example:

A Student has one StudentProfile.

Each StudentProfile belongs to one Student.


```
class Student(Base):
    __tablename__ = "students"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str]

    profile: Mapped["StudentProfile | None"] = relationship(
        back_populates="student",
        uselist=False
    )


class StudentProfile(Base):
    __tablename__ = "student_profiles"

    id: Mapped[int] = mapped_column(primary_key=True)

    student_id: Mapped[int] = mapped_column(
        ForeignKey("students.id"),
        unique=True
    )

    bio: Mapped[str | None]

    student: Mapped["Student"] = relationship(
        back_populates="profile"
    )
```

There are two important details:

uselist=False tells SQLAlchemy to treat the relationship as a scalar object rather than a collection.

unique=True ensures that a student can only be referenced by one profile at the database level.

The unique constraint is important because uselist=False alone does not enforce a one-to-one relationship in the database.

You can now access the profile directly:


```
student = self.session.get(Student, 1)

if student is not None and student.profile is not None:
    print(student.profile.bio)
```

## 8.5 Many-to-Many Relationships

A many-to-many relationship means multiple records on either side can be associated with multiple records on the other side.

For example:

A Student can enroll in multiple Courses.

A Course can have multiple Students.

This requires an association table. 


```
class StudentCourse(Base):
    __tablename__ = "student_course"

    student_id: Mapped[int] = mapped_column(
        ForeignKey("students.id"),
        primary_key=True
    )

    course_id: Mapped[int] = mapped_column(
        ForeignKey("courses.id"),
        primary_key=True
    )
```

Now define the other models:


```
class Student(Base):
    __tablename__ = "students"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str]

    student_courses: Mapped[list["StudentCourse"]] = relationship(
        back_populates="student"
    )


class Course(Base):
    __tablename__ = "courses"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str]

    student_courses: Mapped[list["StudentCourse"]] = relationship(
        back_populates="course"
    )
```

You can now access courses belonging to a student:


```
student = self.session.get(Student, 1)

if student is not None:
    for course in student.courses:
        print(course.name)
```

You can also assign courses:


```
student = self.session.get(Student, 1)
course = self.session.get(Course, 2)

if student is not None and course is not None:
    student.courses.append(course)
    self.session.commit()
```

SQLAlchemy automatically inserts the appropriate record into the association table.

## 8.6 Eager Loading vs Lazy Loading
This is particularly important for application performance.

By default, SQLAlchemy generally uses lazy loading for relationships.

Consider:

```
students = self.session.scalars(
    select(Student)
).all()

for student in students:
    print(student.enrollments)
```

The initial query retrieves all students.

However, when you access student.enrollments, SQLAlchemy may execute another query to retrieve that student's enrollments.

If you retrieve 100 students, this can result in:

1 query to retrieve students.

Up to 100 additional queries to retrieve enrollments.

This is known as the N+1 query problem.

You can avoid this using eager loading.

One common approach is selectinload().


```
from sqlalchemy.orm import selectinload

statement = (
    select(Student)
    .options(selectinload(Student.enrollments))
)

students = self.session.scalars(statement).all()

for student in students:
    print(student.enrollments)
```

SQLAlchemy now retrieves the students and their enrollments using a small number of queries rather than executing one additional query for every student.

Another option is joinedload().


```
from sqlalchemy.orm import joinedload

statement = (
    select(Student)
    .options(joinedload(Student.enrollments))
)

students = self.session.scalars(statement).unique().all()
```

joinedload() uses a SQL JOIN to retrieve related records in the same query.

Notice the use of .unique().

When using joined eager loading for collection relationships, the SQL JOIN can produce multiple rows for the same parent object.

.unique() ensures that duplicate parent objects are removed from the ORM result.

When should you use each?

selectinload(): Often a good default for one-to-many and many-to-many relationships.

joinedload(): Often useful for many-to-one and one-to-one relationships, where joining doesn't multiply parent rows significantly.

Lazy loading: Convenient when related data isn't always needed.

## 9. Joins
Relationships and joins are closely related, but they aren't the same thing.

A relationship defines how two ORM models are associated.

A JOIN combines records from multiple database tables into a query result.

For example, suppose you want to retrieve students alongside their enrollments.

You could use a SQL JOIN.

## 9.1 Basic JOIN
Using the Student and Enrollment models from the previous section:


```
statement = (
    select(Student)
    .join(Student.enrollments)
)

students = self.session.scalars(statement).all()
```

This tells SQLAlchemy:

Select Student objects.

Join the Enrollment table using the Student.enrollments relationship.

Return the matching Student objects.

Equivalent SQL:


```
SELECT students.*
FROM students
JOIN enrollments
    ON students.id = enrollments.student_id;
```

Notice that we're using:


```
.join(Student.enrollments)
```

Rather than manually specifying the join condition.

Because we defined the relationship earlier, SQLAlchemy already knows how the tables are connected.

You can also explicitly specify the join condition:

```
statement = (
    select(Student)
    .join(
        Enrollment,
        Student.id == Enrollment.student_id
    )
)
```
This is useful when you don't have an ORM relationship defined or want to specify the join condition yourself.

## 9.2 JOIN with WHERE
You can combine joins with filtering conditions.

For example, find students enrolled in Mathematics.

```
statement = (
    select(Student)
    .join(Student.enrollments)
    .where(Enrollment.course_name == "Mathematics")
)

students = self.session.scalars(statement).all()
```

Equivalent SQL:


```
SELECT students.*
FROM students
JOIN enrollments
    ON students.id = enrollments.student_id
WHERE enrollments.course_name = 'Mathematics';
```

You can also filter using multiple conditions:

```
statement = (
    select(Student)
    .join(Student.enrollments)
    .where(
        Enrollment.course_name == "Mathematics",
        Student.name == "John"
    )
)
```

Multiple .where() conditions can be combined using and_ OR or_.

## 9.3 Selecting Columns from Multiple Tables
So far, we've been selecting Student objects.

However, you can select specific columns from multiple tables.


```
statement = (
    select(
        Student.name,
        Enrollment.course_name
    )
    .join(Student.enrollments)
)

results = self.session.execute(statement).all()

for student_name, course_name in results:
    print(student_name, course_name)
```

Equivalent SQL:

```
SELECT students.name, enrollments.course_name
FROM students
JOIN enrollments
    ON students.id = enrollments.student_id;
```

The result contains rows with the selected values rather than complete ORM objects.

This is useful when you only need a small amount of information from multiple tables.

## 9.4 INNER JOIN
An INNER JOIN returns only records that have matching records in both tables.

For example:

```
statement = (
    select(Student)
    .join(Student.enrollments)
)
```

Only students with matching enrollment records are included.

A student without any enrollments won't appear in the result.

Equivalent SQL:

```
SELECT students.*
FROM students
INNER JOIN enrollments
    ON students.id = enrollments.student_id;
```

In SQLAlchemy, .join() performs an INNER JOIN by default.

## 9.5 LEFT OUTER JOIN
A LEFT OUTER JOIN returns all records from the left table, even if there are no matching records in the right table.

For example, retrieve all students, including students who don't have any enrollments.

```
statement = (
    select(Student)
    .outerjoin(Student.enrollments)
)

students = self.session.scalars(statement).unique().all()
```

Equivalent SQL:

```
SELECT students.*
FROM students
LEFT OUTER JOIN enrollments
    ON students.id = enrollments.student_id;
```

The important distinction:

INNER JOIN: Only students with matching enrollments.

LEFT OUTER JOIN: All students, regardless of whether they have enrollments.

If you select columns from Enrollment, students without enrollments will have NULL values for those columns.

## 9.6 JOIN with Aggregation
You can combine JOINs with SQL aggregate functions.

For example, count how many enrollments each student has.

```
from sqlalchemy import func, select

statement = (
    select(
        Student.id,
        Student.name,
        func.count(Enrollment.id).label("enrollment_count")
    )
    .outerjoin(Student.enrollments)
    .group_by(Student.id, Student.name)
)

results = self.session.execute(statement).all()

for student_id, name, enrollment_count in results:
    print(student_id, name, enrollment_count)
```

Equivalent SQL:

```
SELECT
    students.id,
    students.name,
    COUNT(enrollments.id) AS enrollment_count
FROM students
LEFT OUTER JOIN enrollments
    ON students.id = enrollments.student_id
GROUP BY students.id, students.name;
```

Notice that we're using outerjoin().

This ensures that students without enrollments are still included.

We're also using:

```
func.count(Enrollment.id)
```

Rather than counting all rows.

This is important because a LEFT OUTER JOIN produces a row with NULL enrollment columns when a student has no enrollments.

COUNT(enrollments.id) ignores NULL values, correctly returning zero.

`func` can call other SQL aggregate functions too. For example, if `Enrollment`
has a numeric `score` column, you can calculate each student's average, lowest,
and highest score:

```
statement = (
    select(
        Student.name,
        func.avg(Enrollment.score).label("average_score"),
        func.min(Enrollment.score).label("lowest_score"),
        func.max(Enrollment.score).label("highest_score"),
    )
    .outerjoin(Student.enrollments)
    .group_by(Student.id, Student.name)
)
```

Another useful aggregate would be `func.sum(Enrollment.score)` to add values. These functions are
translated into SQL aggregate calls; use a column appropriate to the
calculation.

Like `COUNT`: `AVG`, `MIN`, `MAX`, and `SUM` also ignore NULL input values. For a
student with no scores, `AVG`, `MIN`, and `MAX` return NULL; `COUNT` returns
zero.

## 9.7 Filter on join with eager loading

Filter students based on their enrollments while also loading those enrollments:

```
statement = (
    select(Student)
    .join(Student.enrollments)
    .where(Enrollment.course_name == "Mathematics")
    .options(selectinload(Student.enrollments))
)

students = self.session.scalars(statement).unique().all()
```

This retrieves students who are enrolled in Mathematics and also loads their enrollment collections.

## 9.8 A Practical Repository Example
Let's put some of these concepts together.

Imagine you want a repository method that retrieves students enrolled in a particular course.

```
from sqlalchemy import func, select
from sqlalchemy.orm import Session, selectinload

class SqlAlchemyStudentRepository:

    def __init__(self, session: Session):
        self.session = session

    def get_students_by_course(
        self,
        course_name: str
    ) -> list[Student]:

        statement = (
            select(Student)
            .join(Student.enrollments)
            .where(Enrollment.course_name == course_name)
            .options(selectinload(Student.enrollments))
        )

        return self.session.scalars(statement).unique().all()

    def get_students_with_enrollment_counts(
        self
    ) -> list[tuple[int, str, int]]:

        statement = (
            select(
                Student.id,
                Student.name,
                func.count(Enrollment.id).label("enrollment_count")
            )
            .outerjoin(Student.enrollments)
            .group_by(Student.id, Student.name)
            .order_by(Student.name)
        )

        return self.session.execute(statement).all()
```

The first method:
- Joins Student and Enrollment.
- Filters by course name.
- Eagerly loads the students' enrollment collections.
- Returns Student ORM objects.

The second method:
- Uses a LEFT OUTER JOIN.
- Counts enrollments for each student.
- Groups results by student.
- Returns rows containing the student ID, name, and enrollment count.