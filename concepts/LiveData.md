## LiveData

 `LiveData` 는 관찰가능한 데이터 홀더 클래스이다. 보통 관찰(~~은 무엇일까?~~)과 달리 `LiveData` 는 lifecycle-aware 인데, 그것은 `Activity` 와 `Fragment`, 또는 `Service` 와 같은 다른 앱 컴포넌트들의 수명주기와 관계있다. 이 인식은 `LiveData` 가 수명주기 상태에 활성화되어있는 앱 컴포넌트 observer들만을 업데이트하도록 보장한다.

> ★ Note : `LiveData` 컴포넌트를 안드로이드 프로젝트에 `import` 하기 위해서는 [프로젝트에 컴포넌트를 추가하는 방법](https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle) 을 보면 된다.



 `LiveData`는 수명주기가 `STARTED` 또는 `RESUMED ` 상태인 경우 `Observer ` 클래스로 표현되는 observer 를 활성상태로 간주한다. `LiveData` 는 오직 활성화된 observer 에게만 업데이트 사항을 알린다. `LiveData` 객체를 보기위해 등록된 비활성화 observer 들에게는 변동사항을 알려주지 않는다.

 `LifecycleOwner`  인터페이스를 구현하는 객체와 쌍을 이루는 observer 를 등록할 수 있다. 이 관계는 해당 수명주기 객체의 상태가 `DESTORYED` 로 변경될 때 observer 가 제거될 수 있게 해준다.  이는 `Activity` 와 `Fragment` 에 특히 유용한데, 그 이유는 `LiveData` 객체들을 안전하게 관찰하고, 메모리 누수에 대한 걱정이 없기 때문이다. `Activity` 와 `Fragment` 는 그들의 수명주기가 종료될 때, 즉시 구독이 취소(?)된다.

 `LiveData` 사용 방법에 대한 자세한 정보는 [LivaData 객체 사용](https://developer.android.com/topic/libraries/architecture/livedata#work_livedata) 을 참조하면 된다.



#### LiveData 의 장점

1. **Ensures your UI matches your data state** : UI 가 데이터 상태와 일치하는지 확인한다.

   `LiveData` 는 observer 패턴을 따른다. `LiveData` 는 수명주기 상태가 변경될 때 `Observer` 객체에 알린다. 이 `Observer` 객체에서 UI를 업데이트하기 위해 코드를 합칠 수 있다. **앱 데이터가 변경될 때마다** UI 를 변경하는 대신, observer 는 **변경 사항이 있을 때마다** UI 를 업데이트 할 수 있다. 

   _~~앱 데이터가 변경되는것 뭐고 변경사항이 있는 것은 뭘까?...~~_

2. **No memory leaks** : 메모리 누수가 없다.

   observer 는 `Lifecycle` 객체에 바인딩되어 연관된 수명주기가 destroy 되면 자체적으로 정리를 한다.

3. **No crashes due to stopped activities** : 멈춘 액티비티들로 인한  충돌이 없다.

   observer 의 수명주기가 백스택에 있는 액티비티와 같이 비활성화된 경우, `LiveData` 이벤트를 수신하지 않는다.

4. **No more manual lifecycle handling** : 수명주기 처리를 수동으로 하지 않아도 된다.

   UI 컴포넌트는 관련된 데이터를 관찰하고, 멈추거나 다시 시작하지 않는다. `LiveData` 는 관찰하는 동안 관련 수명주기가 변경되었음을 알고 있기 때문에 이 모든 것을 자동으로 관리한다.

5. **Always up to date data** : 항상 최신 데이터를 갖는다.

   수명주기가 비활성화되면, 다시 활성화 되었을 때 최신 데이터를 수신한다. 예를 들어, 백그라운드에 있던 액티비티가 포어그라운드로 돌아온 직후에 최신 데이터를 받는다.

6. **Proper configuration changes** : 적절한 구성 변경

   디바이스 화면을 돌릴때와 같은 구성 변경으로 인해 액티비티와 프래그먼트가 재생성될 때, 즉시 사용 가능한 최신 데이터를 받는다.

7. **Sharing resources** : 리소스 공유

   `LiveData` 객체를 싱글톤 패턴을 사용해 시스템 서비스를 감싸 확장하면 앱에서 공유할 수 있다. `LiveData` 객체는 시스템 서비스에 한번만 연결되고, 리소스가 필요한 모든 observer 는 `LiveData` 객체를 볼 수 있다. 자세한 내용은, [LiveData 확장](https://developer.android.com/topic/libraries/architecture/livedata#extend_livedata) 내용을 참조하면 된다.



### LiveData 와 함께하기

`LiveData` 객체를 사용하기 위해 다음 절차를 수행하라.

1. 특정 데이터 타입을 담기 위한 `LiveData` 객체를 생성한다. 이는 보통 `ViewModel` 클래스에서 수행한다.

2. `onChanged()` 메서드를 정의한 옵저버 객체를 생성한다. `onChanged()` 메서드는 `LiveData` 객체가 유지하고있는 데이터에 변화가 감지되었을 때 제어한다. (변경된 데이터를 UI에 어떻게 적용할 지 구현하면 된다.) 보통 `Activity`와 `Fragment`와 같은 UI 컨트롤러에 `Observer` 객체를 생성한다.

3. `LiveData`의 `observe()` 메소드를 사용하여 `Observer` 객체를 붙인다. `observe()` 메서드는 `LifecycleOwner` 객체를 파라미터로 받는다. `Observer` 객체를 `LiveData` 객체에 구독하여 변경사항을 알린다. 일반적으로 `Observer` 객체는 `Activity`와 `Fragment`와 같은 UI 컨트롤러에 연결한다.

   > ★ Note : `observeForever (Observer)` 메서드를 사용하여 연관된 `LifecycleOwner` 객체 없이 `Observer`를 등록 할 수 있다. 이 경우, `Observer`는 계속 활성화된 상태가 되어 데이터의 변화를 항상 알린다. `removeObserver(Observer)` 메서드를 호출하면 이를 제거할 수 있다.

`LiveData` 객체에 저장된 값을 업데이트할 때, 연결된 `LifecycleOwner`가 활성 상태에 있는 한 등록된 모든 observer가 트리거된다.

`LiveData` 를 사용하면 UI 컨트롤러의 observer가 데이터의 변경사항을 구독하도록 할 수 있다. `LiveData` 객체가 가진 데이터가 변경되면, UI가 자동으로 업데이트된다.



공식문서 번역이 아주 잘 되어있음 : http://dktfrmaster.blogspot.com/2018/02/livedata.html



## MediatorLiveData

public class MediatorLiveData 
extends MutableLiveData<T> 

java.lang.Object
   ↳	android.arch.lifecycle.LiveData<T>
 	   ↳	android.arch.lifecycle.MutableLiveData<T>
 	 	   ↳	android.arch.lifecycle.MediatorLiveData<T>



 다른 `LiveData`  객체를 관찰하고  `onCahnged` 이벤트에 반응할 수 있는 `LiveData` 의 하위클래스이다.

 이 클래스는 활성, 비활성 상태를 원본 `LiveData` 객체로 올바르게 전달한다.

 다음 시나리오를 생각해보라.

 `LiveData` 의 2가지 인스턴스를 가지고 있다. 이름을 `liveData1` 및 `liveData2` 로 지정하고, 하나의 객체인 `liveDataMerger` 에서 해당 데이터를 병합하고자 한다. 그런 다음 `liveData1` 과 `liveData2` 가 `MediatorLiveData` `liveDataMerger` 를 위한 원본이 될 것이고,  `onChanged` 콜백이 호출될 때마다 `liveDataMerger` 에 새로운 값을 설정한다.

``` java
 LiveData liveData1 = ...;
 LiveData liveData2 = ...;

 MediatorLiveData liveDataMerger = new MediatorLiveData<>();
 liveDataMerger.addSource(liveData1, value ->  liveDataMerger.setValue(value));
 liveDataMerger.addSource(liveData2, value ->  liveDataMerger.setValue(value));
```

 `liveData1` 에 의해 생성된 10개의 값만 `liveDataMerger` 에 병합되기를 원한다고 가정해보자. 그런 다음 10개의 값을 입력하면 `liveData1` 의 수신을 멈추고 소스로(?) 제거할 수 있다. (원본을 제거한다는 말일까?)

```java
liveDataMerger.addSource(liveData1, new Observer() {
      private int count = 1;

      @Override public void onChanged(@Nullable Integer s) {
          count++;
          liveDataMerger.setValue(s);
          if (count > 10) {
              liveDataMerger.removeSource(liveData1);
          }
      }
 });
```



### Summary

| Public methods |                                                              |
| -------------- | ------------------------------------------------------------ |
| `<S> void`     | `addSource(LiveData<S> source, Observer<S> onChanged)`<br>원본 `LiveData` 를 수신 대기하고, 원본 값이 변경되었을 때 observer의 `onChanged` 메소드가 호출된다. |
| `<S> void`     | `removeSource(LiveData<S> toRemote)`<br /> `LiveData` 수신을 중지한다. |