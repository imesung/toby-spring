## 1.7 의존관계 주입(DI)

### *제어의 역전(IoC)과 의존관계 주입*

- 스프링의 IoC만 볼 때, **서블릿 컨테이너 처럼 서버에서 동작하는 서비스 컨테이너인지 혹은 단순히 IoC 개념이 적용된 템플릿 메소드 패턴을 이용한 프레임워크인지 한 눈에 파악하기 힘들다.** 
  - 그로 인해, 스프링이 제공하는 IoC 방식에 핵심을 짚어주는 **의존관계 주입** 이라는 좀 더 명확한 이름을 사용했다.
  - **스프링 IoC 기능의 대표적인 동작원리는 주로 의존관계 주입이라고 불린다.** -> 이런 이유로 IoC 컨테이너를 **DI 컨테이너**라고 불리기도 한다.



> **의존관계 주입, 의존성 주입, 의존 오브젝트 주입**
>
> - DI는 오브젝트 레퍼런스를 외부로부터 제공받고 이를 통해 제공 받은 오브젝트와 다이나믹하게 의존관계가 만들어진다.
>   - 이 의미를 함축시켜 만든 키워드 들이 의존관계 주입, 의존성 주입, 의존 오브젝트 주입이다.
>   - 셋 다 모두 같은 의미이다.





### *런타임 의존관계 설정*

**의존관계**

- 의존관계란 무엇인가?

  - 두 개의 클래스가 의존관계에 있다고 확정지을 때는 항상 방향성을 부여해줘야한다. 즉, 누가 누구에게 의존하는 지에 대해 방향성을 부여해야 한다.

  - **Ex. A가 B를 의존한다.**

    <img src="https://user-images.githubusercontent.com/40616436/76214142-7b5f4100-624f-11ea-9642-8998ecbb7db5.png" alt="image" style="zoom:50%;" />

    - 여기서 의존한다는 것은 의존대상인 B가 변하면 A에 영향을 미친다는 뜻이다.
      - B의 메소드가 추가되거가 기존 메소드의 형식이 바뀌면 A도 그에 따라 수정되거나 추가돼야한다.



**UserDao의 의존관계**

- 지금까지 작업했던 UserDao를 보면, UserDao가 ConnectionMaker를 의존하고 있는 형태이다.

  ~~~java
  public class UserDao {
    private ConnectionMaker connectoinmaker;
    ,,,
  }
  ~~~

- UserDao의 의존관계를 그림으로 살펴보자.

  <img src="https://user-images.githubusercontent.com/40616436/76214796-cc236980-6250-11ea-8f43-bc236b196535.png" alt="image" style="zoom:60%;" />

  - UserDao가 ConnectoinMaker 인터페이스를 의존하고 있으므로, **ConnectionMaker 인터페이스가 변화하면 UserDao도 영향을 받을 것이다.**
  - 하지만, ConnectionMaker 인터페이스를 구현한 클래스인 **DConnectionMaker를 다른 것으로 바뀌거나 내부의 메소드에서 변화가 생겨도 UserDao에는 영향을 주지 않는다.** *UserDao는 DConnectionMaker 의 존재 여부도 모른다.*
  - **즉, 인터페이스에 대해서만 의존관계를 만들어두면 인터페이스 구현 클래스와의 관계는 느슨해지면서 변화에 영향을 덜 받는 상태가 되는 것이다. *결합도가 낮다고 설명할 수 있는 것이다.***



**의존관계란, 한쪽의 변화가 다른 쪽에 영향을 주는 것이니, 인터페이스를 통해 의존관계를 제한해주면 그만큼 변경에 자유로워지는 셈이다.**



*런타임 시 오브젝트 의존관계*

