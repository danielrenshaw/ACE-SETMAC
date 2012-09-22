==================
Step-by-step guide
==================

To demonstrate the use of ACE, a simple demonstration project is created step-by-step as follows:

1) Create a new Java project.

    Here we create a project named ACEDemo and use the base package name uk.ac.ed.inf.acedemo.

    This project needs to reference the ACE library as well as libraries used by ACE:

    * Apache Commons CLI 1.2
    * Apache Commons IO 2.3
    * Sqlite JDBC 3.7.2 (only required if you intend to use a Sqlite-based database)
    * Google Guava 12.0
    * Weka 3.7.7 (only required if you intend to use a Weka-based classifier)

2) Prepare the training and testing data.

    For this demonstration, a very small and simple dataset is used - the Spambase [1]. This is a collection of attributes relating to 4601 emails of which roughly 40% have been classified by hand as spam.

    This is a small set of data that does not need any pre-processing. It is provided in CSV format with documentation describing the form and meaning of each attribute.

    In this case, the data is small enough to be embedded as a resource within the ACEDemo JAR. The spambase data file and supporting documentation files are placed in the uk.ac.ed.inf.acedemo.databases package.

3) Create place-holder classes.

    Much of the solution to this classification task (identifying spam emails) will be accomplished through use of common classes found in ACE. However, we will be responsible for providing the classes that store and load the spambase data, and for implementing the classifier itself.

    To get a runnable system, we need to create some empty classes that implement the appropriate interfaces.

    The Classifier is responsible for creating a trainer that can be used to build a model that can be used to classifier unseen instances. In this case we'll be using a Weka-based classifier.

        package uk.ac.ed.inf.acedemo.classifiers;

        import uk.ac.ed.inf.ace.ReadableDocument;
        import uk.ac.ed.inf.ace.SimpleEngine;
        import uk.ac.ed.inf.ace.classifiers.WekaClassifier;
        import weka.core.Instance;
        import weka.core.Instances;

        public class Classifier extends WekaClassifier<SimpleEngine, uk.ac.ed.inf.ace.config.v1.WekaClassifier> {
          public Classifier(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.WekaClassifier config) {
            super(engine, config);
          }

          @Override
          protected Instances constructInstances() {
            throw new UnsupportedOperationException("Not supported yet.");
          }

          @Override
          protected Instance constructInstance(ReadableDocument document) {
            throw new UnsupportedOperationException("Not supported yet.");
          }
        }

    The Database is responsible for storing and providing access to the data. In this case we'll be using Sqlite allowing us to use the base class that implements a lot of standard functionality, within ACE.

        package uk.ac.ed.inf.acedemo.databases;

        import uk.ac.ed.inf.ace.SimpleEngine;
        import uk.ac.ed.inf.ace.databases.SqliteDatabaseBase;

        public class SqliteDatabase extends SqliteDatabaseBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.SqliteDatabase> {

          public SqliteDatabase(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.SqliteDatabase config) throws Exception {
            super(engine, config, null, null);
          }

          @Override
          protected void loadDataSets(long randomSeed, boolean generateTestData) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
          }
        }

    The DocType is responsible for loading an individual document from the database in a particular form.

        package uk.ac.ed.inf.acedemo.doctypes;

        import uk.ac.ed.inf.ace.DocTypeBase;
        import uk.ac.ed.inf.ace.ReadWriteableDocument;
        import uk.ac.ed.inf.ace.SimpleEngine;

        public class DocType extends DocTypeBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.DocType> {
          public DocType(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.DocType config) {
            super(engine, config);
          }

          @Override
          public ReadWriteableDocument getDocument(String documentId) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
          }
        }

    The DataSet is responsible for loading the training, validation and test document sets from the database, using a DocType. The DataSetBase class abstracts away a lot of this work.

        package uk.ac.ed.inf.acedemo.datasets;

        import uk.ac.ed.inf.ace.DataSetBase;
        import uk.ac.ed.inf.ace.SimpleEngine;

        public class DataSet extends DataSetBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.DataSet> {
          public DataSet(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.DataSet config) {
            super(engine, config);
          }

          @Override
          protected Iterator<String> getDocumentIds(boolean forTest, Random random) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
          }
        }

