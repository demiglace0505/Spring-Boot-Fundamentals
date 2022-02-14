# Spring Boot Fundamentals

[Spring Boot Fundamentals](https://www.udemy.com/course/springbootfundamentals/)

- [Spring Boot Fundamentals](#spring-boot-fundamentals)
  - [Spring Boot Basics](#spring-boot-basics)
      - [Creating a Starter REST endpoint](#creating-a-starter-rest-endpoint)
      - [@SpringBootApplication](#springbootapplication)
      - [@SpringBootTest](#springboottest)
      - [Dependency Injection](#dependency-injection)
  - [Spring Data JPA](#spring-data-jpa)
  - [REST CRUD API](#rest-crud-api)
      - [REST Client](#rest-client)
  - [Spring Boot Profiles](#spring-boot-profiles)
  - [Logging](#logging)
  - [Health Checks and Metrics](#health-checks-and-metrics)
  - [Spring Security](#spring-security)
  - [Thymeleaf](#thymeleaf)
      - [Thymeleaf Syntax](#thymeleaf-syntax)
      - [Sending data to the Template](#sending-data-to-the-template)
      - [HTML forms](#html-forms)
  - [Database Caching](#database-caching)
  - [Spring Batch](#spring-batch)
      - [CSV to Database](#csv-to-database)
  - [Unit Testing using MockMvc](#unit-testing-using-mockmvc)
  - [Messaging and JMS](#messaging-and-jms)
      - [Messaging Advantages](#messaging-advantages)
      - [Messaging Models](#messaging-models)
      - [JMS](#jms)
  - [Swagger](#swagger)

## Spring Boot Basics

In traditional Spring development, we use XML configuration or annotations to use modules such as spring-core, MVC, ORM, etc. These tend to be cumbersome to maintain over time. We also need to make sure modules are available in the application by defining all dependencies in pom.xml. We also need to be sure that these module's versions are compatible. Spring Boot automates all of the above for us.

Spring Boot offers 4 key features:

1. Auto Configuration
2. Spring Boot Starters
3. Embedded Servlet Container
4. Spring Boot Actuators

With Auto Configuration, we don't need to use XML or Java based configuration anymore. With Spring Boot Starters, module availability and version compatibility is taken care of. Spring Boot comes with an Embedded Servlet Container, in this case, Tomcat. We no longer need any external deployments. Spring Boot Actuators offers health checks and metrics.

#### Creating a Starter REST endpoint

We create a Spring Boot starter project with dependency for Spring Web. We then proceed on creating our HelloController class, which we will annotate with **@RestController** to make it a REST endpoint. We can access this endpoint at `localhost:8080/hello` through Spring Boot's embedded TomCat server without the need to create a WAR file.

```java
@RestController
public class HelloController {
	@RequestMapping("/hello")
	public String hello() {
		return "Hello Spring Boot";
	}
}
```

#### @SpringBootApplication

The **@SpringBootApplication** annotation is a top-level annotation that contains several other annotations such as **@SpringBootConfiguration**, **@EnableAutoConfiguration**, **@ComponentScan**, etc. @SpringBootConfiguration tells the Spring Container that the class can have several bean definitions. @EnableAutoConfiguration tells Spring Boot to automatically configure the Spring application based on the dependencies that exists on the class path. @ComponentScan tells Spring to scan through the classes and see which classes are marked with annotations such as @Component, @Repository, etc.

#### @SpringBootTest

The **@SpringBootTest** annotation tells Spring Boot to search for a class that is marked with @SpringBootApplication annotation and use that class to create a Spring container with all the beans from that application. Once done, each **@Test** annotation will be run and we can Autowire the needed beans into our test methods.

#### Dependency Injection

In this section, we will create the PaymentDAO interface and the Services layer. We will then inject the PaymentDAO into the Services implementation. The architecture is as follows:

> PaymentServiceImpl **IS A** PaymentService
> PaymentServiceImpl **HAS A** PaymentDAO
> PaymentDAOImpl **IS A** PaymentDAO

To do dependency injection, we need to mark our implementation classes with the **@Component** or in this case, specialized annotations such as **@Service** or **Repository**, which tells Spring that the class is a Spring Bean and an object should be created and wherever it is required should be injected. @Repository is used usually for data access code. We then proceed on using **Autowired** on the dao object defined in PaymentServiceImpl so that the dao dependency should be automatically wired at runtime. Spring will search for an implementation class of the PaymentDAO interface and create a bean of that type and inject it into _dao_.

```java
@Service
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	private PaymentDAO dao;

	public PaymentDAO getDao() {
		return dao;
	}

	public void setDao(PaymentDAO dao) {
		this.dao = dao;
	}
}

@Repository
public class PaymentDAOImpl implements PaymentDAO {

}
```

To test if the dependency injection is working, we start with writing the tests. We inject PaymentService into our test class and mark it with **@Autowired**

```java
@SpringBootTest
class CoreApplicationTests {
	@Autowired
	PaymentServiceImpl service;

	@Test
	public void testDependencyInjection() {
		assertNotNull(service.getDao());
	}
}
```

## Spring Data JPA

Spring Data JPA makes creating a Data Access Layer easy. Before Spring Data JPA was introduced, when we used ORM tools such as Hibernate to perform CRUD operations, we typically create a Dao interface that will have all the CRUD operations and an implementation class that will use the **EntityManager** from the JPA standard. This is cumbersome and results to duplication of code. With Spring Data JPA, all we need to do is create a Repository interface which extends interfaces from the Spring Data JPA api such as JpaRepository for the entity we create.

The starter project should include **spring-boot-starter-data-jpa** dependency in pom.xml. We also need to include the appropriate database JDBC driver. For in-memory database such as h2, Spring automatically creates the data access layer related configuration for us.

In this starter project, we will be using H2 Database dependency and Spring Data JPA. We then proceed on creating the Student model class which we mark with **@Entity** annotation. We annotate the id field with **@Id**. These are the two mandatory annotations for an entity.

```java
@Entity
public class Student {
	@Id
	private long id;
	private String name;
	private int testScore;
```

Afterwards we create the StudentRepository interface which extends the **JpaRepository** interface from Spring which will provide us with the CRUD operations.

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

We can then proceed on testing

```java
@SpringBootTest
class SpringdatajpaApplicationTests {
	@Autowired
	private StudentRepository repository;

	@Test
	void testSaveStudent() {
		Student student = new Student();
		student.setId(1l);
		student.setName("doge");
		student.setTestScore(100);
		repository.save(student);

		Student savedStudent = repository.findById(1l).get();
		assertNotNull(savedStudent);
	}
}
```

If we want to see the SQL statements generated by hibernate behind the scenes, we can enable it in application.properties.

```
spring.jpa.show-sql=true
```

## REST CRUD API

Creating RESTful web applications is easy using Spring MVC or Spring Web. We use the **spring-boot-starter-web** starter and mark our Controller class with **@RestController** annotation and map to a path using **@RequestMapping**. The necessary dependencies for the Spring starter project are MySQL Driver, Spring Data JPA, and Spring Web.

We then proceed with creating the Model class and the Repository. We mark the Product model with the **@Entity** annotation, and we annotate the id field with **@Id**. We also need to tell hibernate that this field is an auto-increment field using **@GeneratedValue** annotation.

```java
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	private String description;
	private double price;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
```

We then proceed on implementing the RESTful API by creating a ProductController class. We begin with injecting the controller with the ProductRepository interface using **@Autowired**. We map our methods using **@RequestMapping**. We can capture path variables using `{}` and **@PathVariable** annotation.

```java
@RestController
public class ProductRestController {
	@Autowired
	ProductRepository repository;

	@RequestMapping(value = "/products/", method = RequestMethod.GET)
	public List<Product> getProducts() {
		return repository.findAll();
	}

	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public Product getProduct(@PathVariable("id") int id) {
		return repository.findById(id).get();
	}

	@RequestMapping(value = "/products/", method = RequestMethod.POST)
	public Product createProduct(@RequestBody Product product) {
		return repository.save(product);
	}

	@RequestMapping(value = "/products/", method = RequestMethod.PUT)
	public Product updateProduct(@RequestBody Product product) {
		return repository.save(product);
	}

	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public void deleteProduct(@PathVariable("id") int id) {
		repository.deleteById(id);
	}
}
```

We then proceed on configuring the data source in our application.properties. By default, Spring Boot does not add any context path to our application. We can configure this as well.

```
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=1234
server.servlet.context-path=/productapi
```

We can also run the application outside Spring Tool Suite. By default, a Spring Boot application will be built as a JAR file. We simply need to do a **Maven Install** on the project which will create a JAR file under `/target` directory. We can launch using `java -jar target/<filename.jar>`.

By default, Srping Boot uses the embedded TomCat server. We need to exclude TomCat in our pom.xml and add the web container we want such as jetty or undertow.

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-tomcat</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jetty</artifactId>
		</dependency>
```

#### REST Client

We can consume RESTful web services using **RestTemplate** provided by spring-web. It is a class that uses various methods to perform HTTP methods. We can use these methods in our Test class.

```java
@SpringBootTest
class ProductrestapiApplicationTests {
	@Test
	public void testGetProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject("http://localhost:8080/productapi/products/1", Product.class);
		assertNotNull(product);
		assertEquals("Nitro 5", product.getName());
	}

	@Test
	public void testCreateProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = new Product();
		product.setName("LG G6");
		product.setDescription("great phone");
		product.setPrice(200d);
		Product newProduct = restTemplate.postForObject("http://localhost:8080/productapi/products/", product, Product.class);
		assertNotNull(newProduct);
		assertNotNull(newProduct.getId());
		assertEquals("LG G6", newProduct.getName());
	}

  @Test
	public void testUpdateProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject("http://localhost:8080/productapi/products/1", Product.class);
		product.setPrice(279d);
		restTemplate.put("http://localhost:8080/productapi/products/", product);
	}
}
```

## Spring Boot Profiles

Profiles allows us to dynamically use different configurations across different environments. For example, we can configure the REST URL. We add the following into our application.properties file. This will use the properties from a new properties files, in this case we call it `application-dev.properties`.

application.properties:

```
productrestapi.services.url=http://localhost:8080/productapi/products/
spring.profiles.active=dev
```

application-dev.properties:

```
productrestapi.services.url=http://devserver:8080/productapi/products/
```

And in our Test class, we can access this URL using the **@Value** annotation. The test class will then use the configuration from application-dev.properties.

```java
	@Value("${productrestapi.services.url}")
	private String baseURL;
```

A better way to switch profiles dynamically is through VM arguments

```
-Dspring.profiles.active=dev
```

## Logging

To use the logger, we need to create the Logger from slf4j in our ProductRestController class.

```java
@RestController
public class ProductRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

	@Autowired
	ProductRepository repository;

	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public Product getProduct(@PathVariable("id") int id) {
		LOGGER.info("finding product by ID" + id);
		return repository.findById(id).get();
	}
```

We can also redirect the logs to a file and set up the log level by configuring application.properties. This will create the logs in a file under `logs/application.log`.

```
logging.file.name=logs/application.log
logging.level.root=info
logging.level.org.springframework=error
logging.level.com.demiglace.springweb.controllers.ProductRestController=error
```

## Health Checks and Metrics

There are 4 key metrics that Spring Boot comes with out of the box which we can enable using **Spring Actuators**.

1. Health Checks - check if application is running
2. Application Configuration - access to the configuration the application is using at runtime
3. Application Metrics - exact health of application such as memory usage
4. Key Application Events - if processes are being performed

To enable Health Checks, we just need to add **spring-boot-starter-actuator** dependency. We can then check out the health related endpoint at `http://localhost:8080/productapi/actuator`. By default, only the health endpoint will be exposed. To expose health details we can configure **management.endpoint.health.showdetails** in application.properties. To include other endpoints, we can use **management.endpoints.web.exposure.include**

```
management.endpoint.health.show-details=always
management.endpoints.web.exposure.include=*
```

We can add the build info of our project included into our info by adding the following _executions_ under `build` section of spring-boot-maven-plugin of our pom.xml. Once added, Spring Boot will automatically expose the information about our project in the `/actuator/info` endpoint.

```xml
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<executions>
					<execution>
						<goals>
							<goal>build-info</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

We can also add our own custom health indicators by creating a class that implements the **HealthIndicator** interface from Spring Boot.

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		boolean error = true;
		if (error) {
			return Health.down().withDetail("Error Key: ", 123).build();
		}
		return Health.up().build();
	}
}
```

With this, we would see the following in the `http://localhost:8080/productapi/actuator/health` endpoint.

```
status: "DOWN",
components: {
custom: {
status: "DOWN",
details: {
Error Key: : 123
}
},
```

## Spring Security

We can secure our actuator endpoints by adding dependency **spring-boot-starter-security**. With this, Spring Boot will automatically add security support and authentication.

## Thymeleaf

Thymeleaf is a templating engine that can be used instead of JSP. At runtime, the Thymeleaf engine converts the dynamic portions of our html page into Java and compiled and then executed by the Thymeleaf container.

The necessary dependencies for a Thymeleaf starter project are **spring-boot-starter-web** and **spring-boot-starter-thymeleaf**. We start by creating our Controller class which we annotate with **@Controller**. We add a method that returns the name of our template.

```java
@Controller
public class HelloController {
	@RequestMapping("/hello")
	public String hello() {
		return "hello";
	}
}
```

We then create our hello.html html file under `src/main/resources/templates`

```html
<html>
  <body>
    <b>Hello Thymeleaf!</b>
  </body>
</html>
```

#### Thymeleaf Syntax

Thymeleaf uses a special syntax to make html pages dynamic. `@{url}` is for using relative paths. The expression language `${el}` is used to read data that is coming from the controller. Typically, our controllers sends a ModelMap with all the data. `*{propertyName}` is used to bind a model property to the form fields.

#### Sending data to the Template

We create a method in our controller that returns a **ModelAndView** object. We will pass the template name into the ModelAndView object, in this case, _data_ and _studentList_. Using **addObject()** we can pass an object into the template.

```java
	@RequestMapping("/sendData")
	public ModelAndView sendData() {
		ModelAndView mav = new ModelAndView("data");
		mav.addObject("message", "Doge Doge Doge");
		return mav;
	}

	@RequestMapping("/students")
	public ModelAndView getStudents() {
		ModelAndView mav = new ModelAndView("studentList");
		Student student = new Student();
		student.setName("Doge");
		student.setScore(100);
		Student student2 = new Student();
		student2.setName("Cate");
		student2.setScore(90);

		List<Student> students = Arrays.asList(student, student2);

		mav.addObject("students", students);
		return mav;
	}
```

In our data.html template, we include thymeleaf's th namespace in order to use Thymeleaf's syntax. We can retrieve the _message_ object that we passed in our controller using `th:text`. The message will be displayed at `http://localhost:8080/sendData`. Likewise, the students array is passed into studentList and we can iterate over using the **th:each** Thymeleaf syntax its properties can be accessed using the `${el}` syntax.

```html
<!-- data.html -->
<html xmlns:th="http://www.thymeleaf.org/">
  <head>
    <title>Data Renderer</title>
  </head>
  <body>
    <div th:text="${message}"></div>
  </body>
</html>

<!-- studentList.html -->
<html xmlns:th="http://www.thymeleaf.org/">
  <head>
    <title>Student Data</title>
  </head>
  <body>
    <h1>Student Details</h1>
    <table>
      <tr>
        <th>Name</th>
        <th>Score</th>
      </tr>
      <tr th:each="student:${students}">
        <td th:text="${student.name}"></td>
        <td th:text="${student.score}"></td>
      </tr>
    </table>
  </body>
</html>
```

#### HTML forms

To use HTML forms inside a Thymeleaf template, we can use Thymeleaf tags **th:object**. In this case, we specify _student_ which means that if we send a Student object to the template, automatically the values will be taken and will be rendered in the fields marked with **th:field** where we bind the _name_ property. Using **th:action**, we specify that the action endpoint will be at `/saveStudent` which has a corresponding mapping at our controller.

```html
<body>
  <form th:object="${student}" th:action="@{/saveStudent}" method="post">
    Name: <input type="text" th:field="*{name}" /> Score:
    <input type="text" th:field="*{score}" />
    <input type="submit" value="save" />
    <input type="reset" value="reset" />
  </form>
</body>
```

```java
	@RequestMapping("/studentForm")
	public ModelAndView displayStudentForm() {
		ModelAndView mav = new ModelAndView("studentForm");
		Student student = new Student();
		mav.addObject("student", student);
		return mav;
	}

	@RequestMapping("/saveStudent")
	public ModelAndView saveStudent(@ModelAttribute Student student) {
		ModelAndView mav = new ModelAndView("result");
		System.out.println(student.getName());
		System.out.println(student.getScore());
		return mav;
	}
```

## Database Caching

To enable caching, Spring Boot uses third party caching providers such as Hazelcast. We being by adding the **spring-boot-starter-cache** and **hazelcast** dependencies.

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>

		<!-- https://mvnrepository.com/artifact/com.hazelcast/hazelcast -->
		<dependency>
			<groupId>com.hazelcast</groupId>
			<artifactId>hazelcast</artifactId>
		</dependency>

		<dependency>
			<groupId>com.hazelcast</groupId>
			<artifactId>hazelcast-spring</artifactId>
		</dependency>
```

Afterwards, we create the cache configuration class which is a Spring Bean that will return a type of HazelCast Config.

```java
@Configuration
public class ProductCacheConfig {
	@Bean
	public Config cacheConfig() {
		return new Config()
				.setInstanceName("hazel-instance")
				.addMapConfig(new MapConfig().setName("product-cache")
				.setTimeToLiveSeconds(3000));
	}
}
```

To enable caching, we annotate our entrypoint with the **@EnableCaching** annotation. Then in our model class, we need to implement the **Serializable** interface.

```java
@SpringBootApplication
@EnableCaching
public class ProductrestapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductrestapiApplication.class, args);
	}
}

@Entity
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
```

Then in our rest controller, we use the **@Cacheable** which we provide with the cache name according to the one defined in the configuration and **Transactional** annotations on our get controller endpoints. In our delete endpoint, we use **@CacheEvict**

```java
	@Cacheable("product-cache")
	@Transactional(readOnly = true)
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public Product getProduct(@PathVariable("id") int id) {
		LOGGER.info("finding product by ID" + id);
		return repository.findById(id).get();
	}

  @CacheEvict("product-cache")
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public void deleteProduct(@PathVariable("id") int id) {
		repository.deleteById(id);
	}
```

## Spring Batch

A batch process is a bunch of tasks that are required for a project. The main task in Spring Batch Framework is called a **Job**, which contains multiple steps with each step that takes care of a particular task. Each step is comprised of:

1. ItemReader - responsible for reading data from a database, file system, message queue, etc
2. ItemProcessor - takes the data of the ItemReader and apply business logic
3. ItemWriter - write the data to the database, file system, message queue etc.

All these will be stored into a **JobRepository** by Spring Batch. We then use **JobLauncher** to run a particular job. We create a Job using a **JobBuilderFactory** wherein we will tell the job the steps. We create a step using a **StepBuilderFactory**. We configure everything in a Java based configuration file **BatchConfig**.

The dependencies needed for the sample project are **spring-boot-starter-batch** and **h2**. We start with creating the Reader class which implements the **ItemReader** interface from Spring. In this reader class, we loop through an array and return the object.

```java
public class Reader implements ItemReader<String> {
	private String[] courses = {"Java", "React", "Nodejs"};
	private int count;

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    System.out.println("INSIDE READ METHOD");
		if (count<courses.length) {
			return courses[count++];
		} else {
			count = 0;
		}
		return null;
	}
}
```

Next is the Processor class which implements the **ItemProcessor** interface. In our case, it will take in a String and return a String type. We will be transforming the input String to uppercase

```java
public class Processor implements ItemProcessor<String, String> {
	@Override
	public String process(String item) throws Exception {
		System.out.println("Inside Process");
		return "PROCESS " + item.toUpperCase();
	}
}
```

The Writer class will implement **ItemWriter** and in the write method, it will get a list of Strings which we will write out to the console.

```java
public class Writer implements ItemWriter<String> {
	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("INSIDE WRITE");
		System.out.println("Writing Data: " + items);
	}
}
```

We then proceed with writing the JobListener, which will implement **JobExecutionListener** which has 2 methods: beforeJob and afterJob.

```java
public class MyJobListener implements JobExecutionListener {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("JOB STARTED");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("JOB ENDED " + jobExecution.getStatus().toString());
	}
}
```

We then proceed on configuring our batch jobs by creating beans in a separate configuration class. Afterwards we can configure the step for our job with the step() method. Lastly, we will configure the Job with the job() method.

```java
@Configuration
public class BatchConfig {
	@Autowired
	private StepBuilderFactory sbf;
	@Autowired
	private JobBuilderFactory jbf;

	public Job job() {
		return jbf.get("job1")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(step())
				.build();
	}

	@Bean
	public Step step() {
		return sbf.get("step1").<String, String>chunk(1)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Bean
	public Reader reader() {
		return new Reader();
	}
	@Bean
	public Writer writer() {
		return new Writer();
	}
	@Bean
	public Processor processor() {
		return new Processor();
	}
	@Bean
	public MyJobListener listener() {
		return new MyJobListener();
	}
}
```

We can now proceed with writing out the test. For this we need to first autowire **JobLauncher** from Spring Batch and inject the Job instance to the test class. We create the job parameters using the **JobParametersBuilder** and pass this into our launcher.

```java
@SpringBootTest
class SpringbatchApplicationTests {
	@Autowired
	JobLauncher launcher;
	@Autowired
	Job job;

	@Test
	void testBatch() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();
		launcher.run(job,  jobParameters);
	}
}
```

By annotating the entrypoint with **@EnableBatchProcessing**, we can enable Spring Batching and in application.properties, we need to disable spring.batch.job

```java
@SpringBootApplication
@EnableBatchProcessing
public class SpringbatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringbatchApplication.class, args);
	}
}
```

```
spring.batch.job.enabled=false
```

database schema not being initialized
spring.batch.initialize-schema=ALWAYS

circular dependency exception
spring.main.allow-circular-references=true

#### CSV to Database

We can use Spring Batch to copy csv values to a database. The Reader will be reading csv rows and convert it into objects. The Processor will then give a discount to the price of the product, and the Writer will then finally store it to the database. We will be using the builtin **FlatFileItemReader** for our reader and **JdbcBatchItemWriter** for the writer. For this new project, we will be using Spring Batch and MySQL Driver dependencies. We will start with the Product model which will have fields:

```java
public class Product {
	private Integer id;
	private String name;
	private String description;
	private Double price;
```

We then proceed on implementing the Reader. We will be using built in classes and methods from Spring Batch such as **FlatFileItemReader**, **DefaultLineMapper**, **DelimitedLineTokenizer**, **BeanWrapperFieldSetMapper**. The FlatFileItemReader is responsible for reading the csv file and converting it into an object of type Product. We then created DefaultLineMapper that is responsible for mapping each line into a Product object. This is done using the DelimitedLineTokenizer for reading each token separated by comma which sets them into the fields id name description and price, and BeanWrapperFieldSetMapper which sets the created object into Product type.

```java
@Configuration
public class BatchConfig {

	@Bean
	public ItemReader<Product> reader() {
		FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("products.csv"));

		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("id", "name", "description", "price");
		BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Product.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

    reader.setLineMapper(lineMapper);
		return reader;
	}
}
```

For the Processor bean, we will simply apply a discount to the product's price by 10%. For this we can use lambda arrow functions.

```java
	@Bean
	public ItemProcessor<Product, Product> processor() {
		return (p->{
			p.setPrice(p.getPrice()*.9);
			return p;
		});
	}
```

We then proceed with the Writer. We will be using **JdbcBatchItemWriter** which is responsible for reading the Product bean that will be given to the writer, and then use the values which will be executed in the SQL statement.

```java
	@Bean
	public ItemWriter<Product> writer() {
		JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO PRODUCT (id,name,description,price) VALUES (:id,:name,:description,:price)");
		return writer;
	}
```

We will also need to configure the Data Source so that the ItemWriter will be able to connect to the database. We use autowiring and configure our application.properties

```java
  @Autowired
	public DataSource dataSource;
```

```
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=1234
spring.batch.jdbc.initialize-schema=always
```

Afterwards we can configure the Step and Job using **JobBuilderFactory** and **StepBuilderFactory**.

```java
	@Autowired
	private JobBuilderFactory jbf;
	@Autowired
	private StepBuilderFactory sbf;

	@Bean
	public Job job() {
		return jbf.get("j1")
				.incrementer(new RunIdIncrementer())
				.start(step())
				.build();
	}

	@Bean
	public Step step() {
		return sbf.get("s1")
				.<Product,Product>chunk(3)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
```

We then configure the app to enable Batch Processing using **@EnableBatchProcessing** annotation and adding `spring.batch.job.enabled=false` to application.properties. Then we write out our test class

```java
@SpringBootTest
class BatchcsvtodbApplicationTests {
	@Autowired
	private JobLauncher launcher;
	@Autowired
	private Job job;

	@Test
	void testBatch() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		launcher.run(job, new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters());
	}
}
```

## Unit Testing using MockMvc

With **@WebMvcTest**, we will be able to run a test class without running a server. **@MockBean** uses the Mockito framework internally. For this section, we will be testing the ProductRestController methods from productrestapi. We start by creating a jUnit 4 test class ProductRestControllerMvcTest. We start by injecting **MockMvc**. We then mock the repository using **@MockBean** from Spring Mockito. We then proceed on mocking the _findAll_ call. We expect the a JSON of the object, this is done with the help of Jackson package.

```java
@WebMvcTest
public class ProductRestControllerMvcTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductRepository repository;

	private static final String PRODUCT_URL = "/productapi/products/";
	private static final String CONTEXT_URL = "/productapi";
	private static final double PRODUCT_PRICE = 2000d;
	private static final String PRODUCT_DESCRIPTION = "Gaming Laptop";
	private static final String PRODUCT_NAME = "Legion";
	private static final int PRODUCT_ID = 1;

	private Product buildProduct() {
		Product product = new Product();
		product.setId(PRODUCT_ID);
		product.setName(PRODUCT_NAME);
		product.setDescription(PRODUCT_DESCRIPTION);
		product.setPrice(PRODUCT_PRICE);
		return product;
	}

	@Test
	public void testFindAll() {
		Product product = buildProduct();
		List<Product> products = Arrays.asList(product);
		when(repository.findAll()).thenReturn(products);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		mockMvc.perform(get(PRODUCT_URL).contextPath(CONTEXT_URL)).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(products)));
	}
}

```

The simple flow is as follows:

1. MockMvc will make a RESTful call
2. The mock repository will be injected into the ProductRestController
3. The method repository.findAll() of the ProductRestController will be mocked with mockito **when()**

For the Create operation, we can use the following test method. In here, we use the **any()** method from Mockito because the returned object will be of a different reference from the original object.

```java
	@Test
	public void testCreateProduct() throws JsonProcessingException, Exception {
		Product product = buildProduct();
		when(repository.save(any())).thenReturn(product);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		mockMvc.perform(post(PRODUCT_URL).contextPath(CONTEXT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(product))).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(product)));
	}
```

For the Update method, it is very similar to Create. For Delete, we use **doNothing()** since the delete operation returns void.

```java
  @Test
	public void testUpdateProduct() throws JsonProcessingException, Exception {
		Product product = buildProduct();
		product.setPrice(100);
		when(repository.save(any())).thenReturn(product);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		mockMvc.perform(put(PRODUCT_URL).contextPath(CONTEXT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(product))).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(product)));
	}

  @Test
	public void testDeleteProduct() {
		doNothing().when(repository.deleteById(PRODUCT_ID));
		mockMvc.perform(delete(PRODUCT_URL + PRODUCT_ID).contextPath(CONTEXT_URL)).andExpect(status().isOk());
	}
```

## Messaging and JMS

Messaging is the process of exchanging business data or information across applications or across components within the same application. The Messaging Server (Message Oriented Middleware) ensures that the messages are delivered to the appropriate receiver.

#### Messaging Advantages

Messaging allows applications built with different programming languages communicate with each other seamlessly. The MOM exposes out an API for each platform to exchange messages. This allows **Heterogenous Integration**. This allows microservices to be **Loosely Coupled**. Messaging also **Reduces System Bottlenecks** through asynchronous processing and multiple consumers. We can also make applications **Scalable** by creating multiple consumers as the load increases. Lastly, Messaging allows **Flexibility and Agility** wherein we can replace our services with new applications if needed.

#### Messaging Models

**Point to Point** messaging allows us to send and receive messages both synchronously and asynchronously through virtual channels called queues. The distinguishing feature of p2p messaging is that the message that is produced is consumed only once. Once consumed, it is gone from the queue. It is the JMS provider's responsibility that the message is only consumed by one queue. p2p messaging supports both _Async Fire and Forget_ and _Synchronous request/reply messaging_.

In the **Pub/Sub** model, the messages are published to a virtual channel called **Topic**. In pub/sub, there are multiple applications consuming from the same Topic which means that the same message can be received by multiple subscribers. The pub/sub is a push model wherein the messages are automatically broadcasted to the consumers without them having to send a request.

#### JMS

Before JMS was introduced, developers had to use vendor-specific APIs to communicate and send messages between applications through messaging servers. Using JMS, we can send and receive messages to any messaging service irrespective of vendor. For this project, we use **Apache ActiveMQ** as the messaging broker. The only dependency needed for the project is Spring for Apache ActiveMQ 5.

We start by creating the MessageSender class. To send a message, we use the **JmsTemplate** class from Spring JMS Core. We can opt to have our queue name defined in application.properties as `springjms.myQueue=myQueue`. Using the convertAndSend method of JmsTemplate, we can send a message and it will automatically convert the message object into a JMS text message using hte **convertAndSend()** method.

```java
@Component
public class MessageSender {
	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${springjms.myQueue}")
	private String queue;

	public void send(String message) {
    System.out.println("MESSAGE SENT: " + message);
		jmsTemplate.convertAndSend(queue, message);
	}
}
```

The Listener class's receive method will then be annotated with **@JmsListener** from Spring JMS.

```java
@Component
public class MyListener {
	@JmsListener(destination="${springjms.myQueue}")
	public void receive(String message) {
		System.out.println("MESSAGE RECEIVED -> " + message);
	}
}
```

We then proceed on writing the test class. To send a message, we need to autowire our sender. We also need to annotate our entrypoint with **@EnableJms** and configure our application.properties

```
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
```

```java
@SpringBootTest
class SpringjmsApplicationTests {
	@Autowired
	MessageSender sender;

	@Test
	void testSendAndReceive() {
		sender.send("Hello Spring JMS!!!");
	}
}
```

## Swagger

Swagger provides a standard way to document our RESTful web services. It documents how the response object will look like in JSON/YAML. We can either use the Swagger API or hand-write according to Swagger specifications using the Swagger editor. The [Swagger Specification (OpenAPI)](https://swagger.io/docs/specification/about/) defines all the elements that can go into a Swagger document. Swagger also includes a UI that is generated on the fly for testing out REST services. Swagger Codegen allows us to generate code for web services in different programming languages.

[SpringDoc OpenAPI](https://springdoc.org/) makes it easy for us to use Swagger for documenting our REST apis. We just need to add the **springdoc-openapi-ui** dependency to our project. This will automatically go through our RESTful controllers and generate documentations. Once configured, we can see a documentation endpoint at `http://localhost:8080/productapi/v3/api-docs` and the Swagger UI at `http://localhost:8080/productapi/swagger-ui.html`. If we want to, we can change our swagger ui url with the property **springdoc.swagger-ui.path**

We can add more information in our Swagger UI using Swagger Annotations. One such annotation is the **@OpenAPIDefinition**, which we use in the starting point of the application.

```java
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Product API", version = "1.0", description = "doge doge doge"))
public class ProductrestapiApplication {
```

The **@Tag** and annotation is used in our REST controllers at the class level, **@Operation** at method level, **@Parameter** at parameter level, **@ApiResponse** at the return object.

```java
@RestController
@Tag(name = "Product Rest Endpoint")
public class ProductRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

	@Autowired
	ProductRepository repository;

	@Cacheable("product-cache")
	@Transactional(readOnly = true)
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	@Operation(summary = "Returns a product", description = "takes id, returns single product")
	public @ApiResponse(description = "Product object") Product getProduct(@Parameter(description = "Id of the product") @PathVariable("id") int id) {
		LOGGER.info("finding product by ID" + id);
		return repository.findById(id).get();
	}
```

If we want to hide a REST operation or the complete REST endpoint, we use the **@Hidden** annotation.
