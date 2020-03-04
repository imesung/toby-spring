## Ch1. 오브젝트와 의존관계
**리팩토링**
- 기존의 코드를 외부의 동작방식에는 변화없이 내부 구조를 변경해서 재구성하는 작업 또는 기술을 말한다.





### **상속을 통한 확장**

- 소스코드를 제공하지 않고 클래스 파일만 제공하여 고객이 필요한 부분만을 수정해서 사용하게끔 할 수 있는가? Ex. DB Connection
- Connection 부분을 추상메소드로 만들어 고객에게 직접 구현할 수 있게끔 한다.
	- 즉, 상속을 통해 확장하는 것이다.
	- 그림 1-1



**템플릿 메소드 패턴**

- 상속을 통해 슈퍼클래스의 기능을 확장할 때 사용하는 가장 대표적인 방법이다.
- 서브 클래스에서 필요에 맞게 추상 메소드를 구현해서 사용하는 방법을 디자인한 것이다.



**팩토리 메소드 패턴**

- 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것을 디자인 한 것이다.



**상속의 단점**

- 현재 상속 받고 있는 슈퍼클래스의 다른 목적이 필요할 경우 해당 슈퍼클래스를 상속받아야 하는데, 자바에서는 다중 상속이 지원되지 않는다.
- 서브클래스와 슈퍼클래스는 밀접한 관계를 가지고 있다. 즉, 슈퍼 클래스의 수정이 서브 클래스에 영향을 미친다.







### **클래스 분리**

- 상속의 단점을 해결하기 위해 두개의 독립된 클래스로 분리해보자.

  <img src="https://user-images.githubusercontent.com/40616436/75679116-5e1bf700-5cd2-11ea-8700-f176678ca17b.png" alt="image" style="zoom:50%;" />

- 하지만 이 방법을 사용할 시 고객사에 납품할 때 UserDao의 소스코드를 함께 제공해야지만 DB 연결방법을 바꿔야한다는 문제점이 나타난 것이다.

  - DB 커넥션의 클래스를 UserDao가 참조하고 있으므로, 해당 클래스를 사용하려면 고객사에서 직접 DB 커넥션 클래스를 추가해줘야 한다.

  ~~~java
  //UserDao.java
  simpleConnectionMaker = new SimpleConnectionMaker();	//해당 클래스는 고객사마다 다르게 구현될 것.
  ~~~



- **자유로운 확장이 가능하게끔 하려면 두 가지 문제를 해결해야 한다.**

  1. SimpleConnectionMaker의 메소드가 문제이다.

     - 각 고객사에서 DB 커넥션하는 메소드의 명이 다르면 UserDao의 add(), get()에서 수정이 필요하게 된다.

       ~~~java
       Connection c = simpleConnectionMaker.openConnection();
       ~~~

       

  2. DB 커넥션을 제공하는 클래스가 어떤 것인지를 UserDao가 구체적으로 알고 있어야 한다.

     - 고객사에서 다른 클래스를 구현하게되면 UserDao에 직접적인 수정이 필요하다.

- 이런 문제를 해결하기 위해서 **인터페이스가 도입되었다.**







### **인터페이스의 도입**

