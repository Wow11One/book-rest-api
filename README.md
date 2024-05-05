<h1>Book rest-api</h1>

<h2>How to run the project?</h2>
<p>In the root folder of the project you need to run the following command:</p>
<p><b>docker compose up -- build</b></p>

<p>It is important to have Docker installed on your PC. Building process can take from 2 to 3 minutes</p>

<h2>How to run tests?</h2>

<p>In the root folder of the project you need to run the following command:</p>
<p><b>mvn test</b></p>

<p>Tests in the project are using testcontainers.</p>

<h2>Entity description</h2>
<p>All fields are non-nullable</p>

<h3>Book entity</h3>
<dl>
<dt>id: Long</dt>
<dd>auto increment id value.</dd>
<dt>yearPublished: Integer</dt>
<dt>publicationHouse: String</dt>
<dt>genre: String</dt>
<dt>pageAmount: Integer</dt>
<dt>author: Author</dt>
<dd>the foreign key for the Author entity. It has many-to-one mapping.</dd>
</dl>

<h3>Author entity</h3>
<dl>
<dt>id: Long</dt>
<dd>auto increment id value.</dd>
<dt>name: String</dt>
<dt>birthYear: Integer</dt>
<dt>country: String</dt>
<dt>books: List &lt;Book&gt; </dt>
<dd>the list of book entities. It has one-to-many mapping.</dd>
</dl>

<h2>Api description</h2>
<p>All endpoints start with the '/api' prefix</p>

<h3>Book endpoints</h3>

