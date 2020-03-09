### *1.6 싱글톤 레지스트리와 오브젝트 스코프*

- DaoFactory를 직접 사용하는 것과 @Configuration을 추가해서 스프링의 애플리케이션 컨텍스트를 통해 사용하는 것은 테스트 결과만 볼 때 동일한 것 같이 보인다.
  - 그저 애플리케이션 컨텍스트에 userDao라는 이름의 빈을 요청하면 DaoFactory의 userDao()메소드를 호출해서 그 결과를 돌려주는 것이라고 볼 수 있다.
- 하지만, 스프링의 애플리케이션 컨텍스트는 **기존에 직접 만들었던 오브젝트 팩토리와 중요한 차이점이 있다.**



> **오브젝트의 동일성과 동등성**
>
> - 동일성
>   - 두개의 오브젝트가 완전히 같은 동일한 오브젝트라고 말하는 것
>   - 동일성의 비교는 == 연산자를 사용
> - 동등성
>   - 동일한 정보를 담고 있는 오브젝트라고 말하는 것
>   - 동등성의 비교는 오브젝트의 equals() 메소드를 사용
>
> - 두 개의 오브젝트가 동일하다라는 것은 사실 **하나의 오브젝트에만 존재하는 것이고, 두 개의 오브젝트 레퍼런스 변수를 갖고 있다는 것이다.**
> - 두 개의 오브젝트가 동일하지는 않지만 동등한 경우에는 **두 개의 각기 다른 오브젝트가 메모리상에 존재하는데, 오브젝트의 동등성 기준에 따라 오브젝트 정보다 동등하다라고 판단하는 것이다.**



**오브젝트 팩토리와 애플리케이션 컨텍스트의 차이**

- Ex. DaoFactory의 userDao() 메소드를 여러 번 호출 했을 경우 동일한 오브젝트가 돌아오는가?

  ~~~java
  DaoFactory daoFactory = new DaoFactory();
  UserDao dao1 = daoFactory.userDao();
  UserDao dao2 = daoFactory.userDao();
  
  System.out.println(dao1);
  System.out.println(dao2);
  ~~~

  - dao1과 dao2의 주소가 동일하면 동일한 오브젝트이다. 하지만 결과는,

    <img src="https://user-images.githubusercontent.com/40616436/76160382-3bc02880-616d-11ea-9265-d6474d687a04.png" alt="image" style="zoom:50%;" />

  - 결과를 확인하듯이 두 오브젝트의 주소는 각기 다른 값을 가진 동일하지 않은 오브젝트이다.

    - 즉, userDao를 매번 호출하게 되면 새로운 오브젝트가 만들어질 것이다.

- Ex. 이번에는 getBean() 메소드를 이용해 userDao 라는 이름으로 등록된 오브젝트를 가져와보자.

  ~~~java
  ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
  UserDao dao3 = context.getBean("userDao", UserDao.class);
  UserDao dao4 = context.getBean("userDao", UserDao.class);
  
  System.out.println(dao3);
  System.out.println(dao4);
  ~~~

  <img src="/Users/mesung/Library/Application Support/typora-user-images/image-20200308230553435.png" alt="image-20200308230553435" style="zoom:50%;" />

  - 이번 결과에서 두 오브젝트는 주소가 같은 값으로 나타나므로 동일한 오브젝트이다.

- **즉, 우리가 만든 오브젝트 팩토리와 스프링의 애플리케이션 컨텍스트의 동작방식에는 무엇인가 차이점이 있는 것이다.**



### *싱글톤 레지스트리로서의 애플리케이션 컨텍스트*

- 애플리케이션 컨텍스트는 우리가 만든 오브젝트 팩토리와 비슷한 방식으로 동작하는 IoC 컨테이너인 것과, **동시에 싱글톤을 저장하고 관리하는 싱글톤 레지스트리이기도 하다.**



**서버 애플리케이션과 싱글톤**

- 왜 스프링은 싱글톤으로 빈을 만드는 것일까?
  - **이유는 스프링이 주로 적용되는 대상이 자바 엔터프라이즈 기술을 사용하는 서버환경이기 때문이다.**
  - 스프링이 설계되었을 때 엔터프라이즈 서버 환경은 서버 하나당 최대의 수로 요청을 처리할 수 있는 성능이 요구되는 환경이었다. 이런 환경으로 인해, 요청이 올 때마다 오브젝트를 새로 생성하게 된다면 서버 부하가 쉽게 걸릴 것이다.
  - 이런 이유로, 하나의 오브젝트를 공유해 동시에 사용하는 것이다. 즉, 싱글톤 형식으로 빈을 만드는 것이다.



**싱글톤 패턴의 한계**