4) Create a main method.

	ACE includes a standard main method that can be called from our demonstration program's own main method.

        package uk.ac.ed.inf.acedemo;

        import uk.ac.ed.inf.ace.Ace;

        public class AceDemo {
          public static void main(String[] args) {
            Ace.main(args);
          }
        }

5) Create a configuration XML file.

    The ACE framework has, at its core, an XML based configuration system which ties all the components together.

    An XML Schema Definition (XSD) file for the common ACE components can be found in the uk.ac.ed.inf.ace.config.v1 package. From this, all of the Java files found in that same package are generated automatically using the JAXB XJC tool. If the schema is ever altered, it is essential that the XJC tool be re-run (instructions can be found in the header of the XSD file).

    For complex tasks, a supplementary XSD may be required to allow configuration of components specific to the task. See the SETMAC project for an example of this (a custom Engine is used which incorporates Sites along with many more complex Classifiers.) Note that if a supplementary XSD is used, the -s (path to the supplementary XSD) and -x (Java package containing the supplementary JAXB classes) command line flags must be specified.

    In our case, ACEDemo does not require a custom XSD since none of our uncommon components require configuration.

        <?xml version="1.0" encoding="UTF-8"?>
        <engine
          xmlns="urn://uk/ac/ed/inf/ace/config/v1"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="urn://uk/ac/ed/inf/ace/config/v1 ..\ACE\src\uk\ac\ed\inf\ace\config\v1\config.xsd"
          name="SimpleEngine"
          type="uk.ac.ed.inf.ace.SimpleEngine">
          <databases>
            <sqliteDatabase
              name="AceDemo"
              type="uk.ac.ed.inf.acedemo.databases.SqliteDatabase"
              pathname="\AceDemo\AceDemo.db" />
          </databases>

          <environments>
            <environment
              name="AceDemo"
              randomSeed="1"
              holdOutProportion="0.2"
              database="AceDemo"
              outputPath="\AceDemo\Output\"
              threadPoolSize="2"
              cacheCapacity="5000"
              cacheEvictAfterAccess="60"
              crossValidationFolds="5"
              classificationDocType="DocType" />
          </environments>

          <dataSets>
            <dataSet
              name="DataSet"
              type="uk.ac.ed.inf.acedemo.datasets.DataSet" />
          </dataSets>

          <docTypes>
            <docType
              name="DocType"
              type="uk.ac.ed.inf.acedemo.doctypes.DocType"
              labelPropertyKey="is_spam" />
          </docTypes>

          <classifiers>
            <wekaClassifier
              name="Classifier"
              type="uk.ac.ed.inf.acedemo.classifiers.Classifier"
              wekaType="weka.classifiers.trees.J48" />
          </classifiers>

          <tasks>
            <binaryIntegerTask
              name="IsSpam"
              labelPropertyKey="is_spam"
              trueLabel="1" />
          </tasks>

          <preCacheProcessors />

          <postCacheProcessors />

          <programmes>
            <programme
              name="Baseline" />
          </programmes>
        </engine>

    Note that we have referred to our place-holder DataSet, DocType, and Classifier; and we have made use of a common task which is appropriate for this binary "is spam" task. We do not need any pre or post cache processors since the document does not need to be altered after loading from the database.

    The Baseline programme will simply combine the single DataSet, single DocType, single classifier, and single Task to perform a single experiment with 5 cross-validation folds. The holdOutProportion attribute will be ignored for this demonstration project - all data will be used for training and validation, none for testing. For examples of more complex configurations, take a look at the config.xml in the SETMAC project.

    If the configuration file is named config.xml and placed in the root directory of the project, then it will be found automatically. Alternatively, it can be placed elsewhere and referenced on the command line using the -c flag.