<h4><b>POST</b> /api/books</h4>
<p>Creates a new Book entity record. All request fields 
are validated (string values should be not blank and numeric - not null.
The <b>circulation</b> field value should be at least 100.
The <b>pageAmount</b> field value should be at least 10.
</p>
<h4>Usage examples</h4>

<h5>Successful request</h5>
<h6>Request body</h6>

```json
{
  "title": "Brave New World",
  "yearPublished": 1890,
  "publicationHouse": "folio",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "authorId": 1
}
```

<h5>Response status</h5>
<b>201 created</b>
<h6>Response body</h6>

```json
{
  "id": 1,
  "title": "Brave New World",
  "yearPublished": 1890,
  "publicationHouse": "folio",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "author": {
    "id": 1,
    "name": "Maksym Kidruk"
  }
}
```

<hr/>

<h5>Not valid request</h5>
<h6>Request body</h6>

```json
{
  "title": "",
  "yearPublished": null,
  "publicationHouse": "    ",
  "genre": null,
  "circulation": 1,
  "pageAmount": -99,
  "authorId": 1
}
```

<h6>Response status</h6>
<b>400 Bad Request</b>
<h6>Response body</h6>

```json
{
  "message": "Validation failed for: genre - genre should not be blank; publicationHouse - publicationHouse should not be blank; circulation - circulation value should be more than 100; pageAmount - pageAmount value should be more than 10; title - title should not be blank; yearPublished - yearPublished should not be null; "
}
```

<hr/>
<h5>Failed request with non-existent author's id</h5>
<h6>Request body</h6>

```json
{
  "title": "Brave New World",
  "yearPublished": 1890,
  "publicationHouse": "folio",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "authorId": 666
}
```

<h6>Response status</h6>
<b>404 Not Found</b>
<h6>Response body</h6>

```json
{
  "message": "author with such id does not exist"
}
```

<hr/>


<h4><b>GET</b> /api/books/:id</h4>
<p>Returns the details of the Book entity record,
including the Author data it refers to. If there is no book with such id,
then 404 status is returned.
</p>

<h4>Usage examples</h4>
<h5>Successful request</h5>
<h6>Request pathname</h6>
<p><b>/api/books/1</b> (id can be any existing book id)</p>

<h6>Response status</h6>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "id": 1,
  "title": "Brave New World",
  "yearPublished": 1890,
  "publicationHouse": "folio",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "author": {
    "id": 1,
    "name": "Maksym Kidruk"
  }
}
```

<hr/>


<h5>Failed request</h5>
<h6>Request pathname</h6>
<p><b>/api/books/666</b> (any non-existent id)</p>

<h6>Response status</h6>
<b>404 Not Found</b>

<h6>Response body</h6>

```json
{
  "message": "book with such id does not exist"
}
```

<hr/>

<h4><b>PUT</b> /api/books/:id</h4>
<p>Updates the data of one record of Book entity by ID.
</p>
<h4>Usage examples</h4>

<h5>Successful request</h5>

<h6>Request pathname</h6>
<p><b>/api/books/1</b> (id can be any existing book id)</p>

<h6>Request body</h6>

```json
{
  "title": "Updated book",
  "yearPublished": 1890,
  "publicationHouse": "updated house",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "authorId": 3
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "id": 1,
  "title": "Updated book",
  "yearPublished": 1890,
  "publicationHouse": "updated house",
  "genre": "gothic",
  "circulation": 100000,
  "pageAmount": 254,
  "author": {
    "id": 3,
    "name": "Ayn Rand"
  }
}
```

<hr/>

<h5>Invalid request</h5>

<h6>Request pathname</h6>
<p><b>/api/books/1</b> (id can be any existing book id)</p>

<h6>Request body</h6>

```json
{
  "title": "",
  "yearPublished": null,
  "publicationHouse": null,
  "genre": "      ",
  "circulation": 100000,
  "pageAmount": 1,
  "authorId": 3
}
```

<h5>Response status</h5>
<b>400 Bad Request</b>

<h6>Response body</h6>

```json
{
  "message": "Validation failed for: genre - genre should not be blank; publicationHouse - publicationHouse should not be blank; title - title should not be blank; yearPublished - yearPublished should not be null; pageAmount - pageAmount value should be more than 10; "
}
```

<hr/>

<h4>DELETE /api/books/:id</h4>
<p>Deletes the data of one record of Book entity by ID.
It returns the 204 status even if there is no book with such id 
(I don't throw exceptions because a book with non-existent id has no content)
</p>
<h4>Usage examples</h4>

<h5>Successful request</h5>

<h6>Request pathname</h6>
<p><b>/api/books/1</b> (id can be any existing book id)</p>

<h5>Response status</h5>
<b>204 No Content</b>

<hr/>


<h4><b>POST</b> /api/books/upload</h4>
<p>
Accepts the JSON file for which the parser was developed in Task 1 
(the format was adapted). Stores all valid records from this file in the database. 
The response generates a JSON showing the number of successfully imported records, 
as well as unsuccessfully ones (in case a json object from the file has invalid fields 
or refers to non-existent author id).
Doesn't save any entities in case the json file format is not correct.
Request body should have 'form-data' format with a 'file' field, where you should pass a necessary file.
</p>

<h4>Usage examples</h4>
<p>
Test data is stored in the 'resources/upload-json-data' folder inside the project.
The names of files indicate the amount of valid and invalid objects inside of it.
(for instance, 10-0.json has 10 valid and 0 invalid json objects, 8-2.json - 8 correct and 2 wrong records and so on).
</p>

<h5>Successful request with 8 out of 10 successfully saved records</h5>

<h6>Request body</h6>
<p>form-data with a 'file' key and a 'resources/upload-json-data/8-2.json' value.</p> 

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "successful": 8,
  "failed": 2
}
```

<hr/>

<h5>Successful request with 10 out of 10 successfully saved records</h5>

