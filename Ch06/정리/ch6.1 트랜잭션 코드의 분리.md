## AOP

### 코드의 분리

~~~java
//UserService
public void upgradeLevles() throws Exception{
  TransactionStatus status = this.transacetionManager.getTransaction(new DefaultTransactionDefinition());
  try {
    
    //비즈니스 로직
    List<User> users = userDao.getAll();
    for(User user : users) {
      if(canUpgardeLevel(user)) {
        upgradeLevel(user);
      }
    }
    
    
    this.transactionManager.commit(status);
  } catch (Exception e) {
    this.transactionManager.rollback(status);
    throw e;
  }
}
~~~

- 위 코드는 비즈니스 로직과 트랜잭션의 시작과 종료를 담당하는 코드로 구성되어 있다.
- 자세히 살펴보면 비즈니스 로직과 트랜잭션의 시작과 종료의 코드는 서로 주고 받는 정보가 없어 **완벽하게 독립적인 코드이다.**
- 유일한 법칙은 **비즈니스 로직은 트랜잭션 시작과 종료 사이에서 작업을 해야한다는 것인데,** 이 법칙만 지키면 코드를 분리할 수 있지 않을까?



**메소드의 분리**

~~~java
//UserService
public void upgradeLevles() throws Exception{
  TransactionStatus status = this.transacetionManager.getTransaction(new DefaultTransactionDefinition());
  try {
    
    upgradeLevelsInternal();
    
    this.transactionManager.commit(status);
  } catch (Exception e) {
    this.transactionManager.rollback(status);
    throw e;
  }
}

//비즈니스 로직
private void upgradeLevelsInternal() {
  List<User> users = userDao.getAll();
  for(User user : users) {
    if(canUpgardeLevel(user)) {
      upgradeLevel(user);
    }
  }
}
~~~

- Private 메소드로 비즈니스 로직을 분리해놓으니 코드가 한결 깔끔해진 것을 볼 수 있다.



**DI를 이용한 클래스의 분리**

- 비즈니스 로직이 분리되어 코드가 깔끔해지긴 했지만, **아직 트랜잭션을 담당하는 기술적인 코드가 UserService에 자리잡고 있는 것을 볼 수 있다.**
- *비즈니스 로직과 트랜잭션 설정 정보가 서로 주고받지 않는다면, UserService 안에 트랜잭션 코드가 존재하지 않는 것처럼 할 수 있지 않을까?*
  - **즉, 트랜잭션 코드를 클래스(UserService) 밖으로 뽑아내자.**



## 

- ***DI 적용을 이요한 트랜잭션 분리***
  
  - 현재 UserService를 클라이언트가 직접 접근하여 사용하고 있는데, 만약 UserService 안에 있는 트랜잭션을 빼버리게 되면, 클라이언트는 트랜잭션 기능이 빠진 UserService를 사용하게 되어 문제가 발생한다.
  
    - **구체적인 구현 클래스를 직접 참조하는 경우의 전형적인 단점이다.**
  
    <img src="https://user-images.githubusercontent.com/40616436/77999286-62890d80-736d-11ea-9914-c2143b3099b8.png" alt="image" style="zoom:50%;" />
  
  - 이를 해결하기 위해, **직접 접근하지 않고 간접적으로 접근하면 된다.**
  
    - 간접적으로 접근하게 되면 클라이언트 소스의 수정없이 구현 클래스를 마음껏 변경해도 된다.
    - DI를 통해 간접적으로 접근하자.
    - DI의 기본 아이디어
      - DI는 실제 사용할 오브젝트의 클래스 정체는 감춘 채 인터페이스를 통해 간접으로 접근하는 개념이다.
  
  - UserService를 인터페이스로 만들고 기존 코드를 UserService의 구현 클래스인 UserServiceImpl로 만드는 것이다.
  
    <img src="https://user-images.githubusercontent.com/40616436/77999380-8a787100-736d-11ea-9601-ab5ca06549cd.png" alt="image" style="zoom:50%;" />
  
    - 이로인해, 클라이언트와의 결합이 약해지고, 직접 구현 클래스에 의존하고 있지 않기 때문에 유연한 확장이 가능해진다.
    - 그러나 우리는 비즈니스 로직과 트랜잭션 설정을 분리시켜야 하므로, 하나의 구현 클래스만 있는 것이 아닌 **두개의 구현 클래스를 만들어 동시에 사용해야한다.**
  
  - 비즈니스 로직의 UserService 구현 클래스와 트랜잭션 설정 UserService 구현 클래스를 만들자.
  
    <img src="https://user-images.githubusercontent.com/40616436/78001568-d973d580-7370-11ea-8dbf-0761e273b51d.png" alt="image" style="zoom:50%;" />
  
    - UserServiceImpl은 비즈니스 로직을 구현한 클래스 이고, UserServiceTx는 트랜잭션 설정을 구현한 클래스이다.
  
    - 두개의 구현 클래스를 동시에 사용하기 위해서는 하나의 구현 클래스에서 다른 구현 클래스를 참조해야한다.
  
      ~~~java
      public interface UserService {
        void add(User user);
        void upgradeLevels();
      }
      
      //비즈니스 로직 구현 클래스
      public class UserServiceImpl implements UserService {
        UserDao userDao;
        MailSender mailSender;
        
        List<User> users = userDao.getAll();
        for(User user : users) {
          if(canUpgardeLevel(user)) {
            upgradeLevel(user);
          }
        }
      }
      
      //트랜잭션 설정 구현 클래스
      public class UserServiceTx implements UserService {
        UserService userService;
        platformTransactionManager transactionManager;
        
        //UserService를 구현한 또 다른 오브젝트를 DI 받는다.
        public void setTransactionManager(
          	PlatformTransactionManager transactionManager) {
        	this.transactionManager = transactionManager;  
        } 
        
        public void setUserService(UserService userService) {
          this.userService = userService;
        }
        
        public void add(User user) {
          this.userService.add(user);
        }
        
        public void upgradeLevels() {
          TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
          try {
            
            userService.upgradeLevels();
            
            this.transactionManager.commit(status);
          } catch(RuntimeExcetion e) {
            this.transactionManager.rollback(status);
            throw e;
          }
        }
        
      }
      ~~~
  
      - 자세히 살펴보면 트랜잭션 설정을 한 구현 클래스는 **UserService를 구현한 또 다른 구현 클래스를 참조하고 있는 것을 볼 수 있다.**
        - 이 이유는, 트랜잭션 설정 구현 클래스는 단지 트랜잭션만 설정하고 비즈니스 로직에 관련된 것은 비즈니스 로직 구현 클래스에게 위임하기 위해서이다.
  
    

