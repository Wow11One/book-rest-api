<h1>Book rest-api</h1>

<h2>How to run the project?</h2>
<p>In the root folder of the project you need to run the following command:</p>
<p><b>docker compose up -- build</b></p>

<p>So it is important to have Docker installed on your PC. Building process can take from 2 to 3 minutes</p>

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
are validated(string values should be not blank and numeric - not null.
The <b>circulation</b> field value should be at least 100.
The <b>pageAmount</b> field value should be at least 10.
</p>
<h5>Usage examples</h5>

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

<h6>Response body</h6>

<h2>Contact</h2>
<p> If you have any questions, you can contact me at any time in Telegram: <b>+380632125159</b></p>