- 모델이나 코드에서 클래스와 인터페이스를 통해 드러나는 의존관계(UserDao, ConnectionMaker) 말고, **런타임 시에 오브젝트 사이에서 만들어지는 의존관계도 있다.**
  - 인터페이스를 통해 설계 시점에 느슨한 의존관계를 갖는 경우에는 UserDao의 오브젝트가 런타임 시 사용할 오브젝트가 어떤 클래스로 만든 것인지 미리 알 수가 없다.(현재는 DConnectionMaker)
  - **프로그램이 시작되고 UserDao 오브젝트가 만들어지고 나서 런타임 시에 의존관계를 맺는 대상, 즉 실제 사용대상인 오브젝트를 *의존 오브젝트* 라고 말한다.**
- **의존관계 주입**은 이렇게 **구체적인 의존 오브젝트**(UserDao)와 **그것을 사용할 주체, 보통 클라이언트라고 부르는 오브젝트**(DConnectionMaker)를 **런타임 시에 연결해주는 작업을 말한다.**



***의존관계 주입이란, 세 가지 조건을 충족하는 작업을 말한다.*** 

1. 클래스 모델이나 코드에는 **런타임 시점의 의존관계가 드러나지 않는다.** 그러기 위해서는 **인터페이스에만 의존하고 있어야 한다.**
2. **런타임 시점의 의존관계는 컨테이너나 팩토리 같은 제3의 존재가 결정한다.** (NConnectoinMaker 혹은 DConnectionMaker 선택)
3. **의존관계는 사용할 오브젝트에 대한 레퍼런스를 외부에서 제공해줌으로써 만들어진다.**



***의존관계 주입의 핵심은 설계 시점에는 알지 못했던 두 오브젝트의 관계를 맺도록 도와주는 제3의 존재가 있다는 것이다.***

- DI에서 말하는 **제 3의 존재**는 전략 패턴에서 등장하는 **클라이언트나 DaoFactory**, 또는 DaoFactory 같은 작업을 일반화해서 만들어진 **애플리케이션 컨텍스느, 빈 팩토리, IoC 컨테이너** 등이 **모두 외부에서 오브젝트 사이의 런타임 관계를 맺어주는 책임을 지닌 제3의 존재이다.**



**UserDao의 의존관계 주입**

- UserDao에 적용된 의존관계 주입 기술을 다시 살펴보면, 

  - 인터페이스를 사이에 두고 UserDao와 ConnectionMaker 구현 클래스간에 의존관계를 느슨하게 만들긴 했지만, UserDao가 사용할 구체적인 클래스를 알고 있어야 한다는 점이 있었다.

    ~~~java
    public UserDao() {
      connectionMaker = new DconnectionMaker();
    }
    ~~~

    - 이 코드는 이미 설계 시점에서 UserDao가 DConnectionMaker라는 구체적인 클래스의 존재를 알고 있다.
    - 이 코드는 ConnectionMaker 인터페이스의 관계 뿐 아니라, **런타임 의존관계 즉, DConnectionMaker 오브젝트를 사용하겠다는 것까지 UserDao가 결정하고 관리하고 있다.**

  - 이 코드의 문제는 이미 런타임 시의 의존관계가 코드 속에 결정되어 있다는 것이다.

  - 그로 인해, **IoC 방식을 써서 UserDao의 런타임 의존관계를 드러내는 코드를 제거(파라미터로 인터페이스를 받음)하고, 제3의 존재(DaoFactory)에 런타임 의존관계 결정 권한을 위임한 것이다.**



*UserDao에 적용된 의존관계 주입 기술을 의존관계 주입 세 가지 조건으로 확인하자면*

1. 런타임 의존관계를 드러내지 않기 위해, 인터페이스에만 의존했다.

   ~~~java
   public class UserDao {
     private ConnectoinMaker connectionMaker;
   }
   ~~~

2. 제3의 존재인 DaoFactory를 생성하여 런타임 의존관계를 결정했다.

   ~~~java
   public class DaoFactory {
   	public UserDao userDao() {
       return new UserDao(connectionMaker());
     }
     
     public ConnectionMaker connectionMaker() {
       return new DConnectionMaker();
     }
   }
   ~~~

