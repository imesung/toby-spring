### 1.4 제어의 역전

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
  - **제어의 역전에서는 *오브젝트가 자신이 사용할 오브젝트를 스스로 선택하지 않고 생성하지도 않는다.***
  - **또한, *자신도 어떻게 만들어지고 어떻게 사용되는지를 알 수 없다.***
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