## 

- ***트랜잭션 적용을 위한 DI 설정***

  - 이제 설정파일 수정하여 비즈니스 로직과 트랜잭션 설정이 분리된 구현 클래스를 사용해보자.

  - 방법은 이렇다. 

    - 클라이언트가 UserService라는 인터페이스를 통해 비즈니스 로직을 이용하려고 할 때, 
    - 먼저 트랜잭션을 담당하는 구현 클래스가 트랜잭션에 관련되 작업을 진행해주고, 
    - 비즈니스 로직을 담은 구현 클래스가 이후에 호출돼서 작업을 수행하도록 만든다.

    ![image](https://user-images.githubusercontent.com/40616436/78004367-fa3e2a00-7374-11ea-9695-edcdf5631250.png)

    - **스프링 DI 설정에 의해 만들어진 최종 의존관계는 위 같은 그림이다.**

  - userService 빈이 의존하고 있던 transactionManager는 UserServiceTx의 빈이 의존하고, 비즈니스 로직과 관련되 UserDao와 mailSender는 UserServiceImpl 빈이 의존하게 한다.

    ~~~xml
    <bean id="userService" class="springbook.user.service.UserServiceTx" >
    	<property name="transactionManager" ref="transactionManager" />
      <property name="userService" ref="userServiceImpl" />
    </bean>
    
    <bean id="userServiceImpl" class="springbook.user.service.UserServiceImpl" >
    	<property name="userDao" ref="userDao" />  	
      <property name="mailSender" ref="mailSender" />
    </bean>
    ~~~

    - 이제 클라이언트는 UserServiceTx 빈을 호출해서 사용하도록 만들어야 하며, userService라는 대표적인 빈 아이디는 UserServiceTx 클래스로 정의된 빈에게 부여해준다.
    - 그리고 userService 빈은 UserServiceImpl 클래스로 정의되는, id가 userServiceImpl인 빈을 Di하게 만드는 것이다.



## 

- ***트랜잭션 경계설정 코드 분리의 장점***
  - **첫번째, 비즈니스 로직을 담당하고 있는 UserServiceImpl의 코드를 작성할 때는 트랜잭션과 같은 기술적인 내용에는 전혀 신경쓰지 않아도 된다.**
    - 트랜잭션은 DI를 이용해 UserServiceTx와 같은 트랜잭션 기능을 가진 오브젝트가 먼저 실행되도록 만들기만 하면 된다.
    - 스프링이나 트랜잭션 같은 로우레벨의 기술적인 지식이 부족한 개발자라고 할지라도, *비즈니스 로직을 잘 이해하고 자바 언어의 기초에 충실하면 복잡한 비즈니스로 로직을 담은 UserService 클래스를 개발할 수 있는 것이다.*
  - **두번째, 비즈니스 로직에 대한 테스트를 손쉽게 만들어낼 수 있다.**
    - 이 부분은 다음 절에서 자세히 살펴보자.