6) First run.

    The project should now contain everything required to run the project for the first time. It won't do anything of course and will in fact throw an exception and halt (UnsupportedOperationException from our place-holder SqliteDatabase implementation).

    However, as the place-holders are filled in, the project can now be run regularly to monitor progress and detect (unexpected) errors.

    To run the project, you must specify a few command line options:

    * -e AceDemo (selects the environment to use, the name must match the name of one of the environments configured in the XML file - see above)
    * -r (forces ACE to rebuild the database from scratch; Spambase is small enough to do this every time, but with larger sources of data you'll need to be more circumspect)
    * -p (forces ACE to purge the output files when ACEDemo is run; reasonable for testing quick running experiments like these, but use with caution after more intensive runs)

    ACE uses standard Java logging facilities and you may wish to alter the format of the output. For example, creating a file name logging.properties containing the following and referencing it on the command line (see below).

        handlers=java.util.logging.ConsoleHandler,java.util.logging.FileHandler
        .level=ALL
        java.util.logging.FileHandler.level=ALL
        java.util.logging.FileHandler.pattern=acedemo.log
        java.util.logging.FileHandler.formatter=uk.ac.ed.inf.ace.cli.LogFormatter
        java.util.logging.ConsoleHandler.level=INFO
        java.util.logging.ConsoleHandler.formatter=uk.ac.ed.inf.ace.cli.LogFormatter

    You may also wish to enable Java assertions, which are used to a limited degree through ACE, to ensure there are no unexpected problems (this is especially useful if you encounter some internal/external/string label type mismatches). Similarly, when processing larger amounts of data, you may benefit from a larger Java heap.

    * -ea (enables Java assertion checking)
    * -Djava.util.logging.config.file=logging.properties (a pointer to the logging settings file)
    * -Xms2G -Xmx2G (set to appropriate figures given the amount of memory on your machine)

7) Implement Database.

    We're using a Sqlite database to store the spambase data but any other JDBC compliant data store would work just fine. A non-JDBC compliant data store could be used, but with a little more effort.

    The basic SqliteDatabase implementation provided by ACE requires a DDL script to define the database structure and we need to provide some code that will load the source data into the database on request. We may also optionally provide some code to perform optimization activities after the data has been loaded.

    The database structure for spambase is very simple. All of the attributes are described in the spambase_names file, and this can easily be converted into a SQL table definition. The name of the table is not important, though it will be referenced again later. The GO directive is important if the DDL contains multiple statements that must be executed independently.

        CREATE TABLE Item (
            word_freq_make NUMERIC NOT NULL,
            word_freq_address NUMERIC NOT NULL,
            word_freq_all NUMERIC NOT NULL,
            word_freq_3d NUMERIC NOT NULL,
            word_freq_our NUMERIC NOT NULL,
            word_freq_over NUMERIC NOT NULL,
            word_freq_remove NUMERIC NOT NULL,
            word_freq_internet NUMERIC NOT NULL,
            word_freq_order NUMERIC NOT NULL,
            word_freq_mail NUMERIC NOT NULL,
            word_freq_receive NUMERIC NOT NULL,
            word_freq_will NUMERIC NOT NULL,
            word_freq_people NUMERIC NOT NULL,
            word_freq_report NUMERIC NOT NULL,
            word_freq_addresses NUMERIC NOT NULL,
            word_freq_free NUMERIC NOT NULL,
            word_freq_business NUMERIC NOT NULL,
            word_freq_email NUMERIC NOT NULL,
            word_freq_you NUMERIC NOT NULL,
            word_freq_credit NUMERIC NOT NULL,
            word_freq_your NUMERIC NOT NULL,
            word_freq_font NUMERIC NOT NULL,
            word_freq_000 NUMERIC NOT NULL,
            word_freq_money NUMERIC NOT NULL,
            word_freq_hp NUMERIC NOT NULL,
            word_freq_hpl NUMERIC NOT NULL,
            word_freq_george NUMERIC NOT NULL,
            word_freq_650 NUMERIC NOT NULL,
            word_freq_lab NUMERIC NOT NULL,
            word_freq_labs NUMERIC NOT NULL,
            word_freq_telnet NUMERIC NOT NULL,
            word_freq_857 NUMERIC NOT NULL,
            word_freq_data NUMERIC NOT NULL,
            word_freq_415 NUMERIC NOT NULL,
            word_freq_85 NUMERIC NOT NULL,
            word_freq_technology NUMERIC NOT NULL,
            word_freq_1999 NUMERIC NOT NULL,
            word_freq_parts NUMERIC NOT NULL,
            word_freq_pm NUMERIC NOT NULL,
            word_freq_direct NUMERIC NOT NULL,
            word_freq_cs NUMERIC NOT NULL,
            word_freq_meeting NUMERIC NOT NULL,
            word_freq_original NUMERIC NOT NULL,
            word_freq_project NUMERIC NOT NULL,
            word_freq_re NUMERIC NOT NULL,
            word_freq_edu NUMERIC NOT NULL,
            word_freq_table NUMERIC NOT NULL,
            word_freq_conference NUMERIC NOT NULL,
            char_freq_sc NUMERIC NOT NULL,
            char_freq_op NUMERIC NOT NULL,
            char_freq_os NUMERIC NOT NULL,
            char_freq_em NUMERIC NOT NULL,
            char_freq_ds NUMERIC NOT NULL,
            char_freq_hs NUMERIC NOT NULL,
            capital_run_length_average NUMERIC NOT NULL,
            capital_run_length_longest NUMERIC NOT NULL,
            capital_run_length_total NUMERIC NOT NULL,
            is_spam NUMERIC NOT NULL
        )
        GO

    The first of the two null parameters currently being used in the call to the super constructor from our place-holder SqliteDatabase implementation is a File object describing the location of the database itself. We can get the pathname for the database from the configuration file from the provided (JAXB) config object.

        private static final String DEFAULT_PATH = "acedemo.db";
        ...
        new File(Utilities.ifNull(config.getPathname(), DEFAULT_PATH))

    The second null parameter is a File object describing the location of the DDL file. In this case, we store the DDL as a resource in the JAR.

        private static final String DDL_NAME = "ddl.sql";
        ...
        new File(SqliteDatabase.class.getResource(DDL_NAME).getFile()))

    The spambase data is provided in a simple CSV format but it isn't necessary for us to parse the CSV content in order to load it into a Sqlite database. All that is necessary is to read it line by line, wrap the line within INSERT INTO Item VALUES (...), and execute these INSERT statements against the database.

        @Override
        protected void loadDataSets(long randomSeed, boolean generateTestData) throws Exception {
          StringBuilder stringBuilder = new StringBuilder("BEGIN;\n");

          try (InputStream inputStream = SqliteDatabase.class.getResourceAsStream(DATA_NAME)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
              try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                  line = line.trim();

                  if (line.isEmpty()) {
                    continue;
                  }

                  stringBuilder.append("INSERT INTO Item VALUES (");
                  stringBuilder.append(line);
                  stringBuilder.append(");\n");
                }
              }
            }
          }

          stringBuilder.append("COMMIT;\n");
          createStatement().executeUpdate(stringBuilder.toString());
        }

    Note that all of the INSERT statements are executed as a single batch. This is not essential but increases performance substantially over executing each individually. If you have a very large number of rows to insert, consider batching them into chunks of 10,000 each.

    The SETMAC project contains a more involved example of loading a much larger dataset from XML files into multiple related tables.

    Running the project now, with the -r command line flag set to force a database rebuild, should load the spambase data into a new Sqlite database at the location you specified in the XML file. The framework will then halt with another UnsupportedOperationException from the Classifier this time, as it attempts to run the experiment's first fold.

