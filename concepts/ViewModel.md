## ViewModel

 `ViewModel` 클래스는 관련된 데이터를 수명주기를 고려한 방식으로 저장하고 UI 를 관리하기 위해 설계되었다. `ViewModel` 클래스는 화면 회전과 같은 구성 변경 사항에도 데이터가 그대로 유지된다.

> ★ Note : `ViewModel` 을 안드로이드 프로젝트로 가져오려면 [프로젝트에 구성 요소 추가하기](https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle) 를 참조하면 된다.

 안드로이드 프레임워크는 `Activity` 나 `Fragment` 와 같은 UI 컨트롤러의 수명주기를 관리한다. 프레임 워크는 특정 사용자의 동작 또는 사용자의 제어에서 완전히 벗어난 장치 이벤트에 대한 응답으로 UI 컨트롤러를 파괴하거나 다시 생성할 수 있다.

 시스템이 UI 컨트롤러를 destroy 하거나 다시 생성하면 임시로 저장된 UI 관련 데이터가 사라진다. 예를 들어, 앱에 사용자의 activity 중 하나에 대한 사용자 목록(데이터)이 포함될 수 있다. 구성 변경을 위해 activity 가 재생성 되었을 때, 새로운 activity 가 사용자 목록(데이터)을 다시 가져와야 한다. 단순한 데이터의 경우, activity 는 `onSaveInstanceState()` 메서드를 사용할 수 있고 그 데이터는 `onCreate()` 메서드에서 번들로부터 데이터를 복원할 수 있다. 그러나 이 방법은 _직렬화 된 후 역직렬화될 수 있는(?)_ 작은 데이터에만 적합하며, 사용자 또는 비트맵들의 목록과 같은 잠재적으로 많은 양의 데이터에는 적합하지 않다.

 또 다른 문제점은 UI 컨트롤러가 비동기 호출을 자주 만들어서 반환하는 데 시간이 걸릴 수 있다는 것이다. UI 컨트롤러는 이러한 호출을 관리하고 잠재적인 메모리 누수를 방지하기 위해 시스템이 destroy 된 후 정리해야한다. 이를 위해서는 많은 유지보수가 필요하고, 구성 변경을 위해 객체가 다시 만들어지는 경우 객체는 이미 만든 호출을 다시 재발행 해야하기 때문에 리소스가 낭비된다.

 `Activity` 및 `Fragment` 와 같은 UI 컨트롤러는 주로 UI 데이터를 표시하거나, 사용자 작업에 반응하거나, 권한 요청과 같은 운영 체제 통신을 처리하기 위해 고안되었다. UI 컨트롤러가 데이터베이스 또는 네트워크에서 데이터를 로드하는 작업을 수행하도록 요구하면 클래스에 _bloat(고창증, 부풀게하는것, 부푸는것, 부풀다....)_ 가 추가된다. UI 컨트롤러에 과도한 책임을 할당하면 _(많은 일을 시키면?)_ 앱의 모든 작업을 자체적으로 처리하려는 단일 클래스가 발생할 수 있다. 이런 방법으로 UI 컨트롤러에게 과도한 책임을 할당하면 테스트가 훨씬 더 어려워진다.

 UI 컨트롤러 로직으로부터 _뷰 데이터의 소유권(?)_을 분리하면 더 쉽고 효율적이다.



## 좋은 블로그 참고 내용