3. 의존관계는 사용할 오브젝트에 대한 레퍼런스를 외부에서 제공 받았다.

   ~~~java
   public class UserDao {
     private ConnectoinMaker connectionMaker;
     
     //connectionMaker 정보를 외부에서 받음.
     public UserDao(ConnectionMaker connectionMaker) {
       this.connectionMaker = connectionMaker;
     }
   }
   ~~~



- DaoFactory는 ***두 오브젝트 사이의 런타임 의존관계를 설정해주는 의존관계 주입 작업을 주도하는 존재(DConectionMaker 혹은 NConnectionMaker 구현체를 의존관계로 전달)***이며, ***동시에 IoC 방식으로 오브젝트의 생성과 초기화, 제공(new, 파라미터 제공) 등의 작업을 수행하는 컨테이너***이다.

- **이로인해, 여기서 DI 컨테이너는 DaoFactory가 되는 것이다.**

- DI 컨테이너는 자신이 결정한 의존관계를 맺어줄 클래스의 오브젝트를 만들고 이 생성자의 파라미터로 오브젝트의 레퍼런스를 전달해준다.

  - Ex. UserDao를 생성하는 시점에 생성자의 파라미터로 이미 만들어진 DConnectionMaker 오브젝트의 레퍼런스를 전달한다.

    ~~~java
    //Ex 작업을 수행하기 위한 필수적인 코드
    public class UserDao {
      private ConnectoinMaker connectionMaker;
      
      //connectionMaker 정보를 외부에서 받음.
      public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
      }
    }
    ~~~

- **이런 과정을 통해 두 개의 오브젝트 간(USerDao, ConnectionMaker)에 런타임 의존관계가 만들어진 것이다.**



- 해당 그림은 **런타임 의존관계 주입** 과 그것으로 발생하는 **런타임 사용 의존관계** 의 모습을 보여준다.

  <img src="https://user-images.githubusercontent.com/40616436/76219248-e2cdbe80-6258-11ea-95c0-1684d251d46b.png" alt="image" style="zoom:50%;" />

  - 의존관계 주입 : 생성자의 파라미터로 ConnectionMaker를 받고 런타임 시 제3의 존재에 의해 ConnectionMaker의 구현 클래스가 결정된다.
  - 사용 의존관계 : 런타임 시 결정된 구현 클래스의 메소드를 사용한다.



**DI는 자신이 사용할 오브젝트에 대한 선택과 생성 제어권을 외부로 넘기고, 자신은 그저 주입받은 오브젝트를 사용한다는 점에서 IoC의 개념에 잘 들어맞는다. 이로인해, 스프링 컨테이너의 IoC는 주로 의존관계 주입 또는 DI라는 데 초점이 맞춰져 있다.**





### *의존관계 검색과 주입*

- 스프링이 제공하는 IoC 방법에는 의존관계 주입만 있는 것이 아니다. **의존관계를 맺는 방법이 외부로부터의 주입이 아니라 스스로 검색을 이용하기 때문에 *의존관계 검색*이라고 불리는 것도 있다.**

- 의존관계 검색은 런타임 시 의존관계를 맺을 오브젝트를 결정하는 것과 오브젝트의 생성 작업은 외부 컨테이너에게 IoC로 맡기지만, **이를 가져올 때는 메소드나 생성자를 통한 주입 대신 *스스로 컨테이너에게 요청하는 방법*을 사용한다.**

- Ex. UserDao 생성자를 아래와 같이 작성해보자

  ~~~java
  public UserDao() {
    DaoFactory daoFactory = new DaoFactory();
    this.connectionMaker = daoFactory.connectionMaker();
  }
  ~~~

  - 이렇게 작성을 해도 UserDao는 여전히 자신히 어떤 ConnectionMaker 오브젝트를 사용할지 미리 알지 못한다. 또한, 여전히 코드의 의존대상은 ConnectionMaker 인터페이스 뿐이다.
  - 런타임 시에 DaoFactory가 만들어서 돌려주는 오브젝트와 런타임 의존관계를 맺으므로, IoC 개념을 잘 따르고 있다.
  - **하지만, 이 소스는 외부로부터의 주입이 아니라 스스로 IoC 컨테이너인 DaoFactory에게 요청하는 것이다.**
    - 이 경우를 **스프링의 애플리케이션 컨텍스트라면 미리 정해놓은 이름을 전달받아 그 이름에 해당하는 오브젝트를 찾게된다.**
    - 또한, **그 대상이 런타임 의존관계를 가질 오브젝트이므로 의존관계 검색이라고 부르는 것이다.**