- 자바에서 싱글톤을 구현하는 방법을 간략히 살펴보자.

  1. 클래스 밖에서는 오브젝트를 생성하지 못하도록 생성자를 private으로 만든다.

  2. 생성된 싱글톤 오브젝트를 저장할 수 있는 자신의 타입과 동일한 static 필드를 정의한다.
  3. Static 팩토리 메소드인 getInstance()를 만들고, 이 메소드가 최초로 호출되는 시점에서만 오브젝트를 만들어지게 한다.
  4. 생선된 오브젝트는 2. 에서 만든 필드에 저장한다.
  5. 한번 오브젝트가 만들어지면 그 후에는 getInstance()를 통해 이미 만들어진 static 필드에 저장해둔 오브젝트를 넘겨준다.

~~~java
public class UserDao {
  private static UserDao INSTANCE;
  
  private UserDao(ConnectionMaker connectionMaker) {
    this.connectionMaker = connectionMaker;
  }
  
  public static synchronized UserDao getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new UserDao();
    }
    return INSTANCE;
  }
  
}
~~~

- 해당 부분을 추가하게 되면, 우리가 작성해둔 DaoFactory에서는 더 이상 UserDao의 생성자를 호출할 수 없기때문에 UserDao를 생성하여 ConnectionMaker 오브젝트를 넣어주는게 불가능해졌다.



- 이 처럼 싱글톤 패턴은 몇가지 문제가 발생하는데, 살펴보자.

1. **Private 생성자를 갖고 있기 때문에 상속을 할 수 없다.**
   - 싱글톤 패턴은 오직 자신만이 오브젝트를 만들 수 있도록 제한했기 때문에 생성자 자체를 private으로 제한한다.
   - private을 가진 생성자는 다른 생성자가 없다면 상속이 불가능하다.
     - 이는 객체지향의 장점인 상속과 다형성을 적용할 수 없는 큰 단점을 가진다.
   - 또한, 상속과 다형성 같은 객체지향의 특징이 적용되지 않은 static 필드와 메소드를 사용하는 것 역시 단점이다.
2. **싱글톤은 테스트하기가 힘들다.**
   - 싱글톤은 만들어지는 방식이 제한적이기 때문에 테스트에서 사용될 때 Mock 오브젝트 등으로 대체하기가 힘들다.
   - 즉, 필요한 오브젝트는 직접 오브젝트를 만들어서 사용할 수 밖에 없어 테스트용 오브젝트로 대체하기 힘들다.
3. **서버 환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.**
   - 서버에서 **클래스 로더를 어떻게 구성**하고 있느냐에 따라서 싱글톤 클래스임에도 하나 이상의 오브젝트가 만들어 질 수 있다.
   - 자바에서는 **여러 개의 JVM에 분산돼서 설치가 되는 경우** 각각 독립적으로 오브젝트가 생기기 때문에 싱글톤으로서의 가치가 떨어진다.
4. **싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못하다.**
   - 싱글톤 클래스에 접근할 때 누구가 접근 가능한 staic 메소드로 접근을 하여 **전역 상태**로 사용되기 쉽다.
   - 즉, 아무 객체나 자유롭게 접근하고 수정하고 공유할 수 있는 전역 상태를 갖는 것이다.
   - 이런 상태는 객체지향 프로그래밍에서는 권장되지 않는 모델이다.



**싱글톤 레지스트리**

- 스프링에서는 서버 환경에서 싱글톤이 만들어져 서비스 오브젝트 방식(하나의 객체를 공유)으로 사용되는 것을 적극 지지한다. **하지만, 자바의 기본적인 싱글톤 패턴은 여러 단점이 있어 스프링은 직접 싱글톤 형태의 오브젝트를 만들고 관리하는 기능을 제공한다.**
  - 그것이 바로 **싱글톤 레지스트리**이다.
- **스프링 컨테이너는 싱글톤을 생성하고, 관리, 공급하는 싱글톤 관리 컨테이너이기도 하다.**
- 오브젝트 생성에 관한 모든 권한은 IoC 기능을 제공하는 애플리케이션 컨텍스트에게 있기 때문에, 일반 자바 클래스를 애플리케이션 컨텍스트가 관리하게 된다면 싱글톤 방식으로 만들어져 관리하게 된다.
- **가장 중요한 것은, 싱글톤 레지스트리를 사용하면**
  - 싱글톤 방식으로 사용될 클래스라도 public 생성자를 가질 수 있다.
  - 싱글톤 패턴과 달리 스프링이 지지하는 객체지향적인 설계 방식과 원칙, 디자인 패턴 등을 적용하는 데 아무런 제약없이 사용할 수 있다.
- **결국, 스프링이 빈을 싱글톤으로 만드는 것은 오브젝트의 생성 방법을 제어하는 IoC 컨테이너로서의 역할이다.**



### *싱글톤과 오브젝트의 상태*

