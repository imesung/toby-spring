## 1.8 XML을 이용한 설정

- DI 의존관계의 설정정보는 Java 코드를 활용하는 것 분만 아니라 다른 방법을 사용할 수 있다. 그 중 대표적인 것은 XML방식이다.
- Java 코드로 DI 의존관계의 설정정보를 이용하게 되면, DI 구성이 바뀔때마다 자바 코드를 수정하고 클래스를 다시 컴파일해야 하는 단점이 발생한다.
- XML은 단순한 텍스트 파일이기 때문에, 컴파일과 같은 작업이 없고 다루기가 쉽다.



### *1.8.1 XML 설정*

- Java와 XML의 설정 차이
  - @Confguration : <beans>
  - @Bean : <bean>
- @Bean 메소드를 통해 얻을 수 있는 **Bean DI 정보를 세가지로 정의할 수 있다.**
  1. 빈의 이름 
     - @Bean 메소드 이름이 **빈의 이름** 이며, 이 이름은 getBean()에서 사용된다.
  2. 빈의 클래스
     -  빈 오브젝트를 어떤 클래스를 이용해서 만들지를 정의한다.
  3. 빈의 의존 오브젝트 
     - 빈의 생성자나 수정자 메소드를 통해 의존 오브젝트를 넣어준다.
     - 의존 오브젝트도 하나의 빈이므로 이름이 있고, 그 이름에 해당하는 메소드를 호출해서 의존 오브젝트를 가져온다.
       - Ex. DaoFactory
     - 의존 오브젝트는 하나 이상일 수 있다.
- **XML도 마찬가지로 위 세가지 정보 혹은 두 가지 정보(의존 오브젝트 생략)를 정의할 수 있다.**



*connectionMaker()를 XML로 전환*

- DaoFactory의 connectionMaker() 메소드에 해당하는 빈을 XML로 정의해보자.
- **connectionMaker()로 정의된 빈은 의존하는 다른 오브젝트가 없으니 DI 정보는 두 가지만 있으면 된다.**

~~~java
@Bean	//<bean
public ConnectionMaker 
  connectionMaker() {	//id="connectionMaker"
  	return new DConnectionMaker();	//class="..ch01toby.dao.DConnectionMaker" />
}
~~~

~~~xml
<bean id="connectionMaker" class="com.mesung.toby.ch01toby.dao.DConnectionMaker"/>
~~~



*userDao()를 XML로 전환*

- userDao()에는 DI 정보의 세 가지 요소가 모두 들어가 있고, 수정자 메소드를 사용해 의존관계를 주입해준다.

~~~java
@Bean
public UserDao userDao() {
  UserDao userDao = new UserDao();
  
  userDao
    .setConnectionMaker(	// <property name="connectionMaker"
				connectionMaker()	// ref="connectionMaker" />
  	 );
  
  return userDao;
}
~~~

- Javad에서 XML로 변환할 때, 수정자 메소드를 사용하면 편리하다. 이유는,
  - set은 <property에 해당한다.
  - set 다음 이름은 name영역에 해당한다.
  - 파라미터는 ref영역에 해당하는 것으로, **수정자 메소드를 통해 주입해줄 오브젝트의 빈 이름이다.**



*XML의 의존관계 주입 정보*

~~~xml
<beans>
	<bean id="connectionMaker" class="com.mesung.toby.ch01toby.dao.DConnectionMaker"/>
  <bean id="userDao" class="com.mesung.toby.ch01toby.dao.UserDao">
  	<property name="connectionMaker" ref="connectionMaker"/>
  </bean>
</beans>
~~~

- 보통 property의 name은 주입할 빈 오브젝트의 인터페이스를 따르는 경우가 많고, Bean 이름 역시 인터페이스 이름을 따르는 경우가 많아 바뀔 수 있는 클래스 이름보다 인터페이스 이름을 따르는 편히 자연스럽다.
- <bean id="connectionMaker" .. />와 <property .. ref="connectionMaker"/>는 서로 동일한 것이고, bean의 이름이 변경되면 property의 ref도 변경되어야 한다.



### *XML을 이용하는 애플리케이션 컨텍스트*

- 이제 애플리케이션 컨텍스트가 DaoFactory를 대신하여 XML 설정정보를 활용하도록 만들어보자.

- XML에서 빈의 의존관계 정보를 이용하는 IoC/DI 작업에는 **GenericXmlApplicationContext** 를 사용한다.

- 애플리케이션 컨텍스트가 사용하는 XML 설정파일의 이름은 관례에 따라 applicationContext.xml이라고 만든다.

  ~~~xml
  <?xml version=1.0 encoding="UTF-8"?>
  <beans>
  	...
  
  	<bean id="connectionMaker" class="com.mesung.toby.ch01toby.dao.DConnectionMaker"/>
    <bean id="userDao" class="com.mesung.toby.ch01toby.dao.UserDao">
    	<property name="connectionMaker" ref="connectionMaker"/>
    </bean>
  </beans>
  ~~~

