
// 1. Create/select and manage a database.
// show is a shell command
// it will work in the mongoDB vs playground, but has issues
// you won't be able to use various show commands in a 
// transaction with other commands, you may get errors or 
// unexpected results 
show dbs;
// won't create the db by itself, need to create a collection 
// or other items first. also need to run in the same transaction
use('school'); 
// 'db' by default will refer to 'test' database 
db.dropDatabase(); // Remove the current database (use carefully).
db.getName();

// 2. Create collections explicitly.
// need to use db reference and commands in the same transaction 
const schoolDB = db.getSiblingDB('school');
schoolDB.createCollection('students');
schoolDB.createCollection('courses');
schoolDB.createCollection('enrollments');

// we can optionally create collections that follow a schema:
const schoolDB = db.getSiblingDB('school');
schoolDB.createCollection('students', {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["name", "email", "age"],
		 additionalProperties:false, // in tandem with validationAction, this will disallow dynamic fields other than what we specify here, i.e. locking our schema
         properties: {
            name: {
               bsonType: "string",
               description: "must be a string and is required"
            },
            email: {
               bsonType: "string",
               pattern: "^.+@.+$",
               description: "must be a valid email string and is required"
            },
            age: {
               bsonType: "int",
               minimum: 5,
               maximum: 100,
               description: "must be an integer between 5 and 100 and is required"
            }
         }
      }
   },
   validationAction: "error" // Rejects invalid documents. Use "warn" just to log warnings.
});

// if we've already created a collection, we can add JSON validation 
// after the fact by modifying the collection:
const schoolDB = db.getSiblingDB('school');

schoolDB.runCommand({
   collMod: "students",
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["name", "email"],
		additionalProperties:true, // this will disallow dynamic fields other than what we specify here, i.e. locking our schema
		properties: {
            name: { bsonType: "string" },
            email: { bsonType: "string" }
         }
      }
   },
   validationAction: "error"
});

// this won't work because show is a shell command 
use('school');
show collections;

// do this instead: 
const schoolDB = db.getSiblingDB('school');
schoolDB.getCollectionNames();

// 3. Insert documents. MongoDB creates _id automatically when it is omitted.
const schoolDB = db.getSiblingDB('school');

schoolDB.students.insertOne({
    name: 'Grace Hopper',
    email: 'grace@example.com',
    age: 25,
    address: { city: 'New York' }
});

schoolDB.students.insertMany([
	{ name: 'Ada Lovelace', email: 'ada@example.com', age: 20, address: { city: 'London' } },
	{ name: 'Alan Turing', email: 'alan@example.com', age: 22, address: { city: 'Manchester' } }
]);

const schoolDB = db.getSiblingDB('school');
schoolDB.courses.insertMany([
	{ code: 'MATH101', title: 'Introductory Mathematics', credits: 3 },
	{ code: 'CS101', title: 'Computer Science', credits: 4 }
]);

// References store another document's _id (similar to a foreign key).
const schoolDB = db.getSiblingDB('school');
const ada = schoolDB.students.findOne({ email: 'ada@example.com' });
const cs101 = schoolDB.courses.findOne({ code: 'CS101' });
schoolDB.enrollments.insertOne({
	studentId: ada._id,
	courseId: cs101._id,
	enrolledOn: new Date(),
	grade: 'A'
});

// 4. Read documents and selected fields.
// for queries/updates - like find(), updateOne(), etc. the first param
// is the filtering condition, while the 2nd param specifies what we want to 
// return if a query, or what we want to update 
const schoolDB = db.getSiblingDB('school');
schoolDB.students.find();
schoolDB.students.find({ age: { $gte: 21 } }, { name: 1, email: 1, _id: 0, address: 1 });
schoolDB.students.findOne({ email: 'ada@example.com' });
schoolDB.students.countDocuments();