<h6>Request body</h6>
<p>form-data with a 'file' key and a file value from 'resources/upload-json-data/10-0.json'.</p> 

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "successful": 10,
  "failed": 0
}
```

<hr/>

<h5>Failed request with invalid json file format</h5>

<h6>Request body</h6>
<p>form-data with a 'file' key and a file value from 'resources/upload-json-data/not-correct-format.json'.</p> 

<h5>Response status</h5>
<b>400 Bad Request</b>

<h6>Response body</h6>

```json
{
  "message": "not correct json format: object should start from start_object token\n at [Source: (sun.nio.ch.ChannelInputStream); line: 2, column: 4]"
}
```

<hr/>

<h4><b>POST</b> /api/books/_list</h4>
<p>
Returns a data structure that has a list of Book entity elements 
that match the requested page and the total number of pages.
Page and size value in request body should not be null.
The entities in the array have a reduced set of Book fields (because not all fields are needed in list mode).
In the request, you can optionally specify fields by which you can filter records.
</p>
There are 3 variables you can use for filtering purposes:
<ul>
<li>authorId</li>
<li>genre</li>
<li>publicationHouse</li>
</ul>

<p>Indexes in DB were created for each filter parameters.</p>

<h4>Usage examples</h4>
<h5>Successful request with no filters</h5>

<h6>Request body</h6>

```json
{
  "page": 1,
  "size": 3
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "totalPages": 6,
  "list": [
    {
      "id": 22,
      "title": "Koloniya",
      "genre": "fantasy",
      "publicationHouse": "Bloomsbury",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    },
    {
      "id": 23,
      "title": "Atlas shrugged",
      "genre": "fiction",
      "publicationHouse": "folio",
      "author": {
        "id": 2,
        "name": "Taras Shevchenko"
      }
    },
    {
      "id": 24,
      "title": "The witcher 3",
      "genre": "fiction",
      "publicationHouse": "our book",
      "author": {
        "id": 5,
        "name": "Andrzej Sapkowski"
      }
    }
  ]
}
```

<hr/>

<h5>Successful request with one filter parameter</h5>

<h6>Request body</h6>

```json
{
  "authorId": 1,
  "page": 1,
  "size": 3
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "totalPages": 2,
  "list": [
    {
      "id": 22,
      "title": "Koloniya",
      "genre": "fantasy",
      "publicationHouse": "Bloomsbury",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    },
    {
      "id": 28,
      "title": "The Catcher in the Rye",
      "genre": "coming-of-age",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    },
    {
      "id": 30,
      "title": "The Hobbit",
      "genre": "fantasy",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    }
  ]
}
```

<hr/>

<h5>Successful request with two filter parameters</h5>

<h6>Request body</h6>

```json
{
  "authorId": 1,
  "publicationHouse": "folio",
  "page": 1,
  "size": 3
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "totalPages": 2,
  "list": [
    {
      "id": 28,
      "title": "The Catcher in the Rye",
      "genre": "coming-of-age",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    },
    {
      "id": 30,
      "title": "The Hobbit",
      "genre": "fantasy",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    },
    {
      "id": 38,
      "title": "The Picture of Dorian Gray",
      "genre": "gothic",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    }
  ]
}
```

<hr/>

<h5>Successful request with three filter parameters</h5>

<h6>Request body</h6>

```json
{
  "authorId": 1,
  "publicationHouse": "folio",
  "genre": "fantasy",
  "page": 1,
  "size": 3
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>

```json
{
  "totalPages": 1,
  "list": [
    {
      "id": 30,
      "title": "The Hobbit",
      "genre": "fantasy",
      "publicationHouse": "folio",
      "author": {
        "id": 1,
        "name": "Maksym Kidruk"
      }
    }
  ]
}
```

<hr/>

<h4>POST /api/books/_report</h4>
<p>
Works in the same way as _list, but generates and offers to download a report file with all records 
that match the filter criteria (not just one page).
File format: Excel.
</p>
There are 3 variables you can use for filtering purposes:
<ul>
<li>authorId</li>
<li>genre</li>
<li>publicationHouse</li>
</ul>

<h4>Usage examples</h4>
<h5>Successful request with empty body</h5>

<h6>Request body</h6>
<p>empty body</p>

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>
<p>
Octet stream with the Excel file with name 'book-report.xls', including all Book records.
You can download it using postman.
</p>

<h5>Successful request with three filter parameters</h5>

<h6>Request body</h6>

```json
{
  "authorId": 1,
  "publicationHouse": "folio",
  "genre": "fantasy"
}
```

<h5>Response status</h5>
<b>200 OK</b>

<h6>Response body</h6>
<p>
Octet stream with the Excel file with name 'book-report.xls', including filtered Book records.
You can download it using postman.
</p>

<hr/>

<h2>Contacts</h2>
<p> If you have any questions, you can contact me at any time in Telegram: <b>+380632125159</b></p>