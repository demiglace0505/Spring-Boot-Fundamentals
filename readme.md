# Spring Boot Fundamentals

[Spring Boot Fundamentals](https://www.udemy.com/course/springbootfundamentals/)

- [Spring Boot Fundamentals](#spring-boot-fundamentals)
  - [Spring Boot Basics](#spring-boot-basics)
      - [Creating a Starter REST endpoint](#creating-a-starter-rest-endpoint)
      - [@SpringBootApplication](#springbootapplication)
      - [@SpringBootTest](#springboottest)
      - [Dependency Injection](#dependency-injection)
  - [Spring Data JPA](#spring-data-jpa)

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