// aside from $gte, there are many other comparison operators:
// $gt: Greater than (e.g., { age: { $gt: 21 } })
// $lt: Less than (e.g., { age: { $lt: 18 } })
// $lte: Less than or equal to (e.g., { age: { $lte: 30 } })
// $ne: Not equal to (e.g., { status: { $ne: 'graduated' } })
// $in: Matches any value in an array. Perfect for checking multiple options at once.
// example: find students whose major is Computer Science or Mathematics
// schoolDB.students.find({ major: { $in: ['Computer Science', 'Mathematics'] } });

// logical operators:
// Find students who are either majoring in CS or are older than 25
// schoolDB.students.find({ $or: [{ major: 'Computer Science' }, { age: { $gt: 25 } }] });
// $and: Joins query clauses with a logical AND (though MongoDB implicitly uses an AND if you list multiple fields, $and is helpful when you need to apply multiple conditions to the same field).
// $not: Inverts the effect of a query expression.

// element operators
// Find all students who have a twitter handle saved
// schoolDB.students.find({ twitterHandle: { $exists: true } });
// $type: Matches documents where a field is of a specific BSON data type (like "string" or "int").

// querying nested documents
// imagine a student document like this:
// {
//   name: "Alice",
//   address: {
//      city: "Boston",
//      zip: "02115"
//   }
// }
// we can do this to find all students living in boston:
// schoolDB.students.find({ "address.city": "Boston" });

// querying nested arrays
// simple element match
// Find any student who has 'CS101' anywhere in their enrolledCourses array
// schoolDB.students.find({ enrolledCourses: "CS101" });
// Find students who are enrolled in BOTH CS101 and MATH201
// schoolDB.students.find({ enrolledCourses: { $all: ["CS101", "MATH201"] } });
// qurying a complex array of objects, imagine the following:
// {
//   name: "Alice",
//   grades: [
//      { course: "CS101", score: 75 },
//      { course: "MATH201", score: 92 }
//   ]
// } 
// we could find a student who has a course matching both criteria 
// (course is "CS101" and score is greater than 80), a regular query 
// might accidentally match across different elements in the array. 
// To guarantee the conditions are met by the same single object 
// inside the array, you use $elemMatch
// schoolDB.students.find({
//    grades: {
//       $elemMatch: { course: "CS101", score: { $gt: 80 } }
//    }
// });

// 5. Update fields and documents.
// $inc = increment (or decrement if we provide negative values)
const schoolDB = db.getSiblingDB('school');
schoolDB.students.updateOne(
	{ email: 'ada@example.com' },
	{ $set: { major: 'Computer Science' }, $inc: { age: 1 } }
);
schoolDB.students.updateMany({ age: { $gte: 21 } }, { $set: { adult: true } });
schoolDB.students.replaceOne(
	{ email: 'alan@example.com' },
	{ name: 'Alan Turing', email: 'alan@example.com', age: 23, address: { city: 'Manchester' } }
);

// we can return the document after updates if needed (instead of just seing the metadata of the operation)
const updatedStudent = schoolDB.students.findOneAndUpdate(
   { email: 'ada@example.com' },
   { $inc: { age: 1 } },
   { returnDocument: 'after' } // Returns the modified document instead of the old one
);


// 6. Arrays, nested fields, and removing fields.
// $push = adds items to arrays (and creates the array if it doesnt's exist and JSON validation allows it)
// $addToSet = adds an item to an array only if it doesn't already exist, i.e. prevents duplicates
// { $pull: { enrolledCourses: 'CS101' } } --- removes specific item from array by value or condition
// $pullAll - remove more than one item  ex: { $pullAll: { enrolledCourses: ['CS101', 'MATH201'] } }
// $pop = removes items from front of array with '-1' or end of array with '1'
// schoolDB.students.updateOne(
//    { email: 'ada@example.com' },
//    { $pop: { enrolledCourses: 1 } } 
// );
const schoolDB = db.getSiblingDB('school');
schoolDB.students.updateOne(
	{ email: 'ada@example.com' },
	{ $push: { skills: 'JavaScript' }, $set: { 'address.postcode': 'NW1' } }
);
schoolDB.students.updateOne({ email: 'ada@example.com' }, { $unset: { adult: '' } });