8) Implement DataSet.

    Since we are not partitioning the data into training/validation and test sets, we simply need to return all rows from the Item table.

    We can make use of the "prepared statements" caching mechanism as follows. This will ensure the SQL will only be compiled once.

        private static final Supplier<String> getDocumentIdsSqlSupplier = new Supplier<String>() {
          @Override
          public String get() {
            return " SELECT CAST(rowid AS TEXT) AS id FROM Item";
          }
        };

        @Override
        protected Iterator<String> getDocumentIds(boolean forTest, Random random) throws Exception {
          PreparedStatement statement = getEngine().getEnvironment().getDatabase().getPreparedStatements().get(getDocumentIdsSqlSupplier);
          return new ScalarResultSetIterator<>(statement.executeQuery());
        }

9) Implement DocType.

    Here we need to load an individual row from the Item table and convert it into a ReadWriteableDocument. The SQL includes all columns from the table (the middle columns are snipped out here for brevity). Note that the SQL is parameterised.

    The type of the document's true label needs to be set carefully - it must match the type expected by the task(s) this DocType will be used with. In this case, the task expects an integer label.

        private static final Supplier<String> getDocumentDetailsSqlSupplier = new Supplier<String>() {
          @Override
          public String get() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" SELECT");
            stringBuilder.append("     word_freq_make,");
            stringBuilder.append("     word_freq_address,");
            ...
            stringBuilder.append("     capital_run_length_total,");
            stringBuilder.append("     is_spam");
            stringBuilder.append(" FROM Item");
            stringBuilder.append(" WHERE rowid = ?");
            return stringBuilder.toString();
          }
        };

        @Override
        public ReadWriteableDocument getDocument(String documentId) throws Exception {
          ReadWriteableDocument document;
          PreparedStatement statement = getEngine().getEnvironment().getDatabase().getPreparedStatements().get(getDocumentDetailsSqlSupplier);
          statement.setLong(1, Long.parseLong(documentId));

          try (ResultSet resultSet = statement.executeQuery()) {
            double[] content = new double[57];

            for (int index = 0; index < content.length; index++) {
              content[index] = resultSet.getDouble(index + 1);
            }

            document = new ReadWriteableDocument(documentId, content);
            document.getProperties().put(getConfig().getLabelPropertyKey(), resultSet.getInt(58));
          }

          return document;
        }