[원본출처](https://medium.com/@jungil.han/%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-viewmodel-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-2e4d136d28d2)

 안드로이드 앱 개발 시 겪는 어려움 중 하나는 안드로이드 컴포넌트의 수명주기를 제어하는 것이다. 그중에서도 액티비티와 프래그먼트의 수명주기는 상당히 복잡하다.

 런타임에 화면 방향이 전환되거나 언어, 글꼴 배율과 같은 기기 구성이 변경되는 경우, 안드로이드는 실행중인 액티비티를 종료하고, 메모리에서 제거 후 다시 생성하기 때문에 이 과정에서 액티비티에 종속된 UI 데이터를 유지하는 것은 어렵다.



### 화면 전환에 대한 이전 해결책

 액티비티가 종료되기 직전에 호출되는 `onSaveInstanceState()` 콜백 메서드에서 액티비티의 상태 및 데이터를 저장할 수 없지만, 직렬화할 수 없는 객체는 저장할 수 없다(사용자가 직접 정의한 객체 등등).

 다른 대안으로는 유보된 프래그먼트(Retained Fragment) _~~가 뭐지?~~_ 를 사용하는 방법이다. UI가 없는 worker 프래그먼트 개념을 도입해 이곳에서 UI 에 필요한 데이터를 관리하고, 프래그먼트를 `setRetainInstace(true)` 로 설정함으로써 액티비티 재생성시 프래그먼트를 메모리에 유지시키는 것이다. 이 때 프래그먼트는 액티비티에서 `onDetach` 된 후 새로운 액티비티로 다시 `onAttach` 될 뿐 소멸과 생성을 반복하지 않는다. 하지만 이 방법은 또 다른 엣지 케이스(일정한 범위를 넘어섰을 때 발생하는 문제)를 다뤄야 한다는 점과 데이터를 보관하기에 적합한지, 혹은 오버-엔지니어링이 아닐지 라는 문제가 있었다.

 

### ViewModel 로 해결하기

 Android Architecture Component 의 `ViewModel` 은 위 문제점을 근본적으로 해결해준다. `ViewModel` 은 액티비티와 프래그먼트에서 사용되는 UI 관련 데이터를 관리하기 위해 설계되었다. 액티비티가 재생성 되는 상황에서도 `ViewModel` 인스턴스를 유지하여 데이터를 안전하게 다룰 수 있다. 또한 데이터의 소유권을 액티비티와 프래그먼트로부터 분리시킴으로써, 액티비티와 프래그먼트는 UI 를 업데이트하는 역할에 집중시킨다는 의미에서 **단일 책임 원칙(?)**을 따를 수 있는 발판이 마련된 셈이다. 덕분에 UI 로직에 대한 테스트도 수월해졌다.

 다음 그림은 액티비티의 수명주기를 나타낸다. 보통 `ViewModel` 인스턴스는 액티비티의 `onCreate()` 에서 요청을 하는데, 아래와 같이 액티비티의 `onCreate()` 가 여러 번 호출되는 상황에서도 `ViewModel` 의 스코프(_수명?_)는 일관성있게 유지되는 것을 알 수 있다.

![viewmodel_lifecycle](images/viewmodel-lifecycle.png)

  `ViewModel` 은 액티비티 스코프의 싱글톤 객체처럼 사용할 수 있기 때문에 프래그먼트들 사이에서 `ViewModel` 을 이용해 데이터를 쉽게 공유할 수 있다. 이는 프래그먼트 간 데이터 공유에 더 이상 중간자 역할로서의 액티비티가 필요하지 않다는 것을 의미하며, 액티비티의 부담을 덜어준다. _(실제로 setArgument 등 액티비티 거쳐서 프래그먼트간에 데이터를 주고받았는데 아주 NICE 하군!)_ `ViewModel` 은 액티비티 스코프가 완전히 종료되는 시점에 종료되고, 이때 `onCleared()` 메서드가 호출된다. 이 콜백은 `ViewModel` 클래스의 유일한 메서드이며 `ViewModel` 의 리소스를 해제하기에 적합한 곳이다.



### ViewModel 좀 더 자세히 살펴보기

 재미있는 부분은 `ViewModel` 라이브러리가 내부적으로 프래그먼트를 사용한다는 점이다. `ViewModel` 생성은 `ViewModelProvider` 로만 가능한데, 액티비티나 프래그먼트에서 최초로 `ViewModel` 을 생성할 때, `ViewModelProvider` 가 `HolderFragment` 라고 명명된 프래그먼트를 생성해 액티비티에 추가한다. 이 `HolderFragment` 가 `ViewModel` 을 멤버 변수로 관리하며, 위에서 설명한 프래그먼트의 `setRetainInstance(true)` 로 프래그먼트를 유지하는 기법을 사용한다. _(그럼 위 방법이랑 똑같다는 것인가!)_

``` java
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class HolderFragment extends Fragment {
    public HolderFragment() {
        setRetainInstance(true);
    }
    ...
}
```

 이런 점에서 `ViewModel` 은 유보된 프래그먼트의 연장선에 있는 라이브러리다. 다만 개발자를 위해 많은 엣지 케이스들을 다루고 있다는 점에서 한 발 더 나아갔다고 볼 수 있다. _(아하!)_

 `ViewModel` 은 추상 클래스이며, 이 클래스를 상속하는 것만으로도 `ViewModel` 을 만들 수 있다. _(~~갑분코?~~)_

```kotlin
class ChronometerViewModel : ViewModel() {
    override fun onCleared() {
        // Do somthing to clean up
        ...
    }
}
```

 `ViewModel` 클래스는 자체적으로 어떤 기능도 포함하고 있지 않기 때문에, 일반적인 객체처럼 new 키워드(Java)로 객체를 생성하는 것은 아무런 의미가 없다. **반드시 `ViewModelProvider` 를 통해서 객체를 생성**해야 `HolderFragment` 에 의해 `ViewModel` 이 관리되며, 기기 구성 변경에서 살아남을 수 있다.

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {        
        val chronometerModel = ViewModelProviders.of(this).get(ChronometerViewModel::class.java)
        ...
    }
}
```

 이런 이유로 커스텀 생성자를 갖는 `ViewModel` 은 `ViewModelProvider` 에게 해당 객체를 생성할 수 있는 방법을 제공해야 한다. 이런 경우를 다루기 위해 `ViewModel` 라이브러리는 개발자에게 `ViewModelProvider.Factory` 인터페이스를 사용하도록 **강제**하고 있다.

 객체 생성 시  Long 형 파라미터가 필요한 `ViewModel` 클래스,

```kotlin
class ChronometerViewModel(val initialTime: Long) : ViewModel() {
   ...
}
```

 `ViewModel` 을 생성하기 위한 Factory 클래스,

```kotlin
class ChronometerViewModelFactory(val initialTime: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Long::class.java).newInstance(initialTime)
    }
}
```

 `ViewModelProvider` 에게 `VhronometerViewModel` 생성을 위한 팩토리를 다음과 같이 설정한다.

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val factory = ChronometerViewModelFactory(SystemClock.elapsedRealtime())
        val chronometerModel = ViewModelProviders.of(this, factory).get(ChronometerViewModel::class.java)
        ...
    }
}
```



### ViewModel 사용 시 주의할 점

 `ViewModel` 사용 시 `ViewModel` 에 액티비티, 프래그먼트, 뷰에 대한 컨텍스트를 저장해서는 안된다. 액티비티가 재생성될 때, `ViewModel` 은 액티비티 수명주기 외부에 존재하기 때문에 UI Context 를 `ViewModel` 에 저장한다면 메모리 릭을 발생시키는 직접적인 원인이 될 수 있다. 다만 `Application` Context 는 전체 앱의 수명주기를 의미하기 때문에 메모리 릭에 영향을 주지 않으며 이런 용도를 위해 `AndroidViewModel` 클래스를 제공한다.

 `ViewModel` 은 기기의 구성이 변경되었을 때만 유지된다. 따라서 사용자가 백 버튼을 클릭하거나, 최근 앱 목록에서 앱을 종료했을 때, 혹은 안드로이드 프레임워크가 앱을 종료했을 때는 어떠한 처리도 하지 않기 때문에 이런 상황에서는 `ViewModel` 이 메모리에 남아있기를 기대하면 안된다.