- 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만들어주는 것이다.

  ![image](https://user-images.githubusercontent.com/40616436/75682088-2c0d9380-5cd8-11ea-9894-3a819ba18a2b.png)

- **UserDao가 인터페이스를 사용하게 되면 인터페이스의 메소드를 통해 알 수 있는 기능에만 관심을 가지면 되므로, 그 기능을 어떻게 구현했는지에 대해서는 관심을 둘 필요가 없다.**

- 결과적으로, **고객에게 납품할 때는 UserDao 클래스와 함께 ConnectionMaker 인터페이스도 전달하게 된다.**

  - 고객은 ConnectionMaker의 인터페이스를 구현한 클래스를 만들고 UserDao를 사용하면 되는 것이다.

- 하지만, 이 방법 또한 UserDao를 직접적으로 수정해야한다.

  ~~~java
  //UserDao
  connectionMaker = new DConnctionMaker();	//D사가 구현한 인터페이스 구현 클래스
  ~~~







### **관계설정 책임의 분리**

UserDao를 직접적으로 수정해야하는 문제점을 해결해보자

- UserDao 오브젝트와 특정 클래스로부터 만들어진 ConnectionMaker 오브젝트 사이에 관계를 설정해주는 것이다.

  - 클래스 사이의 관계가 만들어지는 것은 코드에 다른 클래스의 이름이 나타나 있다는 것이고, **오브젝트 사이의 관계는 해당 클래스가 구현한 인터페이스와 관계를 맺었다는 것이다.(다형성)**

- 인터페이스를 활용한 UserDao의 관계를 자세히 보면 **고객사의 DB 커넥션을 직접적으로 참조하고 있기 때문에 그림과 같이 불필요한 의존관계가 형성된다.**

  ![image](https://user-images.githubusercontent.com/40616436/75683517-9f180980-5cda-11ea-804e-106bea7825cd.png)



- 아래 그림은 런타임 시점의 오브젝트 간 관계를 나타내는 **오브젝트 다이어그램**이다.

  ![image](https://user-images.githubusercontent.com/40616436/75683933-5280fe00-5cdb-11ea-9c4c-a5a8ad5f3885.png)



- **클라이언트와 같은 제 3의 오브젝트가 UserDao 오브젝트가 사용할 ConnectionMaker 오브젝트를 전달해주도록 해보자.**

  - 즉, UserDao 생성자의 파라미터를 ConnectionMaker로 설정하여 클라이언트가 어떤 ConnectionMaker를 사용할지 결정하는 것이다.

    ~~~java
    //UserDao 생성자
    public UserDao(ConnectionMaker connectionMaker) {
      this.connectionMaker = connectionMaker;
    }
    
    //Main(클라이언트)
    public static void main(String [] args) {
      ConnectionMaker connectionMaker = new DConnectionMaker();	//UserDao가 사용할 ConnectionMaker 구현 클래스를 결정하고 오브젝트를 만든다.
      
      UserDao userDao = new UserDao(connectionMaker);
    }
    ~~~

  - 이로 인해, ConnectionMaker 인터페이스를 구현했다면 어떤 클래스로 만든 오브젝트더라도 UserDao의 생성자 파라미터로 들어오기만 하면 되므로, **UserDao는 DB 커넥션에 관련된 클래스에 관심도 없게 된다.**







### 원칙과 패턴

**개방 폐쇄 원칙**

- 개방 폐쇄 원칙을 정의하면 **클래스나 모듈은 확장에는 열려 있어야 하고, 변경에는 닫혀 있어야 한다.**
- 예를 들어, 우리가 리팩토링한 UserDao의 경우
  - DB 연결 방법이라는 기능을 확장하는데는 열려 있고, 확장하는데 UserDao에 전혀 영향을 주지 않는다.
  - UserDao 자신의 핵심 기능을 구현한 코드는 여러 변화에 영향을 받지 않고 유지할 수 있으므로 닫혀 있다고 말할 수 있다.
- 잘 설계된 객체지향 클래스의 구조를 살펴보면 바로 이 **개방 폐쇄 원칙**을 잘 지키고 있다.



**객체 지향 설계의 5가지 원칙**

SOLID 원칙이라고도 한다.

1. SRP : 단일 책임 원칙
2. OCP : 개방 폐쇄 원칙
3. LSP : 리스코프 치환 원칙
4. ISP : 인터페이스 분리 원칙
5. DIP : 의존관계 역전 원칙



**높은 응집도와 낮은 결합도**

- 높은 응집도
  - 하나의 모듈, 클래스가 하나의 책임 또는 관심사에만 집중되어 있다는 뜻이다.
- 낮은 결합도
  - 느슨하게 연결된 형태를 유지하는 것이 바람직하다.
  - 느슨한 연결은 관계를 유지하는데 필요한 최소한의 방법만 간접적인 형태로 제공하고, 나머지는 서로 독립적이고 알 필요 없게 만들어주는 것이다.
    - 관계를 유지할 수 있는 메시지로만 서로를 유지하고, 메시지를 처리하는 방법은 자율적으로 선택할 수 있게 하는 것이다.

- **UserDao는 그 자체로 응집도가 높고 결합도가 낮다.**
  - 높은 응집도
    - 사용자의 데이터를 처리하는 기능이 DAO 안에 모여있다.
    - 또한, ConnectionMaker는 자신의 기능에 충실하도록 독립되어 있어 순수한 자신의 책임을 담당하는데만 충실할 수 있다.
  - 낮은 결합도
    - UserDao와 ConnectionMaker의 관계는 인터페이스를 통해 매우 느슨하게 연결되어 있다.
    - UserDao는 구체적인 ConnectionMaker 구현 클래스를 알 필요도 없고, 구현 방법이나 전략에 대해서 신경쓰지 않아도 된다.



**전략 패턴**

- 전략 패턴은 필요에 따라 변경이 필요한 알고리즘을 인터페이스를 통해 통째로 외부로 분리시키고, 이를 구현한 알고리즘 클래스를 필요에 따라 변경하여 사용할 수 있게 하는 디자인 패턴이다.
  - 즉, UserDao와 ConnectionMaker의 관계를 말하는 것이다. DB Connection의 알고리즘을 통째로 인터페이스로 분리시킨 것처럼..



>  **스프링이란, 지금까지 설명한 객체지향적 설계 원칙과 디자인 패턴에 나타난 장점을 자연스럽게 개발자들이 활용할 수 있게 해주는 프레임워크인 것이다.**







### 제어의 역전

**팩토리**

- 팩토리 : 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 일을 한다.

  - 오브젝트를 생성하는 쪽과 생성된 오브젝트를 사용하는 쪽의 역할과 책임을 분리하려는 목적으로 사용하는 것이다.

  ~~~java
  public class DaoFactory {
    public UserDao userDao() {
      
      //팩토리 메소드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다.
      ConnectionMaker connectionMaker = new DConnectionMaker();
      UserDao userDao = new UserDao(connectionMaker);
      
      return userDao;
    }
  }
  ~~~



- 기존 UserDaoTest의 main에서는 UserDao가 어떻게 만들어지고 어떻게 초기화되는지 알고 있어야 했으나, **팩토리 메소드** 를 도입함으로써, UserDao에 관련된 로직은 모두 팩토리 메소드에게 맡기게 되는 것이다.

  ~~~java
  public class UserDaoTest {
    public static void main(String [] args) throws ClassNotFoundException, SQLException {
      UserDao dao = new DaoFactory.userDao();
    }
  }
  ~~~



**설계도로서의 팩토리**

- 이렇게 분리된 오브젝트들의 역할과 관계를 분석해보면, 

  - UserDao와 ConnectionMaker는 각각 애플리케이션의 핵심적인 데이터 로직과 기술 로직을 담당한다.
  - DaoFactory는 애플리케이션의 오브젝트들을 구성하고 그 관계를 정의하는 책임을 맡고 있다.

- 이 처럼, 실질적인 로직을 담당하는 부분을 **컴포넌트** 로 불리고, 컴포넌트 구조와 관계를 정의하는 부분을 **설계도**라고 불린다.

  <img src="https://user-images.githubusercontent.com/40616436/75882202-08775400-5e64-11ea-9e30-202c05c79682.png" alt="image" style="zoom:50%;" />

  - 이제 고객사에 UserDao를 공급할 때는 UserDao, ConnectionMaker 뿐만 아니라 DaoFactory도 제공해주는데, DaoFactory는 소스로 제공해줘야 한다.
    - DaoFactory에서 ConnectionMaker의 구현 클래스로 변경이 필요하면 수정을 해야하기 때문이다.



**오브젝트 팩토리의 활용**

- **이제 만약에, UserDao뿐만 아니라 다른 DAO(AccountDao, MessageDao)도 추가된다고 가정해보자**

  - 이렇게 되면, DaoFactory의 ConnectionMaker 구현 클래스의 오브젝트를 생성하는 부분이 중복되고 말것이다.

    ~~~java
    public class DaoFactory {
      public UserDao userDao() {
        return new UserDao(new DConnectionMaker());
      }
      
      public AccountDao accountDao() {
        return new AccountDao(new DConnectionMaker());
      }
      
      public MessageDao messageDao() {
        return new MessageDao(new DConnectionMaker());
      }
      
    }
    ~~~

    - userDao(), accountDao(), messageDao()의 ConnectionMaker 구현 클래스의 오브젝트를 생성하는 부분이 중복되는 것을 확인할 수 있다.

  

- 이런 중복 문제를 해결하기 위해선 분리해내는게 가장 좋다. **ConnectionMaker의 구현 클래스를 결정하고 오브젝트를 생성하는 코드를 별도의 메소드로 분리하는 것이다.**

  ~~~java
  public class DaoFactory {
    public UserDao userDao() {
      return new UserDao(connectionMaker());
    }
    
    //구현 클래스의 오브젝트를 생성하는 과정을 메소드로 분리하여 중복을 방지했다.
    public ConnectionMaker connectionMaker() {
      return new DConnectionMaker();
    }
  }
  ~~~

  

**제어권의 이전을 통한 제어관계 역전**

- 기존에 모든 오브젝트는 자신이 사용할 클래스를 정하고, 언제 어떻게 오브젝트를 만들지 스스로 결정한다.
  - 예를 들어, main 메소드는 UserDao 클래스의 오브젝트를 직접 생성하고, 만들어진 오브젝트의 메소드를 사용한다.
- **제어의 역전이란, 이런 제어 흐름의 개념을 거꾸로 뒤집는 것이다.**
  - 제어의 역전에서는 오브젝트가 자신이 사용할 오브젝트를 스스로 선택하지 않고 생성하지도 않는다.
  - 또한, 자신도 어떻게 만들어지고 어떻게 사용되는지를 알 수 없다.
  - Why? 모든 제어 권한을 다른 대상에게 위임했기 때문이다.

- **템플릿 메소드 패턴에서도 제어의 역전 개념을 찾아볼 수 있다.**
  - 서브 클래스에서 슈퍼 클래스의 추상 메소드를 구현한다. 하지만 해당 메소드는 서브 클래스에서 결정되는 것이 아니다.
  - *서브 클래스에서는 단지 슈퍼 클래스의 추상 메소드의 기능을 구현하기만 해 놓으면, 슈퍼 클래스의 템플릿 메소드에서 필요할 때 호출해서 사용하는 것이다.*
    - *Ex. UserDao의 getConnection()은 서브 클래스에서 구현, UserDao의 get(), add()에서 필요에 따라 getConnection()  불러와 사용한다.*
  - **즉, 제어권을 상위 템플릿 메소드에 넘기고 자신은 필요할 때 호출되어서 사용되도록 한다는 것이다.**



- 우리가 만든 UserDao와 ConectionMaker, DaoFactory도 제어의 역전 개념이 적용되어 있다.
  - 기존 ConnectionMaker의 구현 클래스를 결정하고 오브젝트를 생성하는 제어권은 UserDao에게 있었다. (UserDao 생성자에서 ConnectionMaker 생성)
  - 하지만 지금은 DaoFactory에게 있다.
  - 이로 인해, UserDao는 자신이 어떤 ConnectionMaker 구현 클래스를 만들고 사용할지를 결정하는 권한을 넘겼으니 **능동적인 부분에서 수동적인 부분으로 전환됐다.**
  - DaoFactory는 UserDao와 ConnectionMaker의 구현체를 생성하는 책임을 맡게 된 것이다.



**자연스럽게 관심을 분리하고 책임을 나누고 유연하게 확장 가능한 구조로 만들기 위해 DaoFactory를 도입했던 과정이 IoC를 적용하는 작업이었다.**

**현재 우리는 스프링을 사용하지 않고 IoC개념을 적용한 셈이다. 즉, IoC는 프레임워크만의 기술도 아니고 프레임워크가 꼭 필요한 개념도 아니다. 단순히 생각하면 디자인 패턴에서도 발견할 수 있는 프로그래밍 모델인 것이다.**







### 스프링의 IoC

- 스프링의 핵심을 담당하는 건, 바로 **빈 팩토리** 또는 **애플리케이션 컨텍스트**라고 불리는 것이다.
- 이 두 가지는 우리가 만든 DaoFactory가 하는 일을 좀 더 일반화한 것이라고 생각하면 된다.



### *오브젝트 팩토리를 이용한 스프링 IoC*

**애플리케이션 컨텍스트와 설정정보**

- 여기서는 우리가 만든 DaoFacotry를 스프링에서 사용 가능하도록 만들어 볼 것이다.
- 스프링에서는 **스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 빈이라고 부른다.**
- 스프링 빈은 **스프링 컨테이너가 생성과 관계설정, 사용 등을 제어해주는 제어의 역전이 적용된 오브젝트를 가리키는 말이다.**
- 스프링에서 **빈의 생성과 관게설정 같은 제어를 담당하는 IoC 오브젝트를 *빈 팩토리 혹은 애플리케이션 컨텍스트*라고 부른다.**
- **빈 팩토리와 애플리케이션 컨텍스트**
  - 빈 팩토리 : 빈을 생성하고 관계를 설정하는 IoC의 기본 기능에 초점을 맞춘 것이다.
  - 애플리케이션 컨텍스트 : 애플리케이션 전반에 걸쳐 모든 구성요소의 제어 작업을 담당하는 IoC 엔진이라는 의미이다.
  - **빈 팩토리의 확장 버전을 애플리케이션 컨텍스트라고 보면 된다.**



**DaoFactory를 사용하는 애플리케이션 컨텍스트**

DaoFactory를 스프링의 빈 팩토리가 사용할 수 있는 본격적인 설정정보로 만들어보자.

1. 먼저 스프링이 **빈 팩토리를 위한 오브젝트 설정을 담당하는 클래스라고 인식하기 위해 @Configuration을 추가한다.**
2. 그 후 **오브젝트를 만들어 주는 메소드에는 @Bean을 붙여준다.**
   - userDao() 메소드는 UserDao 타입 오브젝트를 생성하고 초기화해서 돌려주는 것이니 당연히 @Bean이 붙여진다.
   - 또한, ConnectionMaker 타입의 오브젝트를 생성해주는 connectionMaker() 메소드에도 @Bean을 붙여준다.
3. 이 두 가지 애노테이션 만으로 **스프링 프레임워크의 빈 팩토리 또는 애플리케이션 컨텍스트가 IoC 방식의 기능을 제공할 때 사용할 설정정보가 완성 된다.**

~~~java
@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        //return new UserDao(new DConnectionMaker());
        return new UserDao(connectionMaker());
    }

		...

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
~~~

4. 이제 DaoFactory를 설정정보로 사용하는 **애플리케이션 컨텍스트를 만들어보자**
5. **애플리케이션 컨텍스트는 ApplicationContext 타입의 오브젝트로서, ApplicationContext를 구현한 클래스는 여러가지가 존재하지는데, 그 중 @Configuration이 붙은 설정정보로 사용하려면 AnnotationConfigApplicationContext를 이용하면 된다.**
6. 애플리케이션 컨텍스트를 만들 때 파라미터로 DaoFactory 클래스를 넣어주고, ApplicationContext의 getBean()을 통해 UserDao 오브젝트를 가져올 수 있다.

~~~java
public class UserDaoTest {
    public static void main(String [] args) throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userDao = context.getBean("userDao", UserDao.class);
    }
}
~~~

- getBean() 메소드는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드이다.
- getBean()의 파라미터 중 "userDao"는 ApplicationContext에 등록된 빈의 이름이고, "userDao"는 DaoFactory 설정 정보에서 userDao() 메소드가 존재해야 하고, 해당 메소드에는 @Bean이라는 어노테이션이 붙여있어야 한다.
  - 여기서 userDao라는 이름의 빈을 가져온다는 것은 DaoFactory의 userDao()메소드를 호출하고 그 결과를 가져온다는 것이다.



- getBean()은 기본적으로 Object 타입으로 리턴하게 되어 있어 매번 캐스팅을 해줘야하는 부담이 있다.

  - 자바 5 이상부터 제네릭 메소드 방식을 사용해서 getBean()의 두번째 파라미터에 리턴 타입을 주면, 지저분한 캐스팅 작업이 나타나지 않는다.

- 스프링의 기능을 사용했으니, 필요한 라이브러리를 추가해줘야 한다.

  <img src="https://user-images.githubusercontent.com/40616436/75903563-1e494100-5e85-11ea-8f5d-f7be77ec6cf2.png" alt="image" style="zoom:50%;" />



