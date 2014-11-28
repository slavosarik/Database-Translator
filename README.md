Database Translator
=========

Author: Slavomír Šárik

Bachelor thesis: Creation of database queries in native language

Faculty of Informatics and Information technologies, Slovak University of Technology Bratislava, Slovakia

Degree Course: Informatics

Supervisor: Ing. Peter Lacko, PhD.

May 2014

> [Link to bachelor thesis](http://www.crzp.sk/crzpopacxe?fs=3570770E49FB47C5B99367B8EA5E6325)

About
----

Database interface, which is able to process user input in slovak language. In the following steps it will analyse input and map it to database to create final query. As a last step it query a database and show attributes of result to user.

Tech info: Java, MySQL, Maven

Installation
--------------

```sh
git clone [git-repo-url] DatabaseTranslator
cd DatabaseTranslator
mvn package
cd target
java -jar DatabaseTranslator-1.0.jar
```

##### Configure database connection properties

\src\main\resources\properties\database.properties


Version
----

1.0


License
----

Further use and modification of source code possible only with the permission of the author.

Special thank
----
>As a data source for the morphological analysis was used Morphological database of Slovak National Corpus, which is developed by Ludovit Stur Institute of Linguistics. Institute of Linguistics has provided this morphological database for non-scientific - research purposes.

>http://korpus.juls.savba.sk/