*스프링의 의존관계 검색*

- 스프링의 IoC 컨테이너인 애플리케이션 컨텍스트는 **getBean()**이라는 메소드를 제공한다. **바로 이 메소드가 의존관계 검색에 사용되는 것이다.**

- 의존관계 검색을 이용한 UserDao 생성자를 확인해보자

  ~~~java
  public UserDao() {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
  }
  ~~~



- **의존관계 검색은 기존 의존관계 주입의 모든 장점을 가지고 있다. 하지만 코드상으로 봤을 때는 의존관계 주입 쪽이 훨씬 단순하고 깔끔하다.** *즉, 의존관계 주입을 사용하는 것이 더 낫다.*
- 그런데 가끔, 의존관계 검색 방식을 사용할 때가 있다.
  - 바로 main()에서 애플리케이션 기동 시점에서는 적어도 한 번 의존관계 검색 방식을 사용하여 오브젝트를 가져와야 한다.



*의존관계 검색(DL)과 의존관계 주입(DI) 적용 시 중요한 차이점*

- **의존관계 검색 방식에서는 검색하는 오브젝트(UserDao)는 자신이 스프링의 Bean일 필요가 없다.**
  - Ex. UserDao에서 스프링의 getBean()을 사용하여 의존관계 검색 방법을 사용하면 getBean()을 통해 검색되어야 하는 ConnectionMaker만 Bean으로 등록되어 있으면 된다.
- **의존관계 주입 방식에서는 UserDao와 ConnectionMaker 사이에 DI가 적용되려면 UserDao도 반드시 Bean으로 등록되어야 한다.**
  - 컨테이너가 UserDao에 ConnectionMaker 오브젝트를 주입해주려면 UserDao에 대한 생성과 초기화 권한을 갖고 있어야함으로 Bean으로 등록되어야 한다.
- **DI를 원하는 오브젝트는 먼저 자기 자신이 컨테이너가 관리하는 Bean이 되어야 한다는 점을 잊지 말자.**



> **DI 받는다.**
>
> - DI는 단지 외부에서 파라미터로 오브젝트를 넘겨줬다고 해서 즉, 주입해줬다고 해서 다 DI가 아니다.
>   - 주입받는 메소드 파라미터가 이미 특정 클래스 타입으로 고정되어 있다면 DI가 일어날 수 없다(**인터페이스가 아니라면**)



### *의존관계 주입의 응용*

- DI 기술의 장점은 무엇일까?
  - DaoFactory 방식이 DI 방식을 구현한 것이니, 해당 장점을 그대로 이어받는다.
    1. 코드에는 런타임 클래스에 대한 의존관계가 나타나지 않는다.
    2. 인터페이스를 통해 결합도가 낮은 코드를 만든다.
    3. 다른 책임을 가진 객체(여러 ConnectionMaker)가 바뀌거가 변경되더라고 자신(UserDao)은 영향을 받지 않는다.
    4. 변경을 통한 다양한 확장 방법이 자유롭다.



- **UserDao가 ConnectionMaker라는 인터페이스에만 의존한다는 것은, 어떤 객체든 ConnectionMaker를 구현하기만 하고 있다면 어떤 오브젝트든지 사용할 수 있다는 뜻이다.**



**몇 가지 응용 사례를 살펴보자**

*기능 구현의 교환*