- 이제. UserDaoTest에서 애플리케이션 컨텍스트 생성 부분을 수정해보자.

  ~~~java
  ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
  ~~~



*다양한 XML 설정정보*

- XML 설정정보는 GenericXmlApplicationContext뿐만 아니라 ClassPathXmlApplicationContext를 이용할 수 있다.

- ClassPathXmlApplicationContext의 기능중에는 클래스패스의 경로정보를 클래스에서 가져오게 하는 것이 있다.

  - 만약, com.mesung.toby.ch01toby.dao 패키지 안에 daoContext.xml이라는 설정파일이 있다고 하면,

  - GenericXmlApplicationContext가 daoContext.xml 파일을 사용하게 하려면 **클래스패스 루트로부터 파일의 위치를 지정해야한다.**

    ~~~java
    new GenericXmlApplicationContext("com/mesung/toby/ch01toby/dao/daoContext.xml");
    ~~~

  - ***반면에***, ClassPathXmlApplicationContext는 **XML 파일과 같은 클래스패스에 있는 클래스 오브젝트를 넘겨 클래스패스에 대한 힌트를 제공할 수 있다.**

  - UserDao는 com.mesung.toby.ch01toby.dao 패키지에 있으므로 daoContext.xml과 같은 클래스패스 위에 있다.

  - **이 UserDao를 함께 넣어주면 XML 파이르이 위치를 UserDao의 위치로부터 상대적으로 지정할 수 있다.**

    ~~~java
    new ClassPathXmlApplicationContext("daoContext.xml", UserDao.class);
    ~~~

- **하지만, 제일 무난한 것은 GenericXmlApplicationContext를 사용하는 것이다.**



### *1.8.3 DataSource 인터페이스로 변환*

*DataSource 인터페이스 적용*

- UserDao에서 ConnectionMaker 대신 Datasource 인터페이스에서 제공해주는 DB 커넥션을 사용하자.

  ~~~java
  import javax.sql.Datasource;
  
  public class UserDao {
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
    }
    
    public void add(User user) throws SQLException {
      Connection c = dataSource.getConnection();
      ...
    }
    
    ...
  }
  ~~~

  

*자바 코드 설정 방식*

```java
@Bean
public UserDao userDao() {
  UserDao userDao = new UserDao();
  userDao.setDataSource(dataSource());	//DataSource 타입의 빈을 DI 받음.
  return userDao;
}

@Bean
public DataSource dataSource() {
  SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

  dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
  dataSource.setUrl("jdbc:mysql://localhost/mesung");
  dataSource.setUsername("spring");
  dataSource.setPassword("book");

  return dataSource;

}

```

- UserDao는 이제 DataSource 타입의 dataSource()를 DI 받는다.



*XML 설정 방식*

~~~xml
<?xml version=1.0 encoding="UTF-8"?>
<beans>
	...

	<bean id="dataSource" class="org.springframwork.jdbc.datasource.SimpleDrvierDataSource"/>
  
  ...
</beans>
~~~

- ConnectionMaker인 <bean> 을 없애고 dataSource라는 이름의 <bean>을 등록한다.
- 그런데 UserDao의 setDataSource() 메소드의 파라미터 값인 SimpleDrvierDataSource 빈을 설정해주는 <bean> 부분은 정의하지 않는다.
  - 그 이유는, UserDao 처럼 다른 빈에 의존하는 경우에는 <property>를 활용하면 됐으나, SimpleDrvierDataSource 오브젝트의 경우 단순 Class 타입의 오브젝트나 텍스트 값이다. 
  - **그로인해, <property>형식으로 나타나지 않는 것이다. 그러면 XML에서는 어떻게 처리를 해야할 것인가?!**



### *1.8.4 프로퍼티 값의 주입*

*값 주입*

- 다른 빈 오브젝트의 레퍼런스가 아니라 단순 값을 주입해주는 것이기 때문에 ref 대신 value를 사용한다.

  <img src="https://user-images.githubusercontent.com/40616436/76705109-f8dbf300-6720-11ea-80a3-3b24d7d9de1f.png" alt="image" style="zoom:50%;" />



*value 값의 자동 변환*

- Url, username, password는 모두 스트링 타입이지만, driverClass는 java.lang.Class 타입인데도 불구하고 동일하게 스트링 형식으로 나타내도 오류가 발생하지 않는다.

- 그 이유는, **스프링이 프로퍼티의 값을 수정자 메소드의 파라미터 타입을 참고로 해서 적절한 형태로 변환해주기 때문인다.**

  - setDriverClass() 메소드의 파라미터 타입이 Class임을 확인하고 "com.mysql.jdbc.Driver"라는 텍스트 값을 com.mysql.jdbc.Driver 오브젝트로 자동 변경해주는 것이다.
  - 내부적으로는 다음과 같은 변환 작업이 일어난다.

  ~~~java
  Class drvierClass = Class.forName("com.mysql.jdbc.Driver");
  dataSource.setDriverClass(driverClass);
  ~~~

  



