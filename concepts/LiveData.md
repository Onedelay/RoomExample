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