- 만약 DI 방식을 사용하지 않고, DBConnectionMaker 클래스를 사용했다고 가정해보자.

  - DI 방식 (즉, 관계설정과 생성 등을 주입)을 사용하지 않고, 개발 서버와 운영 서버를 분리하여 개발을 진행하고 있다.
  - 이런 이유로 개발 서버는 개발 DB Connection의 환경설정이 필요하고, 운영 서버는 운영 DB Connection의 환경설정이 필요하다.
  - 즉, 여러 DAO를 사용할 때 모든 DAO에 DBConnection를 하는 클래스를 각각 생성해줘야하고, 개발 서버에서 개발할 때는 개발 DBConnection으로 변경하고, 운영 서버에 배포할 때는 운영 DBConnection으로 변경해야하는 말도 안되는 일이 발생하는 것이다.

- DI 방식을 사용하게 된다면!

  - DI 방식을 사용하면 모든 DAO 생성 시점에 **ConnectionMaker 타입의 오브젝트를 컨테이너로부터 제공받는다.**

  - 구제적인 사용 클래스 이름은 컨테이너가 사용할 설정 정보에 들어있다.

  - 즉, **@Confguration이 붙은 DaoFactory를 사용한다고 하면 DBConnection에 관련된 메소드만 추가해서 사용하면 된다. 또한, 개발 서버면 개발 DBConnection으로 변경하고, 운영 서버면 운영 DBConnection으로 변경하면 되는 것이다.**

    ~~~java
    //개발 서버
    @Bean
    public ConnectionMaker connectionMaker() {
      return new LocalDBConnectionMaker();
    }
    
    //운영 서버
    @Bean
    public ConnectionMaker connectionMaker() {
      return new LocalDBConnectionMaker();
    }
    ~~~

  - 개발서버든, 운영서버든 단 한줄만 변경하면 되는 것이다! DAO가 아무리 많아도!



*부가기능 추가*

- DB가 몇 번이나 연결되어있는지 확인하고 싶다고 해보자.

  - 그럼 DAO에서 makeConection() 메소드를 호출하는 소스를 수정해야할까? 이것은 올바른 방법이 아니다.

    - DI 방식을 도입한 이유는 DAO코드의 수정을 피하려고 했던 것이 아닌가?!

  - DI 컨테이너를 사용하면 아주 간단하게 가능하다.

  - **컨테이너가 사용하는 설정정보만 수정해서 런타임 의존관계만 새롭게 정의해주면 된다.**

  - CountingConnectionMaker 클래스를 구현하여 카운팅하는 로직을 만들자

    ~~~java
    public class CountingConnectionMaker implements ConnectionMaker {
      int counter = 0;
      private ConnectionMaker realConnectionMaker;
      
      public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
      }
      
      public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeConnection();
      }
      
      public int getCounter() {
        return this.counter;
      }
      
    }
    ~~~

    - DAO가 DB 커넥션을 가져올 때마다 호출하는 makeConnection()에서 DB연결횟수 카운터를 증가시킨다.



