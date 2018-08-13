_**Room 을 사용하고싶은데 이해가 잘 안돼서 공식 사이트 문서 번역해보는 중...**_

## RoomExample

1. Room
2. [ViewModel](https://github.com/Onedelay/RoomExample/blob/master/concepts/ViewModel.md)
3. [LiveData](https://github.com/Onedelay/RoomExample/blob/master/concepts/LiveData.md)



### 공부할 내용

* 뷰모델은 Factory 를 이용하여 액티비티 위에서 생성한다. _(왜?)_
* 뷰모델은 `Activity`, `Fragment` 의 수명주기를 따르며, 서로간에 데이터를 공유할 수 있다.
* 뷰모델을 통해 데이터베이스로부터 데이터를 읽어들여 `LiveData` 에 담아 관리한다. _이때 find 메소드를 쓴다_
* `LiveData` 에 `Observer` 를 달아 데이터 변화를 관찰하며, 데이터가 변동될 때 UI 를 자동으로 변경할 수 있도록 한다.