10) Implement Classifier.

    Since we are using a Weka-based classifier, all we need to provide is code capable of constructing Weka Instances and Instance objects on demand. This is essentially a mapping from the ACE document-space into the Weka instance-space. Note that Weka requires string labels; the task will be used to parse from these strings into their true type.

        @Override
        protected Instances constructInstances() {
          ArrayList<Attribute> attributes = Lists.newArrayList();
          attributes.add(new Attribute("IsSpam", ImmutableList.of("true", "false")));

          for (int index = 0; index < 57; index++) {
            attributes.add(new Attribute("Attribute" + index));
          }

          Instances instances = new Instances("Data", attributes, 1);
          instances.setClassIndex(0);
          return instances;
        }

        @Override
        protected Instance constructInstance(ReadableDocument document) {
          Instance instance = new DenseInstance(58);
          double[] values = (double[])document.getContent();

          for (int index = 0; index < values.length; index++) {
            instance.setValue(index + 1, values[index]);
          }

          return instance;
        }

11) First complete run.

    You should now have a complete working system which, when run produces the following output.

    A directory named Baseline-DataSet-DocType-Classifier-IsSpam containing 10 files (a confusion matrix and misclassification file for each of the 5 folds), a file named Baseline-DataSet-DocType-Classifier-IsSpam-ConfusionMatrix.txt (a unified confusion matrix incorporating the results from all cross-validation folds), and a file named Results.csv (summary measures for each experiment).

    The files containing confusion matrices should be self-explanatory (besides noting that the actual labels are in the first column and the predicted labels are in the first row).

    The misclassification files include a row for each test document which was misclassified. The final column of this file includes a representation of the document content, but since this is a plain Java array in this case, it's not very useful! The third column is the Sqlite rowid which can be used to identify which data was misclassified.

    The Results.csv file includes the following measures for each distinct experiment (averaged over cross-validation folds).

    * ACC: Accuracy (see http://en.wikipedia.org/wiki/Accuracy)
    * PRE: Precision (see http://en.wikipedia.org/wiki/Precision_(information_retrieval))
    * REC: Recall (see http://en.wikipedia.org/wiki/Recall_(information_retrieval))
    * FSC: F1-Score (see http://en.wikipedia.org/wiki/F1_score)
    * NPV: Negative Predictive Value (see http://en.wikipedia.org/wiki/Negative_predictive_value)
    * SPE: Specificity (see http://en.wikipedia.org/wiki/Specificity_(statistics))

12) Start playing.

    You can now start manipulating the configuration, trying out other classifiers for example. By setting up for Weka-based classifiers, we can try lots of different classifier types with no more than a simple change in the XML file. For example, by replacing the <classifiers>...</classifiers> section with the following, you can compare Naive Bayes with the J48 decision-tree classifier used so far.

        <classifiers>
          <wekaClassifier
            name="J48"
            type="uk.ac.ed.inf.acedemo.classifiers.Classifier"
            wekaType="weka.classifiers.trees.J48" />
          <wekaClassifier
            name="NaiveBayes"
            type="uk.ac.ed.inf.acedemo.classifiers.Classifier"
            wekaType="weka.classifiers.bayes.NaiveBayes" />
        </classifiers>

    You should find that Naive Bayes performs poorly in comparison.

    Any class that implements the Weka Classifier interface can be referenced in the wekaType attribute. To see what's available, see http://weka.sourceforge.net/doc/weka/classifiers/Classifier.html.

==========
References
==========

[1] Frank, A. & Asuncion, A. (2010). UCI Machine Learning Repository [http://archive.ics.uci.edu/ml]. Irvine, CA: University of California, School of Information and Computer Science. http://archive.ics.uci.edu/ml/datasets/Spambase