- **기본적으로 싱글톤이 멀티스레드 환경에서 서비스 형태의 오브젝트로 사용되는 경우에는 상태정보를 내부에 갖고 있지 않는 무상태 방식으로 만들어져야 한다.**

  - 즉, 인스턴스 필드의 값을 변경하고 유지할 수 있는 상태가 아닌 방식으로 만들어져야한다. 혹은 해당 필드가 읽기 전용의 상태여야 한다.
  - 만약 상태 방식으로 만들어지게 되면, 테스트를 진행했을 때는 이상이 없다가 서버 배포 후 다른 개발자에 의해서 원하는 값으로 적용되지 않는 경우가 만들어진다.

- **그렇다면, 상태가 없는 방식으로 클래스를 만드는 경우엔 요청에 대한 정보나, DB, 서버의 리소스로부터 생성한 정보는 어떻게 다루는가?**

  - 이 땐, 파라미터와 로컬변수, 리턴 값 등을 이용하면 된다.
  - **메소드 파라미터나 메소드 안의 로컬 변수는 매번 새로운 값을 저장할 독립적인 공간이 만들어지기 때문에 싱글톤이라고 해도 여러 스레드가 변수의 값을 덮어쓸 일은 없다.**

  

- 싱글톤 빈으로 사용되는 클래스에서 인스턴스 변수로 사용했을 시.

  ~~~java
  public class UserDao {
    //초기에 설정하면 사용 중에는 바뀌지 않는 읽기전용 인스턴스 변수
    private ConnectionMaker connectionMaker;
    
    //매번 새로운 값으로 바뀌는 정보를 담은 인스턴스 변수 즉, 심각한 문제..
    private Connectoin c;
    private User user;
    
    public User get(String id) throws ClassNotFoundException, SQLException {
      this.c = connectionMaker.makeConnection();
      ...
      
      this.user = new User();
      this.user.setId(rs.getString("id"));
      this.user.setName(rs.getString("name"));
      this.user.setPassword(rs.getString("password"));
      ...
      return this.user;
    }
  }
  ~~~

- 위 소스에서 기존 UserDao와 다른 점은 로컬 변수로 선언하고 있던 Connection과 User를 클래스의 인스턴스 변수로 선언했다는 것이다. 멀티스레드 환경에서 심각한 문제가 발생한다.

  - **즉, 멀티 스레드 환경에서는 해당 인스턴스 변수는 배번 새로운 값으로 바뀔 수 있다는 것이다.**

  - 따라서, 스프링의 싱글톤 빈으로 사용되는 클래스를 만들 때는 기존의 UserDao 처럼 개별적으로 바뀌는 정보는 로컬 변수로 정의하거나, 파라미터로 주고 받으면서 사용하게 해야 한다.

    ~~~java
    //기존 UserDao
    public User get(String id) throws ClassNotFoundException, SQLException {
      Connection c = connectionMaker.makeNewConnection();
      PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
    
      ps.setString(1, id);
    
      ResultSet rs = ps.executeQuery();
      rs.next();
      User user = new User();	//로컬 변수 정의
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
    
      ps.executeUpdate();
      ps.close();
      c.close();
    
      return user;
    }
    ~~~

- **그런데, 기존의 UserDao에서도 인스턴스 변수로 정의해서 사용한 것이 있다.**

  - 바로 ConnectionMaker 인터페이스 타입의 connectionMaker이다.

    ~~~java
    public class UserDao {
    	private ConnectionMaker connectionMaker;  
    	...
    }
    ~~~

    - 이것은 인스턴스 변수로 사용해도 상관이 없다.
      - 이유는 connectionMaker는 읽기 전용의 정보이기 때문이다.
      - 즉, connectionMaker는 등록, 수정을 하지 않고 그저 읽기만 하는 정보이다.
    - **ConnectionMaker는 스프링이 한 번 초기화해주고 나면 이 후에는 수정되지 않기 때문에 멀티 스레드 환경에서도 아무런 문제없이 사용가능하다.**
    - **이렇게 자신(UserDao)이 사용하는 다른 싱글톤 빈을 저장하려는 용도라면 인스턴스 변수를 사용해도 좋다.**

  - 동일하게 읽기 전용의 속성을 가진 정보라면 싱글톤에서 인스턴스 변수로 사용해도 좋고, **static final이나 final로 선언하는 편이 나을 수도 있다.**



### *스프링 빈의 스코프*

- 스프링이 관리하는 오브젝트, 즉 빈이 생성되고 존재하고 적용되는 범위에 대해서 알아보자.
  - 스프링에서는 이것을 **빈의 스코프** 라고 한다.
  - 스프링 빈의 기본 스코프는 싱글톤이다.
- 경우에 따라서 싱글톤 외의 스코프를 가질 수 있는데, 대표적으로 **프로토타입 스코프**가 있다.
  - 프로트타입은 싱글톤과 달리 컨테이너에 빈을 요청할 때마다 매번 새로운 오브젝트를 만들어준다.
- **그 외에도 웹을 통해 새로운 HTTP 요청이 생길 때마다 생성되는 Request 스코프가 있고, 웹의 세션과 스코프가 유사한 Session 스코프도 있다.**