// upsert either updates (if document exists) or insert (if it doesn't)
schoolDB.students.updateOne(
   { email: 'newstudent@example.com' }, // Filter
   { $set: { name: 'Grace', age: 22 } },  // Update
   { upsert: true }                     // Creates the doc if it doesn't exist!
);

// 7. Delete documents and collections.
const schoolDB = db.getSiblingDB('school');
schoolDB.students.deleteOne({ email: 'alan@example.com' });
schoolDB.students.deleteMany({ adult: false });
schoolDB.enrollments.drop(); // Drop a collection.

// This is an aggregation pipeline taking in data from multiple left joins.
// $lookout - essentially mongoDB left join; will always return an array even if only 
// one document is found 
// $unwind - flattens the returned array from the previous lookups
// (e.g., [{name: "Alice", ...}]) into a plain object (e.g., {name: "Alice", ...}).
// $project - decides which fields appear in the final output
const schoolDB = db.getSiblingDB('school');
schoolDB.enrollments.aggregate([
	{ $lookup: { from: 'students', localField: 'studentId', foreignField: '_id', as: 'student' } },
	{ $unwind: '$student' },
	{ $lookup: { from: 'courses', localField: 'courseId', foreignField: '_id', as: 'course' } },
	{ $unwind: '$course' },
	{ $project: { _id: 0, student: '$student.name', course: '$course.title', grade: 1 } }
]);

// if we wanted to do an inner-join, we would implement $match
schoolDB.enrollments.aggregate([
   { 
     $lookup: { 
       from: 'students', 
       localField: 'studentId', 
       foreignField: '_id', 
       as: 'student' 
     } 
   },
   // This filters out enrollments where the student doesn't exist anymore
   { $match: { "student.0": { $exists: true } } }, 
   { $unwind: '$student' }
]);

// MongoDB doesn't have a direct equivalent to a SQL RIGHT JOIN or FULL OUTER JOIN.
// we COULD do a cross-join. but these are extremely computionally expensive:
// db.sizes.aggregate([
//    { 
//      $lookup: { 
//        from: "fits", 
//        pipeline: [], // An empty pipeline means "grab everything without filtering"
//        as: "fitCombination" 
//      } 
//    },
//    // This flattens the array, multiplying your documents to form the Cartesian product
//    { $unwind: "$fitCombination" }
// ]);

// 9. Indexes improve query performance and can enforce uniqueness.
const schoolDB = db.getSiblingDB('school');
// schoolDB.students.createIndex({ email: 1 }, { unique: true });
// schoolDB.courses.createIndex({ code: 1 }, { unique: true });
// schoolDB.enrollments.createIndex({ studentId: 1, courseId: 1 });
// schoolDB.students.getIndexes();
schoolDB.students.find({ email: 'ada@example.com' }).explain('executionStats');
// schoolDB.students.dropIndex('email_1'); // Remove an index by name.


// schoolDB.students.find(...).explain()
// What it does: Shows you the planned path 
// (e.g., whether it chose a collection scan COLLSCAN or an index scan IXSCAN) 
// without actually running the query.

//schoolDB.students.find(...).explain('executionStats')
// What it does: Runs the query, measures the exact time, 
// and shows execution metrics. This is the one you will use 
// 90% of the time to tune performance.

// schoolDB.students.find(...).explain('allPlansExecution')
// What it does: Runs the winning plan plus all the other 
// candidate plans MongoDB considered, showing why it chose the one it did.

// 10. Sort, limit, and aggregate.
const schoolDB = db.getSiblingDB('school');
schoolDB.students.find().sort({ age: -1 }).limit(5);
schoolDB.students.aggregate([
	{ $group: { _id: '$major', numberOfStudents: { $sum: 1 }, averageAge: { $avg: '$age' } } },
	{ $sort: { numberOfStudents: -1 } }
]);

// Other helpful commands: show dbs, db, show collections, db.stats(), db.serverStatus().