- CountingConnectionMaker가 추가되면서 UserDao와 ConnectionMaker의 의존관계가 어떻게 변화되는지 확인해보자

  - CountingConnectionMaker를 사용하기 전이다.

  <img src="https://user-images.githubusercontent.com/40616436/76698588-32dcd300-66e8-11ea-963c-d6eddfb2308b.png" alt="image" style="zoom:50%;" />

  - **UserDao는 ConnectionMaker의 인터페이스에만 의존하고 있기 때문에, ConnectionMaker 인터페이스를 구현하고 있다면 어떤 것이든 DI가 가능하다.**

  - 그로인해, UserDao 오브젝트가 DConnectionMaker 대신 CountingConnectionMaker 오브젝트로 바꿔치기하여 **DB 커넥션을 할 때마다 CountingConnectionMaker의 makeConnection() 메소드가 실행되어 카운터가 증가되는 것이다.**

    <img src="https://user-images.githubusercontent.com/40616436/76698606-64559e80-66e8-11ea-87a8-30888c7ae8ce.png" alt="image" style="zoom:50%;" />

  - 소스로 확인해보자

    ~~~java
    @Configuration
    public class CountingDaoFactory {
    
        @Bean
        public UserDao userDao() {
            return new UserDao(connectionMaker());
        }
    
        @Bean
        public ConnectionMaker connectionMaker() {
            return new CountingConnectionMaker(realConnectionMaker());
        }
    
        @Bean
        public ConnectionMaker realConnectionMaker() {
            return new DConnectionMaker();
        }
    
    }
    ~~~

    - 기존 DaoFactory와 달리, connectionMaker() 메소드에서 CountingConnectionMaker 타입 오브젝트를 생성하도록 만든다.
    - 그리고 실제 DB Connection을 만들어주는 부분은 realConnectionMaker() 메소드에 의해서 만들어준다.
    - **기존 DAO를 수정하지 않고, 카운팅되는 클래스의 추가만으로 부가 기능을 추가한 것을 볼 수 있다.**

  - DB 커넥션 실행에 따른 카운팅하는 소스를 실행해보자

    ~~~java
    public class UserDaoConnectionCountingTest {
        public static void main(String [] args) throws ClassNotFoundException, SQLException {
    
            ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    
            UserDao userDao = context.getBean("userDao", UserDao.class);
    
            //DL을 사용하여 Bean을 가져오자 (getBean 시 이름을 통해서 Bean을 가져올 수 있다.)
            CountingConnectionMaker countingConnectionMaker = context.getBean("connectionMaker", CountingConnectionMaker.class);
          
            System.out.println("Connection counter : " + countingConnectionMaker.getCounter());
    
        }
    }
    ~~~



- 즉, **DAO가 의존하는 ConnectionMaker를 구현한 오브젝트 타입이면 DAO와 의존하는 것에 문제가 없으므로, CountingConnectionMaker를 구현하여 DAO와 의존시키면 우리가 원하는 DB 카운팅 값을 호출할 수 있는 것이다.**
- **이런 식으로 진행하다, DB 카운팅 분석이 끝나면 그저 설정 클래스를 CountingDaoFactory에서 DaoFactory로 변경만 해주면 되는 것이다.**

- **이 처럼, DI를 사용하게 된다면 많은 장점을 얻을 수 있는 것이다.**



### *메소드를 이용한 의존관계 주입*

- 지금까지 살펴본 의존관계 주입은 생성자를 통해 주입을 했는데, 꼭 생성자를 사용해야 하는 것은 아니다. **생성자가 아닌 일반 메소드를 이용해 의존 관계를 주입할 수 있는데 그 방법을 살펴보자.**



*수정자(Setter) 메소드를 이용한 주입*

- 수정자 메소드는 외부에서 오브젝트 내부의 Attribute값을 변경하려는 용도로 자주 사용된다.
- 수정자 메소드는 **외부로부터 제공받은 오브젝트 레퍼런스를 저장해뒀다가, 내부의 메소드에서 사용하게 하는 DI 방식에서 활용하기에 적당하다.**



*일반 메소드를 이용한 주입*

- 수정자 메소드처럼 메소드 이름이 set으로 시작되어야하고, 한 번에 한 개의 파라미터만 가질 수 있다는 제약 대신, **일반 메소드를 사용하여 DI용을 사용**할 수 있다.
- **임의의 초기화 메소드를 이용하는 DI를 사용하면 적절한 개수의 파라미터를 가진 여러 개의 초기화 메소드를 만들 수 있어, 필요한 모든 메소드를 한 번에 받아야 하는 생성자보다 낫다.**



- UserDao도 수정자 메소드 DI 방식을 사용하도록 해보자

  ~~~java
  public class UserDao {
     private ConnectionMaker connectionMaker;
    
     public void setConnectionMaker(ConnectionMaker connectionMaker) {
          this.connectionMaker = connectionMaker;
      }
  }
  
  //Factory
  @Bean
  public UserDao userDao() {
    //return new UserDao(connectionMaker());
    UserDao userDao = new UserDao();
    userDao.setConnectionMaker(connectionMaker());
    return userDao;
  }
  ~~~

  - 생성자는 모두 제거하고 수정자 메소드만 유지하고, DI를 적용하는 Factory의 코드도 수정해야 한다.

