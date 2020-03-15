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



